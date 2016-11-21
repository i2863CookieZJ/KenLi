package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.CollectionZiXunBean;
import com.sobey.cloud.webtv.utils.MConfig;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionZiXunAdapter extends UniversalAdapter<CollectionZiXunBean> {

	public CollectionZiXunAdapter(Context context, List<CollectionZiXunBean> mDatas) {
		super(context, mDatas, R.layout.item_collectionzixun);
	}

	@Override
	public void convert(UniversalViewHolder holder, CollectionZiXunBean czb) {
		ImageView newsIcon = holder.findViewById(R.id.item_clc_newsicon);
		ImageView liveIcon = holder.findViewById(R.id.item_clc_newsliveicon);
		TextView newsTitle = holder.findViewById(R.id.item_clc_newtitle);
		TextView newsSummary = holder.findViewById(R.id.item_clc_newssummary);
		TextView newsAddTime = holder.findViewById(R.id.item_clc_newsaddtime);
		Picasso.with(mContext).load(czb.getLogo()).placeholder(R.drawable.default_thumbnail_video)
				.error(R.drawable.default_thumbnail_video).into(newsIcon);
		newsTitle.setText(czb.getTitle());
		newsSummary.setText(czb.getSummary());
		newsAddTime.setText(czb.getAddtime());
		newsSummary.setVisibility(View.GONE);
		if (MConfig.TypeVideo == Integer.valueOf(czb.getType())) {
			liveIcon.setVisibility(View.VISIBLE);
		}
	}

}
