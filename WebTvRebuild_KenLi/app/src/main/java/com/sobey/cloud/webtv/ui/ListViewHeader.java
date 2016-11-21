package com.sobey.cloud.webtv.ui;

import com.appsdk.androidadvancedui.listener.AdvancedListViewHeader;
import com.appsdk.androidadvancedui.listener.AdvancedListViewHeaderListener;
import com.dylan.common.utils.DateParse;
import com.dylan.common.utils.ScaleConversion;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListViewHeader extends AdvancedListViewHeader {
	private Context mContext;
	private View mHeaderView;
	private ImageView mHeaderCircleImageView;
	private ProgressBar mHeaderProgressBar;
	private TextView mHeaderNoticeTextView;
	private TextView mHeaderDurationTextView;
	private String mLastRefreshTime;

	public ListViewHeader(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_header, null);
		mHeaderCircleImageView = (ImageView) mHeaderView.findViewById(R.id.listview_header_circle_imageview);
		mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.listview_header_progressbar);
		mHeaderNoticeTextView = (TextView) mHeaderView.findViewById(R.id.listview_header_notice_textview);
		mHeaderDurationTextView = (TextView) mHeaderView.findViewById(R.id.listview_header_duration_textview);
		mHeaderNoticeTextView.setText(mContext.getResources().getString(R.string.listview_header_notice_normal));
		mLastRefreshTime = DateParse.getNowDate(null);

		initHeaderView(mHeaderView, ScaleConversion.dip2px(mContext, 100), ScaleConversion.dip2px(mContext, 60), 0);

		// Fix header height
		setFixHeaderHeight(true);

		// Set listener
		setHeaderListener(new AdvancedListViewHeaderListener() {
			@Override
			public void onTrigger(boolean triggerFlag) {
				if (triggerFlag) {
					mHeaderNoticeTextView.setText(mContext.getResources().getString(R.string.listview_header_notice_ready));
				} else {
					mHeaderNoticeTextView.setText(mContext.getResources().getString(R.string.listview_header_notice_normal));
				}
			}

			@Override
			public void onStartPullDown() {
				mHeaderNoticeTextView.setText(mContext.getResources().getString(R.string.listview_header_notice_loading));
				mHeaderProgressBar.setVisibility(View.VISIBLE);
				mHeaderCircleImageView.setVisibility(View.GONE);
			}

			@Override
			public void onStopPullDown() {
				mHeaderNoticeTextView.setText(mContext.getResources().getString(R.string.listview_header_notice_normal));
				mHeaderProgressBar.setVisibility(View.GONE);
				mHeaderCircleImageView.setVisibility(View.VISIBLE);
				mLastRefreshTime = DateParse.getNowDate(null);
			}

			@Override
			public void onScoll(int height) {
				if (!TextUtils.isEmpty(mLastRefreshTime) && height > 0) {
					mHeaderDurationTextView.setText(DateParse.getDateDif(mLastRefreshTime, null) + "前");
				}
			}
		});
	}

}
