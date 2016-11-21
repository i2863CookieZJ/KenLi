package com.sobey.cloud.webtv.sdk.web;

/**
 * 封装的请求
 */
public class JSRequest
{
    /**
     * 请求的数据
     */
    private String  data;
    private Request request;

    public JSRequest()
    {
        request = new Request();
    }

    public Request getRequest()
    {
        return request;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    public String getOnSuccess()
    {
        return request.getOnSuccess();
    }

    public void setOnSuccess(String string)
    {
        request.setOnSuccess(string);
    }

    public String getOnError()
    {
        return request.getOnError();
    }

    public void setOnError(String string)
    {
        request.setOnError(string);
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    /**
     * 请求的固定格式
     */
    public static class Request
    {
        private String onSuccess;
        private String onError;
        private String appKey;
        private String secretKey;

        public String getOnSuccess()
        {
            return onSuccess;
        }

        public void setOnSuccess(String onSuccess)
        {
            this.onSuccess = onSuccess;
        }

        public String getOnError()
        {
            return onError;
        }

        public void setOnError(String onError)
        {
            this.onError = onError;
        }

        public String getAppKey()
        {
            return appKey;
        }

        public void setAppKey(String appKey)
        {
            this.appKey = appKey;
        }

        public String getSecretKey()
        {
            return secretKey;
        }

        public void setSecretKey(String secretKey)
        {
            this.secretKey = secretKey;
        }
    }
}