package com.sobey.cloud.webtv.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimationImageView extends ImageView {

	public AnimationImageView(Context context) {
		super(context);
		init();
	}

	public AnimationImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AnimationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
		animationDrawable.start();
	}
}
