package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用于广告跳转后的页面查看
 * 
 * @author zouxudong
 *
 */
public class AdvJumpPageActivity extends BaseActivity {

	@GinInjectView(id = R.id.titlebar_name)
	private TextView titleBar;
	@GinInjectView(id = R.id.loadingMask)
	private RelativeLayout loadingMask;
	@GinInjectView(id = R.id.advWebview)
	private WebView webView;
	@GinInjectView(id = R.id.loading_chinese)
	private TextView loadingText;
	@GinInjectView(id = R.id.top_back)
	private ImageButton backBtn;

	protected boolean isDisposed;

	@Override
	public int getContentView() {
		return R.layout.activity_advjump;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setRequestedOrientation(Window.FEATURE_NO_TITLE);
		loadingMask.setVisibility(View.VISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		// webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (isDisposed)
					return false;
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (isDisposed)
					return;
				super.onPageFinished(view, url);
				loadingMask.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (isDisposed)
					return;
				super.onPageStarted(view, url, favicon);
				loadingMask.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if (isDisposed)
					return;
				super.onReceivedError(view, errorCode, description, failingUrl);
				loadingMask.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				if (isDisposed)
					return;
				super.onReceivedSslError(view, handler, error);
				loadingMask.setVisibility(View.GONE);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (isDisposed)
					return;
				super.onProgressChanged(view, newProgress);
				loadingText.setText(newProgress + "%");
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				if (isDisposed)
					return;
				super.onReceivedTitle(view, title);
				titleBar.setText(formatString(title, 12));
			}
		});
		titleBar.setText(getIntent().getStringExtra("name"));
		String url = getIntent().getStringExtra("url");
		webView.loadUrl(url);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isDisposed = true;
				webView.clearCache(false);
				finish();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			webView.onPause();
		} catch (Exception e) {
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		webView.onResume();
	}

	private String formatString(String stringToFormat, int maxLenth) {
		if (stringToFormat.length() > maxLenth) {
			stringToFormat = stringToFormat.substring(0, maxLenth - 1) + "...";
		}
		return stringToFormat;
	}

}
