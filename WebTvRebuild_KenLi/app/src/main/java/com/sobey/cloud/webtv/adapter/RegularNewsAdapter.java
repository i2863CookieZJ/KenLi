package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.Information;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 一般新闻列表 
 * @author zouxudong
 *
 */
public class RegularNewsAdapter extends NewMessageAdapter {

	public RegularNewsAdapter() {
	}
	public RegularNewsAdapter(List<Information> list, Context context,
			int width, String parentid) {
		super(list, context, width, parentid);
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder=null;
		if (convertView==null) {
			holder=new Holder();
			convertView=inflater.inflate(R.layout.item_news_regular, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder=(Holder) convertView.getTag();
		}
		holder.title.setText(list.get(position).getTitle().trim());
//		holder.summary.setText(list.get(position).getSummary().trim());
		holder.summary.setText(formatString(list.get(position).getSummary().trim(),20));
		 // 取控件textView当前的布局参数
		ViewGroup.LayoutParams linearParams =holder.imagev_lin.getLayoutParams();
		linearParams.width = width*2/5;
		holder.imagev_lin.getLayoutParams();
		holder.comments.setVisibility(View.VISIBLE);
		holder.comments.setText(list.get(position).getCommcount());
		if (list.get(position).getType().equals(MConfig.TypeVideo+"")) {
			Log.v("message", "视频");
			holder.comments.setVisibility(View.GONE);
			holder.shipin.setVisibility(View.VISIBLE);
			holder.shipin.setText(list.get(position).getHitcount());
			holder.bf_time.setVisibility(View.VISIBLE);
			holder.bf_time.setText(list.get(position).getDuration());
		}else {
			holder.bf_time.setVisibility(View.GONE);
			holder.shipin.setVisibility(View.GONE);
		}
		//新图片框架
		ImageLoader.getInstance().displayImage(list.get(position).getLogo(), holder.logo);
//		holder.summary.setVisibility(View.GONE);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setParentid(parentid);
				openDetailActivity(Integer.valueOf(list.get(position).getType()),JSON.toJSONString(list.get(position)) );
			}
		});
		return convertView;
	}
	protected String formatString(String stringToFormat,int maxLenth) {
	    if(stringToFormat.length() > maxLenth){
	        stringToFormat = stringToFormat.substring(0, maxLenth - 1) + "...";
	    }
	    return stringToFormat;
	}
	class Holder{
		@GinInjectView(id = R.id.title)
		protected TextView title;
		@GinInjectView(id = R.id.bf_count)
		protected TextView shipin;
		@GinInjectView(id = R.id.pl_count)
		protected TextView comments;
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public AdvancedImageView logo;
		@GinInjectView(id = R.id.summary)
		protected TextView summary;
		@GinInjectView(id = R.id.bf_time)
		protected TextView bf_time;
		@GinInjectView(id = R.id.imagev_lin)
		protected RelativeLayout imagev_lin;
	}

}
