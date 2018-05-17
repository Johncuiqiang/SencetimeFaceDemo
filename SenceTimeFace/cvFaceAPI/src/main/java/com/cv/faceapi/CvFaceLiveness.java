package com.cv.faceapi;

import android.graphics.Bitmap;
import android.util.Log;
import com.cv.faceapi.CvFaceApiBridge.cv_face_t;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CvFaceLiveness {
	private Pointer mTrackHandle;
	private Pointer mLivenessHandle;
	private int mLastFaceID = -1;
	private IntByReference mLivenessState;
	private cv_face_t[] mFaceArray;
	private int mCurrentFaceID = -1;
	private boolean mIsFirst = true;	
	/**
	 * Initialize the Native Handler with model and memory
	 */
	public CvFaceLiveness() {
	}
	public int createTrackHandle(int flag)
	{
	  	Pointer pointer = new Memory(Pointer.SIZE);
    	int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(pointer,null, flag);
//    			| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC_DETECTDEADLINE);
//    	int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_tracker(pointer,null, 
//    			CvFaceApiBridge.CV_DETECT_ENABLE_ALIGN_106 
//    			| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC 
//    			| CvFaceApiBridge.CV_FACE_TRACKING_ASYNC_DETECTDEADLINE);
    	if (rst != ResultCodeTable.CV_OK) {
            //throw new RuntimeException("Calling cv_face_create_tracker() method failed! ResultCode=" + rst);
    		return rst;
        }
    	mTrackHandle = pointer.getPointer(0);
    	return rst;
	}
	public int createLivenessHandle(){	
    	Pointer pointer2 = new Memory(Pointer.SIZE);
		int rst2 = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_create_liveness_detector(pointer2,null, CvFaceApiBridge.CV_LIVENESS_DEFAULT_CONFIG);
		if (rst2 != ResultCodeTable.CV_OK) {
            //throw new RuntimeException("Calling cv_face_create_liveness_detector() method failed! ResultCode=" + rst2);
			return rst2;
        }
		mLivenessHandle = pointer2.getPointer(0);
		return rst2;
	}
	
//	public void reset(){
//		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_liveness_detector_reset(mLivenessHandle);
//	}

	@Override
	protected void finalize() throws Throwable {
		long start_destroy = System.currentTimeMillis();
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_tracker(mTrackHandle);
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_destroy_liveness_detector(mLivenessHandle);
		long end_destroy = System.currentTimeMillis();
		Log.i("liveness", "destroy cost "+ (end_destroy - start_destroy));
	}

	/**
	 * Given the Image by Bitmap to track face
	 * 
	 * @param image
	 *            Input image
	 * @return int liveness_state
	 */
	public CvLivenessResult liveness(byte[] data, int orientation,int width, int height) {
		//int[] colorImage = CvUtils.getBGRAImageByte(bitmap);
		return liveness(data,CVImageFormat.CV_PIX_FMT_NV21, width, height,width*4, orientation);
	}
//	public CvLivenessResult liveness(byte[] colorImage,int cvImageFormat, int imageWidth, int imageHeight,
//			int imageStride, int orientation){
//		int i = 0;
//		CvLivenessResult livenessResult = new CvLivenessResult();
//
//		PointerByReference ptrToArray = new PointerByReference();
//		IntByReference ptrToSize = new IntByReference();
//		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_track(
//				mTrackHandle, colorImage,cvImageFormat, imageWidth, imageHeight, imageStride,
//				orientation, ptrToArray, ptrToSize);
//		if (rst != ResultCode.CV_OK.getResultCode()) {
//			throw new RuntimeException(
//					"Calling cv_face_track() method failed! ResultCode="
//							+ rst);
//		}
//		if (ptrToSize.getValue() == 0) {
//			livenessResult.mStatus = CvLivenessResult.CVLIVENESSRESULT_STATUS_NULL;
//			return livenessResult;
//		}
//		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
//		arrayRef.read();
//		mFaceArray = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(ptrToSize
//				.getValue()));
//
//		if (mIsFirst && mFaceArray.length > 0) {
//			mLastFaceID = mFaceArray[0].ID;
//			mCurrentFaceID = 0;
//		} else if (mIsFirst == false && mFaceArray.length > 0) {
//			for (i = 0; i < mFaceArray.length; i++) {
//				if (mLastFaceID == mFaceArray[i].ID) {
//					mCurrentFaceID = i;
//					break;
//				}
//			}
//			if(i == mFaceArray.length){
//				mCurrentFaceID = 0;
//			}
//		}
//		mIsFirst = false;
//		FloatByReference liveness_score = new FloatByReference();
//		mLivenessState = new IntByReference();
//		long startTime = System.currentTimeMillis();
//		rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_liveness_detect(
//				mLivenessHandle, colorImage,cvImageFormat, imageWidth, imageHeight,
//				imageStride, mFaceArray[mCurrentFaceID], liveness_score, mLivenessState);
//		long endTime = System.currentTimeMillis();
//		Log.i("Test", "liveness detect time: "+(endTime-startTime)+"ms");
//		if (rst != ResultCode.CV_OK.getResultCode()) {
//			throw new RuntimeException(
//					"Calling cv_face_liveness_detect() method failed! ResultCode="
//							+ rst);
//		}
//					
//		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_release_tracker_result(
//				ptrToArray.getValue(), ptrToSize.getValue());
//		livenessResult.mStatus = mLivenessState == null ? CvLivenessResult.CVLIVENESSRESULT_STATUS_NULL : mLivenessState.getValue();
//		livenessResult.mScore = liveness_score.getValue();
//		livenessResult.mFaceRect = new CvFace21(mFaceArray[mCurrentFaceID]);
//		return livenessResult;
//	}

	/**
	 * Given the Image by Byte Array to track and liveness face
	 * 
	 * @param image  Input image
	 * 
	 * @return int liveness_state, 
	 */
	public CvLivenessResult liveness(byte[] colorImage,int cvImageFormat, int imageWidth, int imageHeight,
			int imageStride, int orientation) {
		int i = 0;
		CvLivenessResult livenessResult = new CvLivenessResult(); 
		PointerByReference ptrToArray = new PointerByReference();
		IntByReference ptrToSize = new IntByReference();
		int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_track(
				mTrackHandle, colorImage,cvImageFormat, imageWidth, imageHeight, imageStride,
				orientation, ptrToArray, ptrToSize);
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_track() method failed! ResultCode="+ rst);
		}
		Log.i("Test", "检查到人脸个数 == " +ptrToSize.getValue());
		if (ptrToSize.getValue() == 0) {
			livenessResult.mStatus = CvLivenessResult.CVLIVENESSRESULT_STATUS_NULL;
			return livenessResult;
		}
		cv_face_t arrayRef = new cv_face_t(ptrToArray.getValue());
		arrayRef.read();
		mFaceArray = cv_face_t.arrayCopy((cv_face_t[]) arrayRef.toArray(ptrToSize.getValue()));
        //
		if (mIsFirst && mFaceArray.length > 0) {
			mLastFaceID = mFaceArray[0].ID;
			mCurrentFaceID = 0;
		} else if (mIsFirst == false && mFaceArray.length > 0) {

			for (i = 0; i < mFaceArray.length; i++) {
				if (mLastFaceID == mFaceArray[i].ID) {
					mCurrentFaceID = i;
					break;
				}
			}

//			if (i == mFaceArray.length)
//			{
//				CvFaceApiBridge.FACESDK_INSTANCE.cv_face_liveness_detector_reset(mLivenessHandle);
//				livenessResult.mStatus = CvLivenessResult.CVLIVENESSRESULT_STATUS_RESET;
//				return livenessResult;
//			}

		}
		mIsFirst = false;
		FloatByReference liveness_score = new FloatByReference();
		mLivenessState = new IntByReference();
		long startTime = System.currentTimeMillis();
		rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_liveness_detect(
				mLivenessHandle, colorImage,cvImageFormat, imageWidth, imageHeight,
				imageStride, mFaceArray[mCurrentFaceID], liveness_score, mLivenessState);
		long endTime = System.currentTimeMillis();
		Log.i("Test", "liveness detect time: "+(endTime-startTime)+"ms");
		Log.i("Test", "CvFaceLiveness liveness_score == "+liveness_score + "mLivenessState == " +mLivenessState.getValue());
		if (rst != ResultCodeTable.CV_OK) {
			throw new RuntimeException("Calling cv_face_liveness_detect() method failed! ResultCode="+ rst);
		}
					
		CvFaceApiBridge.FACESDK_INSTANCE.cv_face_release_tracker_result(ptrToArray.getValue(), ptrToSize.getValue());
		livenessResult.mStatus = mLivenessState == null ? CvLivenessResult.CVLIVENESSRESULT_STATUS_NULL : mLivenessState.getValue();
		livenessResult.mScore = liveness_score.getValue();	
		return livenessResult;
	}
}
