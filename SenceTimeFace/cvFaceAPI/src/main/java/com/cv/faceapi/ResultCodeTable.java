/**   
* 
*/

package com.cv.faceapi;

public class ResultCodeTable {
	//internal sdk
 public static final int CV_OK = 0;//< 正常运行
 public static final int CV_E_INVALIDARG = -1;//< 无效参数
 public static final int CV_E_HANDLE = -2;//< 句柄错误
 public static final int CV_E_OUTOFMEMORY = -3;//< 内存不足
 public static final int CV_E_FAIL = -4;//< 内部错误
 public static final int CV_E_DELNOTFOUND = -5;//< 定义缺失
 public static final int CV_E_INVALID_PIXEL_FORMAT = -6;//< 不支持的图像格式
 public static final int CV_E_FILE_NOT_FOUND = -10;//< 模型文件不存在
 public static final int CV_E_INVALID_FILE_FORMAT = -11;//< 模型格式不正确，导致加载失败
 public static final int CV_E_INVALID_APPID = -12;//< 包名错误
 public static final int CV_E_INVALID_AUTH = -13;//< 本地验证失败
 public static final int CV_E_AUTH_EXPIRE = -14;///< SDK过期
 public static final int CV_E_FILE_EXPIRE = -15;///< 模型文件过期
 public static final int CV_E_DONGLE_EXPIRE = -16;//< 加密狗过期
 public static final int CV_E_ONLINE_AUTH_FAIL = -17;//< 在线验证失败
 public static final int CV_E_ONLINE_AUTH_TIMEOUT = -18;//< 在线验证超时
 //external sdk
 public static final int CV_E_UNSURPPORTED = -1000;//该版本sdk未开启此功能
 }
