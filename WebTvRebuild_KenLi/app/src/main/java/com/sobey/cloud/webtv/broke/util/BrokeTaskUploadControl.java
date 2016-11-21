package com.sobey.cloud.webtv.broke.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.DateParse;
import com.sobey.cloud.webtv.broke.BrokeTaskProgressNotificationService;
import com.sobey.cloud.webtv.obj.CacheDataBrokeTask;
import com.sobey.cloud.webtv.senum.BrokeTaskStatus;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class BrokeTaskUploadControl {
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static boolean startUploadService(Context context, CacheDataBrokeTask brokeTask) {
		try {
			if (isServiceRunning(context, BrokeTaskProgressNotificationService.ServiceAction)) {
				Toast.makeText(context, "有任务正在上传", Toast.LENGTH_SHORT).show();
				return false;
			}
			Intent intent = new Intent(context, BrokeTaskProgressNotificationService.class);
			intent.putExtra("id", brokeTask.getId());
			intent.putExtra("index", brokeTask.getIndex());
			intent.putExtra("hostname", MConfig.mFtpHostName);
			intent.putExtra("port", MConfig.mFtpPort);
			intent.putExtra("username_image", MConfig.mFtpUserNameImage);
			intent.putExtra("password_image", MConfig.mFtpPasswordImage);
			intent.putExtra("username_video", MConfig.mFtpUserNameVideo);
			intent.putExtra("password_video", MConfig.mFtpPasswordVideo);
			intent.putExtra("broketask", brokeTaskToJsonObject(brokeTask).toString());
			intent.putExtra("status", "start");
			context.startService(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean stopUploadService(Context context, String brokeTaskId) {
		try {
			if (!isServiceRunning(context, BrokeTaskProgressNotificationService.ServiceAction)) {
				CacheDataBrokeTask brokeTask = BrokeTaskUploadControl.getUploadTask(context, brokeTaskId);
				if (brokeTask != null) {
					if (brokeTask.getStatus() == BrokeTaskStatus.UPLOADING) {
						brokeTask.setStatus(BrokeTaskStatus.PAUSE);
						BrokeTaskUploadControl.saveUploadTask(context, brokeTask);
					}
				}
				return true;
			}
			Intent intent = new Intent(context, BrokeTaskProgressNotificationService.class);
			intent.putExtra("status", "pause");
			context.startService(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static JSONObject brokeTaskToJsonObject(CacheDataBrokeTask brokeTask) {
		try {
			JSONObject taskObject = new JSONObject();
			ArrayList<JSONObject> filePathList;
			JSONArray filePathArray = new JSONArray();
			taskObject.put("username", brokeTask.getUsername());
			taskObject.put("id", brokeTask.getId());
			taskObject.put("index", brokeTask.getIndex());
			taskObject.put("catalogid", brokeTask.getCatalogId());
			taskObject.put("logo", brokeTask.getLogo());
			taskObject.put("title", brokeTask.getTitle());
			taskObject.put("phone", brokeTask.getPhone());
			taskObject.put("location", brokeTask.getLocation());
			taskObject.put("imagecount", brokeTask.getImagecount());
			taskObject.put("videocount", brokeTask.getVideocount());
			filePathList = brokeTask.getFilePathList();
			for (int i = 0; i < filePathList.size(); i++) {
				filePathArray.put(filePathList.get(i));
			}
			taskObject.put("filepath", filePathArray);
			taskObject.put("status", brokeTask.getStatus().toString());
			taskObject.put("progress", brokeTask.getProgress());
			return taskObject;
		} catch (Exception e) {
			return null;
		}
	}

	public static CacheDataBrokeTask jsonObjectToBrokeTask(JSONObject taskObject) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			CacheDataBrokeTask brokeTask = new CacheDataBrokeTask();
			ArrayList<JSONObject> filePathList = new ArrayList<JSONObject>();
			JSONArray filePathArray;
			brokeTask.setUsername(taskObject.optString("username"));
			brokeTask.setId(taskObject.optString("id"));
			brokeTask.setIndex(taskObject.optString("index"));
			brokeTask.setCatalogId(taskObject.optString("catalogid"));
			brokeTask.setLogo(taskObject.optString("logo"));
			brokeTask.setTitle(taskObject.optString("title"));
			brokeTask.setPhone(taskObject.optString("phone"));
			brokeTask.setLocation(taskObject.optString("location"));
			brokeTask.setImagecount(Integer.valueOf(taskObject.optString("imagecount")));
			brokeTask.setVideocount(Integer.valueOf(taskObject.optString("videocount")));
			filePathArray = taskObject.optJSONArray("filepath");
			for (int i = 0; i < filePathArray.length(); i++) {
				filePathList.add(filePathArray.optJSONObject(i));
			}
			brokeTask.setFilePathList(filePathList);
			brokeTask.setStatus(BrokeTaskStatus.valueOf(taskObject.optString("status")));
			brokeTask.setProgress(Integer.valueOf(taskObject.optString("progress")));
			brokeTask.setTime(DateParse.individualTime(dateFormat.format(new Date(Long.parseLong(taskObject.optString("id")))), null));
			return brokeTask;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean saveUploadTask(Context context, CacheDataBrokeTask brokeTask) {
		try {
			SharedPreferences uploadTask = context.getSharedPreferences("upload_task", Context.MODE_MULTI_PROCESS);
			JSONArray taskArray;
			JSONArray taskArrayNew = new JSONArray();
			boolean isExist = false;
			if (uploadTask == null) {
				taskArray = new JSONArray();
			} else {
				try {
					taskArray = new JSONArray(uploadTask.getString("task_doing_array", ""));
				} catch (Exception e) {
					taskArray = new JSONArray();
				}
			}
			for (int i = 0; i < taskArray.length(); i++) {
				if (taskArray.optJSONObject(i).optString("id").equalsIgnoreCase(brokeTask.getId())) {
					JSONObject object = brokeTaskToJsonObject(brokeTask);
					if (object == null) {
						return false;
					}
					taskArrayNew.put(object);
					isExist = true;
				} else {
					taskArrayNew.put(taskArray.optJSONObject(i));
				}
			}
			if (!isExist) {
				taskArrayNew.put(brokeTaskToJsonObject(brokeTask));
			}
			Editor editor = uploadTask.edit();
			editor.putString("task_doing_array", taskArrayNew.toString());
			editor.commit();
			Log.i("dzy", "save success!");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean deleteUploadTask(Context context, String brokeTaskId) {
		return deleteUploadTask(context, brokeTaskId, 0);
	}

	public static boolean deleteUploadTask(Context context, String brokeTaskId, int catalogId) {
		try {
			SharedPreferences uploadTask = context.getSharedPreferences("upload_task", Context.MODE_MULTI_PROCESS);
			JSONArray taskArray;
			JSONArray taskArrayNew = new JSONArray();
			boolean isExist = false;
			if (uploadTask == null) {
				taskArray = new JSONArray();
			} else {
				try {
					if(catalogId == 0) {
						taskArray = new JSONArray(uploadTask.getString("task_doing_array", ""));
					} else {
						taskArray = new JSONArray(uploadTask.getString("task_done_array", ""));
					}
				} catch (Exception e) {
					taskArray = new JSONArray();
				}
			}
			for (int i = 0; i < taskArray.length(); i++) {
				if (taskArray.optJSONObject(i).optString("id").equalsIgnoreCase(brokeTaskId)) {
					isExist = true;
				} else {
					taskArrayNew.put(taskArray.optJSONObject(i));
				}
			}
			Editor editor = uploadTask.edit();
			if(catalogId == 0) {
				editor.putString("task_doing_array", taskArrayNew.toString());
			} else {
				editor.putString("task_done_array", taskArrayNew.toString());
			}
			editor.commit();
			if (isExist) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean finishUploadTask(Context context, String brokeTaskId) {
		try {
			CacheDataBrokeTask brokeTask = getUploadTask(context, brokeTaskId);
			SharedPreferences uploadTask = context.getSharedPreferences("upload_task", Context.MODE_MULTI_PROCESS);
			JSONArray taskArray;
			JSONArray taskArrayNew = new JSONArray();
			boolean isExist = false;
			if (uploadTask == null) {
				taskArray = new JSONArray();
			} else {
				try {
					taskArray = new JSONArray(uploadTask.getString("task_done_array", ""));
				} catch (Exception e) {
					taskArray = new JSONArray();
				}
			}
			for (int i = 0; i < taskArray.length(); i++) {
				if (taskArray.optJSONObject(i).optString("id").equalsIgnoreCase(brokeTask.getId())) {
					JSONObject object = brokeTaskToJsonObject(brokeTask);
					if (object == null) {
						return false;
					}
					taskArrayNew.put(object);
					isExist = true;
				} else {
					taskArrayNew.put(taskArray.optJSONObject(i));
				}
			}
			if (!isExist) {
				taskArrayNew = new JSONArray();
				taskArrayNew.put(brokeTaskToJsonObject(brokeTask));
				for (int i = 0; i < taskArray.length(); i++) {
					taskArrayNew.put(taskArray.optJSONObject(i));
				}
			}
			Editor editor = uploadTask.edit();
			editor.putString("task_done_array", taskArrayNew.toString());
			editor.commit();
			deleteUploadTask(context, brokeTaskId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static ArrayList<CacheDataBrokeTask> getUploadTaskList(Context context, String username, int catalogId) {
		try {
			SharedPreferences uploadTask = context.getSharedPreferences("upload_task", Context.MODE_MULTI_PROCESS);
			JSONArray taskArray;
			ArrayList<CacheDataBrokeTask> brokeTaskList = new ArrayList<CacheDataBrokeTask>();
			brokeTaskList.clear();
			if (uploadTask == null) {
				return null;
			} else {
				try {
					if(catalogId == 0) {
						taskArray = new JSONArray(uploadTask.getString("task_doing_array", ""));
					} else {
						taskArray = new JSONArray(uploadTask.getString("task_done_array", ""));
					}
				} catch (Exception e) {
					return null;
				}
			}
			for (int i = 0; i < taskArray.length(); i++) {
				CacheDataBrokeTask brokeTask = jsonObjectToBrokeTask(taskArray.optJSONObject(i));
				if (brokeTask != null && brokeTask.getUsername().equalsIgnoreCase(username)) {
					brokeTaskList.add(brokeTask);
				}
			}
			if (brokeTaskList.size() > 0) {
				return brokeTaskList;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static CacheDataBrokeTask getUploadTask(Context context, String brokeTaskId) {
		try {
			SharedPreferences uploadTask = context.getSharedPreferences("upload_task", Context.MODE_MULTI_PROCESS);
			JSONArray taskArray;
			if (uploadTask == null) {
				return null;
			} else {
				try {
					taskArray = new JSONArray(uploadTask.getString("task_doing_array", ""));
				} catch (Exception e) {
					return null;
				}
			}
			for (int i = 0; i < taskArray.length(); i++) {
				JSONObject object = taskArray.optJSONObject(i);
				if (object.optString("id").equalsIgnoreCase(brokeTaskId)) {
					CacheDataBrokeTask brokeTask = jsonObjectToBrokeTask(taskArray.optJSONObject(i));
					if (brokeTask != null) {
						return brokeTask;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
