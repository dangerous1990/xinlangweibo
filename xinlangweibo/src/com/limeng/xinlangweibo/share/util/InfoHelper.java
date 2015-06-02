package com.limeng.xinlangweibo.share.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

/**
 * 类说明： 帮助类
 * 
 * @author @Cundong
 * @weibo http://weibo.com/liucundong
 * @blog http://www.liucundong.com
 * @date Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class InfoHelper {

	
	public static final String MENTIONS_SCHEMA = "tofuweibo://sina_profile";
	public static final String TRENDS_SCHEMA = "tofuweibo://sina_profile1";
	
	public static final int LOADING_DATA_FAILED = 0;
	public static final int LOADING_DATA_COMPLETED = 1;
	public static final int LOADING_DATA_BEGIN = 2;
	
	public static final int LOADING_INFO_DATA_FAILED = 3;
	public static final int LOADING_INFO_DATA_COMPLETED = 4;

	public static final String TAG = "tofuTAG";

	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String SDCARD = "/sdcard";

	/**
	 * 使用当前时间戳拼接一个唯一的文件名
	 * 
	 * @param format
	 * @return
	 */
	public static String getFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = sdf.format(new Timestamp(System.currentTimeMillis()));
		return fileName;
	}

	/**
	 * 获取目录
	 * 
	 * @return
	 */
	public static String getWeiboPath() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "TofuWeibo"
				+ File.separator;
		File file = new File(path);
		if (!(file.exists())) {
			file.mkdir();
		}
		return path;
	}

	/**
	 * 获取emotion目录
	 * 
	 * @return
	 */
	public static String getEmotionPath() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "TofuWeibo"
				+ File.separator + "emotion" + File.separator;
		File file = new File(path);
		if (!(file.exists())) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		boolean netWorkOK = false;
		ConnectivityManager connectManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectManager.getActiveNetworkInfo() != null) {
			netWorkOK = true;
		} else {
			Toast.makeText(context, "网络连接失败，请检查网络设置！", Toast.LENGTH_LONG)
					.show();
		}
		return netWorkOK;
	}

	/**
	 * 根据路径获取适应屏幕大小的Bitmap
	 * 
	 * @param context
	 * @param filePath
	 * @param maxWidth
	 * @return
	 */
	public static Bitmap getScaleBitmap(Context context, String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 4;

		return BitmapFactory.decodeFile(filePath, opts);
	}

	public synchronized static void writeLog(String text) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					getWeiboPath() + "mylog" + ".txt"), true));
			bw.write("\n\n" + text);
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}