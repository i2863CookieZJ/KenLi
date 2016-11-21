package com.sobey.cloud.webtv.volley;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;

public class VolleyRequset {

	public static RequestQueue requestQueue;

	public static void initQueue(Context context) {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		}
	}

	public static void doGet(Context context, String url, String tag, final VolleyListener listener) {
		initQueue(context);
		cancelQueue(tag);
		StringRequest strRequest = new StringRequest(Method.GET, url, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				listener.onFinish();
				listener.onSuccess(arg0);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				listener.onFinish();
				listener.onFail(arg0);
			}
		});
		strRequest.setTag(tag);
		requestQueue.add(strRequest);
		requestQueue.start();
	}

	public static void doPost(Context context, String url, String tag, final Map<String, String> map,
			final VolleyListener listener) {
		initQueue(context);
		cancelQueue(tag);
		StringRequest strRequset = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				listener.onFinish();
				listener.onSuccess(arg0);

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				listener.onFinish();
				listener.onFail(arg0);

			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		strRequset.setTag(tag);
		requestQueue.add(strRequset);
	}

	public static void cancelQueue(String tag) {
		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}

	}
}
