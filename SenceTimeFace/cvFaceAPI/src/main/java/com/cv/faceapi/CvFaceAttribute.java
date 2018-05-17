package com.cv.faceapi;

import android.R.integer;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.cv.faceapi.CvFaceApiBridge.cv_face_t;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CvFaceAttribute {
		
	private static final String MODEL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/attribute.model";
	
	private Pointer attribute_handle;
	private Pointer detect_Handle;
	private cv_face_t[] mFaceArray;
	private boolean mIsFirst;
	private int mLastFaceID;
	private int mCurrentFaceIdx = 0;
	private static CvFaceAttribute cfa;
	
	public CvFaceAttribute(){
		//init(MODEL_PATH ,null);
	}
	public static CvFaceAttribute getInstance()
	{
		if(cfa == null)
		{
			cfa = new CvFaceAttribute();
		}
	    return cfa;
	}
	
//	public CvFaceAttribute(String attrPath,String trackPath){
//		init(attrPath, trackPath);
//	}
	public int createAttributHandle()
	{
		Pointer pointer1 = new Memory(Pointer.SIZE);
	    int rst1 = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_attribute_detector(pointer1,MODEL_PATH);
	    if (rst1 != ResultCodeTable.CV_OK) {
			//throw new RuntimeException("Calling cv_face_create_attribute_detector() method failed! ResultCode="+ rst1);
	    	return rst1;
		}
	    attribute_handle = pointer1.getPointer(0);
	    return rst1;
	}

	public int createDetectHandle(int flag){	
	    Pointer pointer2 = new Memory(Pointer.SIZE);
	    int rst2 = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_detector(pointer2,null,flag);
    	if (rst2 != ResultCodeTable.CV_OK) {
            //throw new RuntimeException("Calling cv_face_create_detector() method failed! ResultCode=" + rst2);
    		return rst2;
        }
    	detect_Handle = pointer2.getPointer(0);
    	return rst2;
	}

	@Override
	protected void finalize() throws Throwable {
		long start_destroy = System.currentTimeMillis();
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_attribute_detector(attribute_handle);
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_tracker(detect_Handle);
		long end_destroy = System.currentTimeMillis();
		Log.i("attribute", "destroy cost "+(end_destroy - start_destroy)+" ms");
	}
	
	/**
	 * Given the Image by Bitmap to attribute face
	 * 
	 * @param image : Input image
	 * @return CvAttributeResult attribute result
	 */
	public CvAttributeResult attribute(Bitmap image) {
		return attribute(image, 0);
	}
	
	
	/**
	 * Given the Image by Bitmap to attribute face
	 * 
	 * @param image : Input image
	 * @return CvAttributeResult attribute result
	 */
	public CvAttributeResult attribute(Bitmap image,int rotation) {
		if(image == null || image.isRecycled()){
			return null;
		}
		Bitmap bitmap = image;
		if(rotation != 0){
			bitmap = CvUtils.getRotateBitmap(image, rotation);
		}
		int[] colorImage = CvUtils.getBGRAImageByte(bitmap);
		return attribute(colorImage,CVImageFormat.CV_PIX_FMT_BGRA8888, bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getWidth());
	}
	
	/**
	 * Given the Image by Byte Array to track and attribute face
	 * 
	 * @param image  Input image
	 * 
	 * @return CvAttributeResult 
	 */
	public CvAttributeResult attribute(int[] colorImage,int cvImageFormat, int imageWidth, int imageHeight,
			int imageStride) {
		//int i = 0;
		//CvAttributeResult attributeResult = new CvAttributeResult(mAttributeType);		
		CvAttributeResult attributeResult = new CvAttributeResult();		
		PointerByReference ptrToArray = new PointerByReference();
		IntByReference ptrToSize = new IntByReference();
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_detect(
				detect_Handle, colorImage,cvImageFormat, imageWidth, imageHeight, imageStride,
				1, ptrToArray, ptrToSize);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_detect() method failed! ResultCode="+ rst);
		}
		int p_faces_count = ptrToSize.getValue();
		if (p_faces_count == 0) {
			return attributeResult;
		}
		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
		arrayRef.read();
		mFaceArray = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(ptrToSize.getValue()));
		if(mFaceArray.length == 0){
			attributeResult.setHasFace(false);
			return attributeResult;
		}
		
//		if (mIsFirst) {
//			mLastFaceID = mFaceArray[0].ID;
//			mCurrentFaceIdx = 0;
//
//		} else {
//			for (i = 0; i < mFaceArray.length; i++) {
//				if (mLastFaceID == mFaceArray[i].ID) {
//					mCurrentFaceIdx = i;
//					break;
//				}
//			}
//			if(i == mFaceArray.length){
//				mCurrentFaceIdx = 0;
//			}
//		}
		
//		mIsFirst = false;
		int featureSize = attributeResult.getFeatureSize();
		int emotionSize = attributeResult.getEmotionSize();
		int[] results_attribute_feature = new int[featureSize];
		int[] results_attribute_emotion = new int[emotionSize];
		rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_attribute_detect(attribute_handle, colorImage, cvImageFormat, 
				imageWidth, imageHeight, imageStride, mFaceArray[mCurrentFaceIdx], 
				results_attribute_feature,results_attribute_emotion);
		attributeResult.setFeature(results_attribute_feature);
		attributeResult.setEmotion(results_attribute_emotion);
		attributeResult.setHasFace(true);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_attribute_detect() method failed! ResultCode="+ rst);
		}				
		return attributeResult;
	}	
}
