package com.dylan.uiparts.listview;

import com.dylan.common.utils.DateParse;
import com.third.library.R;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

class DragListViewHeader extends LinearLayout {
	private View mContentView;
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private TextView mTimeTextView;
	private int mState = STATE_NORMAL;
	private String mLastRefreshTime;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	private Animation mRotateRefreshing;

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public DragListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public DragListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundDrawable(Drawable bg, Drawable arrow) {
		if (mContentView != null && bg != null)
			mContentView.setBackgroundDrawable(bg);
		if (mArrowImageView != null && arrow != null)
			mArrowImageView.setImageDrawable(arrow);
	}

	public void setBackgroundResource(int bgid, int arrowid) {
		if (mContentView != null && bgid != 0)
			mContentView.setBackgroundResource(bgid);
		if (mArrowImageView != null && arrowid != 0)
			mArrowImageView.setImageResource(arrowid);
	}

	public void setBackgroundColor(int color) {
		if (mContentView != null)
			mContentView.setBackgroundColor(color);
	}

	private void initView(Context context) {
		mLastRefreshTime = DateParse.getNowDate(null);
		// 初始情况，设置下拉刷新view高度
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.darglistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mContentView = (View) findViewById(R.id.draglistview_header_content);
		mArrowImageView = (ImageView) findViewById(R.id.draglistview_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.draglistview_header_hint_textview);
		mTimeTextView = (TextView) findViewById(R.id.draglistview_header_time);
		mProgressBar = (ProgressBar) findViewById(R.id.draglistview_header_progressbar);
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);

		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);

		mRotateRefreshing = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateRefreshing.setInterpolator(new LinearInterpolator());
		mRotateRefreshing.setDuration(500);
		mRotateRefreshing.setRepeatCount(-1);
	}

	public void setState(int state) {
		mTimeTextView.setText(DateParse.getDateDif(mLastRefreshTime, null) + "前");
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
			mArrowImageView.clearAnimation();
			// mArrowImageView.setVisibility(View.INVISIBLE);
			// mProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.setImageResource(R.drawable.loading_icon);
		} else { // 显示箭头图片
			// mArrowImageView.setVisibility(View.VISIBLE);
			// mProgressBar.setVisibility(View.INVISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.draglistview_arrow);
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(R.string.draglistview_header_hint_normal);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(R.string.draglistview_header_hint_ready);
			}
			break;
		case STATE_REFRESHING:
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mRotateRefreshing);
			mHintTextView.setText(R.string.draglistview_header_hint_loading);
			mLastRefreshTime = DateParse.getNowDate(null);
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
