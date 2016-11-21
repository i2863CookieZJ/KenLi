package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.HomeActivity;
import com.sobey.cloud.webtv.obj.Information;
import com.sobey.cloud.webtv.utils.MConfig;
//import com.sobey.cloud.webtv.HomeActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 直播adapter
 * 
 * @author lgx
 * 
 */
public class LiveAdapter extends BaseAdapter {
	private List<Information> list;
	private LayoutInflater inflater;
	private Context context;
	private int width;
	private String parentid;

	public LiveAdapter(List<Information> list, Context context, int width, String parentid) {
		this.list = list;
		this.parentid = parentid;
		this.width = width;
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
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_live, null);
			viewHolder = new ViewHolder();
			GinInjector.manualInjectView(viewHolder, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Log.v("LiveAdapter", list.size() + "");
		try {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 4;
			linearParams.height = width / 4;
			viewHolder.logo.getLayoutParams();
			viewHolder.name.setText(list.get(position).getTitle());
			// 新图片加载框架
			ImageLoader.getInstance().displayImage(list.get(position).getLogo(), viewHolder.logo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < MConfig.CatalogList.size(); i++) {
					if (MConfig.CatalogList.get(i).id.equals(MConfig.ZHIBOID)) {
						Intent intent0 = new Intent(context, HomeActivity.class);
						intent0.putExtra("index", i);
						try {
							// 用来区分 是音频 还是视频直播
							intent0.putExtra("liveMark", Integer.toString(position));
							intent0.putExtra("title", list.get(position).getTitle());
							intent0.putExtra("state", list.get(position).getId());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						context.startActivity(intent0);
					}
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		@GinInjectView(id = R.id.name)
		private TextView name;
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public ImageView logo;
	}

}
