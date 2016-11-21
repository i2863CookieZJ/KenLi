package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.MyGroupList;
import com.sobey.cloud.webtv.senum.LoginMode;
import com.sobey.cloud.webtv.senum.UserGender;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyProfile extends BaseActivity {
	@GinInjectView(id = R.id.personmenu_login_layout)
	private LinearLayout mLoginLayout;
	@GinInjectView(id = R.id.personmenu_logout_layout)
	private LinearLayout mLogoutLayout;
	@GinInjectView(id = R.id.header_icon)
	private RoundImageView mHeaderIcon;
	@GinInjectView(id = R.id.user_nickname)
	private TextView mNickname;
	@GinInjectView(id = R.id.user_login_state)
	private ImageView mLoginStateIcon;
	@GinInjectView(id = R.id.user_gender)
	private ImageView mGenderIcon;
	@GinInjectView(id = R.id.top_title)
	private TextView tw_title;
	@SuppressLint("UseSparseArrays")
	@GinInjectView(id = R.id.setting)
	private ImageView setting;
	@GinInjectView(id = R.id.top_back)
	private ImageButton top_back;
	@GinInjectView(id = R.id.re_login_or_update)
	private RelativeLayout re_login_or_update;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_myprofile;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// top_back.setVisibility(View.GONE);
		tw_title.setText("个人中心");
		setting.setVisibility(View.VISIBLE);
		// delayCallUserInfo();
		getUserInfo();
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View view=inflater.inflate(R.layout.personal_center, container, false);
	// ViewUtils.inject(this, view);
	// top_back.setVisibility(View.GONE);
	// tw_title.setText("个人中心");
	// setting.setVisibility(View.VISIBLE);
	//// delayCallUserInfo();
	// getUserInfo();
	// return view;
	// }
	protected void delayCallUserInfo() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getUserInfo();
			}
		}, 1000);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUserInfo();
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// getUserInfo();
		// }
		// }, 1000);
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// ViewUtils.inject(this);
	//
	// }
	@GinOnClick(id = { R.id.setting, R.id.top_back, R.id.personmenu_login_layout, R.id.personmenu_logout_layout,
			R.id.my_tiezi, R.id.my_activity,  R.id.all_order, R.id.shop_cart,
			R.id.shouhuo_address, R.id.care_goods, R.id.re_login_or_update })
	public void onclick(View view) {
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		switch (view.getId()) {
		case R.id.care_goods:
			ToastUtil.showToast(this, "功能在开发中……");
			break;
		case R.id.shouhuo_address:
			ToastUtil.showToast(this, "功能在开发中……");
			break;
		case R.id.shop_cart:
			ToastUtil.showToast(this, "功能在开发中……");
			break;
		case R.id.all_order:
			ToastUtil.showToast(this, "功能在开发中……");
			break;
//		case R.id.user_message:
//			ToastUtil.showToast(this, "功能在开发中……");
//			break;
		case R.id.my_activity:

			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			startActivity(new Intent(this, MyActivitiesFragment.class));
			break;
		case R.id.my_tiezi:
			// ToastUtil.showToast(this, "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			startActivity(new Intent(this, MyGroupList.class));
			break;
		// case R.id.user_shoucang:
		// if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id",
		// null))) {
		// startActivity(new Intent(this, LoginActivity.class));
		// return;
		// }
		// startActivity(new Intent(this, NewCollectionActivity.class));
		// break;
		case R.id.setting:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		case R.id.top_back:
			finish();
			break;
		case R.id.personmenu_login_layout:

			break;
		case R.id.personmenu_logout_layout:

			break;
		case R.id.re_login_or_update:
			if (re_login_or_update.getTag().toString().trim().equals("1")) {
				startActivity(new Intent(this, UploadUserInfoActivity.class));
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		}
	}

	public void setUserInfo(String headerIcon, String nickname, String loginState, String gender) {
		if (mLoginLayout != null) {
			mLoginLayout.setVisibility(View.VISIBLE);
			re_login_or_update.setTag("1");
			mLogoutLayout.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(headerIcon)) {
				// 改成新的图片框架
				ImageLoader.getInstance().displayImage(headerIcon, mHeaderIcon);
				// mHeaderIcon.setNetImage(headerIcon);
			}
			if (!TextUtils.isEmpty(nickname)) {
				mNickname.setText(nickname);
			}
			if (!TextUtils.isEmpty(loginState)) {
				switch (LoginMode.valueOf(loginState)) {
				case Login_SinaWB:
					mLoginStateIcon.setImageResource(R.drawable.login_state_sinawb_icon);
					break;
				case Login_TencentWB:
					mLoginStateIcon.setImageResource(R.drawable.login_state_tencentwb_icon);
					break;
				case Login_QQ:
					mLoginStateIcon.setImageResource(R.drawable.login_state_qq_icon);
					break;
				default:
					mLoginStateIcon.setImageResource(R.drawable.trans);
					break;
				}
			}
			if (!TextUtils.isEmpty(gender)) {
				switch (UserGender.valueOf(gender)) {
				case Male:
					mGenderIcon.setImageResource(R.drawable.gender_male_icon);
					break;
				case Female:
					mGenderIcon.setImageResource(R.drawable.gender_female_icon);
					break;
				default:
					mGenderIcon.setImageResource(R.drawable.trans);
					break;
				}
			}
		}
	}

	public void clearUserInfo() {
		if (mLoginLayout != null) {
			mLoginLayout.setVisibility(View.GONE);
			mLogoutLayout.setVisibility(View.VISIBLE);
			re_login_or_update.setTag("2");
			mHeaderIcon.setImageResource(R.drawable.user_new_logo);
			mNickname.setText("");
			mLoginStateIcon.setImageResource(R.drawable.trans);
			mGenderIcon.setImageResource(R.drawable.trans);
		}
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// getUserInfo();
	// }
	// }, 1000);
	// super.onWindowFocusChanged(hasFocus);
	// }
	private void getUserInfo() {
		// if(getActivity()==null)
		// return;
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo != null && !TextUtils.isEmpty(userInfo.getString("id", null))) {
			setUserInfo(userInfo.getString("headicon", ""), userInfo.getString("nickname", ""),
					userInfo.getString("state", ""), userInfo.getString("gender", ""));
		} else {
			clearUserInfo();
		}
	}
}
