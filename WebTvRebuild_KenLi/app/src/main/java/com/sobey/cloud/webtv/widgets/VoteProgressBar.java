package com.sobey.cloud.webtv.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class VoteProgressBar extends LinearLayout {

	private Context mContext;
	private float mProgress = 0.0f;
	private int mProgressWidth = 1;
	private LinearLayout mProgressColorBar;

	public VoteProgressBar(Context context) {
		super(context);
		mContext = context;
	}

	public VoteProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void init(float progress, int color) {
		if (progress > 1 || progress < 0) {
			return;
		}
		mProgress = progress;
		removeAllViews();
		mProgressColorBar = new LinearLayout(mContext);
		mProgressColorBar.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
		mProgressColorBar.setBackgroundColor(color);
		addView(mProgressColorBar);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mProgressColorBar != null) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int progressWidth = Math.round(width * mProgress);
			progressWidth = progressWidth < 1 ? 1 : progressWidth;
			if (mProgressWidth != progressWidth) {
				mProgressWidth = progressWidth;
				mProgressColorBar.setLayoutParams(new LayoutParams(progressWidth, LayoutParams.MATCH_PARENT));
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
