package com.higgses.griffin.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.higgses.griffin.volley.AuthFailureError;
import com.higgses.griffin.volley.NetworkResponse;
import com.higgses.griffin.volley.Response;
import com.higgses.griffin.volley.Response.ErrorListener;
import com.higgses.griffin.volley.Response.Listener;

import android.util.Log;

public class JacksonArrayRequest<T> extends JacksonRequest<Object> {

	private static final String TAG = "JacksonObjectRequest";
	private Class<?> clazz;

	public JacksonArrayRequest(int method, String url, String requestBody,
			Listener<Object> listener, ErrorListener errorListener,
			Class<?> clazz) {
		super(method, url, requestBody, listener, errorListener);
		this.clazz = clazz;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
    {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json");
		return super.getHeaders();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<Object> parseNetworkResponse(NetworkResponse response) {
		Log.d(TAG, getUrl());
		String jsonString = null;
		Object object = null;
		try {
			jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			jsonString = new String(response.data);
		}
		Log.d(TAG, jsonString);
		if (jsonString != null) {
			object = JacksonUtils.getInstance(true).stringToObject(0,
					jsonString, clazz);
		}
		return Response.success(object,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}
