package com.sobey.cloud.webtv.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sobey.cloud.webtv.app.ExceptionApplication;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件操作工具
 * 
 * @author bin
 */
public class FileUtil {
	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String HOST = "/yichengsunshine";
	public static final String SOBEY = SDCARD + HOST + "/";

	public static final String USER = SOBEY + "user/";
	public static final String CONTENT = SOBEY + "content/";
	public static final String BUFFER = SOBEY + "buffer/";
	public static final String FILEAPK = SOBEY + "apk/";
	public static final String CACHE = SOBEY + "cache/";
	public static final String LOG = SOBEY + "log/";
	public static final String PHOTO_APP = SOBEY + "photo/";
	/**
	 * 首页缓缓目录
	 */
	public static final String HOME_CACHE_DIR = CACHE + "home/";

	public static final String TXT = ".txt";
	public static final String TMP = ".tmp";
	public static final String JPG = ".jpg";
	public static final String PNG = ".png";
	public static final String MP3 = ".mp3";
	public static final String APK = ".apk";

	public static final String TAG = "FileUtil";

	/**
	 * 生成文件路径
	 * 
	 * @param dir
	 * @param filename
	 * @param suf
	 * @return
	 */
	public static String createFilePath(String dir, String filename, String suf) {
		String filepath = null;
		try {
			if (!TextUtils.isEmpty(filename)) {
				File file = new File(dir + filename + suf);
				File parent = file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				filepath = file.getPath();
			}
		} catch (Exception ex) {
			filepath = null;
		}
		return filepath;
	}

