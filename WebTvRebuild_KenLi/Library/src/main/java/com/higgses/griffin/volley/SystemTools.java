package com.higgses.griffin.volley;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class SystemTools {
	private static final String TAG = "SystemTools";
	private Context mContext;
	private static SystemTools mTools;

	public interface SizeType {
		public static final String SZIETYPE_BYTE = "byte";
		public static final String SZIETYPE_KB = "kb";
		public static final String SZIETYPE_MB = "mb";
	}

	private SystemTools(Context context) {
		this.mContext = context;
	}

	private SystemTools() {
	}

	/**
	 * @see :you should post the parameter context that is application context
	 * @param context
	 *            :application
	 * @return
	 */
	public static SystemTools getInstance(Context context) {
		if (mTools == null) {
			mTools = context == null ? new SystemTools() : new SystemTools(
					context);
		}
		return mTools;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @see:Does the device have sdcard? if it has:true?false
	 * @return
	 */
	public boolean sdcardStatus() {
		boolean status = false;
		String statusStr = Environment.getExternalStorageState();
		if (StringUtils.equalsIgnoreCase(statusStr, Environment.MEDIA_MOUNTED)) {
			status = true;
		} else {
			status = false;
			Log.i(TAG, "no sdcard.....");
		}
		return status;
	}

	/**
	 * @since 获取sdcard剩余空间大小
	 * @return
	 */
	public long getSDFreeSize(String sizeType) {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blocksSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		if (StringUtils.equalsIgnoreCase(sizeType, SizeType.SZIETYPE_BYTE)) {
			return blocksSize * freeBlocks;
		} else if (StringUtils.equalsIgnoreCase(sizeType, SizeType.SZIETYPE_KB)) {
			return (blocksSize * freeBlocks) / 1024;
		} else if (StringUtils.equalsIgnoreCase(sizeType, SizeType.SZIETYPE_MB)) {
			return (blocksSize * freeBlocks) / 1024 / 1024;
		}
		return -1;
	}

	/**
	 * 
	 * @param directory
	 *            :要创建文件的上级目录
	 * @param filename
	 *            ：要创建的文件名称
	 * @param fileSuffixal
	 *            ：要创建的文件的类型（后缀明 example:.txt;.jpeg.....）
	 * @return
	 */
	public File createFile(String directory, String filename,
			String fileSuffixal) {
		File file = null;
		String directoryPth = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ directory
				+ File.separator;
		File dir = new File(directoryPth);
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String fileString = directoryPth + filename + fileSuffixal;
		try {
			file = new File(fileString);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return file;
	}

	public String getRootDirectory(String directory, String filename,
			String fileSuffixal) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + directory + File.separator + filename
				+ fileSuffixal;
	}

	public String getRootDirectory(String directory) {
		String dir = null;
		String absolutePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + directory;
		File file = new File(absolutePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		dir = file.getAbsolutePath();
		return dir;
	}
}
