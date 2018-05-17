package com.jibo.sentimedemo.sentimeface.track;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jibo.sentimedemo.sentimeface.TrackActivity;
import com.jibo.sentimedemo.sentimeface.agent.VerifyAgent;
import com.cv.faceapi.CvFace;
import com.cv.faceapi.CvFaceTrack;
import com.cv.faceapi.CvUtils;
import com.cv.faceapi.ResultCodeTable;

import java.util.ArrayList;
import java.util.List;

public class FaceOverlapFragment extends CameraOverlapFragment {

    public  static int fps;

    private static  boolean DEBUG = false;
    private boolean killed = false;
    private boolean isNV21ready = false;

    private String flag = null;
    private byte   nv21[];
    private byte[] mData;

    private Thread mThread;
    private Camera mCamera;
    private VerifyAgent   mVerifyAgent;
    private TrackActivity mTrackActivity;
    private CvFaceTrack   mCvFaceTrack = null;
    private TrackCallBack mListener;
    private PoseCallBack  mPoseListener;
    private ErrorCallBack mErrorListener;

    @Override
    @Deprecated
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mTrackActivity = (TrackActivity) activity;
        flag = mTrackActivity.getFlag();

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (mVerifyAgent == null) {
            mVerifyAgent = new VerifyAgent(mTrackActivity);
        }
        nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        this.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (nv21) {
                    System.arraycopy(data, 0, nv21, 0, data.length);
                    isNV21ready = true;
                        mData = data;
                        mCamera = camera;
                }
            }

        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TrackActivity.mAccelerometer != null)
            TrackActivity.mAccelerometer.start();
        // 创建handle
        mCvFaceTrack = new CvFaceTrack();
        int rst = mCvFaceTrack.createHandle(flag);
        if (rst != ResultCodeTable.CV_OK) {
            mErrorListener.onError(rst, flag);
            return;
        }
        mCvFaceTrack.setMaxDetectableFaces(-1);// 32

        killed = false;
        final byte[] tmp = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mThread = new Thread() {
            @Override
            public void run() {
                List<Long> timeCounter = new ArrayList<Long>();
                int start = 0;
                while (!killed) {
                    if (!isNV21ready)
                        continue;
                    synchronized (nv21) {
                        System.arraycopy(nv21, 0, tmp, 0, nv21.length);
                        isNV21ready = false;
                    }

                    /**
                     * 如果使用前置摄像头，请注意显示的图像与帧图像左右对称，需处理坐标
                     */
                    boolean frontCamera = (CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);

                    /**
                     * 获取重力传感器返回的方向 即手机方向 Portrait or landscape,only choose
                     * portrait
                     */
                    int dir = Accelerometer.getDirection();
                    int mOrientation = mCameraInfo.orientation;//MI3 front:270; back:90 
                    Log.i(TAG, "mCameraInfo.orientation == " + mCameraInfo.orientation);
                    if (mOrientation == 0)
                        dir = 1;
                    if (mOrientation == 90)
                        dir = 2;
                    if (mOrientation == 180)
                        dir = 4;
                    if (mOrientation == 270)
                        dir = 8;

                    /**
                     * 调用实时人脸检测函数，返回当前人脸信息
                     */
                    CvFace[] faces = mCvFaceTrack.track(tmp, dir, PREVIEW_WIDTH, PREVIEW_HEIGHT);

                    if (DEBUG) {
                        for (int i = 0; i < faces.length; i++) {
                            Log.i("Test", "detect faces: " + faces[i].getRect().toString());
                        }
                    }

                    long timer = System.currentTimeMillis();
                    timeCounter.add(timer);
                    while (start < timeCounter.size() && timeCounter.get(start) < timer - 1000) {
                        start++;
                    }
                    fps = timeCounter.size() - start;
                    mListener.onTrackdetected("fps", fps);
                    if (start > 100) {
                        timeCounter = timeCounter.subList(start, timeCounter.size() - 1);
                        start = 0;
                    }

                    /**
                     * 绘制人脸框
                     */
                    if (faces != null && frontCamera == (CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                        Canvas canvas = mOverlap.getHolder().lockCanvas();
                        if (canvas == null)
                            continue;
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        canvas.setMatrix(getMatrix());
                        boolean rotate270 = mCameraInfo.orientation == 270;
                        boolean rotate90 = mCameraInfo.orientation == 90;
                        boolean rotate0 = mCameraInfo.orientation == 0;
                        for (int i = 0; i < faces.length; i++) {
                            CvFace r = faces[i];
                            Rect rect = null;
                            if (rotate270) {
                                rect = CvUtils.RotateDeg270(r.getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            } else if (rotate90) {
                                rect = CvUtils.RotateDeg90(r.getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            } else if (rotate0) {
                                rect = r.getRect();
                            }

							/*PointF[] points = r.getPointsArray();
                            for (int i = 0; i < points.length; i++) {
								if (rotate270) {
									points[i] = CvUtils.RotateDeg270(points[i],PREVIEW_WIDTH, PREVIEW_HEIGHT);
								} else if(rotate90){
									points[i] = CvUtils.RotateDeg90(points[i],PREVIEW_WIDTH, PREVIEW_HEIGHT);
								}
							}*/
                            CvUtils.drawFaceRect(canvas, rect, PREVIEW_HEIGHT, PREVIEW_WIDTH, frontCamera);
                            //CvUtils.drawPoints(canvas, points, PREVIEW_HEIGHT,PREVIEW_WIDTH, frontCamera);
							/*
							 * pose
							 */
                            mPoseListener.onPosedetected("pose", r.getYaw(), r.getPitch(), r.getRoll(), r.getEye_dist(), r.getID());
                            if(i==0) {
                                mVerifyAgent.verifyID(r, mData, mCamera, rect);
                            }
                        }
                        mOverlap.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }

        };

        mThread.start();
    }

    @Override
    public void onPause() {
        if (TrackActivity.mAccelerometer != null)
            TrackActivity.mAccelerometer.stop();
        killed = true;
        if (mThread != null)
            try {
                mThread.join(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        if (mCvFaceTrack != null) {
            System.out.println("destroy mCvFaceTrack");
            mCvFaceTrack = null;
        }
        super.onPause();
    }

    public interface TrackCallBack {
        public void onTrackdetected(String flag, int value);
    }

    public void registFPSCallback(TrackCallBack callback) {
        mListener = callback;
    }

    public interface ErrorCallBack {
        public void onError(int rst, String flag);
    }

    public void registErrorCallback(ErrorCallBack callback) {
        mErrorListener = callback;
    }

    public interface PoseCallBack {
        public void onPosedetected(String flag, float yaw, float pitch, float roll, float eyedistance, int ID);
    }

    public void registPoseCallback(PoseCallBack callback) {
        mPoseListener = callback;
    }


}
