package com.sobey.cloud.webtv.views.activity;

import java.io.File;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.personal.HeadImageChooseActivity;
import com.sobey.cloud.webtv.utils.PhotoPopWindow;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("JavascriptInterface")
public class CampaignSignUpActivity extends BaseActivity {
	private String mUrl;
	private String mUserName;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mCampaignSignUpBack;
	@GinInjectView(id = R.id.mCampaignSignUpWebView)
	WebView mCampaignSignUpWebView;
	@GinInjectView(id = R.id.webPageLoadProgress)
	ProgressBar webPageLoadProgress;
	private ValueCallback<Uri> mUploadMessage;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public int getContentView() {
		return R.layout.activity_campaign_sign_up;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}
		try {
			String str = getIntent().getStringExtra("information");
			if (!TextUtils.isEmpty(str)) {
				mUrl = str;
			} else {
				finishActivity();
			}

			mCampaignSignUpBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					finishActivity();
				}
			});
			mCampaignSignUpWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			mCampaignSignUpWebView.getSettings().setJavaScriptEnabled(true);
			mCampaignSignUpWebView.addJavascriptInterface(this, "obj");
			mCampaignSignUpWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					webPageLoadProgress.setVisibility(View.VISIBLE);
					return true;
				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					webPageLoadProgress.setVisibility(View.GONE);
					AlertDialog.Builder b2 = new AlertDialog.Builder(CampaignSignUpActivity.this);
					b2.setTitle("来自网页的消息:" + errorCode).setMessage(description).setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					b2.setCancelable(false);
					b2.create();
					b2.show();
				}

				@Override
				public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
					super.onReceivedSslError(view, handler, error);
					webPageLoadProgress.setVisibility(View.GONE);
					AlertDialog.Builder b2 = new AlertDialog.Builder(CampaignSignUpActivity.this);
					b2.setTitle("来自网页的消息").setMessage(error.toString()).setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					b2.setCancelable(false);
					b2.create();
					b2.show();
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					webPageLoadProgress.setVisibility(View.GONE);
					Log.d("zxd", "onPageFinished");
				}
			});
			mCampaignSignUpWebView.setWebChromeClient(new WebChromeClient() {
				@Override
				public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
					AlertDialog.Builder b2 = new AlertDialog.Builder(CampaignSignUpActivity.this);
					b2.setTitle("报名提示").setMessage(message).setPositiveButton("确定", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CampaignSignUpActivity.this.finishActivity();
							result.confirm();
						}
					});

					b2.setCancelable(false);
					b2.create();
					b2.show();
					return true;
				}

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					int progress = newProgress;
					Log.d("zxd", "进度:" + newProgress);
					webPageLoadProgress.setProgress(progress);
					if (newProgress == 100)
						webPageLoadProgress.setVisibility(View.GONE);
					super.onProgressChanged(view, newProgress);
				}

				public void openFileChooser(ValueCallback<Uri> uploadMsg) {

					mUploadMessage = uploadMsg;
					// LinkPageActivity.this.startActivityForResult(Intent.createChooser(i,chooseFilePrompt),
					// FILECHOOSER_RESULTCODE);

					Intent intent = new Intent(CampaignSignUpActivity.this, HeadImageChooseActivity.class);
					startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);

				}

				public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
					mUploadMessage = uploadMsg;
					// LinkPageActivity.this.startActivityForResult(
					// Intent.createChooser(i, chooseFilePrompt),
					// FILECHOOSER_RESULTCODE);

					Intent intent = new Intent(CampaignSignUpActivity.this, HeadImageChooseActivity.class);
					startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);
				}

				public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
					mUploadMessage = uploadMsg;
					// LinkPageActivity.this.startActivityForResult(
					// Intent.createChooser( i,chooseFilePrompt ),
					// LinkPageActivity.FILECHOOSER_RESULTCODE );
					Intent intent = new Intent(CampaignSignUpActivity.this, HeadImageChooseActivity.class);
					startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);

				}

			});
			String temurl = mUrl + "?username=" + mUserName;
			mCampaignSignUpWebView.loadUrl(temurl);
		} catch (Exception e) {
			finishActivity();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == PhotoPopWindow.CHOOSED_IMAGE_FILE && intent != null) {
			String path = intent.getStringExtra("path");
			if (!TextUtils.isEmpty(path)) {
				File pathFile = new File(path);
				if (pathFile.exists()) {
					Uri result = Uri.fromFile(pathFile);
					mUploadMessage.onReceiveValue(result);
					mUploadMessage = null;
				} else {
					Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public void signUpSuccess() {
		finishActivity();
	}
}
