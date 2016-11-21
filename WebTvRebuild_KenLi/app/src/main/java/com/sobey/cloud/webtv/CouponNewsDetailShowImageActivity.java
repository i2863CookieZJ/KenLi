package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONObject;

import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.sobey.cloud.webtv.kenli.R;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.Toast;

public class CouponNewsDetailShowImageActivity extends Activity {

	private ViewPager mViewPager;
	private static PhotoView mPhotoView = null;
	private static View mControlerView = null;
	private static View mControlerViewTop = null;
	private ImageButton mBackBtn;
	private LinearLayout mDownloadBtn;
	private JSONObject information;
	private ArrayList<String> mPhotoUrls = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPhotoView = null;
		mControlerView = null;
		mControlerViewTop = null;
		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);
		setupNewsDetailActivity();
	}

	public void setupNewsDetailActivity() {
		String str = getIntent().getStringExtra("information");

		try {
			information = new JSONObject(str);
			if (!TextUtils.isEmpty(information.optString("Image"))) {
				mPhotoUrls.add(information.optString("Image"));
			} else {
				mPhotoUrls.add("");
			}
			mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
		} catch (Exception e) {
			e.printStackTrace();
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		mControlerView = inflater.inflate(R.layout.activity_couponnews_detailcontroler, null);
		mDownloadBtn = (LinearLayout) mControlerView.findViewById(R.id.download_btn);
		
		mControlerViewTop = inflater.inflate(R.layout.activity_couponnews_detailcontrolertop, null);
		mBackBtn = (ImageButton) mControlerViewTop.findViewById(R.id.back_btn);

		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CouponNewsDetailShowImageActivity.this.finish();
			}
		});
		mDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(information.optString("Image"))) {
					Toast.makeText(CouponNewsDetailShowImageActivity.this, "开始下载...", Toast.LENGTH_SHORT).show();
					String path = Environment.getExternalStorageDirectory().getPath() + "/";
					String str = String.valueOf(System.currentTimeMillis());
					String saveName = "优惠券_" + str.substring(str.length() - 8, str.length()) + ".png";
					if (mPhotoView.downloadImage(mPhotoUrls.get(((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition()), path, saveName)) {
						Toast.makeText(CouponNewsDetailShowImageActivity.this, "下载图片成功!\n图片保存在手机的SD卡目录下", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(CouponNewsDetailShowImageActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
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
			mPhotoView.setImageURL(urls.get(position));
			mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mPhotoView.canToggleControler(false);
			container.addView(mPhotoView, 0);
			return mPhotoView;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
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
