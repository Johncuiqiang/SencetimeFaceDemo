package com.jibo.sentimedemo.sentimeface.agent;

import android.content.Context;

import com.jibo.sentimedemo.constant.Constants;
import com.jibo.sentimedemo.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/11/2.
 */

public class FaceTool {

    private ArrayList<String> mNameList;
    private ArrayList<String> mNewNameList;

    private Context mContext;
    private SharedPreferencesUtil<byte[]> mSharedPreferencesUtil;
    private SharedPreferencesUtil<ArrayList<String>> mSharedPreferencesUtilName;

    public FaceTool(Context context){
        this.mContext = context;
        mSharedPreferencesUtil = new SharedPreferencesUtil<>();
        mSharedPreferencesUtilName = new SharedPreferencesUtil<>();
    }

    public ArrayList<String> getFaceNameList(){
        List<ArrayList<String>> mFaceList = mSharedPreferencesUtilName.getObject(mContext, Constants.FACE_DB);
        if (mFaceList != null && mFaceList.size()>0) {
            return mFaceList.get(0);
        }else{
            return null;
        }
    }

    public byte[] getFaceBytes(ArrayList<String> list,int i){
            byte[] mOldfeature = {};
            String mFacename = list.get(i);
            List<byte[]> bytes = mSharedPreferencesUtil.getObject(mContext, mFacename);
            for (int j = 0; j < bytes.size(); j++) {
                 mOldfeature = bytes.get(i);
            }
            return mOldfeature;
    }

    public void saveFace(String name,byte[] newfeature){
        List<ArrayList<String>> mFaceNameList = mSharedPreferencesUtilName.getObject(mContext, Constants.FACE_DB);
        mSharedPreferencesUtil.saveObject(newfeature, mContext, name);
        if(mFaceNameList!=null){
            mNameList = mFaceNameList.get(0);
            mNameList.add(name);
            mSharedPreferencesUtilName.saveObject(mNameList, mContext,Constants.FACE_DB);
        }else{
            mNewNameList = new ArrayList<>();
            mNewNameList.add(name);
            mSharedPreferencesUtilName.saveObject(mNewNameList, mContext,Constants.FACE_DB);
        }

    }
}
