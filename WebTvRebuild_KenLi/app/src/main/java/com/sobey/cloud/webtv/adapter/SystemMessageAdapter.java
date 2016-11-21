package com.sobey.cloud.webtv.adapter;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.sobey.cloud.webtv.bean.MsgCommonBean;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SystemMessageAdapter extends UniversalAdapter<MsgCommonBean> {

	public SystemMessageAdapter(Context context, List<MsgCommonBean> mDatas) {
		super(context, mDatas, R.layout.layout_system_message);
	}

	@Override
	public void convert(UniversalViewHolder holder, MsgCommonBean mcb) {
		RoundImageView headIcon = holder.findViewById(R.id.item_msgcommon_headicon);
		TextView time = holder.findViewById(R.id.item_msgcommon_time);
		TextView userName = holder.findViewById(R.id.item_msgcommon_who);
		TextView content = holder.findViewById(R.id.item_msgcommon_content);
		TextView msgCount = holder.findViewById(R.id.item_msgcommon_msgcount);
		if (TextUtils.isEmpty(mcb.getHeadUrl())) {
			headIcon.setImageResource(R.drawable.mymsg_sys);
		} else {
			Picasso.with(mContext).load(mcb.getHeadUrl()).placeholder(R.drawable.default_head)
					.error(R.drawable.default_head).into(headIcon);
		}
		time.setText(mcb.getTime());
		userName.setText(mcb.getUserName());
		try {
			content.setText(URLDecoder.decode(mcb.getContent(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			content.setText(mcb.getContent());
		}
		if (TextUtils.isEmpty(mcb.getMsgCount())) {
			msgCount.setVisibility(View.INVISIBLE);
		} else {
			msgCount.setVisibility(View.VISIBLE);
			msgCount.setText(mcb.getMsgCount());
		}

	}

}
