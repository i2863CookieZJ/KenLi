package com.sobey.cloud.webtv.ebusiness;

import com.dylan.common.animation.AnimationController;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.EbusinessAdressAdapter;
import com.sobey.cloud.webtv.bean.EbusinessRequestMananger;
import com.sobey.cloud.webtv.bean.EbusinessRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GetEbReciverListResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.widgets.CustomTitleView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("JavascriptInterface")
public class MyAddressActivity extends BaseActivity implements OnClickListener {

	@GinInjectView(id = R.id.loadingmask)
	View mLoadingLayout;
	@GinInjectView(id = R.id.empty_layout)
	View failedLayout;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	TextView failedTv;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	WebView webView;
	private EbusinessAdressAdapter adapter;
	private ProgressBar webPageLoadProgress;

	@Override
	public int getContentView() {
		return R.layout.activity_ebusiness_myaddress;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setUpDatas();
	}

	public void setUpDatas() {

		mHeaderCtv.setTitle(R.string.my_address);
		mBackRl.setOnClickListener(this);
		showInWeb();
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// getData();
	}

	private void showInWeb() {
		String url = "http://shop.sobeycache.com/index.php?controller=simple&action=address_list_app&uid=";
		webView = (WebView) findViewById(R.id.activity_my_address_web);
		webPageLoadProgress = (ProgressBar) findViewById(R.id.webPageLoadProgress);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(webView.getSettings().LOAD_NO_CACHE);// 不使用缓存
		webView.addJavascriptInterface(this, "android");
		webView.loadUrl(url + PreferencesUtil.getLoggedUserId());
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				webPageLoadProgress.setProgress(newProgress);
				if (newProgress == 100)
					webPageLoadProgress.setVisibility(View.GONE);
			}

		});
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

		});
		webView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
					if (webView.canGoBack()) {
						webView.goBack();
						return true;
					}
					return false;
				}
				return false;
			}
		});
	}

	/**
	 * UI原生的方式显示
	 */
	private void getData() {
		EbusinessRequestMananger.getInstance().getMyAddress(1, this, new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				mCloseLoadingIcon();
				if (null != result && result instanceof GetEbReciverListResult) {
					GetEbReciverListResult reciverListResult = (GetEbReciverListResult) result;
					if (null == reciverListResult.reciverModels || reciverListResult.reciverModels.size() < 1) {
						failedLayout.setVisibility(View.VISIBLE);
						failedTv.setText(R.string.has_no_result);
						return;
					}
					if (adapter == null) {
						adapter = new EbusinessAdressAdapter(reciverListResult.reciverModels, MyAddressActivity.this);
					}
				} else {
					failedLayout.setVisibility(View.VISIBLE);
					failedTv.setText(R.string.has_no_result);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back_rl:
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finishActivity();
			}
			break;

		default:
			break;
		}
	}

	public void mOpenLoadingIcon() {
		if (mLoadingLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingLayout, 1000, 0);
		}
	}
}
