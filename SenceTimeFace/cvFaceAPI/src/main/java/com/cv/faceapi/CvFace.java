package com.cv.faceapi;

import android.graphics.PointF;
import android.graphics.Rect;

import com.cv.faceapi.CvFaceApiBridge.*;

public class CvFace extends cv_face_t{

    public CvFace() {
    }
    public CvFace(cv_face_t origin) {
		rect.bottom = origin.rect.bottom;
		rect.top    = origin.rect.top;
		rect.left   = origin.rect.left;
		rect.right  = origin.rect.right;

		score = origin.score;
		points_count = origin.points_count;
		yaw = origin.yaw;
		pitch = origin.pitch;
		roll = origin.roll;
		eye_dist = origin.eye_dist;
		ID = origin.ID;

		for (int i = 0; i < points_array.length; i++) {
			points_array[i] = origin.points_array[i];
		}
    }
    public Rect getRect() {
    	Rect r = new Rect();
		r.bottom = rect.bottom;
		r.top    = rect.top;
		r.left   = rect.left;
		r.right  = rect.right;
    	return r;
    }
    public PointF[] getPointsArray() {
    	PointF[] ans = new PointF[points_count];
		for (int i = 0; i < points_count; i++) {
			ans[i] = new PointF();
			ans[i].x = points_array[2 * i];
			ans[i].y = points_array[2 * i + 1];
		}
    	return ans;
    }
    public float getYaw()
    {
    	return yaw;
    }
    public float getPitch()
    {
    	return pitch;
    }
    public float getRoll()
    {
    	return roll;
    }
    public float getEye_dist()
    {
    	return eye_dist;
    }
	public int getID(){
		return ID;
	}

    @Override
    public String toString() {
		return "CvFace(" + getRect() + ", " + score + ")";
    }
}
