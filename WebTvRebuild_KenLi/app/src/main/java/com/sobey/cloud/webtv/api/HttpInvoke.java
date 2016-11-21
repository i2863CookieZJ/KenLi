package com.sobey.cloud.webtv.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.common.utils.CheckNetwork;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

public class HttpInvoke {

	private static final String TAG = "HttpInvoke";
	private static boolean DEBUG_MODE = true;
	private static OnPreExecuteListener mPreExecuteListener = null;
	private static OnPostExecuteListener mPostExecuteListener = null;
	private static Context mContext = null;
	private static String mException = null;

	public interface OnJsonObjectResultListener {
		void onOK(JSONObject result);

		void onNG(String reason);

		void onCancel();
	}

	public interface OnJsonArrayResultListener {
		void onOK(JSONArray result);

		void onNG(String reason);

		void onCancel();
	}

	public interface OnStringResultListener {
		void onOK(String result);

		void onNG(String reason);

		void onCancel();
	}

	public interface OnPreExecuteListener {
		void onPreExecute();
	}

	public interface OnPostExecuteListener {
		void onPostExecute();
	}

	public static void asyncInvokeGetJsonObject(String url, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokeGetJsonObject(url, MConfig.TIMEOUT_DEFAULT, context, jsonObjectResultListener);
	}

