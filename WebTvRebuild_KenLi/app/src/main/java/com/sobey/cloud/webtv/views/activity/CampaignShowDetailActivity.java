package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.androidadvancedui.AdvancedListView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.ScaleConversion;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.VideoAndNormalNewsListActivity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampaignShowDetailActivity extends BaseActivity {
	private LayoutInflater mInflater;
	private JSONObject mInformation;
	private String mUserName;
	private ArrayList<String> mVotedPlayerIdList = new ArrayList<String>();
	private BaseAdapter mAdapter;
	private ArrayList<ArticleObj> mArticles = new ArrayList<ArticleObj>();
	private TextView supportSuccessTv;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	CustomTitleView mHeaderCtv;
	// @GinInjectView(id = R.id.mCampaigndetailImageCarousel)
	AdvancedImageCarousel mCampaigndetailImageCarousel;
	// @GinInjectView(id = R.id.mCampaigndetailTabLayout)
	LinearLayout mCampaigndetailTabLayout;
	@GinInjectView(id = R.id.mCampaigndetailListView)
	AdvancedListView mCampaigndetailListView;
	// @GinInjectView(id = R.id.mCampaigndetailSignUpBtn)
	Button mCampaigndetailSignUpBtn;
	// @GinInjectView(id = R.id.mCampaigndetailFinishBtn)
	Button mCampaigndetailFinishBtn;

	// @GinInjectView(id = R.id.mCampaigndetailInfoTitleTextView)
	TextView mCampaigndetailInfoTitleTextView;
	// @GinInjectView(id = R.id.mCampaigndetailTimeTextView)
	TextView mCampaigndetailTimeTextView;
	// @GinInjectView(id = R.id.mCampaigndetailAddressTextView)
	TextView mCampaigndetailAddressTextView;
	// @GinInjectView(id = R.id.mCampaigndetailSummaryTextView)
	TextView mCampaigndetailSummaryTextView;
	// @GinInjectView(id = R.id.mCampaigndetailSignUpSumTextView)
	TextView mCampaigndetailSignUpSumTextView;

	private String signUpURL;

	private View headView;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_show_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		initHeadView();
		init();
	}

	private void initHeadView() {
		mInflater = LayoutInflater.from(this);
		headView = mInflater.inflate(R.layout.toupiao_headview, null);

		mCampaigndetailImageCarousel = (AdvancedImageCarousel) headView.findViewById(R.id.mCampaigndetailImageCarousel);
		mCampaigndetailInfoTitleTextView = (TextView) headView.findViewById(R.id.mCampaigndetailInfoTitleTextView);
		mCampaigndetailTimeTextView = (TextView) headView.findViewById(R.id.mCampaigndetailTimeTextView);
		mCampaigndetailAddressTextView = (TextView) headView.findViewById(R.id.mCampaigndetailAddressTextView);
		mCampaigndetailSummaryTextView = (TextView) headView.findViewById(R.id.mCampaigndetailSummaryTextView);
		mCampaigndetailSignUpSumTextView = (TextView) headView.findViewById(R.id.mCampaigndetailSignUpSumTextView);

		mCampaigndetailSignUpBtn = (Button) headView.findViewById(R.id.mCampaigndetailSignUpBtn);
		mCampaigndetailTabLayout = (LinearLayout) headView.findViewById(R.id.mCampaigndetailTabLayout);
		mCampaigndetailFinishBtn = (Button) headView.findViewById(R.id.mCampaigndetailFinishBtn);
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

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

		mCampaigndetailImageCarousel.setScaleType(ScaleType.CENTER_CROP);
		mCampaigndetailImageCarousel.setIntervalTime(3000);
		mCampaigndetailImageCarousel.setDotViewMargin(0, 0, ScaleConversion.dip2px(this, 10),
				ScaleConversion.dip2px(this, 8));

		mHeaderCtv.setTitle(mInformation.optString("Name"));
		SharedPreferences settings = this.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(this);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		if (isShowPicture) {
			final AdvancedImageView image = new AdvancedImageView(this);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			image.setLayoutParams(layoutParams);
			image.setErrorImage(R.drawable.default_thumbnail_banner);
			image.setLoadingImage(R.drawable.default_thumbnail_banner);
			image.setScaleType(ScaleType.FIT_XY);
			image.setImageDrawable(getResources().getDrawable(R.drawable.default_thumbnail_banner));
			if (isShowPicture)
				ImageLoader.getInstance().displayImage(mInformation.optString("PosterUrl"), image);
			mCampaigndetailImageCarousel.removeAllCarouselView();
			mCampaigndetailImageCarousel.addCarouselView(image);
		}
		mCampaigndetailImageCarousel.refreshLayout();
		initListView();
		loadTab();
		// loadSignUp();
	}

	private void initListView() {

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.listitem_campaign_detail, null);
					viewHolder = new ViewHolder();
					viewHolder.image = (AdvancedImageView) convertView.findViewById(R.id.image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.title);
					viewHolder.summary = (TextView) convertView.findViewById(R.id.summary);
					viewHolder.upnum = (TextView) convertView.findViewById(R.id.upnum_textview);
					viewHolder.vote = (Button) convertView.findViewById(R.id.vote_textview);
					viewHolder.playerItemRl = (RelativeLayout) convertView.findViewById(R.id.player_item_rl);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView);
				} else {
					loadViewHolder(position, convertView);
				}
				return convertView;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				return mArticles.get(arg0);
			}

			@Override
			public int getCount() {
				return mArticles.size();
			}
		};
		mCampaigndetailListView.initAdvancedListView(this, null, null);
		mCampaigndetailListView.addHeaderView(headView);
		mCampaigndetailListView.setAdapter(mAdapter);
		mCampaigndetailListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Intent intent = new Intent(CampaignShowDetailActivity.this, CampaignPlayerDetailActivity.class);
