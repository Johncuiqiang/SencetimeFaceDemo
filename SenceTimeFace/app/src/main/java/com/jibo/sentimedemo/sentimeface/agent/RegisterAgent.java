package com.jibo.sentimedemo.sentimeface.agent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;

import com.jibo.sentimedemo.sentimeface.TrackActivity;


/**
 * Created by acer on 2016/10/29.
 */

public class RegisterAgent {


    private String mName;
    //是否弹出提示框
    private boolean mIsDialogShow;

    private Context  mContext;
    private FaceTool mFaceTool;
    private AlertDialog mDialog;

    public RegisterAgent(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mFaceTool = new FaceTool(mContext);
        ((TrackActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initDialog();
            }
        });
    }

    /**
     * 执行注册逻辑
     */
    public void register(byte[] feature) {
        long start = System.currentTimeMillis();
        mIsDialogShow =true;
        if(mIsDialogShow){
            showDialog();
        }
        if(!TextUtils.isEmpty(mName)) {
            mFaceTool.saveFace(mName,feature);
            long end = System.currentTimeMillis();
            long time=end-start;
        }else{
            mName ="王五";
            mFaceTool.saveFace(mName,feature);
        }
    }

    private void showDialog() {
        ((TrackActivity) mContext).runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                if(mDialog !=null) {
                    mDialog.show();
                    mIsDialogShow =false;
                }
            }
        });

    }

    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        final EditText et = new EditText(mContext);
        et.setGravity(Gravity.CENTER);
        et.setHint("请输入昵称，不能为空哦");
        et.setHintTextColor(0xffc6c6c6);
        builder.setTitle("提示")
                .setMessage("人脸录入成功，请输入昵称 ")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mName = et.getText().toString();
                        mDialog.cancel();
                    }
                });
        mDialog = builder.create();

    }
}

