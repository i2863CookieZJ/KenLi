package com.sobey.cloud.webtv.push;

import java.util.List;

import com.sobey.cloud.webtv.utils.MConfig;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class PushNotificationUtils {
	public static boolean isServiceRun(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(30);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(PushNotificationService.ServiceAction)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAppRun(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(40);
		if (!tasks.isEmpty()) {
			for(RunningTaskInfo task: tasks) {
				ComponentName baseActivity = task.baseActivity;
				if(baseActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
					if (baseActivity.getClassName().equals(MConfig.mPackageNameHomeActivity) ||
							baseActivity.getClassName().equals(MConfig.mPackageNameBrokeNewsHomeActivity) ||
							baseActivity.getClassName().equals(MConfig.mPackageNameLiveNewsHomeActivity) ||
							baseActivity.getClassName().equals(MConfig.mPackageNameRecommendNewsHomeActivity) ||
							baseActivity.getClassName().equals(MConfig.mPackageNameTopicNewsHomeActivity)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
