package com.sobey.cloud.webtv.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sobey.cloud.webtv.config.AppConfig;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateVersionService extends Service {
	public static final int INIT_UPDATE_NOTIFY = 10011;
	public Resources res;
	// 通知栏
	private NotificationManager updateNotifyMg;
	private Notification updateNotify;

	private boolean isWorking = true;

	@Override
	public void onCreate() {
		// 设置通知栏显示
		updateNotifyMg = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		updateNotify = new Notification();
		updateNotify.contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
		updateNotify.icon = R.drawable.ic_launcher;
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(),
				PendingIntent.FLAG_CANCEL_CURRENT);
		updateNotify.contentIntent = pendingintent;
		updateNotifyMg.notify(INIT_UPDATE_NOTIFY, updateNotify);
		super.onCreate();

		res = this.getResources();
	}

	/**
	 * 检测更新条件是否满足，若满足则开始下载程序
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (!android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			stopSelf();
			return;
		}

		if (intent == null) {
			stopSelf();
			return;
		}
		String apkUrl = intent.getStringExtra("apkUrl");
		if (apkUrl.equals("")) {
			stopSelf();
			return;
		} else {
			new DownloadUpdateFilesTask().execute(apkUrl);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isWorking = false;
		if (updateNotifyMg != null) {
			updateNotifyMg.cancelAll();
		}
	}

	/**
	 * 下载apk文件异步线程，下载完成后提示安装
	 */
	class DownloadUpdateFilesTask extends AsyncTask<String, Integer, File> {

		@Override
		protected File doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				int length = (int) conn.getContentLength();
				InputStream is = conn.getInputStream();

				String apkPath = Environment.getExternalStorageDirectory() + MConfig.CachePath;
				if (length != -1) {

					FileUtil.makeDir(apkPath);
					
					File file = new File(apkPath + "/WebTV.apk");
					if(file.exists())
					{
						file.delete();
					}

					FileOutputStream out = new FileOutputStream(file);
					byte[] buffer = new byte[1024 * 10];
					int readLen = 0;
					int destPos = 0;
					int currentPercent = 0;
					while (((readLen = is.read(buffer)) != -1) && isWorking) {
						out.write(buffer, 0, readLen);
						destPos += readLen;
						int p = destPos * 100 / length;
						if (p > 0 && p != currentPercent) {
							currentPercent = p;
							publishProgress(p);
						}
					}
					out.flush();
					out.close();
				}
				is.close();
				return new File(apkPath + "/WebTV.apk");// 生成本地文件名
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(File result) {
			updateNotifyMg.cancelAll();
			if (!isWorking) {
				return;
			}
			if (result == null || !result.exists()) {
				Toast.makeText(UpdateVersionService.this, res.getString(R.string.app_client_update_error),
						Toast.LENGTH_SHORT).show();
				return;
			}
			Uri uri = Uri.fromFile(result);
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(installIntent);
			UpdateVersionService.this.stopSelf();
		}

		@Override
		protected void onPreExecute() {
			updateNotify.contentView.setProgressBar(R.id.notify_ProgressBar, 100, 0, false);
			updateNotify.contentView.setTextViewText(R.id.percent_tv, "0%");
			updateNotifyMg.notify(INIT_UPDATE_NOTIFY, updateNotify);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			updateNotify.contentView.setProgressBar(R.id.notify_ProgressBar, 100, values[0], false);
			updateNotify.contentView.setTextViewText(R.id.percent_tv, values[0] + "%");
			updateNotifyMg.notify(INIT_UPDATE_NOTIFY, updateNotify);
		}
	}
}
