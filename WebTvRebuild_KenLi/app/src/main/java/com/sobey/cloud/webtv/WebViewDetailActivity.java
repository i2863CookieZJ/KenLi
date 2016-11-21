package com.sobey.cloud.webtv;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.log.GinLog;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.CatalogType;
import com.sobey.cloud.webtv.utils.MConfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewDetailActivity extends BaseActivity {

	private static final String TAG = WebViewDetailActivity.class.getName();

	@GinInjectView(id = R.id.mWebviewdetailWebview)
	WebView mWebviewdetailWebview;

	@GinInjectView(id = R.id.mWebviewdetailBack)
	ImageButton mWebviewdetailBack;

	private CatalogObj mCatalogObj;
	private LocationClient mLocClient;
	private MyLocationListener mMyLocationListener;
	private int mLocateFlag = 0; // 0:初始状态;1:超时;2:定位成功
	private String mUrl;

	@Override
	public int getContentView() {
		return R.layout.activity_webview_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
		mUrl = mCatalogObj.id;

		mWebviewdetailWebview.getSettings().setBlockNetworkImage(false);
		mWebviewdetailWebview.getSettings().setDomStorageEnabled(true);
		mWebviewdetailWebview.getSettings().setJavaScriptEnabled(true);
		mWebviewdetailWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				GinLog.i(TAG, url);
				view.loadUrl(url);
				return true;
			}
		});
		if (mCatalogObj.type != CatalogType.TakeTaxi) {
			mWebviewdetailWebview.loadUrl(mUrl);
		} else {
			initGPS();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mLocateFlag == 0) {
						mLocateFlag = 1;
						Toast.makeText(WebViewDetailActivity.this, "暂时无法获取定位信息", Toast.LENGTH_SHORT).show();
						setLocationInfo(null);
					}
				}
			}, 10000);
		}

		mWebviewdetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void initGPS() {
		Toast.makeText(this, "定位中...", Toast.LENGTH_LONG).show();
		mLocClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(3000);
		option.setNeedDeviceDirect(false);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.requestPoi();
		}
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (mLocateFlag == 0) {
				mLocateFlag = 2;
				setLocationInfo(location);
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
			if (mLocateFlag == 0) {
				mLocateFlag = 2;
				setLocationInfo(location);
			}
		}
	}

	private void setLocationInfo(BDLocation location) {
		if (location != null && location.getLocType() == 161) {
			mUrl += "&fromlat=" + location.getLatitude() + "&fromlng=" + location.getLongitude();
			if (!TextUtils.isEmpty(location.getAddrStr())) {
				mUrl += "&fromaddr=" + location.getAddrStr();
			}
		}
		mWebviewdetailWebview.loadUrl(mUrl);
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
		}
		super.onDestroy();
	}

}
