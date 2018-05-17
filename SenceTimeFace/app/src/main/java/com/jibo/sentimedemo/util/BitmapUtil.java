package com.jibo.sentimedemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by acer on 2016/10/28.
 */

public class BitmapUtil {

    /***
     * 把yuv转换成bitmap
     *
     * @param mData     人脸信息字节流
     * @param myCamera  camera对象
     * @return bitmap对象
     */
    public static Bitmap yuvBitmap(byte[] mData, Camera myCamera) {
        Camera.Size size = null;
        try {
            size = myCamera.getParameters().getPreviewSize(); // 获取预览大小
        } catch (Exception e) {
            return null;
        }
        final int w = size.width; // 宽度
        final int h = size.height;
        Bitmap bmp = null;
        try {
            YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, w, h), 100, stream);
                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();
            }
        } catch (IOException e) {

        }
        return bmp;
    }

    /***
     * 得到yuv文件
     *
     * @param mData
     * @param myCamera
     *
     */
    public static YuvImage getYuv(byte[] mData, Camera myCamera) {
        Camera.Size size = null;
        try {
            size = myCamera.getParameters().getPreviewSize(); // 获取预览大小
        } catch (Exception e) {
            return null;
        }
        final int w = size.width; // 宽度
        final int h = size.height;
        YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
        return image;
    }

    /***
     * 等比例压缩图片
     *
     * @param bitmap
     * @param screenWidth
     * @param screenHight
     * @return
     */
    public static Bitmap cutBitmap(Bitmap bitmap, int screenWidth,
                                   int screenHight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.e("jj", "图片宽度" + w + ",screenWidth=" + screenWidth);
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        float scale2 = (float) screenHight / h;

        scale = scale < scale2 ? scale : scale2;

        // 保证图片不变形.
        matrix.postScale(scale, scale);
        // w,h是原图的属性.
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

}
