package com.sobey.cloud.webtv;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.dylan.uiparts.listview.DragListView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.ui.GeneralNewsHome;
import com.sobey.cloud.webtv.ui.NormalNewsUtil;
import com.sobey.cloud.webtv.utils.AdBanner;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/***
 * 普通新闻&视频新闻列表
 * 
 * @author zouxudong
 *
 */
public class VideoAndNormalNewsListActivity extends BaseActivity {

	@GinInjectView(id = R.id.mNewsList)
	private DragListView mNewsList;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.loadingMask)
	private RelativeLayout loadingMask;
	@GinInjectView(id = R.id.loadingfail)
	private RelativeLayout loadingFail;
	@GinInjectView(id = R.id.mAdLayout)
	private RelativeLayout advLayout;
	@GinInjectView(id = R.id.mAdCloseBtn)
	private ImageButton advCloseBtn;
	@GinInjectView(id = R.id.mAdImage)
	private AdvancedImageCarousel advImage;

	@Override
	public int getContentView() {
		return R.layout.activity_videoandnormalnews_detail;
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.back_rl:
			back();
			break;
		}
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		setRequestedOrientation(Window.FEATURE_NO_TITLE);
		super.onDataFinish(savedInstanceState);
		initializeView();
		if (checkNetwork()) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					String catalogId = getIntent().getStringExtra("ids");
					GeneralNewsHome generalNewsHome = new NormalNewsUtil(loadingMask,
							VideoAndNormalNewsListActivity.this);
					generalNewsHome.init(VideoAndNormalNewsListActivity.this, catalogId, mNewsList, null, null, null,
							null);
					new AdBanner(VideoAndNormalNewsListActivity.this, catalogId, advLayout, advImage, advCloseBtn,
							true);

				}
			}, 500);
		} else {
			loadingFail.setVisibility(View.VISIBLE);
		}
	}

	public VideoAndNormalNewsListActivity() {

	}

	protected void initializeView() {
		mHeaderCtv.setTitle(getIntent().getStringExtra("title"));
	}

	public void back() {
		finishActivity();
	}

	public boolean checkNetwork() {
		ConnectivityManager conMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return true;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		return false;
	}

}
