package com.jibo.sentimedemo.sentimeface.agent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;

import com.jibo.sentimedemo.sentimeface.error.QueryErrorCode;
import com.jibo.sentimedemo.util.BitmapUtil;
import com.cv.faceapi.CvFace;
import com.cv.faceapi.CvFaceVerify;
import com.cv.faceapi.ResultCodeTable;
import java.util.ArrayList;


/**
 * Created by acer on 2016/10/28.
 */

public class VerifyAgent {

    private Context  mContext;
    private FaceTool mFaceTool;
    private RegisterAgent mRegisterAgent;
    private CvFaceVerify  mCvFaceVerify;

    //注册成功的标记
    private boolean mRegisterSucess = false;
    private float  mResults;
    private byte[] mNewFeature;

    private int mRstVerify;
    private int mPersonID;

    public VerifyAgent(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mRegisterAgent = new RegisterAgent(mContext);
        mFaceTool = new FaceTool(mContext);
        mCvFaceVerify = new CvFaceVerify();
        mRstVerify = mCvFaceVerify.createVerifyHandle();
        if (mRstVerify != ResultCodeTable.CV_OK) {
            QueryErrorCode.showErrorMsg(mContext, null, mRstVerify, null);
            return;
        }
    }

    /**
     * 如果faceID改变，执行人脸验证
     *
     * @param face     绘制的人脸框,21/106点外面的人脸框
     * @param data     人脸信息的字节流数据
     * @param camera   保存人脸信息需要转为bitmap对象,需要camera类
     * @param rect     绘制的人脸框的相关数值信息
     */
    public void verifyID(CvFace face ,byte[] data, Camera camera,Rect rect) {
        int personID = face.getID();
        if (personID != 0) {
            if (this.mPersonID == personID) {
                mRegisterSucess = true;
            } else {
                mRegisterSucess = false;
                this.mPersonID = personID;
                getFeature(face,data,camera,rect);
                Verity();
            }
        } else {
            if (!mRegisterSucess) {
                this.mPersonID = personID;
                getFeature(face,data,camera,rect);
                Verity();
                mRegisterSucess =true;
            }
        }
    }

    /**
     *
     * 检查本地库
     */
    private void Verity() {
        boolean nonfeature = mNewFeature != null && mNewFeature.length > 0;
        ArrayList<String> mNameList = mFaceTool.getFaceNameList();
        if (mNameList != null && mNameList.size() > 0) {
            for (int i = 0; i < mNameList.size(); i++) {
                byte[] mFeature = mFaceTool.getFaceBytes(mNameList,i);
                if (mFeature == null && nonfeature) {
                    //注册
                    mRegisterAgent.register(mNewFeature);
                    return;
                }
                if (nonfeature) {
                    //同一个人
                    if(compareTo(mFeature, mNewFeature)){
                        return;
                    }
                }
            }
            //注册
            mRegisterAgent.register(mNewFeature);
        } else {
            if (nonfeature) {
                mRegisterAgent.register(mNewFeature);//注册
            }
        }
    }

    /**
     * 取出bitmap中的人脸特征数组
     */
    private void getFeature(CvFace face,byte[] data, Camera camera,Rect rect) {
        Bitmap bitmap = BitmapUtil.yuvBitmap(data, camera);
        Bitmap newBitmap = BitmapUtil.cutBitmap(bitmap, rect.width(), rect.height());
        if (face != null) {
            mNewFeature = mCvFaceVerify.getFeature(newBitmap, face);
        }
    }

    /**
     * 执行对比逻辑
     *
     * @param oldFeature 原人脸特征
     * @param newFeature 现人脸特征
     * @return true 同一个人
     */
    private boolean compareTo(byte[] oldFeature, byte[] newFeature) {
        if (newFeature != null && oldFeature != null && newFeature.length > 0 && oldFeature.length > 0) {
            mResults = mCvFaceVerify.compareFeature(newFeature, oldFeature);
                //同一个人
                return mResults > 0.9;
        }
        return false;
    }
}
