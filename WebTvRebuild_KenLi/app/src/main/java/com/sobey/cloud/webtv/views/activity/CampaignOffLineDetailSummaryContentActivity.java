package com.sobey.cloud.webtv.views.activity;

import org.apache.cordova.engine.SystemWebView;
import org.json.JSONObject;

import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.widgets.webview.WebViewController;
import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class CampaignOffLineDetailSummaryContentActivity extends BaseActivity {

	private JSONObject mInformation;
	private int mFontSize = MConfig.FontSizeDefault;
	private boolean isShowPicture = true;
	private WebViewController webViewController;

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mCampaigndetailBack;
	@GinInjectView(id = R.id.mCampaigndetailContent)
	ScrollView mCampaigndetailContent;
	@GinInjectView(id = R.id.mCampaigndetailContentTitle)
	TextView mCampaigndetailContentTitle;
	@GinInjectView(id = R.id.mCampaigndetailTimeLayout)
	LinearLayout mCampaigndetailTimeLayout;
	@GinInjectView(id = R.id.mCampaigndetailTimeTextView)
	TextView mCampaigndetailTimeTextView;
	@GinInjectView(id = R.id.mCampaigndetailAddressLayout)
	LinearLayout mCampaigndetailAddressLayout;
	@GinInjectView(id = R.id.mCampaigndetailAddressTextView)
	TextView mCampaigndetailAddressTextView;
	@GinInjectView(id = R.id.mCampaigndetailWebview)
	SystemWebView mCampaigndetailWebview;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_offline_detail_summary_content;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	public void setupNewsDetailActivity() {
		try {
			// Get preferrence
			SharedPreferences settings = getSharedPreferences("settings", 0);
			CheckNetwork network = new CheckNetwork(this);
			isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
					|| network.getWifiState(false) == State.CONNECTED;
			mFontSize = settings.getInt("fontsize", MConfig.FontSizeDefault);

			mInformation = new JSONObject(getIntent().getStringExtra("information"));
			initActivity();
			loadContent();
		} catch (Exception e) {
			finishActivity();
		}
	}

	private void initActivity() {
		mCampaigndetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});

		initWebView();
	}

	private void initWebView() {
		mCampaigndetailWebview.getSettings().setJavaScriptEnabled(true);
		mCampaigndetailWebview.getSettings().setDefaultTextEncodingName("gbk");
		mCampaigndetailWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mCampaigndetailWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
	}

	private void loadContent() {
		try {
			if (mInformation != null) {
				mCampaigndetailContentTitle.setText(mInformation.optString("Title"));
				mCampaigndetailTimeTextView.setText(mInformation.optString("Time"));
				mCampaigndetailAddressTextView.setText(mInformation.optString("Address"));
				String content = mInformation.optString("ActivityDetailsHtml").replaceAll("font-size:", "")
						.replaceAll("font:", "");
				content = "<html><head></head><body style='font-size:" + mFontSize + "px;'>" + content;
				content = content + "</body></html>";
				webViewController = new WebViewController(this, mCampaigndetailWebview, content, isShowPicture);
				webViewController.start();
			} else {
				mCampaigndetailWebview.loadData("暂时无法获取新闻详情", "text/html; charset=UTF-8", null);
			}
		} catch (Exception e) {
			mCampaigndetailWebview.loadData("暂时无法获取新闻详情", "text/html; charset=UTF-8", null);
		}
	}
}
