package com.sobey.cloud.webtv.views.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CampaignOffLineDetailActivity extends BaseActivity {
	private LayoutInflater mInflater;
	private JSONObject mInformation;
	private String mUserName;
	private ArrayList<NameObj> mNameObjs = new ArrayList<NameObj>();
	private ArrayList<String> mAlbumObjs = new ArrayList<String>();
	private int mPage = 0;
	private JSONObject mDetailInformation;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.mCampaigndetailDetailLayout)
	LinearLayout mCampaigndetailDetailLayout;
	@GinInjectView(id = R.id.mCampaigndetailImageView)
	AdvancedImageView mCampaigndetailImageView;
	@GinInjectView(id = R.id.mCampaigndetailInfoTitleTextView)
	TextView mCampaigndetailInfoTitleTextView;
	@GinInjectView(id = R.id.mCampaigndetailTimeLayout)
	LinearLayout mCampaigndetailTimeLayout;
	@GinInjectView(id = R.id.mCampaigndetailTimeTextView)
	TextView mCampaigndetailTimeTextView;
	@GinInjectView(id = R.id.mCampaigndetailAddressLayout)
	LinearLayout mCampaigndetailAddressLayout;
	@GinInjectView(id = R.id.mCampaigndetailAddressTextView)
	TextView mCampaigndetailAddressTextView;
	@GinInjectView(id = R.id.mCampaigndetailSummaryTextView)
	TextView mCampaigndetailSummaryTextView;
	@GinInjectView(id = R.id.mCampaigndetailFinishBtn)
	Button mCampaigndetailFinishBtn;
	@GinInjectView(id = R.id.mCampaigndetailDoingLayout)
	RelativeLayout mCampaigndetailDoingLayout;
	@GinInjectView(id = R.id.mCampaigndetailSignUpBtn)
	Button mCampaigndetailSignUpBtn;
	@GinInjectView(id = R.id.mCampaigndetailMaxTextView)
	TextView mCampaigndetailMaxTextView;
	@GinInjectView(id = R.id.mCampaigndetailSignUpSumTextView)
	TextView mCampaigndetailSignUpSumTextView;
	@GinInjectView(id = R.id.mCampaigndetailNameListLayout)
	LinearLayout mCampaigndetailNameListLayout;
	@GinInjectView(id = R.id.mCampaigndetailNameListContentLayout)
	LinearLayout mCampaigndetailNameListContentLayout;
	@GinInjectView(id = R.id.mCampaigndetailAlbumLayout)
	LinearLayout mCampaigndetailAlbumLayout;
	@GinInjectView(id = R.id.mCampaigndetailAlbumContentLayout)
	LinearLayout mCampaigndetailAlbumContentLayout;
	@GinInjectView(id = R.id.mCampaigndetailCommentLayout)
	LinearLayout mCampaigndetailCommentLayout;
	@GinInjectView(id = R.id.mCampaigndetailCommentContentLayout)
	LinearLayout mCampaigndetailCommentContentLayout;
	@GinInjectView(id = R.id.mCampaigndetailFooterSendBtn)
	Button mCampaigndetailFooterSendBtn;
	@GinInjectView(id = R.id.mCampaigndetailFooterSendEditText)
	EditText mCampaigndetailFooterSendEditText;
	@GinInjectView(id = R.id.mCampaigndetailCommentLoadMoreTextView)
	TextView mCampaigndetailCommentLoadMoreTextView;

	private boolean isPostComment;
	private boolean isLoadMore;// 是否正在 加载更多

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_offline_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	private void init() {
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

		mHeaderCtv.setTitle(mInformation.optString("Name"));

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});
		mCampaigndetailSignUpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(mUserName)) {
					startActivity(new Intent(CampaignOffLineDetailActivity.this, LoginActivity.class));
					return;
				}
				invokeJoinUpActivity();
				// try {
				// Intent intent = new
				// Intent(CampaignOffLineDetailActivity.this,
				// CampaignSignUpActivity.class);
				// intent.putExtra("information",
				// mDetailInformation.optString("SignUpPageUrl"));
				// startActivity(intent);
				// } catch (Exception e) {
				// }
			}
		});
		mCampaigndetailNameListLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Intent intent = new Intent(CampaignOffLineDetailActivity.this,
							CampaignOffLineDetailNameContentActivity.class);
					intent.putExtra("information", mDetailInformation.optJSONArray("Winners").toString());
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mCampaigndetailFooterSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String content = mCampaigndetailFooterSendEditText.getText().toString();
				if (TextUtils.isEmpty(mUserName)) {
					startActivity(new Intent(CampaignOffLineDetailActivity.this, LoginActivity.class));
					return;
				}
				if (!isPostComment && !TextUtils.isEmpty(content)) {
					isPostComment = true;
					News.saveActivityComment(mInformation.optString("ID"), mUserName, content,
							mInformation.optString("ID"), mInformation.optString("Type"),
							CampaignOffLineDetailActivity.this, new OnJsonArrayResultListener() {
								@Override
								public void onOK(JSONArray result) {
									isPostComment = false;
									try {
										if (result.optJSONObject(0).optString("returncode").contains("SUCCESS")) {
											Toast.makeText(CampaignOffLineDetailActivity.this, "评论成功！",
													Toast.LENGTH_SHORT).show();
											mPage = 0;
											loadComment();
											mCampaigndetailFooterSendEditText.setText("");
											((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
													.hideSoftInputFromWindow(
															mCampaigndetailFooterSendEditText.getWindowToken(),
															InputMethodManager.HIDE_NOT_ALWAYS);
										} else {
											Toast.makeText(CampaignOffLineDetailActivity.this,
													result.optJSONObject(0).optString("msg"), Toast.LENGTH_SHORT)
													.show();
										}
									} catch (Exception e) {
										Toast.makeText(CampaignOffLineDetailActivity.this, "网络繁忙，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}
								}

								@Override
								public void onNG(String reason) {
									isPostComment = false;
									Toast.makeText(CampaignOffLineDetailActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								public void onCancel() {
									isPostComment = false;
									Toast.makeText(CampaignOffLineDetailActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							});
				}
			}
		});

		mCampaigndetailCommentLoadMoreTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!isLoadMore) {
					isLoadMore = true;
					loadComment();
				}
			}
		});
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}
		// loadContent();
		// mCloseLoadingIcon();
	}

	// /**
	// * 检测用户是不是参加过
	// */
	// protected void checkUserHadJoinActiviy()
	// {
	// OnJsonArrayResultListener listener=new OnJsonArrayResultListener() {
	//
	// @Override
	// public void onOK(JSONArray result) {
	// boolean hadJoinedCurrentActivity=false;
	// for(int index=0;index<result.length();index++)
	// {
	// try {
	// //如果参加过活动
	// if(result.getJSONObject(index).getString("ID").equals(mInformation.getString("ID")))
	// {
	// hadJoinedCurrentActivity=true;
	// break;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// if(hadJoinedCurrentActivity)
	// {
	//// Toast.makeText(CampaignOffLineDetailActivity.this, "亲，您已报过名啦！",
	// Toast.LENGTH_SHORT).show();
	// }
	// else
	// {
	// }
	// }
	//
	// @Override
	// public void onNG(String reason) {
	// }
	//
	// @Override
	// public void onCancel() {
	// }
	// };
	// News.getUserActivityList(mUserName, this, listener);
	// }
	/**
	 * 请求报名页
	 */
	protected void invokeJoinUpActivity() {
		try {
			Intent intent = new Intent(CampaignOffLineDetailActivity.this, CampaignSignUpActivity.class);
			intent.putExtra("information", mDetailInformation.optString("SignUpPageUrl"));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	@Override
	public void onResume() {
		StatService.onResume(this);
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}
		StatService.onResume(this);

		loadContent();
		mCloseLoadingIcon();
		super.onResume();
	}

	private void loadContent() {
		News.getActivityDetails(mInformation.optString("ID"), mUserName, this, new OnJsonObjectResultListener() {
			@Override
			public void onOK(final JSONObject result) {
				try {
					mDetailInformation = result;
					SharedPreferences settings = CampaignOffLineDetailActivity.this.getSharedPreferences("settings", 0);
					CheckNetwork network = new CheckNetwork(CampaignOffLineDetailActivity.this);
					boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
							|| network.getWifiState(false) == State.CONNECTED;
					mCampaigndetailImageView.setLoadingImage(R.drawable.default_thumbnail_banner);
					mCampaigndetailImageView.setDefaultImage(R.drawable.default_thumbnail_banner);
					if (isShowPicture) {
						mCampaigndetailImageView.setNetImage(result.optString("HeaderImgUrl"));
					}
					mCampaigndetailInfoTitleTextView.setText(result.optString("Title"));
					mCampaigndetailTimeTextView.setText(result.optString("Time"));
					mCampaigndetailAddressTextView.setText(result.optString("Address"));
					mCampaigndetailSummaryTextView.setText(result.optString("DetailsSum"));

					if (!TextUtils.isEmpty(result.optString("SignUpStatus"))) {
						if (result.optString("SignUpStatus").equalsIgnoreCase("0")) {
							mCampaigndetailFinishBtn.setVisibility(View.GONE);
							mCampaigndetailDoingLayout.setVisibility(View.VISIBLE);
							mCampaigndetailMaxTextView.setText(TextUtils.isEmpty(result.optString("MaxSignUpNum"))
									? "无人数限制" : "名额" + result.optString("MaxSignUpNum") + "人");
							mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(result.optString("SignUpNum"))
									? "0" : result.optString("SignUpNum")) + "人参与");
						} else if (result.optString("SignUpStatus").equalsIgnoreCase("1")) {
							mCampaigndetailFinishBtn.setVisibility(View.VISIBLE);
							mCampaigndetailFinishBtn.setText("已报名");
							mCampaigndetailDoingLayout.setVisibility(View.GONE);
							mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(result.optString("SignUpNum"))
									? "0" : result.optString("SignUpNum")) + "人参与");
						} else if (result.optString("SignUpStatus").equalsIgnoreCase("2")) {
							mCampaigndetailFinishBtn.setVisibility(View.GONE);
							mCampaigndetailFinishBtn.setText("活动已结束");
							mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(result.optString("SignUpNum"))
									? "0" : result.optString("SignUpNum")) + "人参与");
							mCampaigndetailDoingLayout.setVisibility(View.GONE);
						}
					} else {
						if (result.optString("Status").equalsIgnoreCase("1")) {
							mCampaigndetailFinishBtn.setVisibility(View.GONE);
							mCampaigndetailDoingLayout.setVisibility(View.VISIBLE);
							mCampaigndetailMaxTextView.setText(TextUtils.isEmpty(result.optString("MaxSignUpNum"))
									? "无人数限制" : "名额" + result.optString("MaxSignUpNum") + "人");
							mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(result.optString("SignUpNum"))
									? "0" : result.optString("SignUpNum")) + "人参与");
						} else if (result.optString("Status").equalsIgnoreCase("2")) {
							mCampaigndetailFinishBtn.setVisibility(View.GONE);
							mCampaigndetailFinishBtn.setText("报名已截止");
							mCampaigndetailSignUpSumTextView.setText((TextUtils.isEmpty(result.optString("SignUpNum"))
									? "0" : result.optString("SignUpNum")) + "人参与");
							mCampaigndetailDoingLayout.setVisibility(View.GONE);
						}
					}

					mCampaigndetailDetailLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(CampaignOffLineDetailActivity.this,
									CampaignOffLineDetailSummaryContentActivity.class);
							intent.putExtra("information", result.toString());
							startActivity(intent);
						}
					});
					JSONArray nameArray = result.optJSONArray("Winners");
					mNameObjs.clear();
					mCampaigndetailNameListContentLayout.removeAllViews();
					if (nameArray != null && nameArray.length() > 0) {
						mCampaigndetailNameListLayout.setVisibility(View.VISIBLE);
						for (int i = 0; i < nameArray.length(); i++) {
							JSONObject obj = nameArray.optJSONObject(i);
							mNameObjs.add(new NameObj(obj.optString("WinnerImgUrl"), obj.optString("WinnerName")));
						}
						for (int i = 0; i < 5; i++) {
							NameObj obj = mNameObjs.size() > i ? mNameObjs.get(i) : null;
							View view = mInflater.inflate(R.layout.layout_campaign_offline_detail_name_item, null);
							((AdvancedImageView) view.findViewById(R.id.imageview))
									.setLoadingImage(R.drawable.default_thumbnail_banner);
							((AdvancedImageView) view.findViewById(R.id.imageview))
									.setDefaultImage(R.drawable.default_thumbnail_banner);
							if (obj != null) {
								isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
										|| network.getWifiState(false) == State.CONNECTED;
								((AdvancedImageView) view.findViewById(R.id.imageview))
										.setLoadingImage(R.drawable.default_thumbnail_banner);
								if (isShowPicture)
									((AdvancedImageView) view.findViewById(R.id.imageview)).setNetImage(obj.imageUrl);
								((TextView) view.findViewById(R.id.textview)).setText(obj.name);
							} else {
								((AdvancedImageView) view.findViewById(R.id.imageview))
										.setImageResource(R.drawable.trans);
								((TextView) view.findViewById(R.id.textview)).setText("");
							}
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							params.weight = 1;
							view.setLayoutParams(params);
							mCampaigndetailNameListContentLayout.addView(view);
						}
						mCampaigndetailNameListContentLayout.invalidate();
					} else {
						mCampaigndetailNameListLayout.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					finishActivity();
				}
			}

			@Override
			public void onNG(String reason) {
				finishActivity();
			}

			@Override
			public void onCancel() {
				finishActivity();
			}
		});

		News.getActivityAlbums(mInformation.optString("ID"), this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(final JSONArray result) {
				try {
					mAlbumObjs.clear();
					for (int i = 0; i < result.length(); i++) {
						if (!TextUtils.isEmpty(result.optJSONObject(i).optString("ImgUrl"))) {
							mAlbumObjs.add(result.optJSONObject(i).optString("ImgUrl"));
						}
					}
					if (mAlbumObjs.size() > 0) {
						mCampaigndetailAlbumContentLayout.removeAllViews();
						for (int i = 0; i < 3; i++) {
							String url = mAlbumObjs.size() > i ? mAlbumObjs.get(i) : "";
							AdvancedImageView imageView = new AdvancedImageView(CampaignOffLineDetailActivity.this);
							imageView.setDefaultImage(R.drawable.default_thumbnail_banner);
							imageView.setLoadingImage(R.drawable.default_thumbnail_banner);
							imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_thumbnail_banner));
							if (!TextUtils.isEmpty(url)) {
								SharedPreferences settings = CampaignOffLineDetailActivity.this
										.getSharedPreferences("settings", 0);
								CheckNetwork network = new CheckNetwork(CampaignOffLineDetailActivity.this);
								boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
										|| network.getWifiState(false) == State.CONNECTED;
								if (isShowPicture)
									imageView.setNetImage(url);
							} else {
								imageView.setVisibility(View.INVISIBLE);
							}
							imageView.setAspectRatio(1.5f);
							imageView.setRondRadius(3);
							imageView.setScaleType(ScaleType.CENTER_CROP);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							params.weight = 1;
							params.leftMargin = 10;
							params.topMargin = 15;
							params.rightMargin = 10;
							params.bottomMargin = 15;
							imageView.setLayoutParams(params);
							mCampaigndetailAlbumContentLayout.addView(imageView);
						}
						mCampaigndetailAlbumContentLayout.invalidate();
						mCampaigndetailAlbumLayout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(CampaignOffLineDetailActivity.this,
										CampaignOffLineDetailAlbumContentActivity.class);
								intent.putExtra("information", result.toString());
								startActivity(intent);
							}
						});
					} else {
						mCampaigndetailAlbumLayout.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					mCampaigndetailAlbumLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNG(String reason) {
				mCampaigndetailAlbumLayout.setVisibility(View.GONE);
			}

			@Override
			public void onCancel() {
				mCampaigndetailAlbumLayout.setVisibility(View.GONE);
			}
		});

		mPage = 0;
		loadComment();
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

	private void loadComment() {
		News.getCommentList(mInformation.optString("ID"), String.valueOf(mPage), mInformation.optString("ID"),
				mInformation.optString("Type"), this, new OnJsonArrayResultListener() {
					@Override
					public void onOK(JSONArray result) {
						try {
							if (mPage < 1) {
								mCampaigndetailCommentContentLayout.removeAllViews();
							}
							for (int i = 0; i < result.length(); i++) {
								JSONObject obj = result.optJSONObject(i);
								View view = mInflater.inflate(R.layout.layout_campaign_offline_detail_comment, null);
								SharedPreferences settings = CampaignOffLineDetailActivity.this
										.getSharedPreferences("settings", 0);
								CheckNetwork network = new CheckNetwork(CampaignOffLineDetailActivity.this);
								boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
										|| network.getWifiState(false) == State.CONNECTED;
								if (isShowPicture)
									((AdvancedImageView) view.findViewById(R.id.image))
											.setNetImage(obj.optString("logo"));
								// 评论全部显示匿名用户
								((TextView) view.findViewById(R.id.user)).setText(obj.optString("name"));
								// ((TextView)
								// view.findViewById(R.id.user)).setText("匿名用户");
								((TextView) view.findViewById(R.id.comments)).setText(obj.optString("comment"));
								Date date = new Date(Long.valueOf(obj.optString("createtime")));
								((TextView) view.findViewById(R.id.date)).setText(
										(new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())).format(date));
								mCampaigndetailCommentContentLayout.addView(view);
							}
							mCampaigndetailCommentContentLayout.invalidate();
							mCampaigndetailCommentLayout.setVisibility(View.VISIBLE);
							if (result.length() < 10) {
								mCampaigndetailCommentLoadMoreTextView.setVisibility(View.GONE);
							} else {
								mCampaigndetailCommentLoadMoreTextView.setVisibility(View.VISIBLE);
							}
							mPage++;
						} catch (Exception e) {
							if (mPage < 1) {
								mCampaigndetailCommentLayout.setVisibility(View.GONE);
							}
						}
						isLoadMore = false;
					}

					@Override
					public void onNG(String reason) {
						if (mPage < 1) {
							mCampaigndetailCommentLayout.setVisibility(View.GONE);
						}
						isLoadMore = false;
					}

					@Override
					public void onCancel() {
						if (mPage < 1) {
							mCampaigndetailCommentLayout.setVisibility(View.GONE);
						}
						isLoadMore = false;
					}
				});
	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	private class NameObj {
		String imageUrl;
		String name;

		public NameObj(String imageUrl, String name) {
			this.imageUrl = imageUrl;
			this.name = name;
		}
	}

}
