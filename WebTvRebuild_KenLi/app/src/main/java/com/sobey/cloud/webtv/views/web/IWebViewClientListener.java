package com.sobey.cloud.webtv.views.web;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * 监听WebViewClient中的回调方法
 */
public interface IWebViewClientListener
{

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    void onPageFinished(WebView view, String url);

    boolean shouldOverrideUrlLoading(WebView view, String url);

    void onPageStarted(WebView view, String url, Bitmap favicon);
}