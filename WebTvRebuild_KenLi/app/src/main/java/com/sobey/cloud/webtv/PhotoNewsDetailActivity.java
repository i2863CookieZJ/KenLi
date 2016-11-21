package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoNewsDetailActivity extends Activity {

	private String mShareImage = null;
	private String mShareUrl = null;
	private String mShareTitle = null;
	private String mShareContent = null;

	private ViewPager mViewPager;
	private static PhotoView mPhotoView = null;
	private static View mControlerView = null;
	private static View mControlerViewTop = null;
	private TextView mTitleView;
	private TextView mSummaryView;
	private static TextView mPhotoIndex = null;
	private TextView mCommentSumView;
	private ImageButton mBackBtn;
	private LinearLayout mCommentBtn;
	private ImageButton mPublishBtn;
	private ImageButton mCollectBtn;
	private ImageButton mDownloadBtn;
	private ImageButton mShareBtn;
	private SharePopWindow mSharePopWindow;
	private JSONObject mInformation;
	private JSONObject information;
	private ArrayList<String> mPhotoUrls = new ArrayList<String>();
	private String mTitle;
	private String mSummary;
	private boolean mCollectionFlag = false;
	private String mUserName;
	
	protected boolean isDisposed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPhotoView = null;
		mControlerView = null;
		mControlerViewTop = null;
		mPhotoIndex = null;
		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);
		setupNewsDetailActivity();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			SharedPreferences userInfo = PhotoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				mUserName = "";
			} else {
				mUserName = userInfo.getString("id", "");
			}
		}
	}

	public void setupNewsDetailActivity() {
		SharedPreferences userInfo = PhotoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}
		
		String str = getIntent().getStringExtra("information");
		try {
			mInformation = new JSONObject(str);
			News.getArticleById(String.valueOf(mInformation.getInt("id")), mInformation.optString("parentid"), mUserName, null, null, this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					if(isDisposed)
						return;
					try {
						information = result.getJSONObject(0);
						if (information.getString("iscollect").equalsIgnoreCase("1")) {
							setCollection(true);
						} else {
							setCollection(false);
						}
						mCommentSumView.setText(information.getString("commcount"));
						JSONArray contentArray = information.optJSONArray("content");
						for (int i = 0; i < contentArray.length(); i++) {
							mPhotoUrls.add(((JSONObject) contentArray.get(i)).getString("filepath"));
						}
						if (mPhotoUrls.size() == 0) {
							mPhotoUrls.add("");
						}
						mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
						getShareUrl();
						News.increaseHitCount(null, information.getString("catalogid"), information.getString("id"));
					} catch (Exception e) {
						e.printStackTrace();
						if (mPhotoUrls.size() == 0) {
							mPhotoUrls.add("");
						}
						mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
					}
				}

				@Override
				public void onNG(String reason) {
					if (mPhotoUrls.size() == 0) {
						mPhotoUrls.add("");
					}
					mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
				}

				@Override
				public void onCancel() {
					if (mPhotoUrls.size() == 0) {
						mPhotoUrls.add("");
					}
					mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		mControlerView = inflater.inflate(R.layout.activity_photonews_detailcontroler, null);
		mTitleView = (TextView) mControlerView.findViewById(R.id.title);
		mSummaryView = (TextView) mControlerView.findViewById(R.id.summary);
		mPhotoIndex = (TextView) mControlerView.findViewById(R.id.photo_index);
		mCommentSumView = (TextView) mControlerView.findViewById(R.id.comment_sum);
		mBackBtn = (ImageButton) mControlerView.findViewById(R.id.back_btn);
		mCommentBtn = (LinearLayout) mControlerView.findViewById(R.id.comment_btn);
		mPublishBtn = (ImageButton) mControlerView.findViewById(R.id.publish_btn);
		mCollectBtn = (ImageButton) mControlerView.findViewById(R.id.collection_btn);
		mShareBtn = (ImageButton) mControlerView.findViewById(R.id.share_btn);
		mSharePopWindow = new SharePopWindow(this, mViewPager);

		mControlerViewTop = inflater.inflate(R.layout.activity_photonews_detailcontrolertop, null);
		mDownloadBtn = (ImageButton) mControlerViewTop.findViewById(R.id.download_btn);

		mTitleView.setText(mTitle);
		mSummaryView.setText(mSummary);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoNewsDetailActivity.this.finish();
			}
		});
		mCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(PhotoNewsDetailActivity.this, ReviewActivity.class);
					intent.putExtra("information", information.toString());
					PhotoNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mPublishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					SharedPreferences userInfo = PhotoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
					if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
						PhotoNewsDetailActivity.this.startActivity(new Intent(PhotoNewsDetailActivity.this, LoginActivity.class));
						return;
					}
					Intent intent = new Intent(PhotoNewsDetailActivity.this, CommentActivity.class);
					intent.putExtra("information", information.toString());
					PhotoNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mCollectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (information == null || TextUtils.isEmpty(information.getString("id"))) {
						Toast.makeText(PhotoNewsDetailActivity.this, "暂时无法获取新闻详情,请稍后收藏", Toast.LENGTH_SHORT).show();
						return;
					}
					String articleId = information.getString("id");
					if (TextUtils.isEmpty(mUserName)) {
						Toast.makeText(PhotoNewsDetailActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(PhotoNewsDetailActivity.this, LoginActivity.class));
						return;
					}
					if (!mCollectionFlag) {
						Toast.makeText(PhotoNewsDetailActivity.this, "正在收藏...", Toast.LENGTH_SHORT).show();
						News.addCollect(mUserName, articleId, PhotoNewsDetailActivity.this, new OnJsonArrayResultListener() {
							@Override
							public void onOK(JSONArray result) {
								if(isDisposed)
									return;
								try {
									if (result.getJSONObject(0).getString("returncode").equalsIgnoreCase("SUCCESS")) {
										setCollection(true);
										Toast.makeText(PhotoNewsDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(PhotoNewsDetailActivity.this, result.getJSONObject(0).getString("returnmsg"), Toast.LENGTH_SHORT).show();
									}
								} catch (Exception e) {
									Toast.makeText(PhotoNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onNG(String reason) {
								Toast.makeText(PhotoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onCancel() {
								Toast.makeText(PhotoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						Toast.makeText(PhotoNewsDetailActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
						News.deleteCollect(mUserName, articleId, PhotoNewsDetailActivity.this, new OnJsonArrayResultListener() {
							@Override
							public void onOK(JSONArray result) {
								if(isDisposed)
									return;
								try {
									if (result.getJSONObject(0).getString("returncode").equalsIgnoreCase("SUCCESS")) {
										setCollection(false);
										Toast.makeText(PhotoNewsDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(PhotoNewsDetailActivity.this, result.getJSONObject(0).getString("returnmsg"), Toast.LENGTH_SHORT).show();
									}
								} catch (Exception e) {
									Toast.makeText(PhotoNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onNG(String reason) {
								Toast.makeText(PhotoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onCancel() {
								Toast.makeText(PhotoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						});
					}
				} catch (Exception e) {
					Toast.makeText(PhotoNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Toast.makeText(PhotoNewsDetailActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
					String path = Environment.getExternalStorageDirectory().getPath() + MConfig.SavePath + "/image/";
					String str = String.valueOf(System.currentTimeMillis());
					String saveName = MConfig.ImageSavePrefix + str.substring(str.length() - 8, str.length()) + ".png";
					if (mPhotoView.downloadImage(mPhotoUrls.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition()), path, saveName)) {
						Toast.makeText(PhotoNewsDetailActivity.this, "下载图片成功!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PhotoNewsDetailActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		mShareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					mShareImage = mPhotoUrls.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition());
					mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
				}
			}
		});
	}

	private void setCollection(boolean collectionFlag) {
		mCollectionFlag = collectionFlag;
		if (mCollectionFlag) {
			mCollectBtn.setImageResource(R.drawable.photonewsdetail_collection_icon_focus);
		} else {
			mCollectBtn.setImageResource(R.drawable.photonewsdetail_collection_icon);
		}
	}

	private void getShareUrl() {
		try {
			mShareContent = information.getString("summary").trim();
			mTitle = information.getString("title");
			mSummary = information.getString("summary");
			mShareUrl = information.getString("url");
			mShareTitle = information.getString("title");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SamplePagerAdapter extends PagerAdapter {
		private ArrayList<String> urls = new ArrayList<String>();
		private int urlsSize;
		private int mPosition = 0;

		public SamplePagerAdapter(ArrayList<String> urls) {
			this.urls = urls;
			urlsSize = urls.size();
		}

		@Override
		public int getCount() {
			return urlsSize;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			if (position >= urlsSize) {
				return null;
			}
			mPhotoView = new PhotoView(container.getContext(), mControlerView, mControlerViewTop);
			mPhotoView.setImageURL(urls.get(position));
			mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			container.addView(mPhotoView, 0);
			return mPhotoView;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			mPhotoIndex.setText((position + 1) + "/" + urlsSize);
			mPosition = position;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public int getCurrentPosition() {
			return mPosition;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSharePopWindow.isShowing()) {
				mSharePopWindow.hideShareWindow();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		isDisposed=true;
		if (mPhotoView != null) {
			mPhotoView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSharePopWindow.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 显示分享
	 */
	public void showSharePopWindow() {
		this.mSharePopWindow.showShareWindow(this.mShareUrl, this.mShareTitle, this.mShareContent, this.mShareImage);
	}
}
