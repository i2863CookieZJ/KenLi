package com.dylan.uiparts.views;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.dylan.common.utils.AsyncImageLoader;
import com.dylan.uiparts.views.NetImageView.OnLoadListener;
import com.third.library.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class NetImageBanner extends LinearLayout implements OnLoadListener {
	
	private static final int SCROLL_TIME = 3000;

	private GestureDetector mGestureDetector = null;

	public NetImageBanner(Context context) {
		this(context, null);
	}

	public NetImageBanner(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
		mFitWidth = a.getBoolean(R.styleable.ImageView_fitWidth, false);
		mFitHeight = a.getBoolean(R.styleable.ImageView_fitHeight, false);
		if(a.hasValue(R.styleable.ImageView_imageDefault))
			mDefaultDrawable = a.getDrawable(R.styleable.ImageView_imageDefault);
		if(a.hasValue(R.styleable.ImageView_imageLoading))
			mLoadingDrawable = a.getDrawable(R.styleable.ImageView_imageLoading);
		if(a != null) {
			a.recycle();
		}
		a = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
		if(a.hasValue(R.styleable.ImageViewBanner_dotNormal))
			mDotNormal = a.getDrawable(R.styleable.ImageViewBanner_dotNormal);
		else
			mDotNormal = getResources().getDrawable(R.drawable.banner_dot_normal);
		if(a.hasValue(R.styleable.ImageViewBanner_dotFocus))
			mDotFocus = a.getDrawable(R.styleable.ImageViewBanner_dotFocus);
		else
			mDotFocus = getResources().getDrawable(R.drawable.banner_dot_focus);
		if(a != null) {
			a.recycle();
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.image_banner, this);
		mContainer = (RelativeLayout) findViewById(R.id.container);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mTextDesc = (TextView) findViewById(R.id.textDesc);
		mBannerIcon = (ImageView) findViewById(R.id.bannerIcon);
		mViewGroup = (ViewGroup) findViewById(R.id.viewGroup);
		mViewHandler = new MyHandler(mViewPager);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mItems != null) {
			for(ImageItem item : mItems) {
				if(item.image == null)
					continue;
				if(item.image.getDrawable() == null) {
					item.image = null;
					continue;
				}
				Bitmap bm = ((BitmapDrawable) item.image.getDrawable()).getBitmap();
				if(bm != null && !bm.isRecycled()) {
					// bm.recycle();
					Log.i("dylan", "mem: recycle a bitmap.");
				}
				bm = null;
				item.image = null;
			}
		}
		mImageViews = null;
		mWillExit = true;
		System.gc();
	}

	public void setDefault(Drawable drawable) {
		mDefaultDrawable = drawable;
	}

	public void setDefault(int resId) {
		mDefaultResId = resId;
	}

	public void setLoading(Drawable drawable) {
		mLoadingDrawable = drawable;
	}

	public void setLoading(int resId) {
		mLoadingResId = resId;
	}

	public void setDotNormal(Drawable drawable) {
		mDotNormal = drawable;
	}

	public void setDotNormal(int resId) {
		mDotNormal = getResources().getDrawable(resId);
	}

	public void setDotFocus(Drawable drawable) {
		mDotFocus = drawable;
	}

	public void setDotFocus(int resId) {
		mDotFocus = getResources().getDrawable(resId);
	}

	public void setFitHeight(boolean fit) {
		mFitHeight = fit;
	}

	public void setFitWidth(boolean fit) {
		mFitWidth = fit;
	}

	public void setRondRadius(int radius) {
		mRoundRadius = radius;
	}

	public TextView getTitleView() {
		return mTextDesc;
	}

	public void clearItems() {
		mItems.clear();
	}

	public int getItemSize() {
		return mItems.size();
	}

	public void addItem(String imageUrl, boolean useCache, AsyncImageLoader loader, String title, Object tag, int resId) {
		if(mItems == null)
			return;
		NetImageView image = new NetImageView(getContext());
		if(mFitHeight || mFitWidth) {
			image.setListener(this);
		}
		if(mDefaultDrawable != null)
			image.setDefault(mDefaultDrawable);
		if(mDefaultResId != 0)
			image.setDefault(mDefaultResId);
		if(mLoadingDrawable != null)
			image.setLoading(mLoadingDrawable);
		if(mLoadingResId != 0)
			image.setLoading(mLoadingResId);
		image.setImage(imageUrl, useCache, loader);
		image.setScaleType(ScaleType.CENTER_CROP);
		image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		image.setRondRadius(mRoundRadius);
		mItems.add(new ImageItem(image, title, tag, resId));
	}

	public void addItem(String imageUrl, boolean useCache, AsyncImageLoader loader, String title) {
		addItem(imageUrl, useCache, loader, title, null, 0);
	}

	public void addItem(String imageUrl, String title, Object tag) {
		addItem(imageUrl, true, null, title, tag, 0);
	}

	public void addItem(String imageUrl, String title) {
		addItem(imageUrl, true, null, title, null, 0);
	}
	
	public void addItem(String imageUrl, String title, int resId) {
		addItem(imageUrl, true, null, title, null, resId);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public void setAspect(int width, int height) {
		mAspectWidth = width;
		mAspectHeight = height;
	}

	private RelativeLayout mContainer = null;
	private ImageView[] mImageViews = null;
	private ImageView mImageView = null;
	private ViewPager mViewPager = null;
	private TextView mTextDesc = null;
	private ImageView mBannerIcon = null;
	private ViewGroup mViewGroup = null;
	private boolean mIsContinue = true;
	private AtomicInteger what = new AtomicInteger(0);
	private FixedSpeedScroller mScroller = null;
	private boolean mDragged = false;
	private Boolean mSizeChanged = false;
	private boolean mWillExit = false;
	private Drawable mDefaultDrawable = null;
	private int mDefaultResId = 0;
	private Drawable mLoadingDrawable = null;
	private int mLoadingResId = 0;
	private boolean mFitWidth = false;
	private boolean mFitHeight = false;
	private Drawable mDotNormal = null;
	private Drawable mDotFocus = null;
	private int mRoundRadius = 0;
	private Thread switchThread = null;
	private int mAspectWidth = 0;
	private int mAspectHeight = 0;
	private static final int SCROLL_DURATION_DEFAULT = 800;
	private int mScrollDuration = SCROLL_DURATION_DEFAULT;

	private class ImageItem {
		public ImageItem(NetImageView i, String t, Object g, int r) {
			image = i;
			title = t;
			tag = g;
			resId = r;
		}

		public NetImageView image;
		public String title;
		public Object tag;
		public int resId;
	}

	private ArrayList<ImageItem> mItems = new ArrayList<ImageItem>();
	private OnItemClickListener mListener = null;

	@Override
	public void onFinish(NetImageView view, boolean result, Bitmap bitmap) {
		if(!result)
			return;
		if(mSizeChanged)
			return;
		if(bitmap == null)
			return;
		synchronized(mSizeChanged) {
			if(mSizeChanged)
				return;
			if(mAspectWidth == 0 || mAspectHeight == 0) {
				int height = bitmap.getHeight();
				int width = bitmap.getWidth();
				if(width < 1 || height < 1)
					return;
				mSizeChanged = true;
				if(mFitWidth) {
					height = getWidth() * height / width;
					if(height > getLayoutParams().height) {
						mContainer.getLayoutParams().height = height;
					}
				} else {
					width = getHeight() * width / height;
					if(width > getLayoutParams().width) {
						mContainer.getLayoutParams().width = width;
					}
				}
			} else {
				mSizeChanged = true;
				if(mFitWidth) {
					int height = getWidth() * mAspectHeight / mAspectWidth;
					mContainer.getLayoutParams().height = height;
				} else {
					int width = getHeight() * mAspectWidth / mAspectHeight;
					mContainer.getLayoutParams().width = width;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void updateLayout() {
		if(mItems == null || mItems.size() == 0)
			return;
		mViewGroup.removeAllViews();
		mImageViews = new ImageView[mItems.size()];
		for(int i = 0; i < mItems.size(); i++) {
			mImageView = new ImageView(getContext());
			mImageView.setLayoutParams(new LayoutParams(20, 20));
			mImageView.setPadding(4, 4, 4, 4);
			mImageViews[i] = mImageView;
			if(i == 0) {
				mImageViews[i].setImageDrawable(mDotFocus);
			} else {
				mImageViews[i].setImageDrawable(mDotNormal);
			}
			mViewGroup.addView(mImageViews[i]);
		}

		mGestureDetector = new GestureDetector(new MySimpleOnGestureListener());
		mViewPager.setAdapter(new AdvAdapter(mItems));
		mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
		mViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
			mField.set(mViewPager, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(switchThread == null) {
			switchThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while(!mWillExit) {
						if(mIsContinue) {
							mViewHandler.sendEmptyMessage(what.get());
							whatOption();
						} else {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							}
						}
					}
					Log.i("dylan", "NetImageBanner thread exit.");
				}
			});
			switchThread.start();
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if((mAspectWidth == 0 || mAspectHeight == 0) && mItems.size() > 0) {
					int height = ((ImageItem) mItems.get(0)).image.getHeight();
					int width = ((ImageItem) mItems.get(0)).image.getWidth();
					if(width < 1 || height < 1)
						return;
					if(mFitWidth) {
						height = getWidth() * height / width;
						if(height > getLayoutParams().height) {
							mContainer.getLayoutParams().height = height;
						}
					} else {
						width = getHeight() * width / height;
						if(width > getLayoutParams().width) {
							mContainer.getLayoutParams().width = width;
						}
					}
				} else {
					if(mFitWidth) {
						int height = getWidth() * mAspectHeight / mAspectWidth;
						mContainer.getLayoutParams().height = height;
					} else {
						int width = getHeight() * mAspectWidth / mAspectHeight;
						mContainer.getLayoutParams().width = width;
					}
				}
				onPageChange(0);
			}
		}, 100);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(mAspectWidth == 0 || mAspectHeight == 0 || mContainer == null || mSizeChanged) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else if(mFitWidth) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = width * mAspectHeight / mAspectWidth;
			this.setMeasuredDimension(width, height);
			mContainer.getLayoutParams().height = height;
		} else {
			int height = MeasureSpec.getSize(heightMeasureSpec);
			int width = height * mAspectWidth / mAspectHeight;
			this.setMeasuredDimension(width, height);
			mContainer.getLayoutParams().height = height;
		}
	}

	private class MySimpleOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(!mDragged && mListener != null) {
				int index = mViewPager.getCurrentItem();
				Object tag = mItems.get(index).tag;
				mListener.onClick(NetImageBanner.this, index, tag);
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			mDragged = false;
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mScrollDuration = 200;
			mDragged = true;
			return false;
		}
	}

	private void whatOption() {
		what.incrementAndGet();
		if(what.get() > mImageViews.length - 1) {
			what.getAndAdd(-1 * mImageViews.length);
		}
		try {
			Thread.sleep(SCROLL_TIME);
		} catch (InterruptedException e) {
		}
	}

	private Handler mViewHandler = null;

	private static class MyHandler extends Handler {
		WeakReference<ViewPager> mViewPager;

		MyHandler(ViewPager pager) {
			mViewPager = new WeakReference<ViewPager>(pager);
		}

		@Override
		public void handleMessage(Message msg) {
			mViewPager.get().setCurrentItem(msg.what);
			super.handleMessage(msg);
		}
	}

	private final class GuidePageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			onPageChange(arg0);
		}
	}
	
	private void onPageChange(int index) {
		if(mItems == null || mItems.size() == 0 || mImageViews == null || mImageViews.length == 0) {
			return;
		}
		what.getAndSet(index);
		if(mDotFocus != null) {
			mImageViews[index].setImageDrawable(mDotFocus);
		}
		for(int i = 0; i < mImageViews.length; i++) {
			if(index != i) {
				if(mDotNormal != null) {
					mImageViews[i].setImageDrawable(mDotNormal);
				}
			}
		}
		mTextDesc.setText(mItems.get(index).title);
		if(mItems.get(index).resId == 0) {
			mBannerIcon.setVisibility(View.GONE);
		} else {
			mBannerIcon.setImageResource(mItems.get(index).resId);
			mBannerIcon.setVisibility(View.VISIBLE);
		}
	}

	private final class AdvAdapter extends PagerAdapter {
		private List<ImageItem> views = null;

		public AdvAdapter(List<ImageItem> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			if(views != null && views.size() > arg1) {
				((ViewPager) arg0).removeView(views.get(arg1).image);
			}
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1).image, 0);
			return views.get(arg1).image;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class FixedSpeedScroller extends Scroller {
		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
			mScrollDuration = SCROLL_DURATION_DEFAULT;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
			mScrollDuration = SCROLL_DURATION_DEFAULT;
		}

		public void setmDuration(int time) {
			mScrollDuration = time;
		}

		public int getmDuration() {
			return mScrollDuration;
		}
	}

	public interface OnItemClickListener {
		void onClick(NetImageBanner banner, int position, Object tag);
	}
}
