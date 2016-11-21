package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.androidadvancedui.AdvancedListView;
import com.appsdk.androidadvancedui.listener.AdvancedListViewFooter;
import com.appsdk.androidadvancedui.listener.AdvancedListViewHeader;
import com.appsdk.androidadvancedui.listener.AdvancedListViewListener;
import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.ui.ListViewFooter;
import com.sobey.cloud.webtv.ui.ListViewHeader;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class CampaignArticleLiveActivity extends BaseActivity {

	private static final int mPeriodTime = 20000;

	private JSONObject mInformation = null;
	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private boolean isShowVideoPlayer = true;
	@SuppressWarnings("unused")
	private boolean isInitVideoPlayer = false;
	private String mArticalId;
	private String mCatalogId;
	private String mUserName;

	private boolean isLoading = false;
	private LayoutInflater mInflater;
	private Timer mTimer;
	private MyTimerTask mTask;
	private MyHandler mHandler;
	private static TimerListener mListener;
	private ArrayList<JSONObject> mChatArticles = new ArrayList<JSONObject>();
	private AdvancedListViewHeader mHeader;
	private AdvancedListViewFooter mFooter;
	private BaseAdapter mAdapter;
	private int mPageIndex = 0;

	@GinInjectView(id = R.id.mCampaignArticleLiveCommentLayout)
	LinearLayout mCampaignArticleLiveCommentLayout;
	@GinInjectView(id = R.id.mCampaignArticleLiveVideoView)
	AdvancedVideoView mCampaignArticleLiveVideoView;
	@GinInjectView(id = R.id.mCampaignArticleLiveBack)
	ImageButton mCampaignArticleLiveBack;
	@GinInjectView(id = R.id.mCampaignArticleLiveFooterSendBtn)
	Button mCampaignArticleLiveFooterSendBtn;
	@GinInjectView(id = R.id.mCampaignArticleLiveFooterSendEditText)
	EditText mCampaignArticleLiveFooterSendEditText;
	@GinInjectView(id = R.id.mCampaignArticleLiveListView)
	AdvancedListView mCampaignArticleLiveListView;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			SharedPreferences userInfo = CampaignArticleLiveActivity.this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				mUserName = "";
			} else {
				mUserName = userInfo.getString("id", "");
			}
		}
	}

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_article_live;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		SharedPreferences userInfo = CampaignArticleLiveActivity.this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		mInflater = LayoutInflater.from(this);
		isShowVideoPlayer = true;
		String str = getIntent().getStringExtra("information");
		initFooter();
		if (isShowVideoPlayer) {
			initVideo();
		}
		try {
			mInformation = new JSONObject(str);
			mArticalId = String.valueOf(mInformation.optInt("id"));
			mCatalogId = mInformation.optString("parentid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getRtspSource();
		initChatRoom();
	}

	private void initChatRoom() {
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.listitem_campaign_article_live_chat, null);
					viewHolder = new ViewHolder();
					viewHolder.image = (AdvancedImageView) convertView.findViewById(R.id.image);
					viewHolder.user = (TextView) convertView.findViewById(R.id.user);
					viewHolder.date = (TextView) convertView.findViewById(R.id.date);
					viewHolder.comment = (TextView) convertView.findViewById(R.id.comments);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView);
				} else {
					loadViewHolder(position, convertView);
				}
				return convertView;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return mChatArticles.size();
			}
		};
		mHeader = new ListViewHeader(this);
		mFooter = new ListViewFooter(this);
		mCampaignArticleLiveListView.initAdvancedListView(this, mHeader, mFooter);
		mCampaignArticleLiveListView.setAdapter(mAdapter);
		mCampaignArticleLiveListView.setListViewListener(new AdvancedListViewListener() {
			@Override
			public void onStopHeaderPullDown() {
			}

			@Override
			public void onStopFooterPullUp() {
			}

			@Override
			public void onStartHeaderPullDown() {
				refreshChat();
			}

			@Override
			public void onStartFooterPullUp() {
				loadMoreChat();
			}
		});

		mListener = new TimerListener() {
			@Override
			public void onRefresh() {
				mCampaignArticleLiveListView.startHeaderPullDown();
			}
		};
		mTask = new MyTimerTask();
		mHandler = new MyHandler();
		mTimer = new Timer();
		mTimer.schedule(mTask, 0, mPeriodTime);
	}

	private class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if (mHandler != null) {
				mHandler.sendEmptyMessage(0);
			}
		}
	}

	private static class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0 && mListener != null) {
				mListener.onRefresh();
			}
			super.handleMessage(msg);
		}
	}

	private interface TimerListener {
		void onRefresh();
	}

	private void refreshChat() {
		if (!isLoading) {
			isLoading = true;
			News.getCommentList(mInformation.optString("activity_id"), "0", mArticalId, "1", this,
					new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								for (int i = result.length() - 1; i >= 0; i--) {
									if (mChatArticles.size() > 0) {
										if (Long.valueOf(mChatArticles.get(0).optString("createtime")) < Long
												.valueOf(result.optJSONObject(i).optString("createtime"))) {
											mChatArticles.add(0, result.optJSONObject(i));
										}
									} else {
										if (!TextUtils.isEmpty(result.optJSONObject(i).optString("createtime"))) {
											mChatArticles.add(result.optJSONObject(i));
										}
									}
								}
							} catch (Exception e) {
							} finally {
								mAdapter.notifyDataSetChanged();
								isLoading = false;
								mCampaignArticleLiveListView.stopHeaderPullDown();
							}
						}

						@Override
						public void onNG(String reason) {
							isLoading = false;
							mCampaignArticleLiveListView.stopHeaderPullDown();
						}

						@Override
						public void onCancel() {
							isLoading = false;
							mCampaignArticleLiveListView.stopHeaderPullDown();
						}
					});
		}
	}

	private void loadMoreChat() {
		if (!isLoading) {
			isLoading = true;
			mPageIndex++;
			News.getCommentList(mInformation.optString("activity_id"), String.valueOf(mPageIndex), mArticalId, "1",
					this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								if (result.length() > 0) {
									for (int i = 0; i < result.length(); i++) {
										if (mChatArticles.size() > 0) {
											if (Long.valueOf(mChatArticles.get(mChatArticles.size() - 1)
													.optString("createtime")) > Long
															.valueOf(result.optJSONObject(i).optString("createtime"))) {
												mChatArticles.add(result.optJSONObject(i));
											}
										} else {
											if (!TextUtils.isEmpty(result.optJSONObject(i).optString("createtime"))) {
												mChatArticles.add(result.optJSONObject(i));
											}
										}
									}
								} else {
									mPageIndex--;
								}
							} catch (Exception e) {
								mPageIndex--;
							} finally {
								mAdapter.notifyDataSetChanged();
								isLoading = false;
								mCampaignArticleLiveListView.stopFooterPullUp();
							}
						}

						@Override
						public void onNG(String reason) {
							isLoading = false;
							mCampaignArticleLiveListView.stopFooterPullUp();
							mPageIndex--;
						}

						@Override
						public void onCancel() {
							isLoading = false;
							mCampaignArticleLiveListView.stopFooterPullUp();
							mPageIndex--;
						}
					});
		}
	}

	private void loadViewHolder(int position, View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		try {
			JSONObject obj = mChatArticles.get(position);
			viewHolder.image.setNetImage(obj.optString("logo"));
			viewHolder.user.setText(obj.optString("name"));
			viewHolder.date.setText(obj.optString("createtime"));
			viewHolder.comment.setText(obj.optString("comment"));
		} catch (Exception e) {
		}
	}

	private void initFooter() {
		mCampaignArticleLiveBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});

		mCampaignArticleLiveFooterSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(mUserName)) {
					Toast.makeText(CampaignArticleLiveActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(CampaignArticleLiveActivity.this, LoginActivity.class));
					return;
				}
				String comment = mCampaignArticleLiveFooterSendEditText.getText().toString();
				if (TextUtils.isEmpty(comment)) {
					Toast.makeText(CampaignArticleLiveActivity.this, "请输入内容后发送", Toast.LENGTH_SHORT).show();
				} else {
					News.saveActivityComment(mInformation.optString("activity_id"), mUserName, comment, mArticalId, "1",
							CampaignArticleLiveActivity.this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								if (result.optJSONObject(0).optString("returncode").equalsIgnoreCase("SUCCESS")) {
									Toast.makeText(CampaignArticleLiveActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
									mCampaignArticleLiveFooterSendEditText.setText("");
									((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
											.hideSoftInputFromWindow(
													mCampaignArticleLiveFooterSendEditText.getWindowToken(),
													InputMethodManager.HIDE_NOT_ALWAYS);
									mCampaignArticleLiveListView.startHeaderPullDown();
								} else {
									Toast.makeText(CampaignArticleLiveActivity.this, "发送失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							} catch (Exception e) {
								Toast.makeText(CampaignArticleLiveActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT)
										.show();
							}
						}

						@Override
						public void onNG(String reason) {
							Toast.makeText(CampaignArticleLiveActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onCancel() {
							Toast.makeText(CampaignArticleLiveActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}

	// Video
	private void initVideo() {
		try {
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int screenWidth = metrics.widthPixels;
			int videoHeight = (int) (screenWidth * 3.0 / 4.0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					videoHeight);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mCampaignArticleLiveVideoView.setLayoutParams(layoutParams);
			mCampaignArticleLiveVideoView.init();
			mCampaignArticleLiveVideoView.showLoadingView(true);
			isInitVideoPlayer = true;
		} catch (Exception e) {
		}
	}

	@Override
	public void onPause() {
		if (mCampaignArticleLiveVideoView != null) {
			mCampaignArticleLiveVideoView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mCampaignArticleLiveVideoView != null) {
			mCampaignArticleLiveVideoView.onDestory();
		}
		if (mTimer != null) {
			mTimer.cancel();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		if (mCampaignArticleLiveVideoView != null) {
			mCampaignArticleLiveVideoView.onResume(true);
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mCampaignArticleLiveVideoView != null) {
			mCampaignArticleLiveVideoView.onConfigurationChanged();
		}
		super.onConfigurationChanged(newConfig);
	}

	private void getOnlineVideoPath() {
		CheckNetwork checkNetwork = new CheckNetwork(CampaignArticleLiveActivity.this);
		if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
			AlertDialog.Builder builder = new AlertDialog.Builder(CampaignArticleLiveActivity.this);
			builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					playOnlineVideo();
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		} else {
			playOnlineVideo();
		}
	}

	private void playOnlineVideo() {
		if (mediaObj != null && mCampaignArticleLiveVideoView != null) {
			mCampaignArticleLiveVideoView.addMedia(mediaObj, true, true);
		} else {
			getRtspSource();
		}
	}

	private void getRtspSource() {
		News.getArticleById(mArticalId, mCatalogId, mUserName, MConfig.TerminalType,
				DateParse.getDate(0, 0, 0, 0, null, null, "yyyyMMdd"), this, new OnJsonArrayResultListener() {
					@Override
					public void onOK(JSONArray result) {
						try {
							JSONArray guideArray = result.getJSONObject(0).getJSONArray("staticfilepaths");
							for (int i = 0; i < guideArray.length(); i++) {
								News.getTvGuide(((JSONObject) guideArray.get(i)).getString("playerpath"),
										CampaignArticleLiveActivity.this, new OnJsonArrayResultListener() {
									@Override
									public void onOK(JSONArray result) {
										try {
											resolutionObjs.clear();
											for (int i = 0; i < result.length(); i++) {
												JSONArray jsonArray = result.getJSONObject(i).getJSONArray("C_Address");
												for (int j = 0; j < jsonArray.length(); j++) {
													resolutionObjs.add(new ResolutionObj(
															((JSONObject) jsonArray.optJSONObject(j))
																	.optString("title"),
															((JSONObject) jsonArray.optJSONObject(j))
																	.optString("url")));
												}
												mediaObj = new MediaObj(result.optJSONObject(i).optString("C_Name"),
														new ResolutionList(resolutionObjs, 0), true);
											}
											getOnlineVideoPath();
										} catch (Exception e) {
											mediaObj = null;
										}
									}

									@Override
									public void onNG(String reason) {
										mediaObj = null;
									}

									@Override
									public void onCancel() {
										mediaObj = null;
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onNG(String reason) {
						mediaObj = null;
					}

					@Override
					public void onCancel() {
						mediaObj = null;
					}
				});
	}

	private class ViewHolder {
		AdvancedImageView image;
		TextView user;
		TextView date;
		TextView comment;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCampaignArticleLiveVideoView != null && mCampaignArticleLiveVideoView.isFullScreen()
					&& keyCode == KeyEvent.KEYCODE_BACK) {
				mCampaignArticleLiveVideoView.toggleFullScreen();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
