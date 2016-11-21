package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.SwitchLayout;
import com.sobey.cloud.webtv.utils.SwitchLayout.OnViewChangeListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NewGuideActivity extends BaseActivity {
	private int mViewCount;// 自定义控件中子控件的个数
	private ImageView mImageView[];// 底部的imageView
	private int mCurSel;// 当前选中的imageView

	@GinInjectView(id = R.id.mNewGuideSwitchLayout)
	SwitchLayout mNewGuideSwitchLayout;

	@GinInjectView(id = R.id.mNewGuideDotLayout)
	LinearLayout mNewGuideDotLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_newguide;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		// 得到子控件的个数
		mViewCount = mNewGuideSwitchLayout.getChildCount();
		mImageView = new ImageView[mViewCount];
		// 设置imageView
		for (int i = 0; i < mViewCount; i++) {
			// 得到LinearLayout中的子控件
			mImageView[i] = (ImageView) mNewGuideDotLayout.getChildAt(i);
			mImageView[i].setEnabled(true);// 控件激活
			mImageView[i].setOnClickListener(new MOnClickListener());
			mImageView[i].setTag(i);// 设置与view相关的标签
		}
		// 设置第一个imageView不被激活
		mCurSel = 0;
		mImageView[mCurSel].setEnabled(false);
		mNewGuideSwitchLayout.setOnViewChangeListener(new MOnViewChangeListener());
	}

	// 点击事件的监听器
	private class MOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			// 设置当前显示的ImageView
			setCurPoint(pos);
			// 设置自定义控件中的哪个子控件展示在当前屏幕中
			mNewGuideSwitchLayout.snapToScreen(pos);
		}
	}

	/**
	 * 设置当前显示的ImageView
	 * 
	 * @param pos
	 */
	private void setCurPoint(int pos) {
		if (pos < 0 || pos > mViewCount - 1 || mCurSel == pos)
			return;
		// 当前的imgaeView将可以被激活
		mImageView[mCurSel].setEnabled(true);
		// 将要跳转过去的那个imageView变成不可激活
		mImageView[pos].setEnabled(false);
		mCurSel = pos;
	}

	// 自定义控件中View改变的事件监听
	private class MOnViewChangeListener implements OnViewChangeListener {
		@Override
		public void onViewChange(int view) {
			if (view < 0 || mCurSel == view) {
				return;
			} else if (view > mViewCount - 1) {
				finishActivity();
			}
			setCurPoint(view);
		}
	}

}