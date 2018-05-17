package com.cv.faceapi;

import android.util.Log;
public class CvAttributeResult {
	
	private static final boolean DEBUG = true;
	private static final String TAG = "CvAttributeResult";
	
	private int mAttributeType;
	private int[] mFeature,mEmotion;
	private boolean mHasFace;
	
	public class Feature {
		public static final int CV_FEATURE_AGE = 0;	///< 年龄 0 - 100
		public static final int CV_FEATURE_GENDER_MALE = 1;	///< 性别 为男性的百分比置信度(0-100)  (>50 男  ; 100-value > 50  女)
		public static final int CV_FEATURE_ATTRACTIVE = 2;	 ///< 魅力 0 - 100
		public static final int CV_FEATURE_EYEGLASS = 3;	///< 眼镜 百分比置信度(0-100) (>50 戴;100-value>50 未戴)
		public static final int CV_FEATURE_SUNGLASS = 4;	///< 太阳镜 百分比置信度(0-100)  (戴眼镜的前提下  >50 太阳镜  ;100-value>50 普通眼镜）)
		public static final int CV_FEATURE_SMILE = 5;		///< 微笑 百分比置信度(0-100)
		public static final int CV_FEATURE_MASK = 6;	///< 面具 百分比置信度(0-100)	
		public static final int CV_FEATURE_RACE = 7;	///< 种族 Yellow 0, Black 1,White 2
		public static final int CV_FEATURE_EYE_OPEN = 8;	///< 眼睛睁开 百分比置信度(0-100)	
		public static final int CV_FEATURE_MOUTH_OPEN = 9;	///< 嘴巴张开 false 0, true 1	
		public static final int CV_FEATURE_BEARD = 10;		///< 有胡子 百分比置信度(0-100)
		public static final int CV_FEATURE_LENGTH = 32;		///< 属性条目数
	} 

	public class Emotion {
		public static final int CV_EMOTION_ANGRY = 0;///< 愤怒 百分比置信度(0-100)
		public static final int CV_EMOTION_CALM = 1;///< 平静 百分比置信度(0-100)
		public static final int CV_EMOTION_CONFUSED = 2;///< 困惑 百分比置信度(0-100)
		public static final int CV_EMOTION_DISGUST = 3;///< 厌恶 百分比置信度(0-100)
		public static final int CV_EMOTION_HAPPY = 4;///< 高兴 百分比置信度(0-100)
		public static final int CV_EMOTION_SAD = 5;///< 悲伤 百分比置信度(0-100)
		public static final int CV_EMOTION_SCARED = 6;///< 惊恐 百分比置信度(0-100)
		public static final int CV_EMOTION_SUPRISED = 7;///< 诧异 百分比置信度(0-100)
		public static final int CV_EMOTION_SQUINT = 8;///< 斜视 百分比置信度(0-100)
		public static final int CV_EMOTION_SCREAM = 9;///< 尖叫 百分比置信度(0-100)
		public static final int CV_EMOTION_LENGTH = 32;///< 属性条目数 百分比置信度(0-100)
	}
	
//	public CvAttributeResult(int type){
//		if(type == 0){
//			mAttributeType = AttributeType.CV_ATTR_FEATURE;
//			mAttribute = new int[Feature.CV_FEATURE_LENGTH]; 
//		}else{
//			mAttributeType = 1;
//			mAttribute = new int[Emotion.CV_EMOTION_LENGTH]; 
//		}
//	}
	public CvAttributeResult(){
			mFeature = new int[Feature.CV_FEATURE_LENGTH]; 
			mEmotion = new int[Emotion.CV_EMOTION_LENGTH]; 
	}
	
	public int[] getFeature(){
		return mFeature;
	}
	public int[] getEmotion(){
		return mEmotion;
	}
	
	public void setFeature(int[] attr){
		if(attr == null || attr.length == 0 || attr.length != attr.length){
			Log.d(TAG, "setAttribute param is inlegal");
			return;
		}
		System.arraycopy(attr, 0, mFeature, 0, attr.length);
	}
	public void setEmotion(int[] attr){
		if(attr == null || attr.length == 0 || attr.length != attr.length){
			Log.d(TAG, "setAttribute param is inlegal");
			return;
		}
		System.arraycopy(attr, 0, mEmotion, 0, attr.length);
	}
	
	public int getFeatureSize(){
		return mFeature.length;
	}
	public int getEmotionSize(){
		return mEmotion.length;
	}

	public int getAttributeType() {
		return mAttributeType;
	}
	
	public void setHasFace(boolean hasFace){
		mHasFace = hasFace;
	}
	public boolean isHasFace() {
		return mHasFace;
	}

	public int getAge(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_AGE];
	}
	
	public int isMale(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_GENDER_MALE];
	}
	
	public int getAttrActive(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_ATTRACTIVE];
	}
	
	public int isEyeGlass(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_EYEGLASS];
	}
	
	public int isSunGlass(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_SUNGLASS];
	}
	
	public int isSmile(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_SMILE];
	}
	public int isMask(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_MASK];
	}
	public int getRace(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return 0;
		}
		return mFeature[Feature.CV_FEATURE_RACE];
	}
	public int isEyeOpen(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_EYE_OPEN];
	}
	public int isMoutnOpen(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_MOUTH_OPEN];
	}
	public int isBeard(){
		if(mFeature == null || mFeature.length == 0 || mFeature.length != mFeature.length){
			return -1;
		}
		return mFeature[Feature.CV_FEATURE_BEARD];
	}
	
	
	//emotion
	public int isAngry(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_ANGRY];
	}
	public int isCalm(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_CALM];
	}
	public int isConfused(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_CONFUSED];
	}
	public int isDisgust(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_DISGUST];
	}
	public int isHappy(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_HAPPY];
	}
	public int isSad(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_SAD];
	}
	public int isScared(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_SCARED];
	}
	public int isSuprised(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_SUPRISED];
	}
	public int isSquint(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_SQUINT];
	}
	public int isScream(){
		if(mEmotion == null || mEmotion.length == 0 || mEmotion.length != mEmotion.length){
			return 0;
		}
		return mEmotion[Emotion.CV_EMOTION_SCREAM];
	}
	
	

//	@Override
//	public String toString() {
//		if(mAttributeType == AttributeType.CV_ATTR_EMOTION){
//			return "mAttributeType : " + mAttributeType + " hasFace : " + mHasFace + " age : " + getAge() + " isMale : " + isMale()
//					+ " active : " + getAttrActive() 
//					+ " eyeglass : " + isEyeGlass() 
//					+ " sunglass = " + isSunGlass() 
//					+ " smile : " +isSmile()
//					+ "mask : " + isMask();
//		}else{
//			return null;
//		}
//	}
	
}
