package com.sobey.cloud.webtv.fragment;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.dylan.common.digest.Base64Parse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.MyActivitiesFragment;
import com.sobey.cloud.webtv.MyCollectionKindActivity;
import com.sobey.cloud.webtv.MyGuanZhuActivity;
import com.sobey.cloud.webtv.MyMsgActivity;
import com.sobey.cloud.webtv.MyOrderActivity;
import com.sobey.cloud.webtv.SettingsActivity;
import com.sobey.cloud.webtv.UploadUserInfoActivity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.ebusiness.MyAddressActivity;
import com.sobey.cloud.webtv.ebusiness.MyShopingCartActivity;
import com.sobey.cloud.webtv.senum.LoginMode;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我的界面
 */
public class MyProfile extends BaseFragment {
	@GinInjectView(id = R.id.header_icon)
	private RoundImageView mHeaderIcon;
	@GinInjectView(id = R.id.user_nickname)
	private TextView mNickname;
	@SuppressLint("UseSparseArrays")
	@GinInjectView(id = R.id.setting)
	private ImageView setting;
	@GinInjectView(id = R.id.re_login_or_update)
	private LinearLayout re_login_or_update;
	@GinInjectView(id = R.id.user_tlephone)
	private TextView userTelPhone;

	@GinInjectView(id = R.id.usercenter_waittopaynum)
	private TextView waitToPayTv;
	@GinInjectView(id = R.id.usercenter_waittosendnum)
	private TextView waitToSendTv;
	@GinInjectView(id = R.id.usercenter_waittogetnum)
	private TextView waitToGetTv;
	@GinInjectView(id = R.id.usercenter_donenum)
	private TextView doneTv;
	@GinInjectView(id = R.id.smallredpoint)
	private TextView smallRedPoint;// 未读消息小红点
	private boolean isShowSmallRedPoint;
	private boolean haveMsg[];
	private View layoutView;

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.fragment_myprofile);
		layoutView = view;
		return view;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setting.setVisibility(View.VISIBLE);
		setting.setImageResource(R.drawable.usercenter_msg);
		getUserInfo();
		getOrderNum();
		initIcon(layoutView);
	}

	private void initIcon(View v) {
//		String colorValue = getResources().getString(R.color.new_green);
//		if ("#ffff0000".equals(colorValue)) {
//			mHeaderIcon.setImageResource(R.drawable.user_new_logo_red);
//			// changeColorAndDrawable(v, R.id.user_shoucang,
//			// R.drawable.user_shoucang_red, R.drawable.user_right_al);
//			// changeColorAndDrawable(v, R.id.user_message,
//			// R.drawable.user_new_message_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.my_activity, R.drawable.my_huodong_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.my_tiezi, R.drawable.my_quanzi_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.all_order, R.drawable.all_orders_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.shop_cart, R.drawable.shop_cart_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.shouhuo_address, R.drawable.shouhuo_address_red, R.drawable.user_right_al);
//			changeColorAndDrawable(v, R.id.care_goods, R.drawable.care_goods_red, R.drawable.user_right_al);
//		}
	}

	/**
	 * 改变图标以及字体颜色
	 */
	private void changeColorAndDrawable(View v, int viewId, int leftImageid, int rightImageid) {
		TextView tv = (TextView) v.findViewById(viewId);
		Drawable leftD = getResources().getDrawable(leftImageid);
		Drawable rightD = getResources().getDrawable(rightImageid);
		leftD.setBounds(0, 0, leftD.getMinimumWidth(), leftD.getMinimumHeight());
		rightD.setBounds(0, 0, rightD.getMinimumWidth(), rightD.getMinimumHeight());
		tv.setCompoundDrawables(leftD, null, rightD, null);
	}

	private void delayCallUserInfo() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getUserInfo();
			}
		}, 1000);
	}

	@Override
	public void onResume() {
		super.onResume();
		initBraodcast();
		getUserInfo();
		getOrderNum();
		haveMsg = new boolean[] { false, false, false };
		getNoReadMsgCount();
	}

	/**
	 * 读取未读消息数量
	 */
	private void getNoReadMsgCount() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "getNotificationNum");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		VolleyRequset.doPost(getActivity(), MConfig.mQuanZiApiUrl, "getNoReadMsgCount", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				try {
					JSONObject jObject = new JSONObject(arg0);
					String talkMsgCount = jObject.getString("newTalkMsgCount");
					String tiezMsgCount = jObject.getString("newSubjectMsgCount");
					if (!isShowSmallRedPoint) {
						if (Integer.valueOf(talkMsgCount) > 0) {
							smallRedPoint.setVisibility(View.VISIBLE);
							haveMsg[0] = true;
							isShowSmallRedPoint = true;
						}
						if (Integer.valueOf(tiezMsgCount) > 0) {
							smallRedPoint.setVisibility(View.VISIBLE);
							haveMsg[1] = true;
							isShowSmallRedPoint = true;
						}
						// if (Integer.valueOf(talkMsgCount) == 0 &&
						// Integer.valueOf(tiezMsgCount) == 0) {
						// smallRedPoint.setVisibility(View.GONE);
						// haveMsg[0] = haveMsg[1] = false;
						// isShowSmallRedPoint = false;
						// }
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {

			}
		});
	}

	private void initBraodcast() {
		MsgCome mc = new MsgCome();
		IntentFilter filter = new IntentFilter("NEWMSG_COME");
		getActivity().registerReceiver(mc, filter);
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// ViewUtils.inject(this);
	//
	// }
	@GinOnClick(id = { R.id.setting, R.id.top_back, R.id.my_tiezi, R.id.my_guanzhu, R.id.my_activity, R.id.all_order,
			R.id.shop_cart, R.id.shouhuo_address, R.id.care_goods, R.id.re_login_or_update, R.id.usercenter_setting,
			R.id.usercenter_waittopaylayout, R.id.usercenter_waittosendlayout, R.id.usercenter_waittogetlayout,
			R.id.usercenter_donelayout })
	public void onclick(View view) {
		SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);
		switch (view.getId()) {
		case R.id.care_goods:
			// ToastUtil.showToast(getActivity(), "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			Intent collectIntent = new Intent(getActivity(), MyCollectionKindActivity.class);
			getActivity().startActivity(collectIntent);
			break;
		case R.id.shouhuo_address:
			// ToastUtil.showToast(getActivity(), "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			Intent addressIntent = new Intent(getActivity(), MyAddressActivity.class);
			getActivity().startActivity(addressIntent);
			break;
		case R.id.shop_cart:
			// ToastUtil.showToast(getActivity(), "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			Intent cartIntent = new Intent(getActivity(), MyShopingCartActivity.class);
			getActivity().startActivity(cartIntent);
			break;
		case R.id.all_order:
			// ToastUtil.showToast(getActivity(), "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			Intent orderIntent = new Intent(getActivity(), MyOrderActivity.class);
			orderIntent.putExtra("getType", 0);
			startActivity(orderIntent);
			break;
		case R.id.usercenter_waittopaylayout:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			orderIntent = new Intent(getActivity(), MyOrderActivity.class);
			orderIntent.putExtra("getType", 1);
			startActivity(orderIntent);
			break;
		case R.id.usercenter_waittosendlayout:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			orderIntent = new Intent(getActivity(), MyOrderActivity.class);
			orderIntent.putExtra("getType", 2);
			startActivity(orderIntent);
			break;
		case R.id.usercenter_waittogetlayout:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			orderIntent = new Intent(getActivity(), MyOrderActivity.class);
			orderIntent.putExtra("getType", 3);
			startActivity(orderIntent);
			break;
		case R.id.usercenter_donelayout:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			orderIntent = new Intent(getActivity(), MyOrderActivity.class);
			orderIntent.putExtra("getType", 4);
			startActivity(orderIntent);
			break;

		case R.id.my_activity:

			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), MyActivitiesFragment.class));
			break;
		case R.id.my_tiezi:
			// ToastUtil.showToast(getActivity(), "功能在开发中……");
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), MyGroupList.class));
			break;
		case R.id.my_guanzhu:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), MyGuanZhuActivity.class));
			break;
		// case R.id.user_shoucang:
		// if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id",
		// null))) {
		// startActivity(new Intent(getActivity(), LoginActivity.class));
		// return;
		// }
		// startActivity(new Intent(getActivity(),
		// NewCollectionActivity.class));
		// break;
		case R.id.setting:// TODO 顶部右上角的设置按钮，改成了消息
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			smallRedPoint.setVisibility(View.GONE);
			isShowSmallRedPoint = false;
			Intent intent = new Intent(getActivity(), MyMsgActivity.class);
			intent.putExtra("havemsg", haveMsg);
			startActivity(intent);
			break;
		case R.id.usercenter_setting:// 以前的顶部右上角的设置功能
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		case R.id.top_back:
			getActivity().finish();
			break;
		case R.id.re_login_or_update:
			if (re_login_or_update.getTag().toString().trim().equals("1")) {
				startActivity(new Intent(getActivity(), UploadUserInfoActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		}
	}

	public void setUserInfo(String headerIcon, String nickname, String loginState, String gender, String telPhone) {
		re_login_or_update.setTag("1");
		if (!TextUtils.isEmpty(headerIcon)) {
			// 改成新的图片框架
			ImageLoader.getInstance().displayImage(headerIcon, mHeaderIcon, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String arg0, View arg1) {

				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					SharedPreferences auserInfo = getActivity().getSharedPreferences("user_info", 0);
					if (auserInfo != null && !TextUtils.isEmpty(auserInfo.getString("id", null))) {
						syncUserInfoInCms(auserInfo.getString("id", ""), auserInfo.getString("nickname", ""),
								auserInfo.getString("gender", ""), auserInfo.getString("email", ""), null);
					}
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					SharedPreferences auserInfo = getActivity().getSharedPreferences("user_info", 0);
					if (auserInfo != null && !TextUtils.isEmpty(auserInfo.getString("id", null))) {
						syncUserInfoInCms(auserInfo.getString("id", ""), auserInfo.getString("nickname", ""),
								auserInfo.getString("gender", ""), auserInfo.getString("email", ""),
								Base64Parse.bitmapToBase64(arg2));
					}
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					SharedPreferences auserInfo = getActivity().getSharedPreferences("user_info", 0);
					if (auserInfo != null && !TextUtils.isEmpty(auserInfo.getString("id", null))) {
						syncUserInfoInCms(auserInfo.getString("id", ""), auserInfo.getString("nickname", ""),
								auserInfo.getString("gender", ""), auserInfo.getString("email", ""), null);
					}
				}
			});
			// mHeaderIcon.setNetImage(headerIcon);
		} else {
			mHeaderIcon.setImageResource(R.drawable.logined_default_head);
		}
		if (!TextUtils.isEmpty(nickname)) {
			mNickname.setText(nickname);
		} else {
			mNickname.setText("匿名");
		}
		if (!TextUtils.isEmpty(telPhone) && StringUtils.isNumeric(telPhone)) {
			userTelPhone.setVisibility(View.VISIBLE);
			String anquanTel = "";
			try {
				anquanTel = telPhone.substring(0, 3) + "****" + telPhone.substring(7);
			} catch (Exception e) {
				anquanTel = telPhone;
			}
			userTelPhone.setText(anquanTel);
		} else {
			userTelPhone.setVisibility(View.GONE);
		}
		Drawable leftDrawable = getResources().getDrawable(R.drawable.trans);
		if (!TextUtils.isEmpty(loginState)) {
			switch (LoginMode.valueOf(loginState)) {
			case Login_SinaWB:
				// mLoginStateIcon.setImageResource(R.drawable.login_state_sinawb_icon);
				leftDrawable = getResources().getDrawable(R.drawable.login_state_sinawb_icon);
				break;
			case Login_TencentWB:
				// mLoginStateIcon.setImageResource(R.drawable.login_state_tencentwb_icon);
				leftDrawable = getResources().getDrawable(R.drawable.login_state_tencentwb_icon);
				break;
			case Login_QQ:
				// mLoginStateIcon.setImageResource(R.drawable.login_state_qq_icon);
				leftDrawable = getResources().getDrawable(R.drawable.login_state_qq_icon);
				break;
			case Login_WeiXin:
				// mLoginStateIcon.setImageResource(R.drawable.login_state_wx_icon);
				leftDrawable = getResources().getDrawable(R.drawable.login_state_wx_icon);
				break;
			default:
				// mLoginStateIcon.setImageResource(R.drawable.trans);
				leftDrawable = getResources().getDrawable(R.drawable.trans);
				break;
			}
		}
		// 设置第三方登录图标
		// leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
		// leftDrawable.getMinimumHeight());
		// mNickname.setCompoundDrawables(leftDrawable, null, null, null);
	}

	public void clearUserInfo() {
		// if (mLoginLayout != null) {
		// mLoginLayout.setVisibility(View.GONE);
		// mLogoutLayout.setVisibility(View.VISIBLE);
		re_login_or_update.setTag("2");
//		String colorValue = getResources().getString(R.color.new_green);
//		if ("#ffff0000".equals(colorValue)) {
//			mHeaderIcon.setImageResource(R.drawable.user_new_logo_red);
//		} else {
//			mHeaderIcon.setImageResource(R.drawable.nologin_default_head);
//		}
		mHeaderIcon.setImageResource(R.drawable.nologin_default_head);
		mNickname.setText(getString(R.string.personmenu_clicklogin));
		userTelPhone.setVisibility(View.GONE);
		// mLoginStateIcon.setImageResource(R.drawable.trans);
		// mGenderIcon.setImageResource(R.drawable.trans);
		// }
	}// 74369264

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
		if (getActivity() == null) {
			return;
		}
		final SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);
		if (userInfo != null && !TextUtils.isEmpty(userInfo.getString("id", null))) {
			setUserInfo(userInfo.getString("headicon", ""), userInfo.getString("nickname", ""),
					userInfo.getString("state", ""), userInfo.getString("gender", ""),
					userInfo.getString("telphone", ""));
		} else {
			clearUserInfo();
		}
		// new Thread() {
		// public void run() {
		// Bitmap bt = returnBitmap(userInfo.getString("headicon", ""));
		// if (bt != null) {
		//
		// handler.obtainMessage(0x100, bt).sendToTarget();
		// }
		// };
		// }.start();
	}

	private void syncUserInfoInCms(String userName, String nickName, String sex, String email, String logo) {
		SignUtil.modifyUserInfoFromUCenter(userName, nickName, sex, email, logo, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// Toast.makeText(getActivity(), "同步成功",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// Toast.makeText(getActivity(), "同步失败",
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取订单状态的数目，显示在小圆圈里面
	 */
	private void getOrderNum() {
		new GetMyOrderData().execute();
	}

	public class GetMyOrderData extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... arg0) {
			JSONObject array = News.getOrderListSize(PreferencesUtil.getLoggedUserId());
			return array;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			refreshData(result);
		}
	}

	public void refreshData(JSONObject result) {
		try {
			if (result == null) {
				return;
			}
			int code = result.getInt("code");
			if (code == 2000) {
				/**
				 * all|所有订单|int|-| |unpaid|待支付|int|-| |unprocessed|待发货|int|-|
				 * |processed|待收货|int|-| |completed|已完成|int|-|
				 * |canceled|已取消|int|-| |deleted|已作废|int|-| |refund|退款|int|-|
				 * |partrefund|部分退款|int|-|
				 */
				JSONObject jObject = result.getJSONObject("data");
				int waitPay = jObject.getInt("unpaid");
				int waitSend = jObject.getInt("unprocessed");
				int waitGet = jObject.getInt("processed");
				int done = jObject.getInt("completed");
				setOrderNum(waitPay, waitSend, waitGet, done);
			} else {
				waitToPayTv.setVisibility(View.GONE);
				waitToSendTv.setVisibility(View.GONE);
				waitToGetTv.setVisibility(View.GONE);
				doneTv.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置4个状态的数字
	 * 
	 * @param waitPay
	 * @param waitSend
	 * @param waitGet
	 * @param done
	 */
	private void setOrderNum(int waitPay, int waitSend, int waitGet, int done) {
		if (waitPay != 0) {
			waitToPayTv.setVisibility(View.VISIBLE);
			waitToPayTv.setText("" + waitPay);
		} else {
			waitToPayTv.setVisibility(View.GONE);
		}
		if (waitSend != 0) {
			waitToSendTv.setVisibility(View.VISIBLE);
			waitToSendTv.setText("" + waitSend);
		} else {
			waitToSendTv.setVisibility(View.GONE);
		}
		if (waitGet != 0) {
			waitToGetTv.setVisibility(View.VISIBLE);
			waitToGetTv.setText("" + waitGet);
		} else {
			waitToGetTv.setVisibility(View.GONE);
		}
		// if (done != 0) {
		// doneTv.setVisibility(View.VISIBLE);
		// doneTv.setText("" + done);
		// } else {
		// 已完成的不用显示数字
		doneTv.setVisibility(View.GONE);
		// }
	}

	class MsgCome extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!isShowSmallRedPoint) {
				smallRedPoint.setVisibility(View.VISIBLE);
				haveMsg[2] = true;
				isShowSmallRedPoint = true;
			}
		}

	}
}
