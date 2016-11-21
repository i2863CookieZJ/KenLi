package com.sobey.cloud.webtv;

import java.io.File;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.personal.HeadImageChooseActivity;
import com.sobey.cloud.webtv.utils.PhotoPopWindow;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 用于内嵌带链接的网页
 * 
 * @author zouxudong
 *
 */
public class LinkPageActivity extends BaseActivity {

	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.advWebview)
	private WebView webView;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.webPageLoadProgress)
	protected ProgressBar webPageLoadProgress;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;
	private final String chooseFilePrompt = "请选择文件";

	@Override
	public int getContentView() {
		return R.layout.activity_link_page;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		setRequestedOrientation(Window.FEATURE_NO_TITLE);
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");
		mHeaderCtv.setTitle(title);

		webView.getSettings().setDatabaseEnabled(true);
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		webView.getSettings().setGeolocationEnabled(true);
		webView.getSettings().setGeolocationDatabasePath(dir);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		// webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 调用拨号程序
				if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")
						|| url.startsWith("sms:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
					return true;
				}
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webPageLoadProgress.setVisibility(View.GONE);
				Log.d("zxd", "onPageFinished");
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webPageLoadProgress.setVisibility(View.VISIBLE);
				Log.d("zxd", "onPageStarted");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				webPageLoadProgress.setVisibility(View.GONE);
				AlertDialog.Builder b2 = new AlertDialog.Builder(LinkPageActivity.this);
				// b2.setTitle("来自网页的消息:" +
				// errorCode).setMessage(description).setPositiveButton("确定",
				// new AlertDialog.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// }
				// });
				// b2.setCancelable(false);
				// b2.create();
				// b2.show();
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				webPageLoadProgress.setVisibility(View.GONE);
				AlertDialog.Builder b2 = new AlertDialog.Builder(LinkPageActivity.this);
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

		});
		webView.setWebChromeClient(new WebChromeClient() {
			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {

				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				// LinkPageActivity.this.startActivityForResult(Intent.createChooser(i,chooseFilePrompt),
				// FILECHOOSER_RESULTCODE);

				Intent intent = new Intent(LinkPageActivity.this, HeadImageChooseActivity.class);
				startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);

			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				// LinkPageActivity.this.startActivityForResult(
				// Intent.createChooser(i, chooseFilePrompt),
				// FILECHOOSER_RESULTCODE);

				Intent intent = new Intent(LinkPageActivity.this, HeadImageChooseActivity.class);
				startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);
			}

			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				// LinkPageActivity.this.startActivityForResult(
				// Intent.createChooser( i,chooseFilePrompt ),
				// LinkPageActivity.FILECHOOSER_RESULTCODE );
				Intent intent = new Intent(LinkPageActivity.this, HeadImageChooseActivity.class);
				startActivityForResult(intent, PhotoPopWindow.CHOOSED_IMAGE_FILE);

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

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				// titleBar.setText(formatString(title,12));
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(LinkPageActivity.this);
				b2.setTitle("来自网页的消息").setMessage(message).setPositiveButton("确定", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});

				b2.setCancelable(false);
				b2.create();
				b2.show();
				return true;
			}

		});

		webView.loadUrl(url);
		webView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
						return goBack();
					}
				}
				return false;
			}
		});
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		mBackRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
	}

	public boolean goBack() {
		if (webView.canGoBack()) {
			webView.goBack();
			return true;
		} else {
			return false;
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		} else if (requestCode == PhotoPopWindow.CHOOSED_IMAGE_FILE && intent != null) {
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
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
				long contentLength) {
			Log.i("tag", "url=" + url);
			Log.i("tag", "userAgent=" + userAgent);
			Log.i("tag", "contentDisposition=" + contentDisposition);
			Log.i("tag", "mimetype=" + mimetype);
			Log.i("tag", "contentLength=" + contentLength);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

}
