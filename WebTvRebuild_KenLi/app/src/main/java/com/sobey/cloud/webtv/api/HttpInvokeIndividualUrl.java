package com.sobey.cloud.webtv.api;

import java.net.SocketTimeoutException;
import java.util.List;

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
import org.json.JSONObject;

import com.dylan.common.utils.CheckNetwork;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class HttpInvokeIndividualUrl {

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

	public static void asyncInvokeGetJsonObject(String url, Context context, OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokeGetJsonObject(url, MConfig.TIMEOUT_DEFAULT, context, jsonObjectResultListener);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context, OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokeGetJsonObject(url, timeout, context, jsonObjectResultListener, null, null);
	}

	public static void asyncInvokeGetJsonObject(String url, int timeout, Context context, OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(url, null, null, false, timeout, context, jsonObjectResultListener, preExecuteListener, postExecuteListener);
	}

	public static void asyncInvokePostJsonObject(String webUrl, List<NameValuePair> param, Context context, OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(webUrl, param, MConfig.TIMEOUT_DEFAULT, context, jsonObjectResultListener);
	}

	public static void asyncInvokePostJsonObject(String webUrl, List<NameValuePair> param, int timeout, Context context, OnJsonObjectResultListener jsonObjectResultListener) {
		asyncInvokePostJsonObject(webUrl, param, timeout, context, jsonObjectResultListener, null, null);
	}

	public static void asyncInvokePostJsonObject(String webUrl, List<NameValuePair> param, int timeout, Context context, final OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonObject(null, webUrl, param, true, timeout, context, jsonObjectResultListener, preExecuteListener, postExecuteListener);
	}

	public static JSONObject invokeGetJsonObject(String url) {
		return invokeGetJsonObject(url, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONObject invokeGetJsonObject(String url, int timeout) {
		return invokeDoJsonObject(url, null, null, false, timeout, false);
	}

	public static JSONObject invokePostJsonObject(String webUrl, List<NameValuePair> param) {
		return invokePostJsonObject(webUrl, param, MConfig.TIMEOUT_DEFAULT);
	}

	public static JSONObject invokePostJsonObject(String webUrl, List<NameValuePair> param, int timeout) {
		return invokeDoJsonObject(null, webUrl, param, true, timeout, false);
	}

	private static void asyncInvokeDoJsonObject(String url, String webUrl, List<NameValuePair> param, boolean postFlag, int timeout, Context context, final OnJsonObjectResultListener jsonObjectResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if(checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, JSONObject>() {

				@Override
				protected void onPreExecute() {
					if(mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected JSONObject doInBackground(Object... params) {
					return invokeDoJsonObject((String) params[0], (String) params[1], (List<NameValuePair>) params[2], (Boolean) params[3], (Integer) params[4], true);
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					if(result == null) {
						if(jsonObjectResultListener != null) {
							jsonObjectResultListener.onNG(mException);
						}
					} else {
						jsonObjectResultListener.onOK(result);
					}
					if(mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, webUrl, param, postFlag, Integer.valueOf(timeout) });
		}
	}

	@SuppressWarnings("unused")
	private static JSONObject invokeDoJsonObject(String url, String webUrl, List<NameValuePair> param, boolean postFlag, int timeout, boolean asyncFlag) {
		if(postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if(!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if(postFlag) {
				if(DEBUG_MODE)
					Log.i(TAG, "request: " + webUrl + param.toString());
				HttpPost request = null;
				request = new HttpPost(webUrl);
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONObject result = new JSONObject(retSrc);
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if(url.contains("http://")) {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + webUrl + "?" + url);
					request = new HttpGet(webUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONObject result = new JSONObject(retSrc);
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

	public static void asyncInvokeGetJsonArray(String url, Context context, OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokeGetJsonArray(url, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokeGetJsonArray(String url, int timeout, Context context, OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokeGetJsonArray(url, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokeGetJsonArray(String url, int timeout, Context context, OnJsonArrayResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonArray(url, null, false, timeout, context, jsonArrayResultListener, preExecuteListener, postExecuteListener);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, Context context, OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokePostJsonArray(param, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, int timeout, Context context, OnJsonArrayResultListener jsonArrayResultListener) {
		asyncInvokePostJsonArray(param, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokePostJsonArray(List<NameValuePair> param, int timeout, Context context, final OnJsonArrayResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoJsonArray(null, param, true, timeout, context, jsonArrayResultListener, preExecuteListener, postExecuteListener);
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

	private static void asyncInvokeDoJsonArray(String url, List<NameValuePair> param, boolean postFlag, int timeout, Context context, final OnJsonArrayResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if(checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, JSONArray>() {

				@Override
				protected void onPreExecute() {
					if(mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected JSONArray doInBackground(Object... params) {
					return invokeDoJsonArray((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2], (Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(JSONArray result) {
					if(result == null) {
						if(jsonArrayResultListener != null) {
							jsonArrayResultListener.onNG(mException);
						}
					} else {
						jsonArrayResultListener.onOK(result);
					}
					if(mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		}
	}

	@SuppressWarnings("unused")
	private static JSONArray invokeDoJsonArray(String url, List<NameValuePair> param, boolean postFlag, int timeout, boolean asyncFlag) {
		if(postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if(!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if(postFlag) {
				if(DEBUG_MODE)
					Log.i(TAG, "request: " + MConfig.mServerUrl + param.toString());
				HttpPost request = null;
				request = new HttpPost(MConfig.mServerUrl);
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONArray result = new JSONArray(retSrc);
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if(url.contains("http://")) {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				JSONArray result = new JSONArray(retSrc);
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
	}

	public static void asyncInvokeGetString(String url, Context context, OnStringResultListener jsonArrayResultListener) {
		asyncInvokeGetString(url, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokeGetString(String url, int timeout, Context context, OnStringResultListener jsonArrayResultListener) {
		asyncInvokeGetString(url, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokeGetString(String url, int timeout, Context context, OnStringResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoString(url, null, false, timeout, context, jsonArrayResultListener, preExecuteListener, postExecuteListener);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, Context context, OnStringResultListener jsonArrayResultListener) {
		asyncInvokePostString(param, MConfig.TIMEOUT_DEFAULT, context, jsonArrayResultListener);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, int timeout, Context context, OnStringResultListener jsonArrayResultListener) {
		asyncInvokePostString(param, timeout, context, jsonArrayResultListener, null, null);
	}

	public static void asyncInvokePostString(List<NameValuePair> param, int timeout, Context context, final OnStringResultListener jsonArrayResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		asyncInvokeDoString(null, param, true, timeout, context, jsonArrayResultListener, preExecuteListener, postExecuteListener);
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

	private static void asyncInvokeDoString(String url, List<NameValuePair> param, boolean postFlag, int timeout, Context context, final OnStringResultListener stringResultListener, OnPreExecuteListener preExecuteListener, OnPostExecuteListener postExecuteListener) {
		mContext = context;
		mPreExecuteListener = preExecuteListener;
		mPostExecuteListener = postExecuteListener;
		CheckNetwork checkNetwork = new CheckNetwork(mContext);
		if(checkNetwork.getNetworkState(true)) {
			new AsyncTask<Object, Integer, String>() {

				@Override
				protected void onPreExecute() {
					if(mPreExecuteListener != null) {
						mPreExecuteListener.onPreExecute();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				protected String doInBackground(Object... params) {
					return invokeDoString((String) params[0], (List<NameValuePair>) params[1], (Boolean) params[2], (Integer) params[3], true);
				}

				@Override
				protected void onPostExecute(String result) {
					if(result == null) {
						if(stringResultListener != null) {
							stringResultListener.onNG(mException);
						}
					} else {
						stringResultListener.onOK(result);
					}
					if(mPostExecuteListener != null) {
						mPostExecuteListener.onPostExecute();
					}
				}
			}.execute(new Object[] { url, param, postFlag, Integer.valueOf(timeout) });
		}
	}

	@SuppressWarnings("unused")
	private static String invokeDoString(String url, List<NameValuePair> param, boolean postFlag, int timeout, boolean asyncFlag) {
		if(postFlag && param == null) {
			mException = "Use POST method, param cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		if(!postFlag && url == null) {
			mException = "Use GET method, url cannot be null.";
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if(postFlag) {
				if(DEBUG_MODE)
					Log.i(TAG, "request: " + MConfig.mServerUrl + param.toString());
				HttpPost request = null;
				request = new HttpPost(MConfig.mServerUrl);
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				request.setEntity(entity);
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			} else {
				HttpGet request = null;
				if(url.contains("http://")) {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + url);
					request = new HttpGet(url);
				} else {
					if(DEBUG_MODE)
						Log.i(TAG, "request:" + MConfig.mServerUrl + "?" + url);
					request = new HttpGet(MConfig.mServerUrl + "?" + url);
				}
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
				HttpConnectionParams.setSoTimeout(httpParams, timeout);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(request);
				if(httpResponse == null) {
					throw new Exception("HttpResponse is null.");
				}
				if(httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
				}
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				String result = retSrc;
				if(DEBUG_MODE)
					Log.i(TAG, "response: " + result.toString());
				if(!asyncFlag) {
					initConfigure();
				}
				return result;
			}
		} catch (ConnectTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Connect timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (SocketTimeoutException e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Socket timeout.";
			}
			if(!asyncFlag) {
				initConfigure();
			}
			return null;
		} catch (Exception e) {
			if(e != null) {
				mException = e.toString();
			} else {
				mException = "Null exception.";
			}
			if(!asyncFlag) {
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

}
