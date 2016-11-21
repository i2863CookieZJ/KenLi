package com.higgses.griffin.volley.toolbox;

import com.higgses.griffin.volley.Request;
import com.higgses.griffin.volley.VolleyLog;

import java.io.UnsupportedEncodingException;

import com.higgses.griffin.volley.NetworkResponse;
import com.higgses.griffin.volley.Response;


public abstract class JacksonRequest<T> extends Request<T>
{

    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = String.format(
            "application/json; charset=%s", PROTOCOL_CHARSET);

    private final Response.Listener<T> mListener;
    private final String               mRequestBody;

    public JacksonRequest(String url, String requestBody, Response.Listener<T> lister,
                          Response.ErrorListener errorListener)
    {
        this(Method.DEPRECATED_GET_OR_POST, url, requestBody, lister,
                errorListener);
    }

    public JacksonRequest(int method, String url, String requestBody,
                          Response.Listener<T> listener, Response.ErrorListener errorListener)
    {
        super(method, url, errorListener);
		this.mListener = listener;
		this.mRequestBody = requestBody;
	}

	@Override
	abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
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
