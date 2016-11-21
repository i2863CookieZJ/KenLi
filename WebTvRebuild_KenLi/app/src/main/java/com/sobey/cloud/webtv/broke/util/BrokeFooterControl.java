package com.sobey.cloud.webtv.broke.util;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.broke.BrokeCaptureActivity;
import com.sobey.cloud.webtv.broke.BrokeMyHomeActivity;
import com.sobey.cloud.webtv.broke.BrokeNewsHomeActivity;
import com.sobey.cloud.webtv.broke.BrokePlayHomeActivity;
import com.sobey.cloud.webtv.broke.BrokeTaskHomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 爆料底部切换的类
 * @author lgx
 *
 */
public class BrokeFooterControl {
	public static void setBrokeFooterSelectedItem(final Activity activity, String name, final int index){
		if(TextUtils.isEmpty(name)) {
			return;
		}
		if(TextUtils.equals(name, "最新")) {
			((TextView) activity.findViewById(R.id.broke_footerbar_news_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_focus));
			((ImageView) activity.findViewById(R.id.broke_footerbar_news_image)).setImageResource(R.drawable.broke_footer_star_icon_forcus);
		} else {
			((TextView) activity.findViewById(R.id.broke_footerbar_news_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_normal));
			((ImageView) activity.findViewById(R.id.broke_footerbar_news_image)).setImageResource(R.drawable.broke_footer_star_icon_normal);
			((LinearLayout) activity.findViewById(R.id.broke_footerbar_news_layout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, BrokeNewsHomeActivity.class);
					intent.putExtra("index", index);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		if(TextUtils.equals(name, "我的")) {
			((TextView) activity.findViewById(R.id.broke_footerbar_my_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_focus));
			((ImageView) activity.findViewById(R.id.broke_footerbar_my_image)).setImageResource(R.drawable.broke_footer_my_icon_forcus);
		} else {
			((TextView) activity.findViewById(R.id.broke_footerbar_my_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_normal));
			((ImageView) activity.findViewById(R.id.broke_footerbar_my_image)).setImageResource(R.drawable.broke_footer_my_icon_normal);
			((LinearLayout) activity.findViewById(R.id.broke_footerbar_my_layout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences userInfo = activity.getSharedPreferences("user_info", 0);
					if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
						activity.startActivity(new Intent(activity, com.sobey.cloud.webtv.views.user.LoginActivity.class));
						return;
					}
					Intent intent = new Intent(activity, BrokeMyHomeActivity.class);
					intent.putExtra("index", index);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		if(TextUtils.equals(name, "任务")) {
			((TextView) activity.findViewById(R.id.broke_footerbar_task_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_focus));
			((ImageView) activity.findViewById(R.id.broke_footerbar_task_image)).setImageResource(R.drawable.broke_footer_task_icon_forcus);
		} else {
			((TextView) activity.findViewById(R.id.broke_footerbar_task_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_normal));
			((ImageView) activity.findViewById(R.id.broke_footerbar_task_image)).setImageResource(R.drawable.broke_footer_task_icon_normal);
			((LinearLayout) activity.findViewById(R.id.broke_footerbar_task_layout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences userInfo = activity.getSharedPreferences("user_info", 0);
					if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
						activity.startActivity(new Intent(activity, com.sobey.cloud.webtv.views.user.LoginActivity.class));
						return;
					}
					Intent intent = new Intent(activity, BrokeTaskHomeActivity.class);
					intent.putExtra("index", index);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		if(TextUtils.equals(name, "玩法")) {
			((TextView) activity.findViewById(R.id.broke_footerbar_play_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_focus));
			((ImageView) activity.findViewById(R.id.broke_footerbar_play_image)).setImageResource(R.drawable.broke_footer_play_icon_forcus);
		} else {
			((TextView) activity.findViewById(R.id.broke_footerbar_play_text)).setTextColor(activity.getResources().getColor(R.color.broke_footer_text_normal));
			((ImageView) activity.findViewById(R.id.broke_footerbar_play_image)).setImageResource(R.drawable.broke_footer_play_icon_normal);
			((LinearLayout) activity.findViewById(R.id.broke_footerbar_play_layout)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, BrokePlayHomeActivity.class);
					intent.putExtra("index", index);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		((ImageButton) activity.findViewById(R.id.broke_footerbar_camera_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences userInfo = activity.getSharedPreferences("user_info", 0);
				if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
					activity.startActivity(new Intent(activity, com.sobey.cloud.webtv.views.user.LoginActivity.class));
					return;
				}
				Intent intent = new Intent(activity, BrokeCaptureActivity.class);
				intent.putExtra("index", index);
				activity.startActivity(intent);
			}
		});
	}
}
