package com.sobey.cloud.webtv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ListView万能适配器
 * 
 * @author Shen Gongfei
 *
 * @param <T>
 */
public abstract class UniversalAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	protected int mItemLayoutId;

	public UniversalAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		this.mContext = context;
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UniversalViewHolder holder = UniversalViewHolder.get(mContext, convertView, parent,
				mItemLayoutId, position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}

	public abstract void convert(UniversalViewHolder holder, T t);
}
