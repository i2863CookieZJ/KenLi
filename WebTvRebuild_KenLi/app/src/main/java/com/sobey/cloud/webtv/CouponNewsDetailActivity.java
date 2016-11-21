package com.sobey.cloud.webtv;

import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.LoadingPopWindow;
import com.sobey.cloud.webtv.utils.PhonePopWindow;
import com.sobey.cloud.webtv.utils.PhonePopWindow.PhonePopWindowCloseListener;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.utils.Utility;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class CouponNewsDetailActivity extends BaseActivity {

	private JSONObject mInformation;
	private String mUserName;
	private String mCouponId;
	private int mTimes;

	private LoadingPopWindow loadingPopWindow;
	private PhonePopWindow mPhonePopWindow;

	@GinInjectView(id = R.id.mCoupondetailFooter)
	LinearLayout mCoupondetailFooter;
	@GinInjectView(id = R.id.mCoupondetailContent)
	ScrollView mCoupondetailContent;
	@GinInjectView(id = R.id.mCoupondetailWebview)
	WebView mCoupondetailWebview;
	@GinInjectView(id = R.id.mCoupondetailBack)
	ImageButton mCoupondetailBack;
	@GinInjectView(id = R.id.mCoupondetailPicture)
	ImageButton mCoupondetailPicture;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_couponnews_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && loadingPopWindow == null) {
			int height = mCoupondetailFooter.getBottom();
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			loadingPopWindow = new LoadingPopWindow(CouponNewsDetailActivity.this, mCoupondetailFooter, height,
					statusBarHeight, 0xffffffff);
			loadingPopWindow.showLoadingWindow();
		}
		if (hasFocus) {
			SharedPreferences userInfo = CouponNewsDetailActivity.this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				mUserName = "";
			} else {
				mUserName = userInfo.getString("id", "");
			}
		}
	}

	public void setupNewsDetailActivity() {
		// Get preferrence
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		initFooter();
		String str = getIntent().getStringExtra("information");
		try {
			mInformation = new JSONObject(str);
			if (TextUtils.isEmpty(mInformation.optString("ListFlg"))) {
				mCoupondetailPicture.setVisibility(View.GONE);
			}
			loadContent();
		} catch (Exception e) {
			loadContent();
		}
	}

	private void initWebView() {
		mCoupondetailWebview.getSettings().setJavaScriptEnabled(true);
		mCoupondetailWebview.getSettings().setDefaultTextEncodingName("gbk");
		mCoupondetailWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	}

	private void loadContent() {
		initWebView();
		mCoupondetailWebview.loadUrl(mInformation.optString("Url"));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (loadingPopWindow != null) {
					try {
						loadingPopWindow.hideLoadingWindow();
					} catch (Exception e) {
					}
				}
			}
		}, 500);
	}

	private void initFooter() {
		mPhonePopWindow = new PhonePopWindow(this, mCoupondetailFooter, new PhonePopWindowCloseListener() {
			@Override
			public void onClose(boolean buttonFlag, String number) {
				sendCouponSms(number);
			}
		});
		mCoupondetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CouponNewsDetailActivity.this.finish();
			}
		});

		mCoupondetailPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CouponNewsDetailActivity.this, CouponNewsDetailShowImageActivity.class);
				intent.putExtra("information", mInformation.toString());
				CouponNewsDetailActivity.this.startActivity(intent);
			}
		});

		mCoupondetailFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(mUserName)) {
					CouponNewsDetailActivity.this
							.startActivity(new Intent(CouponNewsDetailActivity.this, LoginActivity.class));
					return;
				}
				mPhonePopWindow.showPhoneWindow();
			}
		});
	}

	private void sendCouponSms(String number) {
		if (TextUtils.isEmpty(number) || !Utility.isMobileNO(number)) {
			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			return;
		}
		mCouponId = mInformation.optString("ID");
		if (TextUtils.isEmpty(number) || TextUtils.isEmpty(mCouponId) || TextUtils.isEmpty(mUserName)) {
			return;
		}
		SharedPreferences couponTimes = CouponNewsDetailActivity.this.getSharedPreferences("coupon_times", 0);
		mTimes = couponTimes.getInt(mCouponId + "_" + mUserName, 0);
		if (mTimes >= 3) {
			Toast.makeText(CouponNewsDetailActivity.this, "该优惠券您已经下载过3次，不要太贪心哦", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(CouponNewsDetailActivity.this, "开始发送", Toast.LENGTH_SHORT).show();
		News.sendCouponSms(number, mCouponId, mUserName, CouponNewsDetailActivity.this,
				new OnJsonObjectResultListener() {
					@Override
					public void onOK(JSONObject result) {
						if (result.optString("Status").trim().equalsIgnoreCase("1")) {
							Toast.makeText(CouponNewsDetailActivity.this, result.optString("Message"),
									Toast.LENGTH_SHORT).show();
							mTimes++;
							SharedPreferences couponTimes = CouponNewsDetailActivity.this
									.getSharedPreferences("coupon_times", 0);
							Editor editor = couponTimes.edit();
							editor.putInt(mCouponId + "_" + mUserName, mTimes);
							editor.commit();
						} else {
							Toast.makeText(CouponNewsDetailActivity.this, result.optString("Message"),
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onNG(String reason) {
						Toast.makeText(CouponNewsDetailActivity.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onCancel() {
						Toast.makeText(CouponNewsDetailActivity.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
					}
				});
	}
}
