package com.sobey.cloud.webtv.fragment;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.PersonalCenterActivity;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * 圈子
 * 
 * @author lgx
 *
 */
public class GroupFrament extends BaseFragment {
	@GinInjectView(id = R.id.title)
	private TextView title;
	@GinInjectView(id = R.id.webview)
	private WebView webView;

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View v = getCacheView(mInflater, R.layout.new_group_frame);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}
		setupActivity();
	}

	private void setupActivity() {
		title.setText("圈子");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(MConfig.QUANZI);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}

	@GinOnClick(id = { R.id.user_login })
	public void userLogin(View view) {
		Intent intent = new Intent(getActivity(), PersonalCenterActivity.class);
		startActivity(intent);
	}

}
