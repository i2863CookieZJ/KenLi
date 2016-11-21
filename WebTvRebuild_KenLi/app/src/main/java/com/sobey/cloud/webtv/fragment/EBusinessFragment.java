package com.sobey.cloud.webtv.fragment;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.broadcast.ECShopBroadReciver;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EBusinessFragment extends BaseFragment implements ECShopBroadReciver.ReciveHandle {
    @GinInjectView(id = R.id.titlebar_name)
    private TextView title;
    @GinInjectView(id = R.id.webview)
    private WebView webView;
    @GinInjectView(id = R.id.top)
    private View topView;
    @GinInjectView(id = R.id.back_iv)
    private ImageView back_iv;
    @GinInjectView(id = R.id.back_rl)
    private RelativeLayout top_back;
    @GinInjectView(id = R.id.webPageLoadProgress)
    private LinearLayout webPageLoadProgress;
    private SharePopWindow mSharePopWindow;

    private boolean reload;
    private String jumpURL;
    private boolean hadShow;

    private String nowURL = "";
    private ECShopBroadReciver redirectReciverFilter;

    private String myUid = "";

    @Override
    public void onPause() {
        super.onPause();
        try {
            webView.onPause();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(redirectReciverFilter);
        redirectReciverFilter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
        // if (!PreferencesUtil.getLoggedUserId().equals(myUid)) {
        // myUid = PreferencesUtil.getLoggedUserId();
        // }
        needRedirectAfterLogin();

    }

    private void needRedirectAfterLogin() {
        myUid = PreferencesUtil.getLoggedUserId();
        if (reload) {
            webPageLoadProgress.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(jumpURL) && !TextUtils.isEmpty(myUid)) {
                if (jumpURL.contains("uid")) {
                    jumpURL = jumpURL + myUid;
                }
                webView.loadUrl(jumpURL);
            } else {
                webView.loadUrl(MConfig.ECSHOP + myUid);
            }
        }
        reload = false;
        jumpURL = null;
    }

    /**
     * 初始化布局Inflater
     */
    private LayoutInflater mInflater;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = LayoutInflater.from(getActivity());
        View v = getCacheView(mInflater, R.layout.new_e_business_frame);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isUseCache()) {
            return;
        }
        redirectReciverFilter = new ECShopBroadReciver();
        redirectReciverFilter.handle = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ECShopBroadReciver.REDIRECT_AFTERLOGIN);
        getActivity().registerReceiver(redirectReciverFilter, intentFilter);

        setupActivity();
    }

    private void setupActivity() {
        back_iv.setImageDrawable(getResources().getDrawable(R.drawable.ebusiness_home));
        top_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                webPageLoadProgress.setVisibility(View.VISIBLE);
                webView.loadUrl(MConfig.ECSHOP + myUid);
            }
        });
        webPageLoadProgress.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                webPageLoadProgress.setVisibility(View.GONE);
                webView.stopLoading();
                return false;
            }
        });
        title.setText("特卖");
        //onWebSetting(webView.getSettings());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "android");
        // webView.loadUrl(MConfig.DIANSHANG);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                int progress = newProgress;
                Log.d("zxd", "进度:" + newProgress);
                if (newProgress == 100)
                    webPageLoadProgress.setVisibility(View.GONE);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity());
                b2.setTitle("来自电商的消息").setMessage(message).setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                webPageLoadProgress.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webPageLoadProgress.setVisibility(View.GONE);
                String cookie = CookieManager.getInstance().getCookie(url);
                Log.d("zxd", "ecshop:" + cookie);
                if (!TextUtils.isEmpty(cookie)) {
                    SharedPreferences settings = getActivity().getSharedPreferences("iweb_shoppingcart", 0);
                    Editor editor = settings.edit();
                    editor.putString("iweb_shoppingcart", cookie.trim());
                    editor.commit();
                }
                nowURL = url;
                view.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
            }

