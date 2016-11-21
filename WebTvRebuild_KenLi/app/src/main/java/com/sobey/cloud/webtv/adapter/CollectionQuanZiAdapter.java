package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionQuanZiAdapter extends UniversalAdapter<GroupSubjectModel> {

	public CollectionQuanZiAdapter(Context context, List<GroupSubjectModel> mDatas) {
		super(context, mDatas, R.layout.item_collectiontiezi);
	}

	@Override
	public void convert(UniversalViewHolder holder, GroupSubjectModel gsm) {
		ImageView headIcon = holder.findViewById(R.id.item_clctz_headericon);
		TextView title = holder.findViewById(R.id.item_clctz_title);
		TextView joinNum = holder.findViewById(R.id.item_clctz_joinnum);
		TextView dzNum = holder.findViewById(R.id.item_clctz_dznum);
		Picasso.with(mContext).load(gsm.publishUserHeadUrl).placeholder(R.drawable.default_head)
				.error(R.drawable.default_head).into(headIcon);
		title.setText(gsm.subjectTitle);
		joinNum.setText("参与人数：" + gsm.subjectReplyCount);
		dzNum.setText("点赞人数：" + gsm.subjectLikeCount);
	}

}
