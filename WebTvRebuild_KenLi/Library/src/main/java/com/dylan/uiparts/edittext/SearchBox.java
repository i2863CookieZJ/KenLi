package com.dylan.uiparts.edittext;

import com.third.library.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class SearchBox extends EditText {

	Drawable mCleanBtn = null;
	boolean mHasClean = false;
	boolean mShowClean = false;
	public SearchBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	public SearchBox(Context context) {
		super(context);
		init(context);
	}
	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("deprecation")
	public void setStyle(boolean useBackground, boolean showLeftIcon, boolean showRightIcon, boolean showCleanIcon, String hint) {
		if (useBackground) {
			Drawable bg = getContext().getResources().getDrawable(R.drawable.searchbox_bound);
			setBackgroundDrawable(bg);
		}
		if (showLeftIcon) {
			Drawable left = getContext().getResources().getDrawable(R.drawable.searchbox_lefticon);
			left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight()); 
			Drawable[] icons = getCompoundDrawables();
			setCompoundDrawables(left, icons[1], icons[2], icons[3]);
			if(showCleanIcon) {
				mShowClean = true;
			}
		}
		if (showRightIcon) {
			Drawable right = getContext().getResources().getDrawable(R.drawable.searchbox_righticon);
			right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight()); 
			Drawable[] icons = getCompoundDrawables();
			setCompoundDrawables(icons[0], icons[1], right, icons[3]);
		}
		
		setHint(hint);
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
	}
	void init(Context context) {
		if(mShowClean) {
			Resources res = context.getResources();
			mCleanBtn = res.getDrawable(R.drawable.searchbox_clear_btn);
			mCleanBtn.setBounds(0, 0, mCleanBtn.getMinimumWidth(), mCleanBtn.getMinimumHeight()); 
			addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				@Override
				public void afterTextChanged(Editable s) {
					boolean canClean = getText().toString().length() > 0;
					if (canClean == mHasClean)return;
					mHasClean = canClean;
					Drawable[] icons = getCompoundDrawables();
					if (canClean) {
						setCompoundDrawables(icons[0], icons[1], mCleanBtn, icons[3]);
					} else {
						setCompoundDrawables(icons[0], icons[1], null, icons[3]);
					}
				}
			});
			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP && mHasClean) {
						Rect bound = mCleanBtn.getBounds();
						int x = (int)event.getX();
						int width = getWidth();
						if(x >= width - bound.width() && x <= width) {
							setText("");
						}
					} 
					return false;
				}
			});
		}
	}
}
