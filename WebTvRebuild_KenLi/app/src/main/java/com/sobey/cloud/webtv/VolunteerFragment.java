package com.sobey.cloud.webtv;

import com.dylan.uiparts.listview.DragListView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.ui.GeneralNewsHome;
import com.sobey.cloud.webtv.ui.NormalNewsUtil;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * 志愿者
 * 
 * @author lazy
 *
 */
public class VolunteerFragment extends BaseFragment {

	@GinInjectView(id = R.id.mVolunteerNewsList)
	private DragListView mVolunteerNewsList;
	@GinInjectView(id = R.id.title)
	private TextView title;
	@GinInjectView(id = R.id.loadingMask)
	private RelativeLayout loadingMask;

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.fragement_volunteer);
		return view;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isUseCache()) {
			return;
		}

		initializeView();
		// loadVolunteerData();
		loadingMask.setVisibility(View.GONE);
		GeneralNewsHome generalNewsHome = new NormalNewsUtil(loadingMask, getActivity());
		generalNewsHome.init(getActivity(), MConfig.Hot_HuiZhouNews, mVolunteerNewsList, null, null, null, null);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.user_login:
			userLogin();
			break;
		}
	}

	public VolunteerFragment() {

	}

	protected void initializeView() {
		title.setText("志愿者");
	}

	public void userLogin() {
		Intent intent = new Intent(getActivity(), PersonalCenterActivity.class);
		startActivity(intent);
	}
}
