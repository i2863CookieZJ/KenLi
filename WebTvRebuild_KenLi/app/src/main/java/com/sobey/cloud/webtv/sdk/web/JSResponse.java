package com.sobey.cloud.webtv.sdk.web;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * 封装的和JS的交互
 */
public class JSResponse
{
    /**
     * 成功
     */
    public final static String SUCCESS = "1";
    /**
     * 失败
     */
    public final static String ERROR   = "2";
    /**
     * 无效
     */
    public final static String INVALID = "0";

    @Expose
    private String state = INVALID;
    @Expose
    private String code  = "-1";
    @Expose
    private String data;
    private String responseMethod;

    public JSResponse()
    {

    }

    public JSResponse(String data)
    {
        this.data = data;
    }

    public String getResponseMethod()
    {
        return responseMethod;
    }

    public void setResponseMethod(String responseMethod)
    {
        this.responseMethod = responseMethod;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}