//			 @Override
//			 public void onPageStarted(WebView view, String url, Bitmap
//			 favicon) {
////			 super.onPageStarted(view, url, favicon);
//			 webPageLoadProgress.setVisibility(View.VISIBLE);
//			 }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webPageLoadProgress.setVisibility(View.GONE);
                AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity());
                b2.setTitle("来自电商的消息:" + errorCode).setMessage(description).setPositiveButton("确定",
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                b2.setCancelable(false);
                b2.create();
                b2.show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                webPageLoadProgress.setVisibility(View.GONE);
                AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity());
                b2.setTitle("来自电商的消息").setMessage(error.toString()).setPositiveButton("确定",
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                b2.setCancelable(false);
                b2.create();
                b2.show();
            }
        });
        myUid = PreferencesUtil.getLoggedUserId();
        webPageLoadProgress.setVisibility(View.VISIBLE);
        String url = getActivity().getIntent().getStringExtra("url");
        if (null != url && !"".equals(url)) {
            webView.loadUrl(url + "&uid=" + myUid);
        } else {
            webView.loadUrl(MConfig.ECSHOP + myUid);
        }

        webView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
                        if (nowURL.contains(MConfig.ECSHOP)) {
                            return false;
                        } else if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            }
        });

        mSharePopWindow = new SharePopWindow(getActivity(), getActivity().getWindow().getDecorView());
    }

    @JavascriptInterface
    public void resize(final float height) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(getActivity(), height + "",
                // Toast.LENGTH_LONG).show();
                webView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                        (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }

    /**
     * 设置WebView的回调
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void onWebSetting(WebSettings webSettings) {
        if (webSettings == null) {
            return;
        }
        // webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 10);
        String appCachePath = getActivity().getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public boolean goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            Intent intent = new Intent(ECShopBroadReciver.ECSHOP_BROAD);
            intent.putExtra("msg", ECShopBroadReciver.SHOW_ACTION_BAR);
            getActivity().sendBroadcast(intent);
            return false;
        }
    }

    protected void canGoBack() {
        if (webView.canGoBack()) {
            Intent intent = new Intent(ECShopBroadReciver.ECSHOP_BROAD);
            intent.putExtra("msg", ECShopBroadReciver.HIDE_ACTION_BAR);
            // top_back.setVisibility(View.VISIBLE);
            if (null != getActivity()) {
                getActivity().sendBroadcast(intent);
            }
            hadShow = false;
        } else {
            // Intent intent=new Intent(ECShopBroadReciver.ECSHOP_BROAD);
            // intent.putExtra("msg", ECShopBroadReciver.SHOW_ACTION_BAR);
            // // top_back.setVisibility(View.INVISIBLE);
            // getActivity().sendBroadcast(intent);
            // hadShow=true;
        }
    }

    /**
     * 电商 js调用login
     */
    @JavascriptInterface
    public void login(String jumpURL) {
        if (TextUtils.isEmpty(myUid)) {
            reload = true;
            this.jumpURL = MConfig.ECSHOP;
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            reload = true;
            this.jumpURL = MConfig.ECSHOP + myUid;
            AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity());
            b2.setTitle("来自电商的消息").setMessage("亲,您长时间没关注我,请重新登录哟!").setPositiveButton("确定",
                    new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // SignUtil.loginOut(getActivity());
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
            b2.setCancelable(false);
            b2.create();
            b2.show();
        }
    }

    @Override
    public void handle(Intent intent) {
        String msg = intent.getStringExtra("msg");
        if (ECShopBroadReciver.REDIRECT_AFTERLOGIN.equals(msg)) {
            // needRedirectAfterLogin();
        }
    }

    // @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && getActivity() != null && null != webPageLoadProgress && null != webView) {
            myUid = PreferencesUtil.getLoggedUserId();
            webPageLoadProgress.setVisibility(View.VISIBLE);
            webView.loadUrl(MConfig.ECSHOP + myUid);
        }
    }

    /**
     * 分享
     */
    @JavascriptInterface
    public void share_products(final String shareUrl, final String shareTitle, final String shareContent,
                               final String shareImage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                View v = getActivity().getWindow().getDecorView();

                if (v == null) {
                    return;
                }

                mSharePopWindow.showShareWindow(shareUrl, shareTitle, shareContent, shareImage);
            }
        });
    }
}
