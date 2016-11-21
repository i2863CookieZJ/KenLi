package com.sobey.cloud.webtv.views.group;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.widgets.HackyViewPager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

@SuppressLint("HandlerLeak")
public class PhotoDetailViewPagerActivity extends BaseActivity {
	private HackyViewPager mViewPager;
	private String currentUrl;
	private int currentItem = -1;
	/* 是否已经展示过广告 */
	private boolean hasShowedAd = false;
	private final String TAG = this.getClass().getName();
	private Button moreButton, reButton;
	private Map<String, Bitmap> currentBitmapCache = new HashMap<String, Bitmap>();
	String[] imageUrls;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);

		imageUrls = this.getIntent().getStringArrayExtra("imageUrls");
		currentItem = this.getIntent().getIntExtra("currentItem", -1);

		initInAnimaition();
		initOutAnimaition();
		frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.photoviewpager_layout, null);
		titleView = LayoutInflater.from(this).inflate(R.layout.photoviewpager_title_layout, null);
		mViewPager = new HackyViewPager(this);
		frameLayout.addView(mViewPager);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		frameLayout.addView(titleView, params);
		setContentView(frameLayout);
		tvTitle = (TextView) titleView.findViewById(R.id.roster_show_photo_topbar_title_textview);
		tvTitle.setText("努力加载中...");

		reButton = (Button) titleView.findViewById(R.id.roster_show_photo_topbar_return_button);

		moreButton = (Button) findViewById(R.id.roster_more_button);
		reButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savePhoto();
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (null != imageUrls) {
					tvTitle.setText(arg0 + 1 + "/" + imageUrls.length);
					currentUrl = imageUrls[arg0];
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		if (null == samplePagerAdapter) {
			samplePagerAdapter = new SamplePagerAdapter(imageUrls);
		} else {
			samplePagerAdapter.setData(imageUrls);
			samplePagerAdapter.notifyDataSetChanged();
		}
		mViewPager.setAdapter(samplePagerAdapter);
		if (-1 != currentItem) {
			mViewPager.setCurrentItem(currentItem);
			tvTitle.setText(currentItem + 1 + "/" + imageUrls.length);
		}
	}

	private SamplePagerAdapter samplePagerAdapter;

	private FrameLayout frameLayout;
	private View titleView;
	private TextView tvTitle;
	private TranslateAnimation taOut;
	private TranslateAnimation taIn;
	private LinearLayout adLayout;
	private TranslateAnimation taBottomOut;
	private TranslateAnimation taBottomIn;

	private void savePhoto() {
		if (currentUrl == null) {
			Toast.makeText(this, "未得到图片地址，暂时无法保存!", Toast.LENGTH_SHORT).show();
			return;
		}
		String fileName = BaseUtil.getFileName(currentUrl);
		String fullPath = BaseUtil.getFilePath() + fileName;
		if (BaseUtil.isFileExist(fullPath)) {
			Toast.makeText(this, "该图片已下载!", Toast.LENGTH_SHORT).show();
			return;
		} else {
			Toast.makeText(this, "正在下载图片...", Toast.LENGTH_SHORT).show();
			toSavePhoto(currentUrl, fileName);
		}
	}

	private void toSavePhoto(final String path, final String fileName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream inputStream = BaseUtil.getImageStream(path);
					File file = BaseUtil.createSavePhotoFile(fileName);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = BaseUtil.downLoadFile(inputStream, file);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("SimpleDateFormat")
	private String formatTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date d1 = new Date(time);
		String t1 = format.format(d1);
		return t1;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				boolean b = (Boolean) msg.obj;
				if (b) {
					Toast.makeText(PhotoDetailViewPagerActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
				}
			}
		};
	};

	private class SamplePagerAdapter extends PagerAdapter {

		// private static int[] sDrawables = { R.drawable.s0, R.drawable.s1,
		// R.drawable.s2,
		// R.drawable.s3, R.drawable.s4, R.drawable.s16 };
		String[] mImageUrls;

		private void setData(String[] imageUrls) {
			this.mImageUrls = imageUrls;
		}

		public SamplePagerAdapter(String[] imageUrls) {
			this.mImageUrls = imageUrls;
		}

		@Override
		public int getCount() {
			return mImageUrls.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			photoView.setImageResource(R.drawable.default_thumbnail_banner);
			photoView.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View view, float x, float y) {
					doTitleLogic();
				}
			});
			final String url = mImageUrls[position];
			ImageLoader.getInstance().displayImage(url, photoView, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String arg0, View arg1) {

				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					Log.i("PotoDetail", "onLoadingFailed--> FailReason:" + arg2);
					System.gc();
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					currentBitmapCache.put(url, arg2);
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {

				}
			});
			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	private void initOutAnimaition() {
		taOut = new TranslateAnimation(0, 0, 0, -50);
		taOut.setDuration(500);
		taOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleView.setVisibility(View.GONE);
			}
		});

		taBottomOut = new TranslateAnimation(0, 0, 0, 50);
		taBottomOut.setDuration(500);
		taBottomOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				adLayout.setVisibility(View.GONE);
			}
		});
	}

	private void initInAnimaition() {
		taIn = new TranslateAnimation(0, 0, -50, 0);
		taIn.setDuration(500);
		taIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titleView.setVisibility(View.VISIBLE);
			}
		});
		taBottomIn = new TranslateAnimation(0, 0, 50, 0);
		taBottomIn.setDuration(500);
		taBottomIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
	}

	public void doTitleLogic() {
		if (titleView.getVisibility() == View.GONE) {
			titleView.startAnimation(taIn);
		} else {
			titleView.startAnimation(taOut);
		}
	}

}
