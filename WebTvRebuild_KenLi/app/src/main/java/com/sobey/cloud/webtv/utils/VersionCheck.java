package com.sobey.cloud.webtv.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.service.UpdateVersionService;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

public class VersionCheck {
	/**
	 * 开机版本检查
	 * 
	 * @param context
	 */
	public void check(final Context context) {
		News.versionUpdate(context, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					JSONObject object = result.getJSONObject(0);
					String newVersion = object.getString("versioncode");
					if (Integer.valueOf(newVersion) > getVersionCode(context)) {
						getVersionLog(result, context, object.getString("versionname"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
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

	public void check(final Context context, final boolean noticeFlag) {
		News.versionUpdate(context, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					JSONObject object = result.getJSONObject(0);
					String newVersion = object.getString("versioncode");
					if (Integer.valueOf(newVersion) > getVersionCode(context)) {
						getVersionLog(result, context, noticeFlag, object.getString("versionname"));
					}else
					{
						Toast.makeText(context, "当前已经是最新版本!	", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNG(String reason) {
				if (noticeFlag) {
					Toast.makeText(context, "网络繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancel() {
				if (noticeFlag) {
					Toast.makeText(context, "网络繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void getVersionLog(final JSONArray result, final Context context, String newVersion) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "getVersionLog");
		map.put("type", "0");
		map.put("version", newVersion);
		VolleyRequset.doPost(context, MConfig.mQuanZiApiUrl, "getVersionLog", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				if (!TextUtils.isEmpty(arg0)) {
					try {
						JSONObject json = new JSONObject(arg0);
						if ("200".equals(json.getString("returnCode"))) {
							if (TextUtils.isEmpty(json.getString("log"))) {
								doStartCheck(result, context, "立即下载并安装最新版本?");
							} else {
								doStartCheck(result, context, json.getString("log"));
							}
						} else {
							doStartCheck(result, context, "立即下载并安装最新版本?");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {
				doStartCheck(result, context, "立即下载并安装最新版本?");
			}
		});
	}

	public void getVersionLog(final JSONArray result, final Context context, final boolean noticeFlag,
			String newVersion) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "getVersionLog");
		map.put("type", "0");
		map.put("version", newVersion);
		VolleyRequset.doPost(context, MConfig.mQuanZiApiUrl, "getVersionLog", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				if (!TextUtils.isEmpty(arg0)) {
					try {
						JSONObject json = new JSONObject(arg0);
						if ("200".equals(json.getString("returnCode"))) {
							if (TextUtils.isEmpty(json.getString("log"))) {
								doStartCheck(result, context, "立即下载并安装最新版本?");
							} else {
								doStartCheck(result, context, json.getString("log"));
							}
						} else {
							doCheck(result, context, noticeFlag, "立即下载并安装最新版本?");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {
				doCheck(result, context, noticeFlag, "立即下载并安装最新版本?");
			}
		});
	}

	public void doStartCheck(JSONArray result, final Context context, String log) {
		try {
			JSONObject object = result.getJSONObject(0);
			final String downloadUrl = object.getString("url");
			if (!TextUtils.isEmpty(downloadUrl)
					&& Integer.valueOf(object.getString("versioncode")) > getVersionCode(context)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("发现新版本").setMessage(log);
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ToastUtil.showToast(context, "后台下载新版本中,请注意流量...");
						// downLoadFile(context, downloadUrl, false);
						Intent localIntent = new Intent(context, UpdateVersionService.class);
						localIntent.putExtra("apkUrl", downloadUrl);
						context.startService(localIntent);
						dialog.dismiss();
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
		} catch (Exception e) {
		}
	}

	public void doCheck(JSONArray result, final Context context, final boolean noticeFlag, String log) {
		try {
			JSONObject object = result.getJSONObject(0);
			final String downloadUrl = object.getString("url");
			if (Integer.valueOf(object.getString("versioncode")) > getVersionCode(context)) {
				if (noticeFlag) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("发现新版本").setMessage(log);
					builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (TextUtils.isEmpty(downloadUrl)) {
								Toast.makeText(context, "未检测到新版本", Toast.LENGTH_SHORT).show();
								return;
							}
							// downLoadFile(context, downloadUrl, noticeFlag);
							Intent localIntent = new Intent(context, UpdateVersionService.class);
							localIntent.putExtra("apkUrl", downloadUrl);
							context.startService(localIntent);
							dialog.dismiss();
						}
					}).setNegativeButton("否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				} else {
					if (TextUtils.isEmpty(downloadUrl)) {
						return;
					}
					// downLoadFile(context, downloadUrl, noticeFlag);
					Intent localIntent = new Intent(context, UpdateVersionService.class);
					localIntent.putExtra("apkUrl", downloadUrl);
					context.startService(localIntent);
				}
			} else {
				if (noticeFlag) {
					Toast.makeText(context, "当前为最新版本", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			if (noticeFlag) {
				Toast.makeText(context, "未检测到新版本", Toast.LENGTH_SHORT).show();
			}
		}

	}

	public String getVersionName(Context context) {
		String version = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (Exception e) {
		}
		return version;
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void downLoadFile(Context context, String httpUrl, boolean noticeFlag) {
		if (noticeFlag) {
			Toast.makeText(context, "开始下载...", Toast.LENGTH_SHORT).show();
		}
		new AsyncTask<Object, Void, String>() {
			private Context context = null;
			private boolean noticeFlag = true;

			@Override
			protected String doInBackground(Object... params) {
				context = (Context) params[0];
				String httpUrl = (String) params[1];
				noticeFlag = (Boolean) params[2];
				final String fileName = "WebTV.apk";
				String path = Environment.getExternalStorageDirectory().getPath() + MConfig.CachePath;
				File tmpFile = new File(path);
				if (!tmpFile.exists()) {
					tmpFile.mkdirs();
				}
				File file = new File(path + "/" + fileName);
				try {
					URL url = new URL(httpUrl);
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						InputStream is = conn.getInputStream();
						FileOutputStream fos = new FileOutputStream(file);
						byte[] buf = new byte[256];
						conn.connect();
						double count = 0;
						if (conn.getResponseCode() >= 400) {
							fos.close();
							return "下载连接超时,请稍后重试";
						} else {
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
					} catch (IOException e) {
						return "下载出错,请稍后重试";
					}
				} catch (MalformedURLException e) {
					return "下载出错,请稍后重试";
				}
				if (file != null) {
					openFile(context, file);
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				if (!TextUtils.isEmpty(result)) {
					if (noticeFlag && context != null) {
						Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
					}
				}
			};
		}.execute(new Object[] { context, httpUrl, noticeFlag });
	}

	private void openFile(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
