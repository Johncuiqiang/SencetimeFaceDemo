package com.cv.faceapi;

import android.graphics.Bitmap;
import android.util.Log;
import com.cv.faceapi.CvFaceApiBridge.cv_face_t;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CvFaceTrack {
	private Pointer handle;
	private static final int FACE_KEY_POINTS_COUNT = 106;
	static boolean DEBUG = false;
	PointerByReference ptrToArray = new PointerByReference();
	IntByReference ptrToSize = new IntByReference();

	/**
	 * Note track only one face： frist:trackHandle =
	 * CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(null,
	 * CV_FACE_SKIP_BELOW_THRESHOLD); second: setMaxDetectableFaces(1)参数设为1
	 * track多张人脸： first：开启多张人脸检测(CV_TRACK_MULTI_TRACKING) trackHandle =
	 * CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(null,
	 * CV_FACE_SKIP_BELOW_THRESHOLD | CV_TRACK_MULTI_TRACKING);
	 * second:setMaxDetectableFaces(-1)参数设为-1
	 */
	public CvFaceTrack(){};
	public int createHandle(String flag) {
		int rst = 1;
		Pointer pointer = new Memory(Pointer.SIZE);
		if ("21 points".equals(flag)) { 
			rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(pointer,
							null,CvFaceApiBridge.CV_DETECT_ENABLE_ALIGN_21
									| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC
									| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC_DETECTDEADLINE);
		} else if ("106 points".equals(flag)) {
			rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(pointer,null,
							CvFaceApiBridge.CV_DETECT_ENABLE_ALIGN_106
									| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC
									| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC_DETECTDEADLINE);

		}
		if (rst != ResultCodeTable.CV_OK) {
			//throw new RuntimeException("Calling cv_face_create_tracker() method failed! ResultCode="+ rst);
			return rst;
		}
		handle = pointer.getPointer(0);
		return rst;
	}

	public int setMaxDetectableFaces(int max) {
		IntByReference val = new IntByReference();
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_track_set_detect_face_cnt_limit(handle, max, val);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_track_set_detect_face_cnt_limit() method failed! ResultCode="+ rst);
		}
		int number = val.getValue();
		return number;
	}

	@Override
	protected void finalize() throws Throwable {
		long start_destroy = System.currentTimeMillis();
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_tracker(handle);
		long end_destroy = System.currentTimeMillis();
		Log.i("track21", "destroy " + (end_destroy - start_destroy) + " ms");
	}

	/**
	 * Given the Image by Bitmap to track face
	 * @param imageInput image by Bitmap
	 * @param orientationImage orientation
	 * @return CvFace array, each one in array is Detected by SDK native API
	 */
	public CvFace[] track(Bitmap image, int orientation) {
		int[] colorImage = CvUtils.getBGRAImageByte(image);
		return track(colorImage, CVImageFormat.CV_PIX_FMT_BGRA8888,image.getWidth(), image.getHeight(), image.getWidth(),orientation);
	}

	/**
	 * Given the Image by Byte Array to track face
	 * 
	 * @param colorImage
	 *            Input image by int
	 * @param cvImageFormat
	 *            Image format
	 * @param imageWidth
	 *            Image width
	 * @param imageHeight
	 *            Image height
	 * @param imageStride
	 *            Image stride
	 * @param orientation
	 *            Image orientation
	 * @return CvFace array, each one in array is Detected by SDK native API
	 */
	public CvFace[] track(int[] colorImage, int cvImageFormat, int imageWidth,int imageHeight, int imageStride, int orientation) {
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_track(handle,
				colorImage, cvImageFormat, imageWidth, imageHeight,
				imageStride, orientation, ptrToArray, ptrToSize);

		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_track() method failed! ResultCode=" + rst);
		}

		if (ptrToSize.getValue() == 0) {
			if (DEBUG)Log.d("Test", "ptrToSize.getValue() == 0");
			return new CvFace[0];
		}

		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
		arrayRef.read();
		int p_faces_count = ptrToSize.getValue();
		cv_face_t[] array = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(p_faces_count));
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_release_tracker_result(ptrToArray.getValue(), ptrToSize.getValue());

		int length = array.length;
		CvFace[] ret = new CvFace[length];
		for (int i = 0; i < length; i++) {
			ret[i] = new CvFace(array[i]);
		}

		if (DEBUG)
			Log.d("Test", "track : " + ret);

		return ret;
	}

	/**
	 * Given the Image by Byte to track face
	 * 
	 * @param image
	 *            Input image by byte
	 * @param orientation
	 *            Image orientation
	 * @param width
	 *            Image width
	 * @param height
	 *            Image height
	 * @return CvFace array, each one in array is Detected by SDK native API
	 */
	public CvFace[] track(byte[] image, int orientation, int width, int height) {
		return track(image, CVImageFormat.CV_PIX_FMT_NV21, width, height,width, orientation);
	}

	/**
	 * Given the Image by Byte Array to track face
	 * 
	 * @param colorImage
	 *            Input image by byte
	 * @param cvImageFormat
	 *            Image format
	 * @param imageWidth
	 *            Image width
	 * @param imageHeight
	 *            Image height
	 * @param imageStride
	 *            Image stride
	 * @param orientation
	 *            Image orientation
	 * @return CvFace array, each one in array is Detected by SDK native API
	 */
	public CvFace[] track(byte[] colorImage, int cvImageFormat, int imageWidth,int imageHeight, int imageStride, int orientation) {
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_track(handle,
				colorImage, cvImageFormat, imageWidth, imageHeight,
				imageStride, orientation, ptrToArray, ptrToSize);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_multi_track() method failed! ResultCode="+ rst);
		}
		int p_faces_count = ptrToSize.getValue();
		Log.i("track", "p_faces_count = " + p_faces_count);
		if (p_faces_count == 0) {
			return new CvFace[0];
		}

		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
		arrayRef.read();
		cv_face_t[] array = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(p_faces_count));
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_release_tracker_result(ptrToArray.getValue(), ptrToSize.getValue());

		int length = array.length;
		CvFace[] ret = new CvFace[length];
		for (int i = 0; i < length; i++) {
			ret[i] = new CvFace(array[i]);
		}

		if (DEBUG)
			Log.d("Test", "track : " + ret);

		return ret;
	}
}
