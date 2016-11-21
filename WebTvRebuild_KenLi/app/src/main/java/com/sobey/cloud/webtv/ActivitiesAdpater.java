package com.sobey.cloud.webtv;

import java.util.List;

import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.views.activity.CampaignOffLineDetailActivity;
import com.sobey.cloud.webtv.views.activity.CampaignShowDetailActivity;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 活动的自定义adapter
 * 
 * @author lgx
 *
 */

public class ActivitiesAdpater extends BaseAdapter {
	private List<JSONObject> list;
	private LayoutInflater inflater;
	private Context context;
	private int state = 0;

	public ActivitiesAdpater(List<JSONObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public ActivitiesAdpater(List<JSONObject> list, Context context, int state) {
		this.list = list;
		this.state = state;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.new_activity_list_item, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject obj = list.get(position);
			holder.ac_time.setVisibility(View.GONE);
			if (state == 1) {
				holder.cj_lin.setVisibility(View.INVISIBLE);
				holder.title.setVisibility(View.GONE);
			} else {
				holder.title1.setVisibility(View.INVISIBLE);
			}
			holder.title.setText(obj.optString("Name"));
			holder.title1.setText(obj.optString("Name"));
			// 改成新的图片框架
			if (isShowPicture) {
				ImageLoader.getInstance().displayImage(list.get(position).optString("PosterUrl"), holder.logo);
			}
			// holder.logo.setNetImage(obj.optString("PosterUrl"));
			holder.cj_count.setText(obj.optString("JoinNumber") + "人");
			holder.personNumTv.setText(obj.optString("JoinNumber") + "人参与");
			int type = Integer.valueOf(obj.optString("Type"));
			if (type == 1) {
				holder.baomingBtn.setText("立即投票>>");
				holder.type_iv.setImageResource(R.drawable.ic_vote);
			} else {
				holder.baomingBtn.setText("立即报名>>");
				holder.type_iv.setImageResource(R.drawable.ic_sign_up);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (state == 1) {

			holder.logo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						switch (Integer.valueOf(list.get(position).optString("Type"))) {
						case 1:
							Intent intent1 = new Intent(context, CampaignShowDetailActivity.class);
							intent1.putExtra("information", list.get(position).toString());
							context.startActivity(intent1);
							break;

						case 2:
							Intent intent2 = new Intent(context, CampaignOffLineDetailActivity.class);
							intent2.putExtra("information", list.get(position).toString());
							context.startActivity(intent2);
							break;
						}
					} catch (Exception e) {
					}
				}
			});
		}
		
		
		return convertView;
	}

	class ViewHolder {
		@GinInjectView(id = R.id.new_acitivity_listitem_baoming)
		public TextView baomingBtn;
		@GinInjectView(id = R.id.new_acitivity_listitem_personnum)
		public TextView personNumTv;
		@GinInjectView(id = R.id.new_acitivity_listitem_plnum)
		public TextView pinglunTv;
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public AdvancedImageView logo;
		@GinInjectView(id = R.id.ac_title)
		public TextView title;
		@GinInjectView(id = R.id.cj_count)
		public TextView cj_count;
		@GinInjectView(id = R.id.ac_time)
		private TextView time;
		@GinInjectView(id = R.id.cj_lin)
		private LinearLayout cj_lin;
		@GinInjectView(id = R.id.title1)
		private TextView title1;
		@GinInjectView(id = R.id.ac_time)
		private TextView ac_time;

		@GinInjectView(id = R.id.type_iv)
		private ImageView type_iv;
	}

}
