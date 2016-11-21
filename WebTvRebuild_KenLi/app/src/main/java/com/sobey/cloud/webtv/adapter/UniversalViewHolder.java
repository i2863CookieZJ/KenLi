package com.sobey.cloud.webtv.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 万能ViewHolder
 * 
 * @author Shen Gongfei
 * 
 */
public class UniversalViewHolder {
	private SparseArray<View> mViews;// 相比存放<Integer,Object>的效率更高
	private int mPosition;
	private View mConvertView;
	private Context context;

	public UniversalViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		this.context = context;
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		mConvertView.setTag(this);
	}

	public static UniversalViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId,
			int position) {
		if (convertView == null) {
			return new UniversalViewHolder(context, parent, layoutId, position);
		} else {
			UniversalViewHolder holder = (UniversalViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}

	/**
	 * 通过ViewHolder获取控件
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public int getPosition() {
		return mPosition;
	}

	public View getConvertView() {
		return mConvertView;
	}

}
