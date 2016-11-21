package com.sobey.cloud.webtv;

import java.util.List;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.Information;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 节目adapter
 * 
 * @author lgx
 * 
 */
public class ProgramAdapter extends BaseAdapter {
	private List<Information> list;
	private LayoutInflater inflater;
	private Context context;
	private int width;

	public ProgramAdapter(List<Information> list, Context context, int width) {
		this.list = list;
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
			convertView = inflater.inflate(R.layout.item_program, null);
			viewHolder = new ViewHolder();
			GinInjector.manualInjectView(viewHolder, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 10;
			linearParams.height = width / 10;
			viewHolder.logo.getLayoutParams();
			viewHolder.name.setText(list.get(position).getName());
			// 新图片加载框架
			ImageLoader.getInstance().displayImage(list.get(position).getLogo(), viewHolder.logo);
			// bitmapUtils.display(viewHolder.logo,
			// list.get(position).getString("logo"), new
			// DefaultBitmapLoadCallBack<View>() {
			// @Override
			// public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
			// BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
			// super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			// }
			//
			// @Override
			// public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
			// }
			// });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < MConfig.CatalogList.size(); i++) {
					if (MConfig.CatalogList.get(i).id.equals(MConfig.JIEMUID)) {
						Intent intent0 = new Intent(context, HomeActivity.class);
						intent0.putExtra("index", i);
						try {
							// 用来区分 是音频 还是视频直播
							intent0.putExtra("liveMark", Integer.toString(position));
							intent0.putExtra("title", list.get(position).getName());
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
