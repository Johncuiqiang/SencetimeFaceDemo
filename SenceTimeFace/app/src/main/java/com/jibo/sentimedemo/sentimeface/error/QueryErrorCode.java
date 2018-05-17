package com.jibo.sentimedemo.sentimeface.error;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.cv.faceapi.ResultCodeTable;

public class QueryErrorCode {

	public static void showErrorMsg(Context context, TextView textview, int rst, String additional)
	{
		switch (rst) {
		case ResultCodeTable.CV_E_UNSURPPORTED:
			show(context,textview,"该版本未开启"+additional +"功能");
			showToast(context,textview,"该版本未开启"+additional +"功能");
			break;
		case ResultCodeTable.CV_E_INVALIDARG:
			show(context,textview,"无效参数");
			break;
		case ResultCodeTable.CV_E_HANDLE:
			show(context,textview,"句柄错误");
			break;
		case ResultCodeTable.CV_E_FAIL:
			show(context,textview,"内部错误");
			break;
		case ResultCodeTable.CV_E_DELNOTFOUND:
			show(context,textview,"定义缺失");
			break;
		case ResultCodeTable.CV_E_INVALID_PIXEL_FORMAT:
			show(context,textview,"不支持的图像格式");
			break;
		case ResultCodeTable.CV_E_FILE_NOT_FOUND:
			show(context,textview,"模型文件不存在");
			break;
		case ResultCodeTable.CV_E_INVALID_FILE_FORMAT:
			show(context,textview,"模型格式不正确，导致加载失败");
			break;
		case ResultCodeTable.CV_E_INVALID_APPID:
			show(context,textview,"包名错误");
			break;
		case ResultCodeTable.CV_E_INVALID_AUTH:
			show(context,textview,"本地验证失败");
			break;
		case ResultCodeTable.CV_E_AUTH_EXPIRE:
			show(context,textview,"SDK过期");
			break;
		case ResultCodeTable.CV_E_FILE_EXPIRE:
			show(context,textview,"模型文件过期");
			break;
		case ResultCodeTable.CV_E_DONGLE_EXPIRE:
			show(context,textview,"加密狗过期");
			break;
		case ResultCodeTable.CV_E_ONLINE_AUTH_FAIL:
			show(context,textview,"在线验证失败");
			break;
		case ResultCodeTable.CV_E_ONLINE_AUTH_TIMEOUT:
			show(context,textview,"在线验证超时");
			break;
		default:
			break;
		}
	}

	public static void show(Context context, TextView textview, String errorMsg)
	{
		if(textview == null)
		{
			showToast(context, null, errorMsg);
			return;
		}
		textview.setText(errorMsg);
		textview.postInvalidate();
	}

	public static void showToast(Context context, TextView textview, String errorMsg)
	{
		Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();

	}
}
