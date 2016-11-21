package com.sobey.cloud.webtv;

import java.util.ArrayList;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
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
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("JavascriptInterface")
public class MyShopcarPayAcitvity extends BaseActivity {

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
	private LinearLayout webPageLoadProgress;
	private ArrayList<String> idList;

	@Override
	public int getContentView() {
		return R.layout.new_e_business_frame;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		init();
	}

	private void init() {
		title.setText("订单结算");
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		idList = getIntent().getStringArrayListExtra("ids");
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
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			sb.append(idList.get(i));
			if (i < idList.size() - 1) {
				sb.append(",");
			}
		}
		webView.loadUrl(MConfig.PAY_SHOP_CAR + sb.toString());
	}
}
