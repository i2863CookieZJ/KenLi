package com.sobey.cloud.webtv.broke;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.broke.util.BrokeFooterControl;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 玩法介绍界面
 * 
 * @author lgx
 *
 */
public class BrokePlayHomeActivity extends BaseActivity {

	private CatalogObj mCatalogObj;

	@GinInjectView(id = R.id.mWebView)
	WebView mWebView;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_broke_play_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		init();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			SharedPreferences launchMode = this.getSharedPreferences("lauch_mode", 0);
			if (launchMode != null && launchMode.getBoolean("broke_task_home", false)) {
				startActivity(new Intent(BrokePlayHomeActivity.this, BrokeTaskHomeActivity.class));
				finish();
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void init() {
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
		} catch (Exception e) {
			finish();
		}
		initSliding(false);
		setTitle(mCatalogObj.name);
		setModuleMenuSelectedItem(mCatalogObj.index);
		BrokeFooterControl.setBrokeFooterSelectedItem(this, "玩法", mCatalogObj.index);

		initWebView();
		loadWebViewData();

	}

	private void initWebView() {
		mWebView.getSettings().setJavaScriptEnabled(false);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
	}

	private void loadWebViewData() {
		mWebView.loadUrl("file:///android_asset/broke_play_content.html");
	}
}
