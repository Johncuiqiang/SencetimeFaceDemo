package com.jibo.sentimedemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by litao on 2016/3/24.
 */
public class SharedPreferencesUtil<T> {

    private static final String SAVETAG = "list";

    /**
     * 使用SharedPreferences保存对象类型的数据
     * 先将对象类型转化为二进制数据，然后用特定的字符集编码成字符串进行保存
     *
     * @param object     要保存的对象
     * @param context
     * @param shaPreName 保存的文件名
     */
    public  void saveObject(T object, Context context, String shaPreName) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(shaPreName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        List<T> list = getObject(context, shaPreName);
        if (null == list) {
            list = new ArrayList<T>();
        }
        list.add(object);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            String strList = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor.putString(SAVETAG, strList);
            editor.commit();
            oos.close();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件名取得存储的数据对象
     * 先将取得的数据转化成二进制数组，然后转化成对象
     *
     * @param context
     * @param shaPreName 读取数据的文件名
     * @return
     */
    public List<T> getObject(Context context, String shaPreName) {
        List<T> list;
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(shaPreName, Activity.MODE_PRIVATE);
        String message = sharedPreferences.getString(SAVETAG, "");
        byte[] buffer = Base64.decode(message.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            list = (List<T>) ois.readObject();
            ois.close();
            return list;
        } catch (StreamCorruptedException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } finally {
            try {
                bais.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] inputStreamToByteAyyay(InputStream is) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            is.close();
            return data;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}