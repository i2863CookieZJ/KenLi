package com.sobey.cloud.webtv.model.provide.http;

import java.util.Map;

import com.higgses.griffin.volley.RequestQueue;
import com.higgses.griffin.volley.toolbox.JacksonObjectRequest;
import com.higgses.griffin.volley.toolbox.StringRequest;
import com.higgses.griffin.volley.toolbox.Volley;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;

import android.content.Context;

/**
 * 网络请求工具类 Created by higgses on 14-5-13.
 */
public class HttpUtils {
	public static final String TAG = "HttpUtils";
	private static RequestQueue mRequestQueue;

	private HttpUtils() {
	}

	public static void post(Context context, Object tag, String url, Map<String, String> param, IResponse response) {
		PostRequest request = new PostRequest();
		request.post(url, param, response);
		StringRequest stringRequest = (StringRequest) request.getRequest();
		stringRequest.setTag(tag);
		getRequestQueue(context).add(stringRequest);
	}

	public static void get(Context context, Object tag, String url, IResponse response) {
		GetRequest request = new GetRequest();
		request.get(url, response);
		StringRequest stringRequest = (StringRequest) request.getRequest();
		stringRequest.setTag(tag);
		getRequestQueue(context).add(stringRequest);
	}

	public static void post(Context context, Object tag, String url, String jsonString, Class<?> clazz,
			IResponse response) {
		post(context, tag, url, jsonString, clazz, null, response);
	}

	public static void post(Context context, Object tag, String url, String jsonString, Class<?> clazz,
			Map<String, String> headers, IResponse response) {
		PostRequest request = new PostRequest();
		request.post(url, jsonString, clazz, headers, response);
		JacksonObjectRequest jsonObjectRequest = (JacksonObjectRequest) request.getJsonObjectRequest();
		// jsonObjectRequest.setRetryPolicy(new RetryPolicy()
		// {
		// @Override
		// public int getCurrentTimeout()
		// {
		// return 1000;
		// }
		//
		// @Override
		// public int getCurrentRetryCount()
		// {
		// return 1;
		// }
		//
		// @Override
		// public void retry(VolleyError error) throws VolleyError
		// {
		//
		// }
		// });
		getRequestQueue(context).add(jsonObjectRequest);
	}

	public static void get(Context context, Object tag, String url, Map<String, String> headers, IResponse response) {
		GetRequest request = new GetRequest();
		request.get(url, headers, response);
		StringRequest stringRequest = (StringRequest) request.getRequest();
		stringRequest.setTag(tag);
		getRequestQueue(context).add(stringRequest);
	}

	public static void post(Context context, String url, Map<String, String> param, IResponse response) {
		post(context, TAG, url, param, response);
	}

	public static void get(Context context, String url, IResponse response) {
		get(context, TAG, url, null, response);
	}

	public static void get(Context context, String tag, String url, IResponse response) {
		get(context, tag, url, null, response);
	}

	public static void post(Context context, String url, String jsonString, IResponse response) {
		post(context, TAG, url, jsonString, null, response);
	}

	public static void post(Context context, String tag, String url, String jsonString, IResponse response) {
		post(context, tag, url, jsonString, null, response);
	}

	public static void post(Context context, String url, String jsonString, Class<?> clazz, IResponse response) {
		post(context, TAG, url, jsonString, clazz, response);
	}

	public static void post(Context context, String url, String jsonString, Map<String, String> headers,
			IResponse response) {
		post(context, TAG, url, jsonString, null, headers, response);
	}

	public static void post(Context context, String url, String jsonString, Class<?> clazz, Map<String, String> headers,
			IResponse response) {
		post(context, TAG, url, jsonString, clazz, headers, response);
	}

	public static void get(Context context, String url, Map<String, String> headers, IResponse response) {
		get(context, TAG, url, headers, response);
	}

	public static void postFromCache(Context context, Object tag, String key, String url, Map<String, String> param,
			IResponse response) {
		PostRequest request = new PostRequest();
		request.postFromCache(key, url, param, response);
		StringRequest stringRequest = (StringRequest) request.getRequest();
		stringRequest.setTag(tag);
		getRequestQueue(context).add(stringRequest);
	}

	public static void postFromCache(Context context, String key, String url, Map<String, String> param,
			IResponse response) {
		postFromCache(context, TAG, key, url, param, response);
	}

	public static void cancelAll(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public static void cancelAll() {
		cancelAll(TAG);
	}

	private static RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			synchronized (HttpUtils.class) {
				if (mRequestQueue == null) {
					mRequestQueue = Volley.newRequestQueue(context);
				}
			}
		}
		return mRequestQueue;
	}
}
