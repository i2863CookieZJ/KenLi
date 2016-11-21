package com.sobey.cloud.webtv.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.obj.AdObj;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class AdManager {

	public static AdObj getScreenAdPicUrl(final Context context) {
		SharedPreferences adManager = context.getSharedPreferences("ad_manager", 0);
		final AdObj obj = AdObj.StringToAdObj(adManager.getString("screen", null));
		News.getAdvertisement(context, null, "screen", new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0) {
						JSONObject obj1 = result.optJSONObject(0);
						JSONObject obj2 = obj1.optJSONArray("advertisement").optJSONObject(0);
						String id = obj1.optString("id");
						String name = obj2.optString("adname");
						String picUrl = obj2.optJSONObject("adcontent").optString("UploadFilePath1");
						String linkUrl = obj2.optJSONObject("adcontent").optString("outurl");
						if (!TextUtils.isEmpty(picUrl) && (obj == null || !obj.getId().equalsIgnoreCase(id) || !obj.getName().equalsIgnoreCase(name) || !obj.getPicUrl().equalsIgnoreCase(picUrl) || !obj.getLinkUrl().equalsIgnoreCase(linkUrl))) {
							AdObj adObj = new AdObj(id, name, picUrl, linkUrl);
							saveScreenAdPic(context, adObj);
						}
						if (TextUtils.isEmpty(picUrl)) {
							SharedPreferences adManager = context.getSharedPreferences("ad_manager", 0);
							Editor editor = adManager.edit();
							editor.putString("screen", "");
							editor.commit();
						}
					} else {
						SharedPreferences adManager = context.getSharedPreferences("ad_manager", 0);
						Editor editor = adManager.edit();
						editor.putString("screen", "");
						editor.commit();
					}
				} catch (Exception e) {
					SharedPreferences adManager = context.getSharedPreferences("ad_manager", 0);
					Editor editor = adManager.edit();
					editor.putString("screen", "");
					editor.commit();
				}
			}

			@Override
			public void onNG(String reason) {
			}

			@Override
			public void onCancel() {
			}
		});
		return obj;
	}

	public static void saveScreenAdPic(Context context, AdObj adObj) {
		new AsyncTask<Object, Void, String>() {
			private Context context = null;

			@Override
			protected String doInBackground(Object... params) {
				try {
					context = (Context) params[0];
					AdObj adObj = (AdObj) params[1];
					String httpUrl = adObj.getPicUrl();
					final String fileName = httpUrl.substring(httpUrl.lastIndexOf("/"));
					String path = Environment.getExternalStorageDirectory().getPath() + MConfig.CachePath;
					File tmpFile = new File(path);
					if (!tmpFile.exists()) {
						tmpFile.mkdirs();
					}
					File file = new File(path + "/" + fileName);

					URL url = new URL(httpUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buf = new byte[256];
					conn.connect();
					double count = 0;
					if (conn.getResponseCode() < 400) {
						while (count <= 100) {
							if (is != null) {
								int numRead = is.read(buf);
								if (numRead <= 0) {
									break;
								} else {
									fos.write(buf, 0, numRead);
								}
							} else {
								break;
							}
						}
					}
					conn.disconnect();
					fos.close();
					is.close();
					if (file != null) {
						adObj.setPicPath(file.getAbsolutePath());
						SharedPreferences adManager = context.getSharedPreferences("ad_manager", 0);
						Editor editor = adManager.edit();
						editor.putString("screen", AdObj.AdObjToString(adObj));
						editor.commit();
						Log.i("dzy", "下载开屏图片成功:" + adObj.getPicPath());
					}
				} catch (Exception e) {
					return "下载出错,请稍后重试";
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
			};
		}.execute(new Object[] { context, adObj });
	}
}
