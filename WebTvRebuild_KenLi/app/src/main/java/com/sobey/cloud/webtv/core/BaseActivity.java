package com.sobey.cloud.webtv.core;

import java.util.Timer;
import java.util.TimerTask;

import com.appsdk.advancedimageview.AdvancedImageView;

import com.higgses.griffin.core.AbstractActivity;
import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.netstate.utils.GinUNetWork;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.CollectionActivity;
import com.sobey.cloud.webtv.CouponNewsHomeActivity;
import com.sobey.cloud.webtv.GuessHomeActivity;
import com.sobey.cloud.webtv.HomeActivity;
import com.sobey.cloud.webtv.LiveNewsHomeActivity;
import com.sobey.cloud.webtv.ModuleMenu;
import com.sobey.cloud.webtv.ModuleMenu.ModuleChoiceListener;
import com.sobey.cloud.webtv.MyReviewActivity;
import com.sobey.cloud.webtv.PersonMenu;
import com.sobey.cloud.webtv.PersonMenu.PersonChoiceListener;
import com.sobey.cloud.webtv.RecommendNewsHomeActivity;
import com.sobey.cloud.webtv.SearchActivity;
import com.sobey.cloud.webtv.SettingsActivity;
import com.sobey.cloud.webtv.SuggestionActivity;
import com.sobey.cloud.webtv.TopicNewsHomeActivity;
import com.sobey.cloud.webtv.UploadUserInfoActivity;
import com.sobey.cloud.webtv.WebViewDetailActivity;
import com.sobey.cloud.webtv.broke.BrokeNewsHomeActivity;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.share.ShareControl;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.activity.CampaignHomeActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

/**
 * 基础Activity Created by higgses on 14-4-4.
 */
public abstract class BaseActivity<Model extends GinIModel> extends AbstractActivity<Model> {
	protected static final String TAG = "BaseActivity";
	protected Handler mHandler = null;

	// @GinInjectView(id = R.id.layout_nocontent_icon)
	protected ImageView noContentIcon;
	// @GinInjectView(id = R.id.layout_nocontent_tip)
	protected TextView noContentTip;

	private ModuleMenu mModuleMenu = null;
	private PersonMenu mPersonMenu = null;
	private SlidingMenu mSliding = null;
	private String mName = null;

