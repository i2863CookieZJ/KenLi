package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * 招聘求职
 * 
 * @author lgx
 *
 */
public class JobRecruitmentActivity extends BaseActivity {
	@GinInjectView(id = R.id.top_title)
	private TextView top_title;
	@GinInjectView(id = R.id.webview)
	private WebView webView;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.property_management;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		top_title.setText("招聘求职");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(MConfig.ZHAOPIN);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}

	@GinOnClick(id = { R.id.top_back })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finishActivity();
			break;
		}
	}
}
