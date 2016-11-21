package com.sobey.cloud.webtv.adapter;

import java.util.Date;
import java.util.List;

import com.dylan.common.utils.DateParse;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.HomeHeadLineBean;
import com.sobey.cloud.webtv.fragment.HomeHeadLines.NewsType;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeHeadLinesAdapter extends UniversalAdapter<HomeHeadLineBean> {

	public HomeHeadLinesAdapter(Context context, List<HomeHeadLineBean> mDatas) {
		super(context, mDatas, R.layout.item_homeheadline);
	}

	@Override
	public void convert(UniversalViewHolder holder, HomeHeadLineBean hhlb) {
		ImageView newsPic = holder.findViewById(R.id.item_homeheadline_newspic);
		ImageView videoTagPic = holder.findViewById(R.id.item_homeheadline_videotagpic);
		TextView videoTagTime = holder.findViewById(R.id.item_homeheadline_videotagtime);
		TextView newsTitle = holder.findViewById(R.id.item_homeheadline_newstitle);
		TextView newsSummary = holder.findViewById(R.id.item_homeheadline_newssummary);
		TextView pushTime = holder.findViewById(R.id.item_homeheadline_newspushtime);
		videoTagPic.setVisibility(View.VISIBLE);
		videoTagTime.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(hhlb.getDuration())) {
			videoTagTime.setText(hhlb.getDuration());
		}
		if (NewsType.NORMAL.equals(hhlb.getType()) || NewsType.QZ.equals(hhlb.getType())
				|| NewsType.IMAGE.equals(hhlb.getType())) {
			videoTagPic.setVisibility(View.GONE);
			videoTagTime.setVisibility(View.GONE);
		} else if (hhlb.getType().equals("" + NewsType.VIDEO_VOD)) {

		}
		if (!TextUtils.isEmpty(hhlb.getLogo1())) {
			Picasso.with(mContext).load(hhlb.getLogo1()).placeholder(R.drawable.default_item_loading)
					.error(R.drawable.default_item_loading).into(newsPic);
		} else {
			newsPic.setImageResource(R.drawable.default_item_loading);
		}
		newsTitle.setText(hhlb.getTitle());
		newsSummary.setText(hhlb.getSummary());
		pushTime.setText(formatDate(hhlb.getPublishdate(), "yyyy-MM-dd HH:mm:ss"));
	}

	private String formatDate(String dateStr, String formatStr) {
		Date date = DateParse.parseDate(dateStr, formatStr);
		Date now = new Date(System.currentTimeMillis());
		long dif = now.getTime() - date.getTime();
		// 5分钟内
		if (dif <= (5 * 60 * 1000)) {
			return "刚刚";
		} else if (date.getYear() == now.getYear() && date.getMonth() == now.getMonth()) {
			if (date.getDate() == now.getDate())
				return "今天 "
						+ DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "HH:mm");
			else if (date.getDate() + 1 == now.getDate())
				return "昨天 "
						+ DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "HH:mm");
		}
		return DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "MM-dd");
	}
}
