package com.sobey.cloud.webtv.broke;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.FtpUtils;
import com.dylan.common.utils.FtpUtils.UploadStatus;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.broke.util.BrokeTaskUploadControl;
import com.sobey.cloud.webtv.obj.CacheDataBrokeTask;
import com.sobey.cloud.webtv.senum.BrokeTaskStatus;
import com.sobey.cloud.webtv.utils.MConfig;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("HandlerLeak")
public class BrokeTaskProgressNotificationService extends Service {

	public static final String ServiceAction = "com.sobey.cloud.webtv.broke.BrokeTaskProgressNotificationService";
	public static final int NOTIFICATION_CONNECTED = 0;

	private Intent intent;
	private FtpUtils ftpUtils;
	private NotificationManager notificationManager;
	private Notification notification;

	private String catalogIndex;
	private String id;
	private CacheDataBrokeTask brokeTask;
	private String hostname;
	private int port;
	private String usernameImage;
	private String passwordImage;
	private String usernameVideo;
	private String passwordVideo;
	ArrayList<JSONObject> localFilePathArrayList;
	private ArrayList<String> localFilePathList = new ArrayList<String>();
	private ArrayList<String> remoteFileUrlList = new ArrayList<String>();
	private int totalSum = 0;
	private int index = 0;
	private ArrayList<Boolean> uploadStatusList = new ArrayList<Boolean>();
	private int lastProgress = 0;
	private int lastAllProgress = 0;
	private BrokeTaskStatus status = BrokeTaskStatus.WAITING;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (intent.getStringExtra("status").equalsIgnoreCase("pause")) {
				status = BrokeTaskStatus.PAUSE;
				refreshNotification("暂停上传", lastProgress);
				ftpUtils.stopUploadFile();
				stopSelf();
			} else {
				notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				id = intent.getStringExtra("id");
				catalogIndex = intent.getStringExtra("index");
				brokeTask = BrokeTaskUploadControl.jsonObjectToBrokeTask(new JSONObject(intent.getStringExtra("broketask")));
				hostname = intent.getStringExtra("hostname");
				port = Integer.valueOf(intent.getStringExtra("port"));
				usernameImage = intent.getStringExtra("username_image");
				passwordImage = intent.getStringExtra("password_image");
				usernameVideo = intent.getStringExtra("username_video");
				passwordVideo = intent.getStringExtra("password_video");
				localFilePathArrayList = brokeTask.getFilePathList();
				for (int i = 0; i < localFilePathArrayList.size(); i++) {
					JSONObject localFilePath = localFilePathArrayList.get(i);
					File file = new File(localFilePath.optString("path"));
					if (file.exists() && file.getTotalSpace() > 0) {
						localFilePathList.add(localFilePath.optString("path"));
						remoteFileUrlList.add(localFilePath.optString("url"));
					}
				}
				if (localFilePathList.size() > 0) {
					totalSum = localFilePathList.size();
					index = 1;
				} else {
					showTextNotification("获取本地上传文件失败");
				}
				this.intent = new Intent(ServiceAction);
				initNotification();
				new Thread() {
					@Override
					public void run() {
						uploadFile();
					}
				}.start();
			}
		} catch (Exception e) {
			Log.i("dzy", "0000" + e.toString());
			showTextNotification("上传出错，请稍后重试");
		}
		return START_NOT_STICKY;
	}

	public void uploadFile() {
		try {
			ftpUtils = new FtpUtils(handler);
			for (int i = 0; i < localFilePathList.size(); i++) {
				if (uploadStatusList.size() > i && uploadStatusList.get(i)) {
					continue;
				}
				boolean result = true;
				if (remoteFileUrlList.get(i).indexOf(MConfig.mFtpRemoteImagePath) < 0) {
					result = ftpUtils.connect(hostname, port, usernameVideo, passwordVideo);
				} else {
					result = ftpUtils.connect(hostname, port, usernameImage, passwordImage);
				}
				if (result) {
					index = i + 1;
					UploadStatus status = ftpUtils.upload(localFilePathList.get(i), remoteFileUrlList.get(i));
					if (status == UploadStatus.Upload_New_File_Success || status == UploadStatus.Upload_From_Break_Success || status == UploadStatus.File_Exits) {
						uploadStatusList.add(true);
						refreshNotification("上传第" + index + "个文件成功", 100);
					} else if (status == UploadStatus.Upload_Stop) {
						Log.i("dzy", "暂停上传");
						refreshNotification("暂停上传", lastProgress);
						ftpUtils.disconnect();
						return;
					} else {
						uploadStatusList.add(false);
						refreshNotification("上传第" + index + "个文件失败", 100);
					}
				} else {
					showTextNotification("连接服务器失败");
					return;
				}
				ftpUtils.disconnect();
			}
			JSONArray content = new JSONArray();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());   //初始化Formatter的转换格式。
			for(int i =0 ; i<localFilePathArrayList.size(); i++) {
				JSONObject object = localFilePathArrayList.get(i);
				JSONObject objectNew = new JSONObject();
				if(object.optString("type").equalsIgnoreCase("video")) {
					objectNew.put("type", "video");
					objectNew.put("videoid", object.optString("guid"));
					objectNew.put("length", formatter.format(object.optInt("duration")));
				} else if(object.optString("type").equalsIgnoreCase("image")) {
					objectNew.put("type", "image");
					objectNew.put("imgpath", object.optString("url"));
				} else {
					continue;
				}
				content.put(objectNew);
			}
			News.addBrokes(brokeTask.getUsername(), brokeTask.getCatalogId(), brokeTask.getPhone(), "", brokeTask.getLocation(), brokeTask.getTitle(), content.toString(), BrokeTaskProgressNotificationService.this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					try {
						Log.i("dzy", result.optJSONObject(0).optString("returncode").substring(0, 7));
						if (result.optJSONObject(0).optString("returncode").substring(0, 7).equalsIgnoreCase("SUCCESS")) {
							intent.putExtra("id", id);
							intent.putExtra("progress", 200);
							sendBroadcast(intent);
							status = BrokeTaskStatus.FINISHED;
							BrokeTaskUploadControl.finishUploadTask(BrokeTaskProgressNotificationService.this, id);
							new Handler(getMainLooper()).postDelayed(new Runnable() {
								@Override
								public void run() {
									showTextNotification("上传完成!");
								}
							}, 300);
						} else {
							showTextNotification("上传失败");
						}
					} catch (Exception e) {
						showTextNotification("上传失败");
						e.printStackTrace();
					}
				}

				@Override
				public void onNG(String reason) {
					showTextNotification("上传失败");

				}

				@Override
				public void onCancel() {
					showTextNotification("上传失败");
				}
			});
		} catch (Exception e) {
			showTextNotification("上传失败");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				int progress = msg.arg1;
				refreshNotification("正在上传第" + index + "个文件", progress);
				lastProgress = progress;
				lastAllProgress = 100 * ((index - 1) * 100 + progress) / (totalSum * 100);
				intent.putExtra("id", id);
				intent.putExtra("progress", lastAllProgress);
				sendBroadcast(intent);
				status = BrokeTaskStatus.UPLOADING;
				CacheDataBrokeTask brokeTask = BrokeTaskUploadControl.getUploadTask(BrokeTaskProgressNotificationService.this, id);
				if (brokeTask != null) {
					brokeTask.setProgress(lastAllProgress);
					brokeTask.setStatus(status);
					BrokeTaskUploadControl.saveUploadTask(BrokeTaskProgressNotificationService.this, brokeTask);
				}
			}
		};
	};

	private void initNotification() {
		notification = new Notification();
		notification.icon = android.R.drawable.ic_menu_upload;
		notification.when = System.currentTimeMillis();
		notification.tickerText = "正在上传";
		notification.contentView = new RemoteViews(getPackageName(), R.layout.notification_broketask_progress);
		notification.contentView.setTextViewText(R.id.title, "开始上传");
		notification.contentView.setTextViewText(R.id.progressText, "0%");
		Intent intent = new Intent(this, BrokeTaskHomeActivity.class);
		try {
			intent.putExtra("index", Integer.valueOf(catalogIndex));
		} catch (Exception e) {
			intent.putExtra("index", 0);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(NOTIFICATION_CONNECTED, notification);
	}

	private void refreshNotification(String content, int progress) {
		notification.contentView.setProgressBar(R.id.progressBar, 100, progress, false);
		notification.contentView.setTextViewText(R.id.title, content);
		notification.contentView.setTextViewText(R.id.progressText, "" + progress + "%");
		notificationManager.notify(NOTIFICATION_CONNECTED, notification);
	}

	private void showTextNotification(String content) {
		intent.putExtra("id", id);
		intent.putExtra("progress", -1);
		sendBroadcast(intent);
		stopSelf();
		notification = new Notification();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.icon = android.R.drawable.ic_menu_info_details;
		notification.when = System.currentTimeMillis();
		notification.tickerText = content;
		Intent intent1 = new Intent(this, BrokeTaskHomeActivity.class);
		try {
			intent1.putExtra("index", Integer.valueOf(catalogIndex));
		} catch (Exception e) {
			intent1.putExtra("index", 0);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pendingIntent;
		notification.contentView = new RemoteViews(getPackageName(), R.layout.notification_broketask_text);
		notification.contentView.setTextViewText(R.id.title, content);
		notificationManager.notify(NOTIFICATION_CONNECTED, notification);
	}

	@Override
	public void onDestroy() {
		CacheDataBrokeTask brokeTask = BrokeTaskUploadControl.getUploadTask(this, id);
		if (brokeTask != null) {
			brokeTask.setProgress(lastAllProgress);
			if (status == BrokeTaskStatus.UPLOADING) {
				status = BrokeTaskStatus.PAUSE;
			}
			brokeTask.setStatus(status);
			BrokeTaskUploadControl.saveUploadTask(this, brokeTask);
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.gc();
		super.onDestroy();
	}
}
