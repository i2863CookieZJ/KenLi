package com.sobey.cloud.webtv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VideoTestActivity extends BaseActivity {
	WebView web;

	@Override
	public int getContentView() {
		return R.layout.activity_vediotest;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		String info = getIntent().getStringExtra("information");
		web = (WebView) findViewById(R.id.videotest);
		WebSettings settings = web.getSettings();
		settings.setJavaScriptEnabled(true);
		web.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		try {
			JSONObject jsonObject = null;
			jsonObject = new JSONObject(info);
			// String url = jsonObject.getString("link");

			// web.loadUrl("http://webtv.sobeycloud.com/thgbdst//zb/zb/27358189.shtml");
			wanwan();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			web.loadData(msg.obj.toString(), "text/html", "utf-8");
		};
	};

	public void wanwan() {
		new Thread() {
			public void run() {
				try {
					URL url = new URL("http://www.baidu.com");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
//					conn.setDoInput(true);
					conn.setConnectTimeout(30 * 1000);
//					conn.setRequestProperty("User-Agent",
//							"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
					conn.connect();
					InputStream ins = conn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(ins));
					StringBuffer sb = new StringBuffer();
					while (br.readLine() != null) {
						sb.append(br.readLine());
					}
					Message msg = new Message();
					msg.obj = sb.toString();
					handler.obtainMessage(0x100, msg).sendToTarget();
					br.close();
					ins.close();
					conn.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
}
