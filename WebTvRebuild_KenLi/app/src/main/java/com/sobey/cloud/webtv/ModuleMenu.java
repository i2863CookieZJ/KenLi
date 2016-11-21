package com.sobey.cloud.webtv;

import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.ModuleMenuItem;
import com.sobey.cloud.webtv.utils.MConfig;

import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ModuleMenu extends ListFragment {

	private BaseAdapter mAdapter = null;
	private ArrayList<ModuleMenuItem> mMenuItems = new ArrayList<ModuleMenuItem>();
	private ModuleChoiceListener mListener;
	private int selectedItem = -1;

	public interface ModuleChoiceListener {
		void onChoice(String module);
	}

	public void setListener(ModuleChoiceListener listener) {
		mListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sliding_modulemenu, null);
		fixBackgroundRepeat(view);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		for (int i = 0; i < MConfig.CatalogList.size(); i++) {
			CatalogObj obj = MConfig.CatalogList.get(i);
			if (TextUtils.isEmpty(obj.url)) {
				mMenuItems.add(new ModuleMenuItem(obj.name, null, getActivity().getResources().getDrawable(obj.resId)));
			} else {
				mMenuItems.add(new ModuleMenuItem(obj.name, obj.url, null));
			}
		}
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_modulemenu, null);
					viewHolder = new ViewHolder();
					viewHolder.image = (AdvancedImageView) convertView.findViewById(R.id.image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.title);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView, viewHolder);
				} else {
					loadViewHolder(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return mMenuItems.size();
			}
		};
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mListener != null)
					mListener.onChoice(mMenuItems.get(position).getTitle());
			}
		});
	}
	
	private void loadViewHolder(int position, View convertView, ViewHolder viewHolder) {
		viewHolder = (ViewHolder) convertView.getTag();
		if (selectedItem == position) {
			convertView.setBackgroundColor(getActivity().getResources().getColor(R.color.modulemenu_selected_background));
		} else {
			convertView.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));
		}
		if (TextUtils.isEmpty(mMenuItems.get(position).getUrl())) {
			viewHolder.image.setImageDrawable(mMenuItems.get(position).getIcon());
		} else {
			viewHolder.image.setNetImage(mMenuItems.get(position).getUrl());
		}
		viewHolder.title.setText(mMenuItems.get(position).getTitle());
	}

	public void setSelectedItem(int position) {
		selectedItem = position;
	}

	public static void fixBackgroundRepeat(View view) {
		Drawable bg = view.getBackground();
		if (bg != null) {
			if (bg instanceof BitmapDrawable) {
				BitmapDrawable bmp = (BitmapDrawable) bg;
				bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
	}
	
	private class ViewHolder {
		public AdvancedImageView image;
		public TextView title;
	}
}
