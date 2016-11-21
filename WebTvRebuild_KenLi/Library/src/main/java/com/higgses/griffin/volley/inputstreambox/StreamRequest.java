package com.higgses.griffin.volley.inputstreambox;

import com.higgses.griffin.volley.NetworkResponse;
import com.higgses.griffin.volley.Request;
import com.higgses.griffin.volley.VolleyLog;

import java.io.UnsupportedEncodingException;

import com.higgses.griffin.volley.Response;


public abstract class StreamRequest<T> extends Request<T>
{

    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format(
            "application/json; charset=%s", PROTOCOL_CHARSET);

    private final Response.Listener<T> mListener;
    private final String               mRequestBody;

    public StreamRequest(int method, String url, Response.Listener<T> listener,
                         Response.ErrorListener errorListener)
    {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mRequestBody = url;
    }

    public StreamRequest(String url, Response.ErrorListener errorListener,
                         Response.Listener<T> listener)
    {
        this(Method.GET, url, listener, errorListener);
	}

	@Override
	abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	/**
	 * @deprecated Use {@link #getBodyContentType()}.
	 */
	@Override
	public String getPostBodyContentType() {
		return getBodyContentType();
	}

	/**
	 * @deprecated Use {@link #getBody()}.
	 */
	@Override
	public byte[] getPostBody() {
		return getBody();
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			return mRequestBody == null ? null : mRequestBody
					.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog
					.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
							mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}
}
