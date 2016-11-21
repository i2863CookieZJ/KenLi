package com.sobey.cloud.webtv.model.provide.http;

import java.util.Map;

import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.volley.AuthFailureError;
import com.higgses.griffin.volley.Request;
import com.higgses.griffin.volley.Response.ErrorListener;
import com.higgses.griffin.volley.Response.Listener;
import com.higgses.griffin.volley.VolleyError;
import com.higgses.griffin.volley.toolbox.JacksonObjectRequest;
import com.higgses.griffin.volley.toolbox.StringRequest;
import com.sobey.cloud.webtv.model.provide.ServerData;
import com.sobey.cloud.webtv.model.provide.http.inf.IPostRequest;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;

import android.text.TextUtils;

/**
 * 网络请求的类 Created by LiuBin on 14-5-13.
 */
public class PostRequest implements IPostRequest {
	Request<String> request;
	Request<Object> jackJsonObjectRequest;
	static String mUrl = "";
	private String TAG = "HixHttpRequest";

	public PostRequest() {
	}

	@Override
	public void post(final String url, final Map<String, String> param, final IResponse response) {
		mUrl = url;
		request = new StringRequest(Request.Method.POST, url, new Listener<String>() {
			@Override
			public void onResponse(String s) {
				GinLog.i(TAG, "服务器返回的数据【" + url + "】:\n" + s);
				if (response != null) {
					response.onResponse(s);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				GinLog.i(TAG, "请求服务器失败【" + url + "】");
				if (response != null) {
					response.onError(volleyError);
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				GinLog.i(TAG, "服务器请求参数【" + url + "】:" + GinLog.getMapString(param));
				return param;
			}
		};
	}

	@Override
	public void post(final String url, String jsonString, Class<?> clazz, final IResponse response) {
		mUrl = url;

		jackJsonObjectRequest = new JacksonObjectRequest(Request.Method.POST, url, jsonString, clazz,
				new Listener<Object>() {
					@Override
					public void onResponse(Object s) {
						GinLog.i(TAG, "服务器返回的数据【" + url + "】:\n" + s);
						if (response != null) {
							response.onResponse(s);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						GinLog.i(TAG, "请求服务器失败【" + url + "】");
						if (response != null) {
							response.onError(volleyError);
						}
					}
				});
	}

	@Override
	public void post(final String url, String jsonString, Class<?> clazz, Map<String, String> headers,
			final IResponse response) {
		mUrl = url;

		jackJsonObjectRequest = new JacksonObjectRequest(Request.Method.POST, url, jsonString, clazz, headers,
				new Listener<Object>() {
					@Override
					public void onResponse(Object s) {
						GinLog.i(TAG, "服务器返回的数据【" + url + "】:" + s);
						if (response != null) {
							response.onResponse(s);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						GinLog.i(TAG, "请求服务器失败【" + url + "】");
						if (response != null) {
							response.onError(volleyError);
						}
					}
				});
	}

	/**
	 * 如果网络错误，则从缓存获取数据
	 *
	 * @param url
	 * @param param
	 * @param response
	 */
	public void postFromCache(final String key, final String url, final Map<String, String> param,
			final IResponse<String> response) {
		mUrl = url;
		final Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String s) {
				GinLog.i(TAG, "服务器返回的数据【" + url + "】:\n" + s);
				if (response != null) {
					ServerData.cacheData(url + key, s);
					response.onResponse(s);
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				assert response != null;
				String data = ServerData.loadCacheData(url + key);
				GinLog.i(TAG, "请求服务器失败__从缓存中获取数据【" + url + "】" + key + ":\n" + data);
				if (TextUtils.isEmpty(data)) {
					response.onError(volleyError);
				} else {
					response.onResponse(data);
				}
			}
		};
		request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				GinLog.i(TAG, "服务器请求参数【" + url + "】:" + GinLog.getMapString(param));
				return param;
			}
		};
	}

	public Request<String> getRequest() {
		return request;
	}

	public Request<Object> getJsonObjectRequest() {
		return jackJsonObjectRequest;
	}

	/**
	 * 返回String类型的响应 Created by higgses on 14-5-13.
	 */
	public static class Response implements IResponse<String> {
		private static final String TAG = "Response";

		@Override
		public void onResponse(String data) {

		}

		@Override
		public void onError(Object error) {

		}
	}
}
