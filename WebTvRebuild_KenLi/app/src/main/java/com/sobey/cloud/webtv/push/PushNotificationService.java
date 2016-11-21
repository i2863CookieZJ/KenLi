package com.sobey.cloud.webtv.push;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.DateParse;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.SplashActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;

@SuppressLint("HandlerLeak")
public class PushNotificationService extends Service {

	public static final String ServiceAction = "com.sobey.cloud.webtv.push.PushNotificationService";
	private static final int LOOP_TIME = 60000;

	private NotificationManager notificationManager;
	private Notification notification;

	private Handler mHandler;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private int mIndex = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		startLoop();
		return START_NOT_STICKY;
	}

	private void startLoop() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 100) {
					getPush();
				}
			}
		};
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 100;
				if (mHandler != null) {
					mHandler.sendMessage(msg);
				}
			}
		};
		mTimer = new Timer();
		mTimer.schedule(mTimerTask, 0, LOOP_TIME);
	}

	private void getPush() {
		SharedPreferences lastTimePref = this.getSharedPreferences("comment_preference", 0);
		String lastTime = lastTimePref.getString("last_time", null);
		News.pushNotifiToAndroid(lastTime, this, new OnJsonArrayResultListener() {

			@Override
			public void onOK(JSONArray result) {
				try {
					String lastDate = "2010-01-01 00:00:00";
					for (int i = 0; i < result.length(); i++) {
						JSONObject obj = result.getJSONObject(i);
						String content = obj.optString("Content");
						String id = obj.optString("ID");
						String type = obj.optString("Type");
						String time = obj.optString("PushTime");
						if (DateParse.parseDate(time, null).after(DateParse.parseDate(lastDate, null))) {
							lastDate = DateParse.getDate(0, 0, 0, 1, time, null, null);
						}
						if (TextUtils.isEmpty(type)) {
							if (!TextUtils.isEmpty(content)) {
								showTextNotification(null, null, content, lastDate);
							}
						} else {
							if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content)) {
								showTextNotification(type, id, content, lastDate);
							}
						}
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onNG(String reason) {
			}

			@Override
			public void onCancel() {
			}
		});
	}

	private void showTextNotification(String type, String id, String content, String time) {
		try {
			SharedPreferences lastTimePref = this.getSharedPreferences("comment_preference", 0);
			Editor lastTimeEditor = lastTimePref.edit();
			lastTimeEditor.putString("last_time", time);
			lastTimeEditor.commit();
			mIndex++;
			notification = new Notification();
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.icon = R.drawable.ic_launcher;
			notification.when = System.currentTimeMillis();
			notification.tickerText = content;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			Intent intent1 = new Intent(this, SplashActivity.class);
			JSONObject information = new JSONObject();
			information.put("id", (id == null ? "" : id));
			information.put("type", (type == null ? "" : type));
			intent1.putExtra("information", information.toString());
			PendingIntent pendingIntent = PendingIntent.getActivity(this, mIndex, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = pendingIntent;
			notification.contentView = new RemoteViews(getPackageName(), R.layout.notification_broketask_text);
			notification.contentView.setTextViewText(R.id.title, content);
			notificationManager.notify(mIndex, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.gc();
		super.onDestroy();
	}
}
