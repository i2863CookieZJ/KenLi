package com.appsdk.video.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class PauseAdvertiseLayout extends RelativeLayout {
	private static final float mAspectRatio = 1.33f;
	private Context mContext;

	public PauseAdvertiseLayout(Context context) {
		this(context, null);
	}

	public PauseAdvertiseLayout(Context context, AttributeSet attrs) {
		this(context, null, 0);
	}

	public PauseAdvertiseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		height = height - dip2px(mContext, 80.0f);
		width = (int) (height * mAspectRatio);
		this.setMeasuredDimension(width, height);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