	public static void asyncInvokeGetJsonObject(String url, Context context, OnJsonArrayResultListener listener) {
		asyncInvokeGetJsonObject(url, MConfig.TIMEOUT_DEFAULT, context, listener);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context,
			OnJsonArrayResultListener listener) {
		asyncInvokeGetJsonObject(url, timeout, context, listener, null, null);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context,
			OnJsonArrayResultListener listener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(url, null, false, timeout, context, listener, preExecuteListener, postExecuteListener);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokeGetJsonObject(url, timeout, context, jsonObjectResultListener, null, null);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context,
			OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(url, null, false, timeout, context, jsonObjectResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static void asyncInvokePostJsonObject(List<NameValuePair> param, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(param, MConfig.TIMEOUT_DEFAULT, context, jsonObjectResultListener);
	}

	public static void asyncInvokePostJsonObject(String url, List<NameValuePair> param, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(url, param, MConfig.TIMEOUT_DEFAULT, context, jsonObjectResultListener);
	}

	public static void asyncInvokePostJsonObject(List<NameValuePair> param, int timeout, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(param, timeout, context, jsonObjectResultListener, null, null);
	}

	public static void asyncInvokePostJsonObject(String url, List<NameValuePair> param, int timeout, Context context,
			OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(url, param, timeout, context, jsonObjectResultListener, null, null);
	}

	public static void asyncInvokePostJsonObject(List<NameValuePair> param, int timeout, Context context,
			final OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(null, param, true, timeout, context, jsonObjectResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static void asyncInvokePostJsonObject(String url, List<NameValuePair> param, int timeout, Context context,
			final OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(url, param, true, timeout, context, jsonObjectResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static JSONObject invokeGetJsonObject(String url) {
		return invokeGetJsonObject(url, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONObject invokeGetJsonObject(String url, int timeout) {
		return invokeDoJsonObject(url, null, false, timeout, false);
	}

	public static JSONObject invokePostJsonObject(List<NameValuePair> param) {
		return invokePostJsonObject(param, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONObject invokePostJsonObject(String url, List<NameValuePair> param) {
		return invokePostJsonObject(url, param, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONObject invokePostJsonObject(List<NameValuePair> param, int timeout) {
		return invokeDoJsonObject(null, param, true, timeout, false);
	}

	public static JSONObject invokePostJsonObject(String url, List<NameValuePair> param, int timeout) {
		return invokeDoJsonObject(url, param, true, timeout, false);
	}

	private static void asyncInvokeDoJsonObject(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			Context context, final OnJsonArrayResultListener listener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if (checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, JSONObject>() {

				@Override
				protected void onPreExecute() {
					if (mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected JSONObject doInBackground(Object... params) {
					return invokeDoJsonObject((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2],
							(Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					if (result == null) {
						if (listener != null) {
							listener.onNG(mException);
						}
					} else {
						try {
							listener.onOK(new JSONArray("[" + result.toString() + "]"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					if (mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		} else {
			if (listener != null) {
				listener.onNG(mException);
			}
		}
	}

	private static void asyncInvokeDoJsonObject(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			Context context, final OnJsonObjectResultListener jsonObjectResultListener,
			OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if (checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, JSONObject>() {

				@Override
				protected void onPreExecute() {
					if (mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected JSONObject doInBackground(Object... params) {
					return invokeDoJsonObject((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2],
							(Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					if (result == null) {
						if (jsonObjectResultListener != null) {
							jsonObjectResultListener.onNG(mException);
						}
					} else {
						jsonObjectResultListener.onOK(result);
					}
					if (mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		} else {
			if (jsonObjectResultListener != null) {
				jsonObjectResultListener.onNG(mException);
			}
		}
	}

	// TODO Post
	private static JSONObject invokeDoJsonObject(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			boolean asyncFlag) {
		String mUrl;
		if (postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if (!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if (postFlag) {

				if (TextUtils.isEmpty(url)) {
					mUrl = MConfig.mServerUrl;
				} else {
					mUrl = url;
				}
				if (DEBUG_MODE)
					Log.i(TAG, "request: " + mUrl + param.toString());

				HttpPost request = null;
				request = new HttpPost(mUrl);
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONObject result = new JSONObject(retSrc);
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if (url.contains("http://")) {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONObject result = new JSONObject(retSrc);
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

	public static void asyncInvokeGetJsonArray(String url, Context context,
			OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokeGetJsonArray(url, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokeGetJsonArray(String url, int timeout, Context context,
			OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokeGetJsonArray(url, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokeGetJsonArray(String url, int timeout, Context context,
			OnJsonArrayResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonArray(url, null, false, timeout, context, jsonArrayResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, Context context,
			OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokePostJsonArray(param, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, int timeout, Context context,
			OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokePostJsonArray(param, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, int timeout, Context context,
			final OnJsonArrayResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonArray(null, param, true, timeout, context, jsonArrayResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static JSONArray invokeGetJsonArray(String url) {
		return invokeGetJsonArray(url, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONArray invokeGetJsonArray(String url, int timeout) {
		return invokeDoJsonArray(url, null, false, timeout, false);
	}

	public static JSONArray invokePostJsonArray(List<NameValuePair> param) {
		return invokePostJsonArray(param, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONArray invokePostJsonArray(List<NameValuePair> param, int timeout) {
		return invokeDoJsonArray(null, param, true, timeout, false);
	}

	private static void asyncInvokeDoJsonArray(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			Context context, final OnJsonArrayResultListener jsonArrayResultListener,
			OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if (checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, JSONArray>() {

				@Override
				protected void onPreExecute() {
					if (mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected JSONArray doInBackground(Object... params) {
					return invokeDoJsonArray((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2],
							(Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(JSONArray result) {
					if (result == null) {
						if (jsonArrayResultListener != null) {
							jsonArrayResultListener.onNG(mException);
						}
					} else {
						jsonArrayResultListener.onOK(result);
					}
					if (mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		}
	}

	@SuppressWarnings("unused")
	private static JSONArray invokeDoJsonArray(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			boolean asyncFlag) {
//		String tag = param.get(0).getValue();
		String tag = param!=null&&param.size()>0?param.get(0).getValue():"";
		if (tag.equals("upload")) {// 如果是上传的post，需要更改url
			param.remove(0);
		}
		if (postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if (!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			// TODO api要过这里
			if (postFlag) {
				if (DEBUG_MODE)
					Log.i(TAG, "request: " + MConfig.mServerUrl + param.toString());
				HttpPost request = null;
				if (tag.equals("upload")) {
					request = new HttpPost("http://uc.sobeycache.com/interface");// 如果是上传的post，需要更改url
				} else {
					request = new HttpPost(MConfig.mServerUrl);
				}
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				if (!retSrc.contains("[")) {
					retSrc = "[" + retSrc + "]";
				}
				JSONArray result = new JSONArray(retSrc);
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if (url.contains("http://")) {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONArray result = new JSONArray(retSrc);
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

	public static void asyncInvokeGetString(String url, Context context,
			OnStringResultListener jsonArrayResultListener) {
		asyncInvokeGetString(url, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokeGetString(String url, int timeout, Context context,
			OnStringResultListener jsonArrayResultListener) {
		asyncInvokeGetString(url, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokeGetString(String url, int timeout, Context context,
			OnStringResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoString(url, null, false, timeout, context, jsonArrayResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, Context context,
			OnStringResultListener jsonArrayResultListener) {
		asyncInvokePostString(param, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, int timeout, Context context,
			OnStringResultListener jsonArrayResultListener) {
		asyncInvokePostString(param, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, int timeout, Context context,
			final OnStringResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoString(null, param, true, timeout, context, jsonArrayResultListener, preExecuteListener,
				postExecuteListener);
	}

	public static String invokeGetString(String url) {
		return invokeGetString(url, MConfig.TIMEOUT_DEFAULT);
	}

	public static String invokeGetString(String url, int timeout) {
		return invokeDoString(url, null, false, timeout, false);
	}

	public static String invokePostString(List<NameValuePair> param) {
		return invokePostString(param, MConfig.TIMEOUT_DEFAULT);
	}

	public static String invokePostString(List<NameValuePair> param, int timeout) {
		return invokeDoString(null, param, true, timeout, false);
	}

	private static void asyncInvokeDoString(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			Context context, final OnStringResultListener stringResultListener, OnPreExecuteListener preExecuteListener,
			OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if (checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, String>() {

				@Override
				protected void onPreExecute() {
					if (mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected String doInBackground(Object... params) {
					return invokeDoString((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2],
							(Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(String result) {
					if (result == null) {
						if (stringResultListener != null) {
							stringResultListener.onNG(mException);
						}
					} else {
						stringResultListener.onOK(result);
					}
					if (mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		}
	}

	@SuppressWarnings("unused")
	private static String invokeDoString(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			boolean asyncFlag) {
		if (postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if (!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if (postFlag) {
				if (DEBUG_MODE)
					Log.i(TAG, "request: " + MConfig.mServerUrl + param.toString());
				HttpPost request = null;
				request = new HttpPost(MConfig.mServerUrl);
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if (url.contains("http://")) {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

	private static void initConfigure() {
		mPreExecuteListener = null;
		mPostExecuteListener = null;
		mContext = null;
		mException = null;
	}

	/**
	 * 提交数据到服务器 并且返回取出的值
	 * 
	 * @param path
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> sendGETRequest(String path, String params) throws Exception {
		Map<String, String> tempMap = new HashMap<String, String>();
		boolean b = false;
		Log.v("url", path + params);
		// params=URLEncoder.encode(params,"UTF-8");
		HttpURLConnection conn = (HttpURLConnection) new URL(path + params).openConnection();
		// conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "text/xml");
		conn.setRequestProperty("Charset", "UTF-8");
		// conn.setRequestProperty("Cookie", Constant.youxinCookie);
		conn.setConnectTimeout(20000);
		// 如果请求响应码是200，则表示成功
		if (conn.getResponseCode() == 200) {
			System.out.println(conn.getResponseCode());
			// 获得服务器响应的数据
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			// 数据
			String retData = null;
			String responseData = "";
			while ((retData = in.readLine()) != null) {
				responseData += retData;
			}
			in.close();
			System.out.println(responseData.toString());
			tempMap.put("list", responseData.toString());
			return tempMap;
		}
		// tempMap.put("innerEorr", "服务器响应超时");
		return tempMap;
	}

	/**
	 * get请求 带请求头的
	 * 
	 * @param url
	 * @param header
	 * @return
	 */
	public static String invokeGetString(String url, Map<String, String> header) {
		return invokeDoString(url, null, false, MConfig.TIMEOUT_DEFAULT, false, header);
	}

	protected static String invokeDoString(String url, List<NameValuePair> param, boolean postFlag, int timeout,
			boolean asyncFlag, Map<String, String> header) {
		if (postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if (!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if (postFlag) {
				if (DEBUG_MODE)
					Log.i(TAG, "request: " + MConfig.mServerUrl + param.toString());
				HttpPost request = null;
				request = new HttpPost(MConfig.mServerUrl);
				if (header != null) {
					Iterator<Map.Entry<String, String>> iter = header.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						String key = (String) entry.getKey();
						String val = (String) entry.getValue();
						Log.i(TAG, "request header key:" + key + "  header value:" + val);
						request.setHeader(key, val);
					}
				}
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if (url.contains("http://")) {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if (DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				if (header != null) {
					Iterator<Map.Entry<String, String>> iter = header.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = iter.next();
						String key = (String) entry.getKey();
						String val = (String) entry.getValue();
						Log.i(TAG, "request header key:" + key + "  header value:" + val);
						request.setHeader(key, val);
					}
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if (httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if (DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if (!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if (e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if (!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

}
