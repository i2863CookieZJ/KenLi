package com.sobey.cloud.webtv.widgets.webview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.web.Md5Builder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class WebViewController {
	@SuppressWarnings("unused")
	private Context context;
	private WebView webView;
	private String htmlData;
	private boolean isShowImage;
	private HtmlParser parser;
	private ShowImageClickListener mShowImageClickListener;

	public interface ShowImageClickListener {
		void onClick(JSONArray imageUrlArray);
	}

	public void setShowImageClickListener(ShowImageClickListener listener) {
		mShowImageClickListener = listener;
	}

	public WebViewController(Context context, WebView webView, String htmlData, boolean isShowImage) {
		this.context = context;
		this.webView = webView;
		this.htmlData = htmlData;
		this.isShowImage = isShowImage;
	}

	public void start() {
		Log.d("WebViewController", "start setup webview");
		setupWebView();
		Log.d("WebViewController", "end setup webview");
	}

	public void setFontSize(int fontsize) {
		webView.loadUrl("javascript:(function(){" + "document.body.style.fontSize='" + fontsize + "px';" + "})()");
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void setupWebView() {
		webView.addJavascriptInterface(new Js2JavaClickDownloadInterface(), HtmlParser.Js2JavaClickDownload);
		webView.addJavascriptInterface(new Js2JavaShowImageInterface(), HtmlParser.Js2JavaShowImage);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBlockNetworkImage(false);

		webView.setWebChromeClient(new WebChromeClient() {
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				Log.d("MyApplication", message + " -- From line " + lineNumber + " of " + sourceID);
			}
		});

		parser = new HtmlParser(webView, htmlData, isShowImage);
		parser.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
	}

	public class Js2JavaClickDownloadInterface {
		@JavascriptInterface
		public void setImgSrc(String imgUrl, String imgSrc, String imageId) {
			DownloadWebImgTask downloadTask = new DownloadWebImgTask();
			downloadTask.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool(), imgUrl, imgSrc, imageId);
		}
	}

	public class Js2JavaShowImageInterface {
		@JavascriptInterface
		public void showImage(String imageId) {
			ArrayList<ImageUrlInfo> arrayList = parser.getImageUrlInfo();
			JSONArray information = new JSONArray();
			try {
				for (ImageUrlInfo info : arrayList) {
					JSONObject obj = new JSONObject();
					obj.put("url", info.getUrl());
					obj.put("imageId", info.getImageId());
					obj.put("localFilePath", info.getLocalFilePath());
					obj.put("isDownload", info.isDownload());
					information.put(obj);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mShowImageClickListener.onClick(information);
		}
	}

	public class DownloadWebImgTask extends AsyncTask<String, String, String[]> {
		@Override
		protected void onProgressUpdate(String... values) {
			if (values == null || values.length != 1) {
				return;
			}
			String imageId = values[0];
			webView.loadUrl("javascript:(function(){" + "var obj = document.getElementById('" + imageId
					+ "').setAttribute(\"src\",'" + HtmlParser.LoadingImgSrc + "');" + "})()");
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result == null || result.length != 3) {
				return;
			}
			String imgSrc = result[0];
			String imageId = result[1];
			webView.loadUrl(
					"javascript:(function(){" + "var obj = document.getElementById('" + imageId
							+ "').setAttribute(\"src\",'" + imgSrc + "');"
							+ (result[2] == "success" ? "var obj = document.getElementById('" + imageId
									+ "').setAttribute(\"onclick\", \"mShowImage('" + imageId + "')\");" : "")
							+ "})()");
			parser.setDownloadState(imageId);
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(String... params) {
			if (params.length != 3) {
				return null;
			}

			String imgUrl = params[0];
			String imgSrc = params[1];
			String imageId = params[2];
			if (imgUrl == null || imgSrc == null || imageId == null) {
				return null;
			}
			publishProgress(imageId);
			URL url = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			HttpURLConnection urlCon = null;

			File dir = new File(Environment.getExternalStorageDirectory() + MConfig.CachePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				String imageName = Md5Builder.getMD5(imgUrl, "MD5");
				imageName = imageName + imageId.substring(imageId.lastIndexOf("."), imageId.length());
				File file = new File(Environment.getExternalStorageDirectory() + MConfig.CachePath + "/" + imageName);
				imgSrc = "file://" + file.getAbsolutePath();
				if (file.exists() && file.length() > 0) {
					String[] strings = { Build.VERSION.SDK_INT >= 23 ? imgUrl : imgSrc, imageId, "success" };
					return strings;
				}
				file.createNewFile();
				url = new URL(imgUrl);
				urlCon = (HttpURLConnection) url.openConnection();
				urlCon.setRequestMethod("GET");
				urlCon.setDoInput(true);
				urlCon.connect();
				inputStream = urlCon.getInputStream();
				outputStream = new FileOutputStream(file);
				byte buffer[] = new byte[1024];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, bufferLength);
				}
				outputStream.flush();
				String[] strings = { Build.VERSION.SDK_INT >= 23 ? imgUrl : imgSrc, imageId, "success" };
				return strings;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					if (outputStream != null) {
						outputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String[] strings = { HtmlParser.NoneImgSrc, imageId, "fail" };
			return strings;
		}
	}

}
