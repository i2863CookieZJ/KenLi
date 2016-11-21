package com.sobey.cloud.webtv.adapter;

import java.util.List;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MessageHomePriceAdapter extends BaseAdapter{
	protected List<Information> list;
	protected LayoutInflater inflater;
	protected Context context;
	public MessageHomePriceAdapter(List<Information> list,Context context){
		this.list=list;
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
			convertView=inflater.inflate(R.layout.item_hone_price, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		//改成新的图片框架
		ImageLoader.getInstance().displayImage(list.get(position).getLogo(), holder.logo);
//		holder.logo.setNetImage(list.get(position).optString("logo"));
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDetailActivity(Integer.valueOf(list.get(position).getType()), list.get(position).toString());
			}
		});
		return convertView;
	}
	class ViewHolder{
		@GinInjectView(id = R.id.campaign_logo_imageview)
		public AdvancedImageView logo;
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
