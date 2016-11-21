package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;

import com.dylan.common.utils.CheckNetwork;
import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CampaignOffLineDetailAlbumContentSingleActivity extends Activity {
	private ViewPager mViewPager;
	private static PhotoView mPhotoView = null;
	private static View mControlerView = null;
	private static View mControlerViewTop = null;
	private TextView mTitleView;
	private static TextView mSummaryView;
	private static TextView mPhotoIndex = null;
	private TextView mCommentSumView;
	private ImageButton mBackBtn;
	private ImageButton mDownloadBtn;
	private static JSONArray mInformation;
	private ArrayList<String> mPhotoUrls = new ArrayList<String>();

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

	public void setupNewsDetailActivity() {
		try {
			mInformation = new JSONArray(getIntent().getStringExtra("information"));
			for (int i = 0; i < mInformation.length(); i++) {
				mPhotoUrls.add(mInformation.optJSONObject(i).optString("ImgUrl"));
			}
			if (mPhotoUrls.size() == 0) {
				mPhotoUrls.add("");
			}
			mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
		} catch (Exception e) {
			finish();
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		mControlerView = inflater.inflate(R.layout.activity_photonews_detailcontroler, null);
		mTitleView = (TextView) mControlerView.findViewById(R.id.title);
		mSummaryView = (TextView) mControlerView.findViewById(R.id.summary);
		mPhotoIndex = (TextView) mControlerView.findViewById(R.id.photo_index);
		mCommentSumView = (TextView) mControlerView.findViewById(R.id.comment_sum);
		mBackBtn = (ImageButton) mControlerView.findViewById(R.id.back_btn);
		mControlerView.findViewById(R.id.comment_btn).setVisibility(View.INVISIBLE);
		mControlerView.findViewById(R.id.publish_btn).setVisibility(View.INVISIBLE);
		mControlerView.findViewById(R.id.collection_btn).setVisibility(View.INVISIBLE);
		mControlerView.findViewById(R.id.share_btn).setVisibility(View.INVISIBLE);

		mControlerViewTop = inflater.inflate(R.layout.activity_photonews_detailcontrolertop, null);
		mDownloadBtn = (ImageButton) mControlerViewTop.findViewById(R.id.download_btn);

		mTitleView.setText("活动相册");
		mCommentSumView.setText(String.valueOf(mInformation.length()));
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mInformation != null) {
					Toast.makeText(CampaignOffLineDetailAlbumContentSingleActivity.this, "开始下载...", Toast.LENGTH_SHORT)
							.show();
					String path = Environment.getExternalStorageDirectory().getPath() + MConfig.SavePath + "/image/";
					String str = String.valueOf(System.currentTimeMillis());
					String saveName = MConfig.ImageSavePrefix + str.substring(str.length() - 8, str.length()) + ".png";
					if (mPhotoView.downloadImage(
							mPhotoUrls.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition()), path,
							saveName)) {
						Toast.makeText(CampaignOffLineDetailAlbumContentSingleActivity.this, "下载图片成功!",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(CampaignOffLineDetailAlbumContentSingleActivity.this, "网络不给力,请稍后再试吧",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
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
			SharedPreferences settings = container.getContext().getSharedPreferences("settings", 0);
			CheckNetwork network = new CheckNetwork(container.getContext());
			boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
					|| network.getWifiState(false) == State.CONNECTED;
			if (isShowPicture)
				mPhotoView.setImageURL(urls.get(position));
			mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			container.addView(mPhotoView, 0);
			return mPhotoView;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			mSummaryView.setText(TextUtils.isEmpty(mInformation.optJSONObject(position).optString("Description")) ? ""
					: mInformation.optJSONObject(position).optString("Description"));
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
	protected void onDestroy() {
		if (mPhotoView != null) {
			mPhotoView.destroy();
		}
		super.onDestroy();
	}
}
