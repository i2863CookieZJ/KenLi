package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.EbusinessReciverModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EbusinessAdressAdapter extends BaseAdapter {
	private List<EbusinessReciverModel> mRecivers;
	private Context mContext;
	public EbusinessAdressAdapter(List<EbusinessReciverModel> recivers,Context ctx){
		this.mRecivers = recivers;
		this.mContext = ctx;
	}
	@Override
	public int getCount() {
		return mRecivers.size();
	}

	@Override
	public EbusinessReciverModel getItem(int position) {
		return mRecivers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		EbusinessReciverModel reciverModel = getItem(position);
		if(null == convertView){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ebusiness_address_layout, null);
			holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
			holder.phoneNumTv = (TextView) convertView.findViewById(R.id.phone_num_tv);
			holder.addressTv = (TextView) convertView.findViewById(R.id.address_tv);
			holder.postTv = (TextView) convertView.findViewById(R.id.post_no_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTv.setText(reciverModel.reciver);
		holder.phoneNumTv.setText(reciverModel.phoneNo);
		holder.addressTv.setText(reciverModel.address);
		holder.postTv.setText(reciverModel.postNo);
		return convertView;
	}
	private class ViewHolder{
		TextView nameTv;
		TextView phoneNumTv;
		TextView addressTv;
		TextView postTv;
	}
}
