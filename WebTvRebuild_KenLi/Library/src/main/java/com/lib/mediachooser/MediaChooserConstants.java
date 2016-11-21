package com.lib.mediachooser;

import java.io.File;

public class MediaChooserConstants {

	/**
	 * No of item that can be selected. Default is 100.
	 */
	public static int MAX_IMAGE_LIMIT = 100;
	public static int MAX_VIDEO_LIMIT = 100;
	
	public static boolean SHOW_SELECTED_NUM = true;

	public static String fileType = "all";
	
	public static String footerBarOkString = "确定";
	public static String footerBarCancelString = "取消";

	public static int SELECTED_IMAGE_SIZE_IN_MB = 2000;
	public static int SELECTED_VIDEO_SIZE_IN_MB = 2000;

	public static final int BUCKET_SELECT_IMAGE_CODE = 1000;
	public static final int BUCKET_SELECT_VIDEO_CODE = 2000;

	public static final int MEDIA_TYPE_IMAGE = 10000;
	public static final int MEDIA_TYPE_VIDEO = 20000;

	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	public static long ChekcMediaFileSize(File mediaFile, boolean isVideo) {

		/** Get length of file in bytes */
		long fileSizeInBytes = mediaFile.length();

		/** Convert the bytes to Kilobytes (1 KB = 1024 Bytes) */
		long fileSizeInKB = fileSizeInBytes / 1024;

		/** Convert the KB to MegaBytes (1 MB = 1024 KBytes) */
		long fileSizeInMB = fileSizeInKB / 1024;

		int requireSize = 0;
		if (isVideo) {
			requireSize = MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB;
		} else {
			requireSize = MediaChooserConstants.SELECTED_IMAGE_SIZE_IN_MB;
		}
		if (fileSizeInMB <= requireSize) {
			return 0;
		}
		return fileSizeInMB;
	}

}
