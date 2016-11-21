package com.sobey.cloud.webtv;

import java.util.List;

import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 首页资讯的adapter
 * 
 * @author lgx
 *
 */

public class InformationAdapter extends BaseAdapter {
	private List<JSONObject> list;
	private LayoutInflater inflater;
	private Context context;
	private int state = 0;

	public InformationAdapter(List<JSONObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public InformationAdapter(List<JSONObject> list, Context context, int state) {
		this.list = list;
		this.state = state;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_information, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.comments.setText(list.get(position).optString("commcount"));
		holder.title.setText(list.get(position).optString("title"));
		// 改成新的图片框架
		ImageLoader.getInstance().displayImage(list.get(position).optString("logo"), holder.logo);
		// holder.logo.setNetImage(list.get(position).optString("logo"));
		holder.message.setVisibility(View.GONE);
		if (state == 1) {
			holder.shipin.setVisibility(View.INVISIBLE);
			holder.title.setMaxLines(1);
			;
			holder.message.setText(list.get(position).optString("summary"));
			holder.message.setVisibility(View.VISIBLE);
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openDetailActivity(Integer.valueOf(list.get(position).optString("type")),
						list.get(position).toString());
			}
		});
		return convertView;
	}

	class ViewHolder {
		@GinInjectView(id = R.id.title)
		private TextView title;
		@GinInjectView(id = R.id.didian)
		private TextView didian;
		@GinInjectView(id = R.id.shipin)
		private ImageView shipin;
		@GinInjectView(id = R.id.comments)
		private TextView comments;
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public AdvancedImageView logo;
		@GinInjectView(id = R.id.message)
		private TextView message;
	}

	public void openDetailActivity(int type, String information) {
		Intent intent = null;
		switch (type) {
		case MConfig.TypePicture:
			intent = new Intent(context, PhotoNewsDetailActivity.class);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(context);
			intent = new Intent(context, VideoNewsDetailActivity.class);
			break;
		case MConfig.TypeNews:
			intent = new Intent(context, GeneralNewsDetailActivity.class);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(context);
			intent = new Intent(context, VideoNewsDetailActivity.class);
			break;
		}
		intent.putExtra("information", information);
		context.startActivity(intent);
	}

}
