package com.sobey.cloud.webtv.views.web;

import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.utils.GinUApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;


/**
 * 包含一个简单的WebView视图，提供了简单的使用WebView的一个方式。
 */
@SuppressLint("NewApi")
public abstract class BaseWebViewActivity<Model extends GinIModel> extends AbstractWebViewActivity<Model> {
    private static final String TAG = "BaseWebViewActivity";
//    /**
//     * 错误页面
//     */
    /**
     * WebSetting
     */
    private WebSettings mWebSettings;
    /**
     * 和JS交互的时候带结果的返回回调
     */
    private OnActivityResult onActivityResult;
    /**
     * WebView
     */
    private WebView mWebView;
    /**
     * 第一次进来的根网址
     */
    protected String mRootUrl;

    @Override
    public void onDataFinish(Bundle savedInstanceState) {
        super.onDataFinish(savedInstanceState);
        mWebView = getWebView();
        mWebSettings = mWebView.getSettings();
        onWebSetting(mWebSettings);
        mWebView.addJavascriptInterface(this, "fastJiaJs2Java");
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception ex) {

                }
            }
        });
    }

    @Override
    protected void onCannotBack() {
        finishActivity();
    }

    /**
     * 跳转
     */
    @JavascriptInterface
    public void doJump(int type) {
//        ToastUtil.show(this, "fastJiaJs2Java");
        if (type == 1) {

        }
    }

    /**
     * 分享
     */
    @JavascriptInterface
    public void doShare() {

    }

    /**
     * 登录
     */
    @JavascriptInterface
    public void doLogin() {

    }

    /**
     * 发布动态
     */
    @JavascriptInterface
    public void doPublish() {

    }

    /**
     * 设置结果返回回调
     *
     * @param onActivityResult
     */
    public void setOnActivityResult(OnActivityResult onActivityResult) {
        this.onActivityResult = onActivityResult;
    }

    /**
     * 设置WebView的回调
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void onWebSetting(WebSettings webSettings) {
        if (webSettings == null) {
            return;
        }
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 10);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        webSettings.setPluginState(PluginState.ON);
    }

    /**
     * 重新加载第一次进入的地址
     */
    public void reloadWebUrl() {
        setJumpCount(-1);
        loadWebUrl(mRootUrl);
    }

    /**
     * 加载网页
     *
     * @param url
     */
    @Override
    public void loadWebUrl(final String url) {
        String tmpUrl = url;

        super.loadWebUrl(tmpUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onActivityResult != null) {
            onActivityResult.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        if (TextUtils.isEmpty(mRootUrl)) {
            mRootUrl = url;
        }
        super.onPageFinished(view, url);
        dismissDialog();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")) {
            // 如果拦截到的url是电话，那就拨打电话
            GinLog.d(TAG, "拦截到的url：" + url);
            mWebView.goBack();
            String num = url.replace("-", "");
            GinUApp.call(getBaseContext(), num);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}