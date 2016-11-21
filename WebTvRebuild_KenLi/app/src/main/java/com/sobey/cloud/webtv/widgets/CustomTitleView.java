package com.sobey.cloud.webtv.widgets;

import com.sobey.cloud.webtv.kenli.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lazy on 2015/09/13. version：自定义title description：
 */
public class CustomTitleView extends LinearLayout {

	// 显示 title和back
	public static final int MODE_BACK = 1;
	// 主页显示设置
	public static final int MODE_MAIN = 2;
	// 显示title, back和操作
	public static final int MODE_OPERATOR = 3;
	// 只显示title
	public static final int MODE_SINGLE = 4;
	// 只显示操作按钮
	public static final int MODE_OPERATOR_ONLY = 5;

	private RelativeLayout mBackRl, mOperatorRl;
	private TextView mTitleTv;

	private Context mContext;
	private String mTitleString;
	private int backgroundId;
	private int mode = 1;

	public CustomTitleView(Context context) {
		super(context);
		initView(context);
	}

	public CustomTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.custom_title);
		mode = array.getInt(R.styleable.custom_title_style, 0);
		mTitleString = array.getString(R.styleable.custom_title_c_title);
		backgroundId = array.getResourceId(R.styleable.custom_title_operation_background, R.color.white);
		array.recycle();
		initView(context);
	}

	@SuppressLint("NewApi")
	public CustomTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.layout_base_header, this);
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mBackRl = (RelativeLayout) findViewById(R.id.back_rl);
		mOperatorRl = (RelativeLayout) findViewById(R.id.operator_rl);
		switch (mode) {
		case MODE_BACK:// 显示back和title
			mBackRl.setVisibility(View.VISIBLE);
			mOperatorRl.setVisibility(View.GONE);
			break;
		case MODE_MAIN:// 显示operator和title
			mBackRl.setVisibility(View.GONE);
			mOperatorRl.setVisibility(View.VISIBLE);
			break;
		case MODE_OPERATOR:// 显示back和operator
			mBackRl.setVisibility(View.VISIBLE);
			mOperatorRl.setVisibility(View.VISIBLE);
			mOperatorRl.setBackgroundResource(backgroundId);
			break;
		case MODE_SINGLE:
			mBackRl.setVisibility(View.GONE);
			mOperatorRl.setVisibility(View.GONE);
			break;
		case MODE_OPERATOR_ONLY:
			mBackRl.setVisibility(View.GONE);
			mTitleTv.setVisibility(View.GONE);
			mOperatorRl.setBackgroundResource(backgroundId);
			break;
		default:
			break;
		}
		if (mTitleString != null) {
			mTitleTv.setText(mTitleString);
		}
		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});
	}

	public void setTitle(int titleId) {
		mTitleTv.setText(titleId);
	}

	public void setTitle(String title) {
		mTitleTv.setText(title);
	}

	public void setOperatorOnclick(OnClickListener l) {
		mOperatorRl.setOnClickListener(l);
	}

	public void hideOperator() {
		mOperatorRl.setVisibility(View.GONE);
	}

	public void hideBack() {
		mBackRl.setVisibility(View.GONE);
	}

	public void setBackOnclick(OnClickListener l) {
		mBackRl.setOnClickListener(l);
	}

	public void setOperatorBackground(int id) {
		mOperatorRl.setBackgroundResource(id);
	}
}
