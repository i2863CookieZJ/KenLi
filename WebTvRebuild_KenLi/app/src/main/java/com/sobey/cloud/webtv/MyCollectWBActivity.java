package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyCollectWBActivity extends BaseActivity {

	@GinInjectView(id = R.id.titlebar_name)
	private TextView title;
	@GinInjectView(id = R.id.webview)
	private WebView webView;
	@GinInjectView(id = R.id.titlebar_person)
	private View loginView;
	@GinInjectView(id = R.id.top)
	private View topView;
	@GinInjectView(id = R.id.top_back)
	private ImageButton top_back;
	@GinInjectView(id = R.id.webPageLoadProgress)
	private ProgressBar webPageLoadProgress;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.new_e_business_frame;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		init();
	}

	@SuppressLint("JavascriptInterface")
	private void init() {
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
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
				webPageLoadProgress.setProgress(progress);
				if (newProgress == 100) {
					webPageLoadProgress.setVisibility(View.GONE);
				}
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
		webView.loadUrl(MConfig.MY_COLLECT + userId);
	}
}