	public static long fileLength(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				return file.length();
			}
		} catch (Exception ex) {

		}
		return 0;
	}

	/**
	 * 创建文件夹
	 *
	 * @param dirName
	 */
	public static void makeDir(String dirName) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File destDir = new File(dirName);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		} else {
			File destDir = new File(dirName);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}
	}

	/**
	 * 新建文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static File createDirectory(String path) {
		File file;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			file = null;
		}
		return file;
	}

	/**
	 * 新建文件
	 * 
	 * @param path
	 * @return
	 */
	public static File createFile(String path) {
		File file;
		try {
			file = new File(path);
			if (!file.exists()) {
				File parent = file.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				file.createNewFile();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			file = null;
		}
		return file;
	}

	/**
	 * 新建临时文件
	 * 
	 * @param prefix
	 *            文件名
	 * @param suffix
	 *            文件类型 ".txt"
	 * @param directory
	 *            根目录
	 * @return
	 */
	public static File createTempFile(String prefix, String suffix, File directory) {
		File file;
		try {
			if (!directory.exists()) {
				directory.mkdirs();
			}
			file = File.createTempFile(prefix, suffix, directory);
		} catch (IOException ex) {
			ex.printStackTrace();
			file = null;
		}
		return file;
	}

	/**
	 * 最后修改时间
	 * 
	 * @param dir
	 * @param filename
	 * @param suf
	 * @return
	 */
	public static long lastModified(String dir, String filename, String suf) {
		return lastModified(createFilePath(dir, filename, suf));
	}

	/**
	 * 最后修改时间
	 * 
	 * @param path
	 * @return
	 */
	public static long lastModified(String path) {
		try {
			if (!TextUtils.isEmpty(path)) {
				File file = new File(path);
				if (file.exists()) {
					return file.lastModified();
				}
			}
		} catch (Exception ex) {
		}
		return 0;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean hasFile(String path) {
		boolean value = false;
		try {
			if (!TextUtils.isEmpty(path)) {
				File file = new File(path);
				if (file.exists() && file.isFile()) {
					value = true;
				}
			}
		} catch (Exception ex) {
		}
		return value;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delFile(String path) {
		if (null == path || path.length() == 0)
			return true;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delFile(File file) {
		if (file.exists() && file.isFile()) {
			return file.getAbsoluteFile().delete();
		}
		return false;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delDirectory(String path) {
		File directory = new File(path);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (!delDirectory(file))
						delFile(file);
				}
			}
			return directory.delete();
		}
		return false;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (!delDirectory(file))
						delFile(file);
				}
			}
			return directory.delete();
		}
		return false;
	}

	/**
	 * 清空文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean clearDirectory(String path) {
		File directory = new File(path);

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();

			if (null != files) {
				for (File file : files) {
					if (!delDirectory(file))
						delFile(file);
				}
			}
		}

		return true;
	}

	/*
	 * 获取SD卡路径
	 */
	public static String getSDPath() {
		return SDCARD;
	}

	/*
	 * 文件更名
	 */
	public static void renameFile(String strDest, String strSrc) {
		File fileDest = new File(strDest), fileSrc = new File(strSrc);

		if (fileDest.exists() && fileDest.isFile()) {
			fileDest.delete();
		}

		if (fileSrc.exists() && fileSrc.isFile()) {
			fileSrc.renameTo(fileDest);
		}

		if (fileSrc.exists() && fileSrc.isFile()) {
			fileSrc.delete();
		}
	}

	/**
	 * 
	 * @param strPath
	 * @param strText
	 * @return
	 */
	public static boolean saveTextFile(String strPath, String strText) {
		Log.i(TAG, "-->saveTextFile 路径：" + strPath + ",内容：" + strText);
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		boolean bRet = true;
		File file = null;

		try {
			delFile(strPath);

			file = new File(strPath);

			outputStream = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(strText);
			outputStreamWriter.close();
		} catch (Exception e) {
			bRet = false;
		}

		return bRet;
	}

	/**
	 * 读取文本内容
	 * 
	 * @param strPath
	 *            带文件名的文件的全路径
	 * @return
	 */
	public static String readTextFile(String strPath) {
		InputStream inputStream = null;
		BufferedInputStream bufferedInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		String strText;
		StringBuffer stringBuffer = null;
		File file = null;

		try {
			file = new File(strPath);
			if (file.exists() && (file.isFile())) {
				stringBuffer = new StringBuffer();

				inputStream = new FileInputStream(file);
				bufferedInputStream = new BufferedInputStream(inputStream);
				inputStreamReader = new InputStreamReader(bufferedInputStream, "UTF-8");
				bufferedReader = new BufferedReader(inputStreamReader);

				while ((strText = bufferedReader.readLine()) != null) {
					stringBuffer.append(strText);
				}

				strText = stringBuffer.toString();
			} else {
				strText = null;
			}
		} catch (Exception e) {
			strText = null;
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
					bufferedReader = null;
				}
				if (null != inputStreamReader) {
					inputStreamReader.close();
					inputStreamReader = null;
				}
				if (null != bufferedInputStream) {
					bufferedInputStream.close();
					bufferedInputStream = null;
				}
				if (null != inputStream) {
					inputStream.close();
					inputStream = null;
				}
			} catch (Exception ex) {
			}
		}

		return strText;
	}

	/**
	 * 保存数据到data/data目录下
	 * 
	 * @param fileName
	 * @param text
	 */
	public static void saveTextDataDir(String fileName, String text) {
		try {
			FileOutputStream outStream = ExceptionApplication.app.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(text.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * 从data/data目录下读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readTextFromDataDir(String fileName) {
		String text = "";
		try {
			FileInputStream inStream = ExceptionApplication.app.openFileInput(fileName);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();

			text = stream.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
		return text;
	}

	/**
	 * 读取文件流
	 * 
	 * @param ins
	 * @return
	 */
	public static String readTextInputStream(InputStream ins) {
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		String strText;
		StringBuffer stringBuffer = null;

		try {
			stringBuffer = new StringBuffer();

			inputStreamReader = new InputStreamReader(ins, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);

			while ((strText = bufferedReader.readLine()) != null) {
				stringBuffer.append(strText);
			}

			strText = stringBuffer.toString();
		} catch (Exception e) {
			strText = null;
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
					bufferedReader = null;
				}
				if (null != inputStreamReader) {
					inputStreamReader.close();
					inputStreamReader = null;
				}
			} catch (Exception e) {
			}
		}

		return strText;
	}

	/**
	 * 复制文件
	 * 
	 * @param ins
	 * @param ops
	 * @return
	 */
	public static boolean copyFile(InputStream ins, OutputStream ops) {
		boolean result = false;
		try {
			byte[] byBuffer = new byte[1024];
			int readLen = 0;
			while ((readLen = ins.read(byBuffer)) > 0) {
				ops.write(byBuffer, 0, readLen);
			}
			ops.flush();

			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			try {
				if (ops != null) {
					ops.close();
				}
				if (ins != null) {
					ins.close();
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 复制文件
	 * 
	 * @param strDest
	 * @param strSrc
	 * @return
	 */
	public static boolean copyFile(String strDest, String strSrc) {
		boolean result = false;

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(strSrc);
			outputStream = new FileOutputStream(strDest, false);

			byte byBuffer[] = new byte[1024];
			int nRead;
			while ((nRead = inputStream.read(byBuffer)) > 0) {
				outputStream.write(byBuffer, 0, nRead);
			}
			outputStream.flush();

			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	public static boolean hasStorage() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public static boolean isFileOutofLength(String path, double targetLength) {
		try {
			File file = new File(path);
			if (file.exists()) {
				return file.length() > targetLength;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean isFileEnable(String path) {
		try {
			File file = new File(path);
			if (file.exists() && file.length() > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getGalleryPhotos(Activity act) {
		Map<String, ArrayList<String>> maps = new HashMap<String, ArrayList<String>>();
		ArrayList<String> galleryList = new ArrayList<String>();
		try {
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = act.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
					orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					String item = new String();
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					item = imagecursor.getString(dataColumnIndex);
					File file = new File(item);
					putFile2Folders(maps, file.getAbsolutePath(), item);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.reverse(galleryList);
		return galleryList;
	}

	@SuppressWarnings("deprecation")
	public static Map<String, ArrayList<String>> getGalleryPhotosWithFolder(Activity act) {
		Map<String, ArrayList<String>> maps = new HashMap<String, ArrayList<String>>();
		ArrayList<String> galleryList = new ArrayList<String>();
		try {
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = act.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
					orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					String item = new String();
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					item = imagecursor.getString(dataColumnIndex);
					File file = new File(item);
					String path = file.getAbsolutePath();
					if (null != file.getParent()) {
						path = file.getParent();
					}
					putFile2Folders(maps, getKey(path), item);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.reverse(galleryList);
		maps.put("all*--", galleryList);
		return maps;
	}

	private static String getKey(String dir) {
		if (dir.contains("/")) {
			int index = dir.lastIndexOf("/");
			dir = dir.substring(index + 1, dir.length());
		}
		return dir;
	}

	private static void putFile2Folders(Map<String, ArrayList<String>> maps, String key, String path) {
		if (maps.containsKey(key)) {
			ArrayList<String> filePaths = maps.get(key);
			filePaths.add(path);
		} else {
			ArrayList<String> filePaths = new ArrayList<String>();
			filePaths.add(path);
			maps.put(key, filePaths);
		}
	}

	/**
	 * 用时间戳生成照片名称
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	@SuppressLint("UseSparseArrays")
	public static void uploadFile(List<String> upLoadfiles, ArrayList<String> uploadFileSuccessUrls, Handler handler) {
		handler.sendEmptyMessage(1);
		Map<Integer, String> upLoadResultCodes = new HashMap<Integer, String>();
		UpLoadTask downLoadTask = new UpLoadTask(upLoadResultCodes, handler, upLoadfiles);
		int resultCode = downLoadTask.sendPices();
		if (resultCode < 0) {
			handler.sendEmptyMessage(SobeyConstants.CODE_FOR_UPLOAD_FILE_FAIL);
			return;
		}
		for (int i = 0; i < upLoadfiles.size(); i++) {
			if (upLoadResultCodes.containsKey(i)) {
				String result = upLoadResultCodes.get(i);
				int successId = -1;
				try {
					successId = Integer.parseInt(result);
				} catch (NumberFormatException e) {
					successId = -1;
				}
				if (successId > 0) {
					String successUrl = appendUploadFileUrl(successId);
					uploadFileSuccessUrls.add(successUrl);
				}
			}
		}
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putStringArrayList("uploadFileSuccessUrls", uploadFileSuccessUrls);
		msg.setData(data);
		msg.what = SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE;
		handler.sendMessage(msg);
	}

	public static void uploadFile3(List<String> upLoadfiles, List<String> uploadFileSuccessUrls, Handler handler) {
		handler.sendEmptyMessage(1);
		for (int i = 0; i < upLoadfiles.size(); i++) {
			String fileName = upLoadfiles.get(i);
			String uploadFilePaht = BaseUtil.compressPhoto(fileName);
			File file = new File(uploadFilePaht);
			// GroupRequestMananger.getInstance();
			Log.i(TAG, "要上传文件大小：" + file.length());
			String result = GroupRequestMananger.getInstance().sendHttpMultipartRequest(file);
			// msg.obj = i;
			// msg.what = SobeyConstants.CODE_FOR_UPLOAD_FILE_CHANGE;
			// handler.sendMessage(msg);
			// String tips = new
			// StringBuilder().append("正在上传第").append(i+1).append("张，")
			// .append("共").append(upLoadfiles.size()).append("张...").toString();
			// loadingTipsTv.setText(tips);//can not change ui state

			// 发送完成后清除建立的压缩缓存图片
			FileUtil.delFile(file);
			int successId = -1;
			try {
				successId = Integer.parseInt(result);
			} catch (NumberFormatException e) {
				successId = -1;
			}
			if (successId > 0) {
				String successUrl = appendUploadFileUrl(successId);
				uploadFileSuccessUrls.add(successUrl);
			}
		}
		Message msg = new Message();
		msg.obj = uploadFileSuccessUrls;
		msg.what = SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE;
		handler.sendMessage(msg);
	}

	private static String appendUploadFileUrl(int successId) {
		StringBuilder sb = new StringBuilder();
		sb.append("[attachimg]").append(successId).append("[/attachimg]");
		return sb.toString();
	}
}
