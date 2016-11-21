package com.sobey.cloud.webtv.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class BaseUtil {

	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	// 隐藏虚拟键盘
	public static void HideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

		}
	}

	// 显示虚拟键盘
	public static void ShowKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 下载文件
	 */
	public static boolean downLoadFile(InputStream in, File file) {
		OutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buffer = new byte[1046];
			int totalWritten = 0;
			int numRead = 0;
			while ((numRead = in.read(buffer)) > 0) {
				// 将文件写入服务器流
				fos.write(buffer, 0, numRead);
				totalWritten += numRead;
			}
			Log.d("BaseUtil", "共下载字节数:" + totalWritten);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != fos) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	@SuppressLint("UseValueOf")
	public static InputStream getImageStream(String path) throws Exception {
		Log.i("BaseUtil", "建立链接的Url:" + path);

		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(path);
		request.getParams().setParameter("http.socket.timeout", new Integer(10 * 1000));
		request.addHeader("Pragma", "no-cache");
		request.addHeader("Cache-Control", "no-cache");
		request.addHeader("Charset", "UTF-8");
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();
		return inputStream;
	}

	public static File createSavePhotoFile(String fileName) {
		// File sdcardDir = Environment.getExternalStorageDirectory();
		// String ALBUM_PATH = sdcardDir.getPath() + "/" + "NMCoolPics";
		String ALBUM_PATH = FileUtil.PHOTO_APP;
		Log.i("Utils", "ALBUM_PATH:" + ALBUM_PATH);
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(ALBUM_PATH, fileName);
		return myCaptureFile;
	}

	public static String getFilePath() {
		return FileUtil.PHOTO_APP;
		// String sdcardDir =
		// Environment.getExternalStorageDirectory().getPath()
		// + "/" + "NMCoolPics";
		// return sdcardDir;
	}

	/**
	 * MD5加码。32位
	 * 
	 * @param inStr
	 * @return
	 */
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

	public static String getFileName(String inStr) {
		return MD5(inStr) + ".jpg";
	}

	public static boolean isFileExist(String path) {
		Log.i("BaseUtil", "要保存的文件:" + path);
		if (null != path) {
			File file = new File(path);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 去掉html的标签 只显示文本内容
	 * 
	 * @param content
	 * @return
	 */
	public static String clearHtmlTag(String content) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		String regex = "<(img|a|p|b|div|br)\\s*([\\w]*=(\"|\')([^\"\'<]*)(\"|\')\\s*)*(/>|>)";
		content = content.replaceAll(regex, "");
		String regex2 = "<span\\s*([\\w]*=(\"|\')(([^\"\'/>><']*)(\"|\'))*\\s*)*(/>|>)";
		content = content.replaceAll(regex2, "");
		content = content.replaceAll("<(/a|b|/b|p|/p|/span|/div)>", "");
		content = content.replaceAll("\r|\n|\t|&nbsp;", "");
		return content;
	}

	/**
	 * 处理图片的大小（防止溢出）
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<Object> getResizedBitmapByPath(String path) {
		File f = new File(path);
		Bitmap resizeBmp = null;
		ArrayList<Object> arrayList = new ArrayList<Object>();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		if (f.length() < 1024 * 100) {
			opts.inSampleSize = 1;
		} else {
			opts.inSampleSize = computeSampleSize(opts, -1, 640 * 480);
		}
		opts.inJustDecodeBounds = false;

		// if (f.length() < 50000) {
		// opts.inSampleSize = 1;
		// } else if (f.length() < 100000) {
		// opts.inSampleSize = 1;
		// } else if (f.length() < 200000) {
		// opts.inSampleSize = 1;
		// } else if (f.length() < 442500) {
		// opts.inSampleSize = 1;
		// } else if (f.length() < 885000) {
		// opts.inSampleSize = 2;
		// } else if (f.length() < 1770000) {
		// opts.inSampleSize = 4;
		// } else if (f.length() < 3540000) {
		// opts.inSampleSize = 6;
		// } else {
		// opts.inSampleSize = 8;
		// }
		Log.i("BaseUtil", "压缩前文件大小:" + f.length() + ",inSampleSize:" + opts.inSampleSize);
		int size = opts.inSampleSize;
		// 每个像素点用2个字节存储
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		resizeBmp = BitmapFactory.decodeFile(f.getPath(), opts);
		// resizeBmp = getBitmap(path);
		arrayList.add(resizeBmp);
		arrayList.add(size);
		return arrayList;
	}

	public static String saveBitmap2File(Bitmap bm, String fileName) throws IOException {
		Log.i("BaseUtil", "开始保存压缩后的文件-->");
		String ALBUM_PATH = FileUtil.PHOTO_APP;
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		String fullPath = ALBUM_PATH + fileName;
		Log.i("BaseUtil", "压缩后的文件路径-->" + fullPath);
		File myCaptureFile = new File(ALBUM_PATH, fileName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
		return fullPath;
	}

	public static String compressPhoto(String path) {
		Log.i("Utils", "开始压缩文件--> path:" + path);
		ArrayList<Object> list = getResizedBitmapByPath(path);
		Bitmap bmp = (Bitmap) list.get(0);
		String fileName = getFileName(path);
		try {
			return saveBitmap2File(bmp, fileName);
		} catch (IOException e) {
			Log.i("BaseUtil", "压缩文件失败，原因：");
			e.printStackTrace();
			return null;
		}
	}

	// .xxx
	public static String getFileSuffix(String path) {
		int index = path.lastIndexOf(".");
		return path.substring(index, path.length());
	}

	public static void lengthFilter(final Context context, final EditText editText, final int max_length,
			final String err_msg) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(max_length) {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart,
					int dend) {
				int destLen = BaseUtil.getCharacterNum(dest.toString()); // 获取字符个数(一个中文算2个字符)
				int sourceLen = BaseUtil.getCharacterNum(source.toString());
				if (destLen + sourceLen > max_length) {
					Toast.makeText(context, err_msg, Toast.LENGTH_SHORT).show();
					return "";
				}
				return source;
			}
		};
		editText.setFilters(filters);
	}

	/**
	 * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * @param content
	 * @return
	 */
	public static int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	/**
	 * @description 返回字符串里中文字或者全角字符的个数
	 * @param s
	 * @return
	 */
	public static int getChineseNum(String s) {

		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
