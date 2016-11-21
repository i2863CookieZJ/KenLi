package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
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
 * 交通违章查询
 * 
 * @author lgx
 *
 */
public class QueryTrafficViolationsActivity extends BaseActivity {
	@GinInjectView(id = R.id.top_title)
	private TextView top_title;
	@GinInjectView(id = R.id.webview)
	private WebView webView;

	@Override
	public int getContentView() {
		return R.layout.property_management;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		top_title.setText("违章查询");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(MConfig.WEIZHANG);
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

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.top_back:
			finishActivity();
			break;
		}
	}
}