	private static Boolean isExit = false;

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

		}
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		ShareSDK.initSDK(this);
	}

	@Override
	public void preOnCreate() {
		super.preOnCreate();
	}

	@Override
	public boolean onDoubleClickExit() {
		boolean isExit = false;
		if (super.onDoubleClickExit()) {
			isExit = true;
		}
		return isExit;
	}

	@Override
	public void onConnect(GinUNetWork.NetType type) {
		super.onConnect(type);
		showToast(R.string.toast_net_connect);
	}

	@Override
	public void onDisConnect() {
		super.onDisConnect();
		showToast(R.string.toast_net_dis_connect);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void onResume() {
		super.onResume();

		// JPushInterface.onResume(this);
	}

	public void onPause() {
		super.onPause();
		dismissDialog();
		// JPushInterface.onPause(this);
	}

	/**
	 * 显示加载框
	 *
	 * @param type
	 */
	public synchronized void showLoadDialog(int type) {

	}

	public void showLoadDialog() {
	}

	/**
	 * 隐藏加载框
	 */
	public void dismissDialog() {
	}

	public void showNoContent(View layout, int iconId, String tip) {
		layout.setVisibility(View.VISIBLE);
		if (noContentIcon == null || noContentTip == null) {
			noContentIcon = (ImageView) findViewById(R.id.layout_nocontent_icon);
			noContentTip = (TextView) findViewById(R.id.layout_nocontent_tip);
		}
		noContentIcon.setImageResource(iconId);
		noContentTip.setText(tip);
	}

	/**
	 * 返回Application
	 *
	 * @return
	 */
	public ContextApplication getApp() {
		return ContextApplication.getApp();
	}

	protected void initSliding(boolean enableFling) {
		if (mModuleMenu == null)
			mModuleMenu = new ModuleMenu();
		mModuleMenu.setListener(new ModuleChoiceListener() {
			@Override
			public void onChoice(String module) {
				for (int i = 0; i < MConfig.CatalogList.size(); i++) {
					CatalogObj obj = MConfig.CatalogList.get(i);
					if (module.equalsIgnoreCase(obj.name) && !mName.equalsIgnoreCase(obj.name)) {
						switch (obj.type) {
						case News:
						case Video:
						case Photo:
							// ToastUtil.showToast(BaseActivity.this,
							// "政务"+obj.resId);
							Intent intent0 = new Intent(BaseActivity.this, HomeActivity.class);
							intent0.putExtra("index", i);
							BaseActivity.this.startActivity(intent0);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Live:

							Intent intent2 = new Intent(BaseActivity.this, LiveNewsHomeActivity.class);
							intent2.putExtra("index", i);
							BaseActivity.this.startActivity(intent2);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Broke:
							Intent intent4 = new Intent(BaseActivity.this, BrokeNewsHomeActivity.class);
							intent4.putExtra("index", i);
							BaseActivity.this.startActivity(intent4);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Recommend:
							// ToastUtil.showToast(BaseActivity.this,
							// "推荐"+obj.resId);
							Intent intent5 = new Intent(BaseActivity.this, RecommendNewsHomeActivity.class);
							intent5.putExtra("index", i);
							BaseActivity.this.startActivity(intent5);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Topic:
							// ToastUtil.showToast(BaseActivity.this,
							// "专题---"+obj.resId);
							Intent intent6 = new Intent(BaseActivity.this, TopicNewsHomeActivity.class);
							intent6.putExtra("index", i);
							BaseActivity.this.startActivity(intent6);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Coupon:
							Intent intent7 = new Intent(BaseActivity.this, CouponNewsHomeActivity.class);
							intent7.putExtra("index", i);

							BaseActivity.this.startActivity(intent7);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case App:
							break;
						case ChargeMobileFee:
							Intent intent9 = new Intent(BaseActivity.this, WebViewDetailActivity.class);
							intent9.putExtra("index", i);
							BaseActivity.this.startActivity(intent9);
							break;
						case SearchBusLine:
							Intent intent10 = new Intent(BaseActivity.this, WebViewDetailActivity.class);
							intent10.putExtra("index", i);
							BaseActivity.this.startActivity(intent10);
							break;
						case SearchIllegal:
							Intent intent11 = new Intent(BaseActivity.this, WebViewDetailActivity.class);
							intent11.putExtra("index", i);
							BaseActivity.this.startActivity(intent11);
							break;
						case LifeAround:
							Intent intent12 = new Intent(BaseActivity.this, WebViewDetailActivity.class);
							intent12.putExtra("index", i);
							BaseActivity.this.startActivity(intent12);
							break;
						case TakeTaxi:
							Intent intent13 = new Intent(BaseActivity.this, WebViewDetailActivity.class);
							intent13.putExtra("index", i);
							BaseActivity.this.startActivity(intent13);
							break;
						case Guess:
							Intent intent14 = new Intent(BaseActivity.this, GuessHomeActivity.class);
							intent14.putExtra("index", i);
							BaseActivity.this.startActivity(intent14);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						case Campaign:
							Intent intent15 = new Intent(BaseActivity.this, CampaignHomeActivity.class);
							intent15.putExtra("index", i);

							BaseActivity.this.startActivity(intent15);
							BaseActivity.this.finish();
							activityAnimation();
							break;
						default:
							break;
						}
					}
				}
			}
		});

		if (mPersonMenu == null)
			mPersonMenu = new PersonMenu();
		mPersonMenu.setListener(new PersonChoiceListener() {
			@Override
			public void onAction(String module) {
				if (module == "推荐给朋友") {
					ShareControl shareControl = new ShareControl();
					shareControl.shareMessage(BaseActivity.this, MConfig.ShareFriendSMSContent);
				} else if (module == "我的评论") {
					BaseActivity.this.startActivity(new Intent(BaseActivity.this, MyReviewActivity.class));
				} else if (module == "我的收藏") {
					BaseActivity.this.startActivity(new Intent(BaseActivity.this, CollectionActivity.class));
				} else if (module == "搜索资讯") {
					BaseActivity.this.startActivity(new Intent(BaseActivity.this, SearchActivity.class));
				} else if (module == "意见反馈") {
					BaseActivity.this.startActivity(new Intent(BaseActivity.this, SuggestionActivity.class));
				}
			}

			@Override
			public void onUserCenterClick() {
				BaseActivity.this.startActivity(new Intent(BaseActivity.this, UploadUserInfoActivity.class));
			}

			@Override
			public void onSettingsClick() {
				BaseActivity.this.startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
			}
		});

		mSliding = new SlidingMenu(this);
		if (enableFling)
			mSliding.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		else
			mSliding.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mSliding.setShadowWidthRes(R.dimen.shadow_width);
		mSliding.setShadowDrawable(R.drawable.slidingmenu_shadow);
		mSliding.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSliding.setFadeDegree(0.35f);
		mSliding.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSliding.setMenu(R.layout.sliding_frameleft);
		mSliding.setBehindOffset(0.25f);
		mSliding.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_left, mModuleMenu).commit();

		mSliding.setMode(SlidingMenu.LEFT);
		mSliding.setMode(SlidingMenu.LEFT_RIGHT);
		mSliding.setSecondaryMenu(R.layout.sliding_frameright);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_right, mPersonMenu).commit();

		ImageButton left = (ImageButton) findViewById(R.id.titlebar_module);
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSliding.showMenu(true);
			}
		});
		ImageButton right = (ImageButton) findViewById(R.id.titlebar_person);
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSliding.showSecondaryMenu(true);
			}
		});
	}

	private void activityAnimation() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		if (version >= 5) {
			overridePendingTransition(R.anim.activity_right_in, android.R.anim.fade_out);
		}
	}

	protected void setTitle(String name) {
		mName = name;
		((TextView) findViewById(R.id.titlebar_name)).setText(name);
	}

	protected void setModuleMenuSelectedItem(int position) {
		mModuleMenu.setSelectedItem(position);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (mSliding.isMenuShowing()) {
	// mSliding.showContent();
	// return true;
	// }
	// //exitBy2Click();
	// }
	// return false;
	// }

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			AdvancedImageView.destory();
			finish();
			System.exit(0);
		}
	}

	private void getUserInfo() {
		if (mPersonMenu != null) {
			SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
			if (userInfo != null && !TextUtils.isEmpty(userInfo.getString("id", null))) {
				mPersonMenu.setUserInfo(userInfo.getString("headicon", ""), userInfo.getString("nickname", ""),
						userInfo.getString("state", ""), userInfo.getString("gender", ""));
			} else {
				mPersonMenu.clearUserInfo();
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getUserInfo();
			}
		}, 1000);
		super.onWindowFocusChanged(hasFocus);
	}

}
