package com.higgses.griffin.volley.inputstreambox;

import com.higgses.griffin.volley.NetworkResponse;
import com.higgses.griffin.volley.Request;
import com.higgses.griffin.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.higgses.griffin.volley.Response;


public class InputStreamRequest extends StreamRequest<InputStream> {

	/**
	 * 
	 * @param method
	 *            :if method is zero,http request will use Get method
	 * @param url
	 * @param listener
	 * @param errorListener
	 */
	public InputStreamRequest(int method, String url,
			Response.Listener<InputStream> listener, Response.ErrorListener errorListener) {
		super(method == 0 ? Request.Method.GET : method, url, listener, errorListener);
	}

	public InputStreamRequest(String url, Response.ErrorListener errorListener,
			Response.Listener<InputStream> listener) {
		this(Request.Method.GET, url, listener, errorListener);
	}

	@Override
	protected Response<InputStream> parseNetworkResponse(
			NetworkResponse response) {
		InputStream result = null;
		result = new ByteArrayInputStream(response.data);
		return Response.success(result,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}
