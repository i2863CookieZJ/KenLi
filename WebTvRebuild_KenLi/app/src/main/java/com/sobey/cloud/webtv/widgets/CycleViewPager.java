package com.sobey.cloud.webtv.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 循环ViewPager,可设置是否自动滑动 16/1/4
 * 
 * @author Administrator
 */
public class CycleViewPager extends ViewPager {

	private final int DELAYTIME_DEFAULT = 3000;// 默认延迟时间

	private InnerPagerAdapter mAdapter;
	private int index;// 页面索引
	private int delayTime;// 自动播放延迟时间
	private boolean isAutoPlay;// 是否自动播放

	public CycleViewPager(Context context) {
		super(context);
		setOnPageChangeListener(null);
	}

	public CycleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnPageChangeListener(null);
	}

	@Override
	public void setAdapter(PagerAdapter arg0) {
		mAdapter = new InnerPagerAdapter(arg0);
		super.setAdapter(mAdapter);
		setCurrentItem(1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		super.setOnPageChangeListener(new InnerOnPageChangeListener(listener));
	}

	private class InnerOnPageChangeListener implements OnPageChangeListener {

		private OnPageChangeListener listener;
		private int position;

		public InnerOnPageChangeListener(OnPageChangeListener listener) {
			this.listener = listener;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (null != listener) {
				listener.onPageScrollStateChanged(arg0);
			}
			if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
				if (position == mAdapter.getCount() - 1) {
					setCurrentItem(1, false);
				} else if (position == 0) {
					setCurrentItem(mAdapter.getCount() - 2, false);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (null != listener) {
				listener.onPageScrolled(arg0, arg1, arg2);
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			position = arg0;
			if (null != listener) {
				listener.onPageSelected(arg0);
			}
			// 自动播放执行代码
			removeCallbacks(autoPlay);
			if (isAutoPlay && position != mAdapter.getCount() - 1) {
				index = position + 1;
				postDelayed(autoPlay, delayTime);
			}

		}
	}

	private class InnerPagerAdapter extends PagerAdapter {

		private PagerAdapter adapter;

		public InnerPagerAdapter(PagerAdapter adapter) {
			this.adapter = adapter;
			adapter.registerDataSetObserver(new DataSetObserver() {

				@Override
				public void onChanged() {
					notifyDataSetChanged();
				}

				@Override
				public void onInvalidated() {
					notifyDataSetChanged();
				}

			});
		}

		@Override
		public int getCount() {
			return adapter.getCount() + 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return adapter.isViewFromObject(arg0, arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (position == 0) {
				position = adapter.getCount() - 1;
			} else if (position == adapter.getCount() + 1) {
				position = 0;
			} else {
				position -= 1;
			}
			return adapter.instantiateItem(container, position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			adapter.destroyItem(container, position, object);
		}

	}

	/**
	 * 设置自动播放*一定要放在setAdapter之前*
	 * 
	 */
	public void setAutoPlay() {
		if (isAutoPlay) {
			return;
		}
		delayTime = DELAYTIME_DEFAULT;
		isAutoPlay = true;
	}

	public void setAutoPlay(int delayTime) {
		if (delayTime < 0) {
			setAutoPlay();
			return;
		}
		this.delayTime = delayTime;
		isAutoPlay = true;
	}

	public void stopPlay() {
		if (isAutoPlay) {
			removeCallbacks(autoPlay);
		}
	}

	Runnable autoPlay = new Runnable() {
		@Override
		public void run() {
			setCurrentItem(index);
		}
	};
}