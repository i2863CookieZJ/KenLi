package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("JavascriptInterface")
public class MyOderWBActivity extends BaseActivity {

	@GinInjectView(id = R.id.titlebar_name)
	private TextView title;
	@GinInjectView(id = R.id.webview)
	private WebView webView;
	@GinInjectView(id = R.id.titlebar_person)
	private View loginView;
	@GinInjectView(id = R.id.top)
	private View topView;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout top_back;
	@GinInjectView(id = R.id.webPageLoadProgress)
	private LinearLayout webPageLoadProgress;
	private String orderID;
	private String url;

	@Override
	public int getContentView() {
		return R.layout.new_e_business_frame;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	private void init() {
		orderID = getIntent().getStringExtra("my_order_id");
		url = getIntent().getStringExtra("url");
		title.setText("我的订单");
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		String userId = PreferencesUtil.getLoggedUserId();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "android");
		// webView.loadUrl(MConfig.DIANSHANG);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				int progress = newProgress;
				Log.d("zxd", "进度:" + newProgress);
//				webPageLoadProgress.setProgress(progress);
				if (newProgress == 100)
					webPageLoadProgress.setVisibility(View.GONE);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webPageLoadProgress.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webPageLoadProgress.setVisibility(View.VISIBLE);
			}
		});
		webView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
						if (webView.canGoBack()) {
							webView.goBack();
							return true;
						}
						return false;
					}
				}
				return false;
			}
		});
		webView.loadUrl(url);
	}

}
