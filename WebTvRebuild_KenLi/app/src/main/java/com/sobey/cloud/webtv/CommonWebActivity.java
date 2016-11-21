package com.sobey.cloud.webtv;

import java.lang.reflect.InvocationTargetException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.views.web.BaseWebViewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 通用网页浏览
 * 
 * @author lazy
 *
 */
@SuppressLint("NewApi")
public class CommonWebActivity extends BaseWebViewActivity implements OnClickListener {

	/**
	 * WebView
	 */
	@GinInjectView(id = R.id.common_wv)
	private WebView mCommonWv;

	/**
	 * 标题
	 */
	@GinInjectView(id = R.id.title_tv)
	private TextView mTitleTv;

	/**
	 * 评论
	 */
	@GinInjectView(id = R.id.comment_tv)
	private TextView mCommentTv;

	/**
	 * 新闻信息
	 */
	private JSONObject information;

	/**
	 * 用户名
	 */
	private String mUserName;

	/**
	 * 分享
	 */
	private SharePopWindow mSharePopWindow;

	private String mShareImage = null;
	private String mShareUrl = null;
	private String mShareTitle = null;
	private String mShareContent = null;

	@Override
	public int getContentView() {
		return R.layout.activity_common_web;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		
		initWebView();

		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String url = getIntent().getStringExtra("url");
			String title = getIntent().getStringExtra("title");

			if (null != title) {
				mTitleTv.setText(title);
			}

			String str = getIntent().getStringExtra("information");
			if (null != str) {
				try {
					JSONObject obj = new JSONObject(str);
					News.getArticleById(String.valueOf(obj.getInt("id")), obj.optString("parentid"), mUserName,
							MConfig.TerminalType, null, this, new OnJsonArrayResultListener() {
								@Override
								public void onOK(JSONArray result) {
									try {
										information = result.getJSONObject(0);
										String linkUrl = information.getString("url");
										mCommonWv.loadUrl(linkUrl);
									} catch (Exception e) {
									}
								}

								@Override
								public void onNG(String reason) {

								}

								@Override
								public void onCancel() {

								}
							});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (url != null) {
				mCommonWv.loadUrl(url);
			}

		}

		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_common_web_layout);
		mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);
		mCommentTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		// 分享
		case R.id.mNewsdetailMore:
			share();
			break;

		// 评论
		case R.id.comment_tv:
			comment();
			break;
		}
	}

	@Override
	public void onPause() {
		try {
			mCommonWv.getClass().getMethod("onPause").invoke(mCommonWv, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			mCommonWv.getClass().getMethod("onResume").invoke(mCommonWv, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
		}
		super.onResume();
	}

	/**
	 * 初始化WebView
	 */
	private void initWebView() {
		mCommonWv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		mCommonWv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				super.onHideCustomView();
			}
		});
	}

	/**
	 * 分享
	 */
	private void share() {
		try {
			mShareUrl = information.getString("url") + "?type=1";
			mShareTitle = information.getString("title");
			mShareContent = information.getString("summary").trim();
			mShareImage = information.getString("logo");
			mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
		} catch (Exception e) {
		}
	}

	/**
	 * 评论
	 */
	private void comment() {
		if (information != null) {
			SharedPreferences userInfo = getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("information", information.toString());
			startActivity(intent);
		}
	}

	@Override
	protected WebView getWebView() {
		// TODO Auto-generated method stub
		return mCommonWv;
	}
}
