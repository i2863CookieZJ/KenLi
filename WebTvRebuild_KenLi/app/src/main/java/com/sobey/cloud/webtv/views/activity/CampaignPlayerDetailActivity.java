package com.sobey.cloud.webtv.views.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampaignPlayerDetailActivity extends BaseActivity {
	private LayoutInflater mInflater;
	private JSONObject mInformation;
	private String mUserName;
	private int mHitCount = 0;
	private boolean mVoted = false;
	private TextView supportSuccessTv;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.mCampaignPlayerDetailLogoImageView)
	AdvancedImageView mCampaignPlayerDetailLogoImageView;
	@GinInjectView(id = R.id.mCampaignPlayerDetailNameTextView)
	TextView mCampaignPlayerDetailNameTextView;
	@GinInjectView(id = R.id.mCampaignPlayerDetailSummaryTextView)
	TextView mCampaignPlayerDetailSummaryTextView;
	@GinInjectView(id = R.id.mCampaignPlayerDetailUpNumTextView)
	TextView mCampaignPlayerDetailUpNumTextView;
	@GinInjectView(id = R.id.mCampaignPlayerDetailVoteBtn)
	TextView mCampaignPlayerDetailVoteBtn;
	@GinInjectView(id = R.id.mCampaignPlayerDetailListLayout)
	LinearLayout mCampaignPlayerDetailListLayout;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_player_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		mOpenLoadingIcon();

		try {
			String str = getIntent().getStringExtra("information");
			if (!TextUtils.isEmpty(str)) {
				mInformation = new JSONObject(str);
			} else {
				finishActivity();
			}
		} catch (Exception e) {
			finishActivity();
		}

		mInflater = LayoutInflater.from(this);

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

		mCampaignPlayerDetailVoteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				vote();
			}
		});

		mHeaderCtv.setTitle(mInformation.optString("name"));
		loadContent();
	}

	private void loadContent() {
		SharedPreferences settings = this.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(this);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		if (isShowPicture)
			mCampaignPlayerDetailLogoImageView.setNetImage(mInformation.optString("logo"));
		mCampaignPlayerDetailNameTextView.setText(mInformation.optString("name"));
		mCampaignPlayerDetailSummaryTextView.setText(mInformation.optString("summary"));
		mHitCount = TextUtils.isEmpty(mInformation.optString("hitcount")) ? 0
				: Integer.valueOf(mInformation.optString("hitcount"));
		mCampaignPlayerDetailUpNumTextView.setText(String.valueOf(mHitCount));

		News.getActivityPlayerArticleList(mInformation.optString("id"), this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0) {
						mCampaignPlayerDetailListLayout.removeAllViews();
						for (int i = 0; i < result.length(); i++) {
							final JSONObject obj = result.optJSONObject(i);
							obj.put("player_id", mInformation.optString("id"));
							View view = mInflater.inflate(R.layout.listitem_campaign_player_detail, null);
							SharedPreferences settings = CampaignPlayerDetailActivity.this
									.getSharedPreferences("settings", 0);
							CheckNetwork network = new CheckNetwork(CampaignPlayerDetailActivity.this);
							boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
									|| network.getWifiState(false) == State.CONNECTED;
							if (isShowPicture)
								((AdvancedImageView) view.findViewById(R.id.logo_imageview))
										.setNetImage(obj.optString("logo"));
							((TextView) view.findViewById(R.id.title_textview)).setText(obj.optString("summary"));
							view.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									Intent intent = new Intent(CampaignPlayerDetailActivity.this,
											CampaignPlayerArticleDetailActivity.class);
									intent.putExtra("information", obj.toString());
									startActivity(intent);
								}
							});
							mCampaignPlayerDetailListLayout.addView(view);
						}
					}
				} catch (Exception e) {
				} finally {
					mCloseLoadingIcon();
				}

			}

			@Override
			public void onNG(String reason) {
				mCloseLoadingIcon();
			}

			@Override
			public void onCancel() {
				mCloseLoadingIcon();
			}
		});
	}

	private void vote() {
		if (!TextUtils.isEmpty(mUserName)) {
			// if (mVoted) {
			// Toast.makeText(CampaignPlayerDetailActivity.this, "请勿重复投票",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			News.voteActivityPlayer(mInformation.optString("activity_id"), mInformation.optString("id"),
					mInformation.optString("name"), mUserName, this, new OnJsonObjectResultListener() {
						@Override
						public void onOK(JSONObject result) {
							try {
								if (result.optInt("Status") == 1) {
									mCampaignPlayerDetailUpNumTextView.setText(String.valueOf(++mHitCount));
									mVoted = true;
									if (supportSuccessTv == null) {
										supportSuccessTv = (TextView) findViewById(R.id.mCampaignPlayerDetail_addp);
									}
									Animation anim_praise = AnimationUtils
											.loadAnimation(CampaignPlayerDetailActivity.this, R.anim.short_notice);
									supportSuccessTv.setText("投票成功");
									supportSuccessTv.setVisibility(View.VISIBLE);
									supportSuccessTv.setAnimation(anim_praise);
									new Handler().postDelayed(new Runnable() {
										public void run() {
											supportSuccessTv.setVisibility(View.GONE);
										}
									}, 1000);
									// CommonMethod.showToast(CampaignPlayerDetailActivity.this,
									// "投票成功!");
									// Toast.makeText(CampaignPlayerDetailActivity.this,
									// "投票成功!", Toast.LENGTH_SHORT)
									// .show();
								} else {
									Toast.makeText(CampaignPlayerDetailActivity.this, result.optString("Message"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								Toast.makeText(CampaignPlayerDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT)
										.show();
							}
						}

						@Override
						public void onNG(String reason) {
							Toast.makeText(CampaignPlayerDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onCancel() {
							Toast.makeText(CampaignPlayerDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			Toast.makeText(this, "请先登录后投票", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@Override
	public void onResume() {
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		mUserName = userInfo.getString("id", "");
		StatService.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		super.onPause();
	}
}
