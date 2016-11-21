package com.sobey.cloud.webtv.ebusiness;

import com.higgses.griffin.annotation.app.GinInjectView;

import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

/**
 * 物品详情页面
 * 
 * @author zouxudong
 *
 */
public class GoodsDetailActivity extends BaseActivity {

	@GinInjectView(id = R.id.webview)
	private WebView webView;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout top_back;
	@GinInjectView(id = R.id.top)
	private View topView;
	private boolean hadShow;

	@Override
	public int getContentView() {
		return R.layout.activity_ebusiness_goodsdetail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		
//		top_back.setVisibility(View.VISIBLE);
//		top_back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(ECShopBroadReciver.ECSHOP_BROAD);
//				intent.putExtra("msg", ECShopBroadReciver.SHOW_ACTION_BAR);
//				sendBroadcast(intent);
//				GoodsDetailActivity.this.finish();
//			}
//		});
//		String url = getIntent().getStringExtra("url");
//		webView = (WebView) findViewById(R.id.webview);
//		webView.setWebViewClient(new WebViewClient() {
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				String cookie = CookieManager.getInstance().getCookie(url);
//				Log.d("zxd", "ecshop:" + cookie);
//				if (!TextUtils.isEmpty(cookie)) {
//					SharedPreferences settings = GoodsDetailActivity.this.getSharedPreferences("iweb_shoppingcart", 0);
//					Editor editor = settings.edit();
//					editor.putString("iweb_shoppingcart", cookie.trim());
//					editor.commit();
//				}
//			}
//		});
//		webView.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
//				if (event.getAction() == KeyEvent.ACTION_DOWN) {
//					if (keyCode == KeyEvent.KEYCODE_BACK) {
//						if (webView.canGoBack()) {
//							webView.goBack();
//							return true;
//						}
//						return false;
//					}
//				}
//				return false;
//			}
//		});
//		hadShow = true;
//		webView.loadUrl(url);
	}

}
