package com.sobey.cloud.webtv.views.web;

import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.log.GinLog;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.sdk.web.inf.ILoadUrlListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * 抽象的加载网页的界面。模板方法的母模板
 * Created by Carlton on 2014/8/12.
 */
public abstract class AbstractWebViewActivity<Model extends GinIModel> extends BaseActivity<Model> implements IWebViewClientListener, IWebPageJumpListener, ILoadUrlListener
{
    private static final String TAG        = "AbstractWebViewActivity";
    /**
     * 页面跳转计数
     */
    private              int    mJumpCount = 0;
    /**
     * WebView
     */
    private WebView                mWebView;
    /**
     * WebClient方法监听
     */
    private IWebViewClientListener mWebViewClientListener;
    /**
     * WebClient页面跳转事件监听
     */
    private IWebPageJumpListener   mWebPageJumpListener;

    @Override
    public void onDataFinish(Bundle savedInstanceState)
    {
        super.onDataFinish(savedInstanceState);
        mWebView = getWebView();
        if (mWebView == null)
        {
            return;
        }
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        WebViewClient webViewClient = getWebViewClient();
        if (webViewClient != null)
        {
            mWebView.setWebViewClient(webViewClient);
        }
        mWebViewClientListener = this;
        mWebPageJumpListener = this;
    }

    /**
     * 钩子方法，返回WebView
     *
     * @return
     */
    protected abstract WebView getWebView();

    /**
     * 点击返回的时候页面不能返回的时候的回调
     */
    protected abstract void onCannotBack();

    /**
     * 返回WebViewClient
     *
     * @return
     */
    protected WebViewClient getWebViewClient()
    {
        return new WebViewClient()
        {
            // 页面错误监听 重写错误页面
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                --mJumpCount;
                mWebViewClientListener.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                mWebViewClientListener.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return mWebViewClientListener.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                mWebViewClientListener.onPageStarted(view, url, favicon);
            }
        };
    }

    /**
     * 重新加载页面
     */
    @Override
    public void reload()
    {
        mWebView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mWebView.reload();
            }
        });
    }

    /**
     * 加载网页
     *
     * @param url
     */
    @Override
    public void loadWebUrl(final String url)
    {
        mWebView.post(new Runnable()
        {
            @Override
            public void run()
            {
                GinLog.i(TAG, "==> load web url:【" + url + "】");
                mWebView.loadUrl(url);
            }
        });
    }

    @Override
    public void loadJavaScript(final String url)
    {
        mWebView.post(new Runnable()
        {
            @Override
            public void run()
            {
                GinLog.i(TAG, "==> load javascript:【" + url + "】");
                mWebView.loadUrl(url);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (mJumpCount <= 0 || !mWebView.canGoBack())
        {
            onCannotBack();
        }
        else
        {
            setJumpCount(--mJumpCount);
            mWebView.goBack();
        }
    }

    /**
     * 设置跳转的页面数量
     *
     * @param count
     */
    protected void setJumpCount(int count)
    {
        if (count < 0)
        {
            count = 0;
        }
        mJumpCount = count;
        mWebPageJumpListener.onJump(count);
    }

    @Override
    public void onJump(int count)
    {
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        setJumpCount(++mJumpCount);
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {

    }
}
