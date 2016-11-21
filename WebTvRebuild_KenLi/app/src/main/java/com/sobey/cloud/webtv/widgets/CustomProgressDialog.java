package com.sobey.cloud.webtv.widgets;

import com.sobey.cloud.webtv.kenli.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author XZQ
 * @version
 */
public class CustomProgressDialog extends Dialog {

	public Context context;// 上下文

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public CustomProgressDialog(Context context, String messages) {
		super(context, R.style.CustomProgressDialog);
		this.context = context;
		View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_progress_dialog, null);
		TextView messageText = (TextView) view.findViewById(R.id.messageText);
		messageText.setText(messages);
		// 加载自己定义的布局
		ImageView img_loading = (ImageView) view.findViewById(R.id.loadingImg);
		// LinearLayout img_close = (LinearLayout)
		// view.findViewById(R.id.closeBtn);
		RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context,
				R.anim.rotate_refresh_drawable_default); // 加载XML文件中定义的动画
		img_loading.setAnimation(rotateAnimation);// 开始动画
		setContentView(view);// 为Dialoge设置自己定义的布局
		// // 为close的那个文件添加事件
		// img_close.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// dismiss();
		// }
		// });
	}
}
