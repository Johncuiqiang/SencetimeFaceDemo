package com.cv.faceapi;

import com.cv.faceapi.CvFaceApiBridge.*;
import com.cv.faceapi.CvFace;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import android.graphics.Bitmap;
import android.util.Log;

public class CvFaceDetector {
	private Pointer handle = null;
	public CvFaceDetector() {}
	public int createHandle(String flag)
	{
		int rst = 1;
		Pointer pointer = new Memory(Pointer.SIZE);
		if ("21 points".equals(flag)) {
			rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_detector(pointer, null, CvFaceApiBridge.CV_DETECT_ENABLE_ALIGN_21);
		} else if ("106 points".equals(flag)) {
			rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_detector(pointer, null, CvFaceApiBridge.CV_DETECT_ENABLE_ALIGN_106);
		}
		if (rst != ResultCodeTable.CV_OK) {
			//throw new RuntimeException("Calling cv_face_create_detector() method failed! ResultCode="+ rst);
			return rst;
		}
		handle = pointer.getPointer(0);
		return rst;
	}
	public float cv_face_detect_get_threshold() {
		FloatByReference td = new FloatByReference();
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_detect_get_threshold(handle, td);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_detect_get_threshold() method failed! ResultCode="+ rst);
		}
		float threshold = td.getValue();
		return threshold;
	}

	public void cv_face_detect_set_threshold(float threshold) {
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_detect_set_threshold(handle,threshold);
	}

	@Override
	protected void finalize() throws Throwable {
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_detector(handle);
	}

	public CvFace[] detect(Bitmap image) {
		int[] colorData = CvUtils.getBGRAImageByte(image);
		return detect(colorData, CVImageFormat.CV_PIX_FMT_BGRA8888,image.getWidth(), image.getHeight(), image.getWidth());
	}
	public CvFace[] detect(int[] colorImage, int cvImageFormat,int imageWidth, int imageHeight, int imageStride) {
		PointerByReference ptrToArray = new PointerByReference();
		IntByReference ptrToSize = new IntByReference();
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_detect(handle,
				colorImage, cvImageFormat, imageWidth, imageHeight,
				imageStride, 1, ptrToArray, ptrToSize);//

		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_detect() method failed! ResultCode=" + rst);
		}
		if (ptrToSize.getValue() == 0) {
			return new CvFace[0];
		}
		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
		arrayRef.read();
		int p_faces_count = ptrToSize.getValue();
		cv_face_t[] array = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(p_faces_count));
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_release_detector_result(ptrToArray.getValue(), ptrToSize.getValue());

		int length = array.length;
		CvFace[] ret = new CvFace[length];
		for (int i = 0; i < length; i++) {
			ret[i] = new CvFace(array[i]);
		}

		return ret;
	}
}
