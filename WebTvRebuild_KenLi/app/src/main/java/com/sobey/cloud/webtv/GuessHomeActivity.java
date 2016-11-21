package com.sobey.cloud.webtv;

import com.dylan.common.animation.AnimationController;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.GuessHomeListFragment;
import com.sobey.cloud.webtv.fragment.GuessHomeMineFragment;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.utils.MConfig;
import com.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class GuessHomeActivity extends BaseActivity {

	private RelativeLayout mLoadingLayout;
	private TabPageIndicator mTabPageIndicator;
	private ViewPager mViewPager;

	private CatalogObj mCatalogObj;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_guess_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
			finish();
		}
		initSliding(false);
		setTitle(mCatalogObj.name);
		setModuleMenuSelectedItem(mCatalogObj.index);

		mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case 0:
					return GuessHomeListFragment.newInstance(true);
				case 1:
					return GuessHomeListFragment.newInstance(false);
				case 2:
					return GuessHomeMineFragment.newInstance();
				default:
					break;
				}
				return GuessHomeListFragment.newInstance(true);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				switch (position) {
				case 0:
					return "进行中";
				case 1:
					return "已结束";
				case 2:
					return "我的竞猜";
				}
				return "";
			}
		});

		mTabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mTabPageIndicator.setIndividualViewPager(mViewPager, R.style.GuessTabTextStyle,
				R.drawable.guess_tab_item_background);
		// mTabPageIndicator.setViewPager(mViewPager);
	}

	public void showLoading() {
		if (mLoadingLayout != null && mLoadingLayout.getVisibility() != View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingLayout);
		}
	}

	public void hideLoading() {
		if (mLoadingLayout != null && mLoadingLayout.getVisibility() != View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingLayout, 1000, 0);
		}
	}

}
