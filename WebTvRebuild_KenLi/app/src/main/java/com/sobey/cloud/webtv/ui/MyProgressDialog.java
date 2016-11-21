package com.sobey.cloud.webtv.ui;

import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义转圈
 * 
 */
@SuppressLint("InflateParams")
public class MyProgressDialog extends Dialog {

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static MyProgressDialog createLoadingDialog(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialogview, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		ImageView spaceshipImage = (ImageView) v
				.findViewById(R.id.loadingImageView);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.progress_round);
		LinearInterpolator lin = new LinearInterpolator();
		hyperspaceJumpAnimation.setInterpolator(lin); // 设置匀速旋转
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		MyProgressDialog loadingDialog = new MyProgressDialog(context,
				R.style.myProgressDialog);// 创建自定义样式dialog
		loadingDialog.setCancelable(true);// 是否可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;

	}
}
