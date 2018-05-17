package com.jibo.sentimedemo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.jibo.sentimedemo.sentimeface.error.QueryErrorCode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * 文件操作类
 */
public class FileUtil {

	public static final String MODEL_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String MODEL_NAME = "verify.model";
	public static final String MODEL_FOLDERNAME = "";
	public static final String MODEL_PATH = MODEL_ROOT +"/"+ MODEL_FOLDERNAME +"/" +MODEL_NAME;
	private static final String TAG = "FileUtil";

	/*
	 *  read one line every time，结果like this
	 *  ############################################################# SenseTime License# License Product: FaceSdk# Expiration: 20000101~20990101# License SN: 89c1ad09-5d79-4d14-b08d-.......................................
	 */
	public static String readFileFromSDCard(Context context, File path,
											String filename) {
		StringBuffer sb = null;
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				QueryErrorCode.showToast(context, null, "找不到" + filename + "文件");
				return null;
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";
			sb = new StringBuffer();
			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				sb.append(readline);
			}
			br.close();
			Log.i(TAG, "license:" + sb.toString());
			return sb.toString();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*
	 *  read all a time. like this  结果like this:
	 *  ############################################################
        # SenseTime License
        # License Product: FaceSdk
        # Expiration: 20000101~20990101
        # License SN: 89c1ad09-5d79-4d14-b08d-f4141ae653b9
        ############################################################
        sGfdd5sxA8NCweDGA+vU2qzOgOjTn64wtsOftvbVw+MzmQXlL9gGE+JsM0nU
        ........................
        
              由于cv_face_init_license_config(license)函数在处理license串时以\n为分隔符，所以保留。
	 */
	public static String readFileFromSDCard2(Context context, File path, String filename) {
		String result = null;
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				//QueryErrorCode.showToast(context, null, filename + "文件不存在");
				return null;
			}
			FileInputStream inputStream = new FileInputStream(file);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			result = new String(b);
			Log.i(TAG, "readFileFromSDCard2");
			Log.i(TAG, result);
		} catch (Exception e) {
			//Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public static String readFileFromAssets(Context context, String fileName) {
		String res = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];

			in.read(buffer);
			in.close();
			res = new String(buffer,"UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/*private static String pathName=Environment.getExternalStorageDirectory()+"/";

	public static void writeToSDCard(String s, Bitmap bitmap) {
		String sdStatus = Environment.getExternalStorageState();
		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		try {
			File path = new File(s);
			File file = new File(bitmap);
			if( !path.exists()) {
				Log.d(TAG, "Create the path:" + pathName);
				path.mkdir();
			}
			if( !file.exists()) {
				Log.d(TAG, "Create the file:" + s);
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			byte[] buf = s.getBytes();
			stream.write(buf);
			stream.close();

		} catch(Exception e) {
			Log.e(TAG, "Error on writeFilToSD.");
			e.printStackTrace();
		}
	}*/
	public static void copyModelToSDCard()
	{
		File file = new File(MODEL_ROOT);
		if(file.exists() && file.isDirectory())
		{
			File modelFile = new File(MODEL_PATH);
			if(modelFile.exists() && file.isFile())
			{
				return;
			}
		}
		file.mkdirs();
		try {
			copyBigDataToSDCard(MODEL_NAME,MODEL_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyBigDataToSDCard(String sourseName,String strOutFileName) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(strOutFileName);
		myInput = ContextHolder.getContext().getAssets().open(sourseName);
		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while(length > 0){
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}

		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

}
