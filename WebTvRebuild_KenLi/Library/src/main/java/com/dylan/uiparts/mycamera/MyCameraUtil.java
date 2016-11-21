package com.dylan.uiparts.mycamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class MyCameraUtil {
	/**
	 * 检测设备是否存在Camera硬件
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 打开一个Camera
	 * 
	 * @return
	 */
	public static Camera getCameraInstance(int cameraId) {
		Camera camera = null;
		try {
			camera = Camera.open(cameraId);
		} catch (Exception e) {
			return null;
		}
		return camera;
	}
}
