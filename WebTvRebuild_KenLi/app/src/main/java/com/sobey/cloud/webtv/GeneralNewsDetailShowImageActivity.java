package com.sobey.cloud.webtv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.common.utils.BitmapResize;
import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GeneralNewsDetailShowImageActivity extends Activity {

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
	private TextView mCommentSumView;
	private ImageButton mBackBtn;
	private LinearLayout mCommentBtn;
	private ImageButton mPublishBtn;
	private ImageButton mCollectBtn;
	private ImageButton mDownloadBtn;
	private ImageButton mShareBtn;
	private SharePopWindow mSharePopWindow;
	private static TextView mPhotoIndex = null;
	private JSONObject mInformation;
	private JSONArray mImageUrlArray;
	private ArrayList<String> mPhotoFilePath = new ArrayList<String>();
	private String mTitle;
	private String mSummary;
	private static int mScreenWidth = 0;
	private static int mScreenHeight = 0;
	private boolean mCollectionFlag = false;
	private String mUserName;
	private static Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
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
		SharedPreferences userInfo = GeneralNewsDetailShowImageActivity.this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}
	}

	public void setupNewsDetailActivity() {
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = getResources().getDisplayMetrics().heightPixels;
		try {
			String str_info = getIntent().getStringExtra("information");
			String str_image = getIntent().getStringExtra("image_url");
			mImageUrlArray = new JSONArray(str_image);
			mInformation = new JSONObject(str_info);
			mShareUrl = mInformation.getString("url");
			mShareTitle = mInformation.getString("title");
			mShareContent = mInformation.getString("summary").trim();
			mTitle = mInformation.getString("title");
			mSummary = mInformation.getString("summary");
			for (int i = 0; i < mImageUrlArray.length(); i++) {
				if (((JSONObject) mImageUrlArray.get(i)).getBoolean("isDownload")) {
					mPhotoFilePath.add(
							((JSONObject) mImageUrlArray.get(i)).getString("localFilePath").replace("file://", ""));
				}
			}
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
				finish();
			}
		});
		mCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GeneralNewsDetailShowImageActivity.this, ReviewActivity.class);
				intent.putExtra("information", mInformation.toString());
				GeneralNewsDetailShowImageActivity.this.startActivity(intent);
			}
		});
		mPublishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences userInfo = GeneralNewsDetailShowImageActivity.this.getSharedPreferences("user_info",
						0);
				if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
					GeneralNewsDetailShowImageActivity.this
							.startActivity(new Intent(GeneralNewsDetailShowImageActivity.this, LoginActivity.class));
					return;
				}
				Intent intent = new Intent(GeneralNewsDetailShowImageActivity.this, CommentActivity.class);
				intent.putExtra("information", mInformation.toString());
				GeneralNewsDetailShowImageActivity.this.startActivity(intent);
			}
		});
		mCollectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (mInformation == null || TextUtils.isEmpty(mInformation.getString("id"))) {
						Toast.makeText(GeneralNewsDetailShowImageActivity.this, "暂时无法获取新闻详情,请稍后收藏", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					String articleId = mInformation.getString("id");
					mCommentSumView.setText(mInformation.getString("commcount"));
					if (TextUtils.isEmpty(mUserName)) {
						Toast.makeText(GeneralNewsDetailShowImageActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(GeneralNewsDetailShowImageActivity.this, LoginActivity.class));
						return;
					}
					if (!mCollectionFlag) {
						Toast.makeText(GeneralNewsDetailShowImageActivity.this, "正在收藏...", Toast.LENGTH_SHORT).show();
						News.addCollect(mUserName, articleId, GeneralNewsDetailShowImageActivity.this,
								new OnJsonArrayResultListener() {
									@Override
									public void onOK(JSONArray result) {
										try {
											if (result.getJSONObject(0).getString("returncode")
													.equalsIgnoreCase("SUCCESS")) {
												setCollection(true);
												Toast.makeText(GeneralNewsDetailShowImageActivity.this, "收藏成功",
														Toast.LENGTH_SHORT).show();
											} else {
												Toast.makeText(GeneralNewsDetailShowImageActivity.this,
														result.getJSONObject(0).getString("returnmsg"),
														Toast.LENGTH_SHORT).show();
											}
										} catch (Exception e) {
											Toast.makeText(GeneralNewsDetailShowImageActivity.this, "操作失败，请稍后重试",
													Toast.LENGTH_SHORT).show();
										}
									}

									@Override
									public void onNG(String reason) {
										Toast.makeText(GeneralNewsDetailShowImageActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onCancel() {
										Toast.makeText(GeneralNewsDetailShowImageActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}
								});
					} else {
						Toast.makeText(GeneralNewsDetailShowImageActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
						News.deleteCollect(mUserName, articleId, GeneralNewsDetailShowImageActivity.this,
								new OnJsonArrayResultListener() {
									@Override
									public void onOK(JSONArray result) {
										try {
											if (result.getJSONObject(0).getString("returncode")
													.equalsIgnoreCase("SUCCESS")) {
												setCollection(false);
												Toast.makeText(GeneralNewsDetailShowImageActivity.this, "取消成功",
														Toast.LENGTH_SHORT).show();
											} else {
												Toast.makeText(GeneralNewsDetailShowImageActivity.this,
														result.getJSONObject(0).getString("returnmsg"),
														Toast.LENGTH_SHORT).show();
											}
										} catch (Exception e) {
											Toast.makeText(GeneralNewsDetailShowImageActivity.this, "操作失败，请稍后重试",
													Toast.LENGTH_SHORT).show();
										}
									}

									@Override
									public void onNG(String reason) {
										Toast.makeText(GeneralNewsDetailShowImageActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onCancel() {
										Toast.makeText(GeneralNewsDetailShowImageActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}
								});
					}
				} catch (Exception e) {
					Toast.makeText(GeneralNewsDetailShowImageActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(GeneralNewsDetailShowImageActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
				String path = Environment.getExternalStorageDirectory().getPath() + MConfig.SavePath + "/image/";
				String str = String.valueOf(System.currentTimeMillis());
				String saveName = MConfig.ImageSavePrefix + str.substring(str.length() - 8, str.length()) + ".png";
				if (Build.VERSION.SDK_INT >= 23) {
					Toast.makeText(GeneralNewsDetailShowImageActivity.this, "下载图片成功!", Toast.LENGTH_SHORT).show();
					return;
				}
				File fromFile = new File(
						mPhotoFilePath.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition()));
				File toFile = new File(path + saveName);
				if (copyfile(fromFile, toFile, true)) {
					Toast.makeText(GeneralNewsDetailShowImageActivity.this, "下载图片成功!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(GeneralNewsDetailShowImageActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mShareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mShareImage = ((JSONObject) mImageUrlArray
							.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition())).getString("url");
					mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		try {
			if (mInformation.getString("iscollect").equalsIgnoreCase("1")) {
				setCollection(true);
			} else {
				setCollection(false);
			}
		} catch (Exception e) {
		}

		mViewPager.setAdapter(new SamplePagerAdapter(mPhotoFilePath));
	}

	private void setCollection(boolean collectionFlag) {
		if (mCollectBtn != null) {
			mCollectionFlag = collectionFlag;
			if (mCollectionFlag) {
				mCollectBtn.setImageResource(R.drawable.photonewsdetail_collection_icon_focus);
			} else {
				mCollectBtn.setImageResource(R.drawable.photonewsdetail_collection_icon);
			}
		}
	}

	private static class SamplePagerAdapter extends PagerAdapter {
		private ArrayList<String> filePath = new ArrayList<String>();
		private int filePathSize;
		private int mPosition = 0;

		public SamplePagerAdapter(ArrayList<String> filePath) {
			this.filePath = filePath;
			filePathSize = filePath.size();
		}

		@Override
		public int getCount() {
			return filePathSize;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			if (position >= filePathSize) {
				return null;
			}
			mPhotoView = new PhotoView(container.getContext(), mControlerView, mControlerViewTop);
			File file;
			Bitmap bitmapTemp;
			file = new File(filePath.get(position));
			if (file.exists() && Build.VERSION.SDK_INT < 23) {
				bitmapTemp = BitmapResize.getBitmapByName(filePath.get(position), mScreenWidth, mScreenHeight);
				mPhotoView.setImageBitmap(bitmapTemp);
			}
			mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			container.addView(mPhotoView, 0);
			return mPhotoView;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			mPhotoIndex.setText((position + 1) + "/" + filePathSize);
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
	protected void onDestroy() {
		if (mPhotoView != null) {
			mPhotoView.destroy();
		}
		super.onDestroy();
	}

	public boolean copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return false;
		}

		if (!fromFile.isFile()) {
			return false;
		}

		if (!fromFile.canRead()) {
			return false;
		}

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}

		if (toFile.exists() && rewrite) {
			toFile.delete();
		}

		try {
			FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSharePopWindow.onActivityResult(requestCode, resultCode, data);
	}
}
