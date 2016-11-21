package com.higgses.griffin.volley.toolbox;

import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.volley.AuthFailureError;
import com.higgses.griffin.volley.DefaultRetryPolicy;
import com.higgses.griffin.volley.NetworkResponse;
import com.higgses.griffin.volley.RetryPolicy;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.volley.Response;
import com.higgses.griffin.volley.Response.ErrorListener;
import com.higgses.griffin.volley.Response.Listener;

public class JacksonObjectRequest extends JacksonRequest<Object>
{
    private static final String TAG = "JacksonObjectRequest";
    private Class<?> clazz;

    /**
     * Header验证
     */
    private Map<String, String> headers;

    public JacksonObjectRequest(int method, String url, String requestBody,
                                Class<?> clazz, Listener<Object> listener,
                                ErrorListener errorListener)
    {
        super(method, url, requestBody, listener, errorListener);
        setRetryPolicy(getRetryPolicy());

        GinLog.i(TAG, "不带Header:" + url);
        GinLog.i(TAG, "不带Header:" + requestBody);
        this.clazz = clazz;
    }

    public JacksonObjectRequest(int method, String url, String requestBody,
                                Class<?> clazz, Map<String, String> headers, Listener<Object> listener,
                                ErrorListener errorListener)
    {
        super(method, url, requestBody, listener, errorListener);

        GinLog.i(TAG, "带Header:" + url);
        GinLog.i(TAG, "带Header:" + requestBody);
        if (headers != null)
        {
            for (String string : headers.keySet())
            {
                GinLog.i(TAG, "Header:" + headers.get(string));
            }
        }
        this.clazz = clazz;
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        if (null != headers)
        {
            return headers;
        }
        else
        {
            return super.getHeaders();
        }
    }


    @Override
    protected Response<Object> parseNetworkResponse(NetworkResponse response)
    {
        String jsonString;
        Object object = null;
        try
        {
            jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        }
        catch (UnsupportedEncodingException e)
        {
            jsonString = new String(response.data);
        }
        if (!"".equals(jsonString))
        {
            if (null != clazz)
            {
                object = JacksonUtils.getInstance(true).stringToObject(0,
                        jsonString, clazz);
            }
            else
            {
                try
                {
                    object = new JSONObject(jsonString);
                }
                catch (JSONException e)
                {
                    object = null;
                }
            }
        }
        return Response.success(object,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public RetryPolicy getRetryPolicy()
    {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

}
