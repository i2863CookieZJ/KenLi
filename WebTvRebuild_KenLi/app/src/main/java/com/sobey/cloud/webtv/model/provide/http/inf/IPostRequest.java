package com.sobey.cloud.webtv.model.provide.http.inf;

import java.util.Map;

/**
 * 网络请求类
 * Created by higgses on 14-5-13.
 */
public interface IPostRequest
{
    /**
     * post请求
     *
     * @param url
     * @param param
     */
    public void post(String url, Map<String, String> param, IResponse response);

    /**
     * post请求
     *
     * @param url
     * @param jsonString
     */
    public void post(String url, String jsonString, Class<?> clazz, IResponse response);

    /**
     * post请求
     *
     * @param url
     * @param jsonString
     */
    public void post(String url, String jsonString, Class<?> clazz, Map<String, String> headers, IResponse response);

}
