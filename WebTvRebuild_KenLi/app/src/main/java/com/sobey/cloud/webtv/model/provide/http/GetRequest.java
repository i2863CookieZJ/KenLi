package com.sobey.cloud.webtv.model.provide.http;

import java.util.Map;

import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.volley.AuthFailureError;
import com.higgses.griffin.volley.Request;
import com.higgses.griffin.volley.Response.ErrorListener;
import com.higgses.griffin.volley.Response.Listener;
import com.higgses.griffin.volley.VolleyError;
import com.higgses.griffin.volley.toolbox.StringRequest;
import com.sobey.cloud.webtv.model.provide.http.inf.IGetRequest;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;

import android.util.Log;

/**
 * 网络请求的类 Created by LiuBin on 14-5-13.
 */
public class GetRequest implements IGetRequest {
	Request<String> request;
	static String mUrl = "";
	private String TAG = "HixHttpRequest";

	public GetRequest() {
	}

	@Override
	public void get(final String url, final IResponse response) {
		mUrl = url;
		GinLog.i(TAG, url);
		request = new StringRequest(Request.Method.GET, url, new Listener<String>() {
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

		};
	}

	@Override
	public void get(final String url, final Map<String, String> headers, final IResponse response) {
		mUrl = url;
		GinLog.i(TAG, url);
		request = new StringRequest(Request.Method.GET, url, new Listener<String>() {
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
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (headers != null) {
					for (String string : headers.keySet()) {
						Log.i(TAG, string + ":" + headers.get(string));
					}
					return headers;
				} else {
					return super.getHeaders();
				}
			}
		};
	}

	public Request<String> getRequest() {
		return request;
	}

	/**
	 * 返回String类型的响应 Created by LiuBin on 14-5-13.
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
