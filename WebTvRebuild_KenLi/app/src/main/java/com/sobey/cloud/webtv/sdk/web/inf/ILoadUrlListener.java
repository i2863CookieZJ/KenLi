package com.sobey.cloud.webtv.sdk.web.inf;

/**
 * 加载页面的接口
 * Created by Carlton on 2014/6/13.
 */
public interface ILoadUrlListener
{
    /**
     * 重新加载页面
     */
    public void reload();

    /**
     * 加载HTML5 javascript页面
     *
     * @param url
     */
    public void loadJavaScript(String url);

    /**
     * 加载正常WebView链接
     *
     * @param url
     */
    public void loadWebUrl(String url);
}
