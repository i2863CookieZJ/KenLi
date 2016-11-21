package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.Information;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 首页资讯的adapter
 * @author lgx
 *
 */

public class NewMessageAdapter extends BaseAdapter {
	protected List<Information> list;
	protected LayoutInflater inflater;
	protected Context context;
	protected int state=0;
	protected int width;
	protected String parentid;
	protected String parentName;
	public NewMessageAdapter() {
	}
	public NewMessageAdapter(List<Information> list,Context context,int width,String parentid){
		this.list=list;
		this.parentid=parentid;
		this.width=width;
		this.context=context;
		inflater=LayoutInflater.from(context);
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
		ViewHolder holder=null;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_information, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		holder.title.setText(list.get(position).getTitle());
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.imagev_lin
				.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.width = width*2/5;
		holder.imagev_lin.getLayoutParams();
		holder.comments.setText(list.get(position).getCommcount());
		if (list.get(position).getType().equals(MConfig.TypeVideo+"")) {
			Log.v("message", "视频");
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
		holder.message.setVisibility(View.GONE);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setParentid(parentid);
				openDetailActivity(Integer.valueOf(list.get(position).getType()),JSON.toJSONString(list.get(position)) );
			}
		});
		return convertView;
	}
	class ViewHolder{
		@GinInjectView(id = R.id.title)
		protected TextView title;
		@GinInjectView(id = R.id.didian)
		protected TextView didian;
		@GinInjectView(id = R.id.shipin)
		protected TextView shipin;
		@GinInjectView(id = R.id.comments)
		protected TextView comments;
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public AdvancedImageView logo;
		@GinInjectView(id = R.id.message)
		protected TextView message;
		@GinInjectView(id = R.id.bf_time)
		protected TextView bf_time;
		@GinInjectView(id = R.id.imagev_lin)
		protected RelativeLayout imagev_lin;
	}
	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(context, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
//			if (!TextUtils.isEmpty(mVoteInformation)) {
//				intent.putExtra("vote", mVoteInformation);
//			}
			context.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(context);
			Intent intent1 = new Intent(context, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
//			if (!TextUtils.isEmpty(mVoteInformation)) {
//				intent1.putExtra("vote", mVoteInformation);
//			}
			context.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(context, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			context.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(context);
			Intent intent3 = new Intent(context, VideoNewsDetailActivity.class);
			intent3.putExtra("information", information);
			context.startActivity(intent3);
			break;
		}
	}
		
}
