package com.sobey.cloud.webtv.model.provide.http.inf;

import java.util.Map;

/**
 * 网络请求类
 * Created by LiuBin on 14-5-13.
 */
public interface IGetRequest
{
    /**
     * Get请求
     *
     * @param url
     */
    public void get(String url, IResponse response);

    /**
     * Get请求
     *
     * @param url
     */
    public void get(String url, Map<String, String> headers, IResponse response);

}
