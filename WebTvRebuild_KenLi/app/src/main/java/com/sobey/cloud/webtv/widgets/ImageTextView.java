package com.sobey.cloud.webtv.widgets;

import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageTextView extends LinearLayout {

	private ImageView iv;
	private TextView tv;

	public ImageTextView(Context context) {
		this(context, null);
	}

	public ImageTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public ImageTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.custom_view, this);
		iv = (ImageView) findViewById(R.id.iv);
		tv = (TextView) findViewById(R.id.tv);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.mycustom, defStyle, 0);
		int resId = a.getResourceId(R.styleable.mycustom_resid, R.drawable.ic_launcher);
		String txt = a.getString(R.styleable.mycustom_txt);
		setText(txt);
		setImg(resId);
		a.recycle();
	}

	public void setText(String txt) {
		tv.setText(txt);
	}

	public void setImg(int resId) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
		iv.setLayoutParams(lp);
		iv.setImageResource(resId);
		iv.setScaleType(ScaleType.CENTER_INSIDE);
	}
}
