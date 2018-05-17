package com.jibo.sentimedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.jibo.sentimedemo.sentimeface.error.QueryErrorCode;
import com.jibo.sentimedemo.sentimeface.TrackActivity;
import com.jibo.sentimedemo.util.FileUtil;
import com.cv.faceapi.CvFaceApiBridge;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String LICENSE_NAME = "license.lic";
    private Button mBtnRegister;
    private String mLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();
        initView();
        initData();
    }

    private void initPermission() {
        //android M版本以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
        readLicense(Environment.getExternalStorageDirectory(), LICENSE_NAME);
    }

    // 读取授权文件
    private void readLicense(File path, String filename) {
        mLicense = FileUtil.readFileFromAssets(this, filename);
        if(mLicense == null || "".equals(mLicense))
        {
            mLicense = FileUtil.readFileFromSDCard2(MainActivity.this, path,filename);
            if(mLicense == null) return;
        }

        // cv_face_init_license_config(mLicense)：处理license串时以\n为分隔符。
        int rst = CvFaceApiBridge.FACESDK_INSTANCE.cv_face_init_license_config(mLicense);
        if (rst != 0) {
            QueryErrorCode.showErrorMsg(getApplicationContext(), null,rst, null);
        }
    }

    private void initView() {
        mBtnRegister = (Button) findViewById(R.id.bt_register);
    }

    private void initData() {
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TrackActivity.class);
                intent.putExtra("flag", "106 points");
                startActivity(intent);
            }
        });

    }

}