//				intent.putExtra("information", mArticles.get(arg2 - 1).jsonObject.toString());
//				startActivity(intent);
			}
		});
	}

	private void loadViewHolder(final int position, View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		try {
			JSONObject obj = mArticles.get(position).jsonObject;
			SharedPreferences settings = this.getSharedPreferences("settings", 0);
			CheckNetwork network = new CheckNetwork(this);
			boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
					|| network.getWifiState(false) == State.CONNECTED;
			if (isShowPicture) {
				viewHolder.image.setNetImage(obj.optString("logo"));
			}
			viewHolder.title.setText(obj.optString("name"));
			viewHolder.summary.setText(obj.optString("summary"));
			// viewHolder.upnum.setText(String.valueOf(mArticles.get(position).hitCount));
			viewHolder.upnum.setText(obj.optString("hitcount"));
			viewHolder.vote.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					vote(position);
				}
			});
			viewHolder.playerItemRl.setTag(obj.toString());
			viewHolder.playerItemRl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CampaignShowDetailActivity.this, CampaignPlayerDetailActivity.class);
					intent.putExtra("information", (String)v.getTag());
					startActivity(intent);
				}
			});
		} catch (Exception e) {
		}
	}

	/**
	 * 获取现场信息
	 */
	private void loadTab() {
		News.getActivityCatalog(mInformation.optString("ID"), this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					mCampaigndetailTabLayout.removeAllViews();
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							// final JSONObject obj = result.optJSONObject(i);
							// obj.put("activity_id",
							// mInformation.optString("ID"));
							// TextView tabTextView = new
							// TextView(CampaignShowDetailActivity.this);
							// tabTextView.setLayoutParams(
							// new LinearLayout.LayoutParams(0,
							// LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
							// tabTextView.setGravity(Gravity.CENTER);
							// tabTextView.setTextSize(18);
							// tabTextView.setTextColor(0xff000000);
							// tabTextView.setText(obj.optString("catalogname"));
							// tabTextView.setOnClickListener(new
							// OnClickListener() {
							// @Override
							// public void onClick(View arg0) {
							// // Intent intent = new
							// // Intent(CampaignShowDetailActivity.this,
							// // CampaignArticleListActivity.class);
							// // intent.putExtra("information",
							// // obj.toString());
							// Intent intent = new
							// Intent(CampaignShowDetailActivity.this,
							// VideoAndNormalNewsListActivity.class);
							// intent.putExtra("ids", obj.optString("id"));
							// intent.putExtra("title",
							// obj.optString("catalogname"));
							// startActivity(intent);
							// }
							// });
							// mCampaigndetailTabLayout.addView(tabTextView);
							// if (i < result.length() - 1) {
							// View view = new
							// View(CampaignShowDetailActivity.this);
							// view.setBackgroundColor(0xffdcdcdc);
							// LinearLayout.LayoutParams params = new
							// LinearLayout.LayoutParams(1,
							// LinearLayout.LayoutParams.MATCH_PARENT);
							// params.setMargins(0, 5, 0, 5);
							// view.setLayoutParams(params);
							// mCampaigndetailTabLayout.addView(view);
							// }

							final JSONObject localJSONObject = result.optJSONObject(i);
							localJSONObject.put("activity_id", localJSONObject.optString("ID"));
							View localView = getLayoutInflater().inflate(R.layout.layout_activity_tab_item, null);
							RelativeLayout localRelativeLayout = (RelativeLayout) localView.findViewById(R.id.click_rl);
							((TextView) localView.findViewById(R.id.name_tv))
									.setText(localJSONObject.optString("catalogname"));
							localRelativeLayout.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent localIntent = new Intent(CampaignShowDetailActivity.this,
											VideoAndNormalNewsListActivity.class);
									localIntent.putExtra("ids", localJSONObject.optString("id"));
									localIntent.putExtra("title", localJSONObject.optString("catalogname"));
									startActivity(localIntent);
								}
							});
							mCampaigndetailTabLayout.addView(localView);
						}
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onNG(String reason) {
			}

			@Override
			public void onCancel() {
			}
		});

	}

	private void loadSignUp() {
		News.getActivitySignUpPage(mInformation.optString("ID"), this, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					if (!TextUtils.isEmpty(result.optString("SignUpPageUrl"))) {
						final String url = result.optString("SignUpPageUrl");
						signUpURL = url;
						mCampaigndetailSignUpBtn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (TextUtils.isEmpty(mUserName)) {
									startActivity(new Intent(CampaignShowDetailActivity.this, LoginActivity.class));
									return;
								}
								invokeJoinUpActivity();
								// Intent intent = new
								// Intent(CampaignShowDetailActivity.this,
								// CampaignSignUpActivity.class);
								// intent.putExtra("information", url);
								// startActivity(intent);
							}
						});
					} else {
						mCampaigndetailSignUpBtn.setVisibility(View.GONE);
						mCampaigndetailFinishBtn.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					mCampaigndetailSignUpBtn.setVisibility(View.GONE);
					mCampaigndetailFinishBtn.setVisibility(View.GONE);
				} finally {
					mCloseLoadingIcon();
				}
			}

			@Override
			public void onNG(String reason) {
				mCampaigndetailSignUpBtn.setVisibility(View.GONE);
				mCampaigndetailFinishBtn.setVisibility(View.GONE);
				mCloseLoadingIcon();
			}

			@Override
			public void onCancel() {
				mCampaigndetailSignUpBtn.setVisibility(View.GONE);
				mCampaigndetailFinishBtn.setVisibility(View.GONE);
				mCloseLoadingIcon();
			}
		});
	}

	/**
	 * 检测用户是不是参加过
	 */
	private void checkUserHadJoinActiviy() {
		OnJsonArrayResultListener listener = new OnJsonArrayResultListener() {

			@Override
			public void onOK(JSONArray result) {
				boolean hadJoinedCurrentActivity = false;
				for (int index = 0; index < result.length(); index++) {
					try {
						// 如果参加过活动
						if (result.getJSONObject(index).getString("ID").equals(mInformation.getString("ID"))) {
							hadJoinedCurrentActivity = true;
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (hadJoinedCurrentActivity) {
					mCampaigndetailSignUpBtn.setVisibility(View.GONE);
					mCampaigndetailFinishBtn.setVisibility(View.VISIBLE);
					// Toast.makeText(CampaignShowDetailActivity.this,
					// "亲，您已报过名啦！", Toast.LENGTH_SHORT).show();
				} else {
					// invokeJoinUpActivity();
					mCampaigndetailSignUpBtn.setVisibility(View.VISIBLE);
					mCampaigndetailFinishBtn.setVisibility(View.GONE);
				}
				loadContent();
			}

			@Override
			public void onNG(String reason) {
				mCampaigndetailSignUpBtn.setVisibility(View.VISIBLE);
				mCampaigndetailFinishBtn.setVisibility(View.GONE);
				loadContent();
			}

			@Override
			public void onCancel() {
				mCampaigndetailSignUpBtn.setVisibility(View.VISIBLE);
				mCampaigndetailFinishBtn.setVisibility(View.GONE);
				loadContent();
			}
		};
		News.getUserActivityList(mUserName, this, listener);
	}

	/**
	 * 请求报名页
	 */
	protected void invokeJoinUpActivity() {
		Intent intent = new Intent(CampaignShowDetailActivity.this, CampaignSignUpActivity.class);
		intent.putExtra("information", signUpURL);
		startActivity(intent);
	}

	/**
	 * 获取投票活动信息
	 */
	private void getActivityInfo() {
		News.getActivityDetails(mInformation.optString("ID"), mUserName, this, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				Log.i("fuck", result.toString());
				// SharedPreferences settings =
				// CampaignShowDetailActivity.this.getSharedPreferences("settings",
				// 0);
				// CheckNetwork network = new
				// CheckNetwork(CampaignShowDetailActivity.this);
				// boolean isShowPicture = (settings.getInt("show_picture", 1)
				// == 1 ? true : false)
				// || network.getWifiState(false) == State.CONNECTED;
				// mCampaigndetailImageCarousel.setLoadingImage(R.drawable.default_thumbnail_banner);
				// mCampaigndetailImageCarousel.setDefaultImage(R.drawable.default_thumbnail_banner);
				// if (isShowPicture) {
				// }
				signUpURL = result.optString("SignUpPageUrl");
				mCampaigndetailSignUpBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (TextUtils.isEmpty(mUserName)) {
							startActivity(new Intent(CampaignShowDetailActivity.this, LoginActivity.class));
							return;
						}
						invokeJoinUpActivity();
					}
				});
				mCampaigndetailInfoTitleTextView.setText(checkResult(result.optString("Title")));
				if (TextUtils.isEmpty(result.optString("DetailsSum"))) {
					mCampaigndetailSummaryTextView.setVisibility(View.GONE);
				} else {
					mCampaigndetailSummaryTextView.setText(result.optString("DetailsSum"));
				}

				mCampaigndetailTimeTextView.setText(checkResult(result.optString("Time")));
				// mCampaigndetailSignUpSumTextView.setText(
				// (TextUtils.isEmpty(result.optString("SignUpNum")) ? "0" :
				// result.optString("SignUpNum"))
				// + "人参与");
				mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(mInformation.optString("JoinNumber")) ? "0"
						: mInformation.optString("JoinNumber")) + "人参与");
				mCampaigndetailAddressTextView.setText(checkResult(result.optString("Address")));
				if (!TextUtils.isEmpty(result.optString("SignUpStatus"))) {
					if (result.optString("SignUpStatus").equalsIgnoreCase("0")) {
						mCampaigndetailFinishBtn.setVisibility(View.GONE);
						mCampaigndetailSignUpBtn.setVisibility(View.VISIBLE);
					} else if (result.optString("SignUpStatus").equalsIgnoreCase("1")) {
						mCampaigndetailFinishBtn.setVisibility(View.VISIBLE);
						mCampaigndetailFinishBtn.setText("已报名");
						mCampaigndetailSignUpBtn.setVisibility(View.GONE);
					} else if (result.optString("SignUpStatus").equalsIgnoreCase("2")) {
						mCampaigndetailFinishBtn.setVisibility(View.GONE);
						mCampaigndetailFinishBtn.setText("活动已结束");
						mCampaigndetailSignUpBtn.setVisibility(View.GONE);
					}
				} else {
					if (result.optString("Status").equalsIgnoreCase("1")) {
						mCampaigndetailFinishBtn.setVisibility(View.GONE);
						mCampaigndetailSignUpBtn.setVisibility(View.VISIBLE);
					} else if (result.optString("Status").equalsIgnoreCase("2")) {
						mCampaigndetailFinishBtn.setVisibility(View.GONE);
						mCampaigndetailFinishBtn.setText("报名已截止");
						mCampaigndetailSignUpBtn.setVisibility(View.GONE);
					}
				}
				mCloseLoadingIcon();
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
		loadContent();
	}

	/**
	 * 检测活动信息内容
	 * 
	 * @param result
	 * @return
	 */
	private String checkResult(String result) {
		return TextUtils.isEmpty(result) ? "无" : result;
	}

	/**
	 * 获取参赛选手列表
	 */
	private void loadContent() {
		News.getActivityPlayerList(mInformation.optString("ID"), this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					mArticles.clear();
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject obj = result.optJSONObject(i);
							obj.put("activity_id", mInformation.optString("ID"));
							mArticles.add(new ArticleObj(obj, TextUtils.isEmpty(obj.optString("hitcount")) ? 0
									: Integer.valueOf(obj.optString("hitcount"))));
						}
					}
					mAdapter.notifyDataSetChanged();
				} catch (Exception e) {
				} finally {
					// loadSignUp();
					// mCloseLoadingIcon();
				}
			}

			@Override
			public void onNG(String reason) {
				// mCloseLoadingIcon();
				// loadSignUp();
			}

			@Override
			public void onCancel() {
				// mCloseLoadingIcon();
				// loadSignUp();
			}
		});
	}

	private void vote(final int position) {
		// TODO 投票频率过快问题
		if (!TextUtils.isEmpty(mUserName)) {
			JSONObject jsonObject = mArticles.get(position).jsonObject;
			// for (String playerId : mVotedPlayerIdList) {
			// if (playerId.equalsIgnoreCase(jsonObject.optString("id"))) {
			// Toast.makeText(CampaignShowDetailActivity.this, "请勿重复投票",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			// }
			News.voteActivityPlayer(mInformation.optString("ID"), jsonObject.optString("id"),
					jsonObject.optString("name"), mUserName, this, new OnJsonObjectResultListener() {
						@Override
						public void onOK(JSONObject result) {
							try {
								if (result.optInt("Status") == 1) {
									// mArticles.get(position).hitCount++;
									(mArticles.get(position).jsonObject).put("hitcount",
											++mArticles.get(position).hitCount);
									mVotedPlayerIdList.add(mArticles.get(position).jsonObject.optString("id"));
									mAdapter.notifyDataSetChanged();
									if (supportSuccessTv == null) {
										supportSuccessTv = (TextView) findViewById(R.id.mCampaigndetail_addp);
									}
									Animation anim_praise = AnimationUtils
											.loadAnimation(CampaignShowDetailActivity.this, R.anim.short_notice);
									supportSuccessTv.setText("投票成功");
									supportSuccessTv.setVisibility(View.VISIBLE);
									supportSuccessTv.setAnimation(anim_praise);
									new Handler().postDelayed(new Runnable() {
										public void run() {
											supportSuccessTv.setVisibility(View.GONE);
										}
									}, 1000);
									// Animation animation =
									// Toast.makeText(CampaignShowDetailActivity.this,
									// "投票成功!", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(CampaignShowDetailActivity.this, result.optString("Message"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								Toast.makeText(CampaignShowDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT)
										.show();
							}
						}

						@Override
						public void onNG(String reason) {
							Toast.makeText(CampaignShowDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onCancel() {
							Toast.makeText(CampaignShowDetailActivity.this, "网络繁忙，请稍后投票", Toast.LENGTH_SHORT).show();
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
		// if (TextUtils.isEmpty(mUserName)) {
		// loadContent();
		// } else {
		// checkUserHadJoinActiviy();
		// }
		getActivityInfo();
		super.onResume();
	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	private class ViewHolder {
		AdvancedImageView image;
		TextView title;
		TextView summary;
		TextView upnum;
		Button vote;
		RelativeLayout playerItemRl;
	}

	private class ArticleObj {
		JSONObject jsonObject;
		int hitCount;

		public ArticleObj(JSONObject jsonObject, int hitCount) {
			this.jsonObject = jsonObject;
			this.hitCount = hitCount;
		}
	}
}
