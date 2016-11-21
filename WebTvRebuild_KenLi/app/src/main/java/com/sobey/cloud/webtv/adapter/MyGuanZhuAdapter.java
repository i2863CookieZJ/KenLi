package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.FriendsBean;
import com.sobey.cloud.webtv.bean.MyGuanZhuBean;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

public class MyGuanZhuAdapter extends UniversalAdapter<FriendsBean> {

	public MyGuanZhuAdapter(Context context, List<FriendsBean> mDatas) {
		super(context, mDatas, R.layout.item_myguanzhu);
	}

	@Override
	public void convert(UniversalViewHolder holder, FriendsBean fd) {
		RoundImageView headView = holder.findViewById(R.id.item_myguanzhu_headicon);
		TextView nameTv = holder.findViewById(R.id.item_myguanzhu_username);

		if (TextUtils.isEmpty(fd.getHead())) {
			headView.setImageResource(R.drawable.default_head);
		} else {
			Picasso.with(mContext).load(fd.getHead()).placeholder(R.drawable.default_head)
					.error(R.drawable.default_head).into(headView);
		}
		nameTv.setText(fd.getNickname());
	}

}
