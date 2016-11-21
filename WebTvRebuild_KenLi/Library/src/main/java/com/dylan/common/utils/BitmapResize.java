package com.dylan.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitmapResize {
	/**
	 * Get bitmap by filename and width height, avoid out of memory error
	 * 
	 * @param height
	 * @param width
	 */
	public static Bitmap getBitmapByName(String filename, int width, int height) {
		Bitmap bitmap = null;
		if (!hasSDcard()) {
			return null;
		}
		File file = new File(filename);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		try {
			Bitmap bmp = null;
			Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bmp = BitmapFactory.decodeFile(file.getPath(), opts);
			final int minSideLength = Math.min(width, height);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
			opts.inJustDecodeBounds = false;
			opts.inInputShareable = true;
			opts.inPurgeable = true;
			opts.inDither = false;
			opts.inPurgeable = true;
			opts.inTempStorage = new byte[16 * 1024];
			if (fs != null) {
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, opts);
				bitmap = Bitmap.createBitmap(bmp);
			}
			if (bmp != null) {
				bmp = null;
				System.gc();
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
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

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
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

	private static boolean hasSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
