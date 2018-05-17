package com.jibo.sentimedemo.sentimeface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.jibo.sentimedemo.R;
import com.jibo.sentimedemo.sentimeface.error.QueryErrorCode;
import com.jibo.sentimedemo.sentimeface.track.Accelerometer;
import com.jibo.sentimedemo.sentimeface.track.FaceOverlapFragment;

public class TrackActivity extends Activity {

	public  static Accelerometer mAccelerometer;
	public  static TrackActivity mTrackActivity;
	private static String mFlag;

	private TextView mFaceID;
	private TextView mFpstView;
	private TextView mErrorView;
	private TextView mYawText;
	private TextView mPitchText;
	private TextView mRollText;
	private TextView mEyedistText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		mFlag = intent.getExtras().getString("flag");
        setContentView(R.layout.activity_track);

		mFpstView = (TextView)findViewById(R.id.fpstext);
		mErrorView = (TextView)findViewById(R.id.error);
		mYawText = (TextView)findViewById(R.id.yaw);
		mPitchText = (TextView)findViewById(R.id.pitch);
		mRollText = (TextView)findViewById(R.id.roll);
		mEyedistText = (TextView)findViewById(R.id.eye_dist);
		mFaceID = (TextView)findViewById(R.id.face_id);
		mTrackActivity = this;
		/**
		 * 
		 * 开启重力传感器监听
		 * 
		 */
		mAccelerometer = new Accelerometer(this);
		mAccelerometer.start();
	}

	public static String getFlag()
	{
		return mFlag;
	}

	@Override
	public void onResume() {
		super.onResume();
		final FaceOverlapFragment fragment = (FaceOverlapFragment) getFragmentManager().findFragmentById(R.id.overlapFragment);
		fragment.registFPSCallback(new FaceOverlapFragment.TrackCallBack() {
			
			@Override
			public void onTrackdetected(final String flag, final int value) {
				// TODO Auto-generated method stub
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if("fps".equals(flag))
						{
							mFpstView.setText("FPS: " + value);
						}
					}
				});
			}
		});

		fragment.registErrorCallback(new FaceOverlapFragment.ErrorCallBack() {
			
			@Override
			public void onError(final int rst,final String flag) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						QueryErrorCode.showErrorMsg(TrackActivity.this, mErrorView, rst, flag);
					}
				});
			}
		});
		
		fragment.registPoseCallback(new FaceOverlapFragment.PoseCallBack() {
			@Override
			public void onPosedetected(final String flag, final float yaw, final float pitch,
									   final float roll, final float eyedistance ,final int ID) {
				// TODO Auto-generated method stub	
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if("pose".equals(flag))
						{
							mYawText.setText("yaw: "+yaw);
						    mPitchText.setText("pitch: "+pitch);
							mRollText.setText("roll: "+roll);
							mEyedistText.setText("eyedistance: "+eyedistance);
							mFaceID.setText("faceID: "+ID);
						}
					}
				});
			}		

		});
	}

}
