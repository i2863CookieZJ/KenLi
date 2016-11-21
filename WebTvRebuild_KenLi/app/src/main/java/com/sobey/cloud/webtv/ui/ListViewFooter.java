package com.sobey.cloud.webtv.ui;

import com.appsdk.androidadvancedui.listener.AdvancedListViewFooter;
import com.appsdk.androidadvancedui.listener.AdvancedListViewFooterListener;
import com.dylan.common.utils.ScaleConversion;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListViewFooter extends AdvancedListViewFooter {
	private Context mContext;
	private View mFooterView;
	private ProgressBar mFooterProgressBar;
	private TextView mFooterNoticeTextView;

	public ListViewFooter(Context context) {
		super(context);
		mContext = context;
		mFooterView = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_footer, null);
		mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.listview_footer_progressbar);
		mFooterNoticeTextView = (TextView) mFooterView.findViewById(R.id.listview_footer_notice_textview);
		mFooterNoticeTextView.setText(mContext.getResources().getString(R.string.listview_footer_notice_normal));

		initFooterView(mFooterView, ScaleConversion.dip2px(mContext, 100), ScaleConversion.dip2px(mContext, 60), ScaleConversion.dip2px(mContext, 44));
		setFixFooterHeight(true);
		setFooterListener(new AdvancedListViewFooterListener() {
			@Override
			public void onTrigger(boolean triggerFlag) {
				if (triggerFlag) {
					mFooterNoticeTextView.setText(mContext.getResources().getString(R.string.listview_footer_notice_ready));
				} else {
					mFooterNoticeTextView.setText(mContext.getResources().getString(R.string.listview_footer_notice_normal));
				}
			}

			@Override
			public void onStartPullUp() {
				mFooterNoticeTextView.setVisibility(View.GONE);
				mFooterProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStopPullUp() {
				mFooterNoticeTextView.setVisibility(View.VISIBLE);
				mFooterNoticeTextView.setText(mContext.getResources().getString(R.string.listview_footer_notice_normal));
				mFooterProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onScoll(int height) {
			}
		});
	}

}
