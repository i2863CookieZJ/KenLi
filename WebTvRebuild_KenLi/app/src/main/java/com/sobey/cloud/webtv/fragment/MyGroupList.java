package com.sobey.cloud.webtv.fragment;

import java.util.ArrayList;
import java.util.List;

import com.dylan.common.animation.AnimationController;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.MyTieziAdapter;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的帖子信息
 * 
 * @author lgx
 *
 */
// @ContentView(R.layout.my_group_list)
public class MyGroupList extends BaseActivity {
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.listView)
	private ListView listView;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	private String uid = "";
	private List<GroupSubjectModel> groupSubjectModels = new ArrayList<GroupSubjectModel>();

	@Override
	public int getContentView() {
		return R.layout.my_group_list;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("我的帖子");
		// emptyTv.setText(R.string.has_no_result);
		uid = PreferencesUtil.getLoggedUserId();
		if (!TextUtils.isEmpty(uid)) {
			getGroupList();
		}
		mOpenLoadingIcon();
	}
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// // ViewUtils.inject(this);
	// titlebar_name.setText("我的帖子");
	// emptyTv.setText(R.string.has_no_result);
	// uid = PreferencesUtil.getLoggedUserId();
	// if (!TextUtils.isEmpty(uid)) {
	// getGroupList();
	// }
	// mOpenLoadingIcon();
	// }

	@GinOnClick(id = { R.id.back_rl })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		}
	}

	/**
	 * 获得帖子的信息
	 */
	public void getGroupList() {
		// XXX change by bin 5/29
		GroupRequestMananger.getInstance().getUserInfo(PreferencesUtil.getLoggedUserId(), this,
				new RequestResultListner() {
					// GroupRequestMananger.getInstance().getUserInfo("1", this,
					// new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						// TODO Auto-generated method stub
						mCloseLoadingIcon();
						if (result instanceof GroupUserModel) {
							GroupUserModel groupUserModel = (GroupUserModel) result;
							groupSubjectModels = groupUserModel.postedSubjectList;
							if (groupSubjectModels.size() > 0) {
								MyTieziAdapter adapter = new MyTieziAdapter(MyGroupList.this);
								adapter.setData(groupSubjectModels);
								listView.setAdapter(adapter);
							} else {
								listView.setVisibility(View.GONE);
								// emptyLayout.setVisibility(View.VISIBLE);
								showNoContent(emptyLayout, R.drawable.nocontent_mytiezi, "您还没有发布帖子...");
							}
							// Log.v("myGroupList",
							// groupUserModel.postedSubjectList.get(0).subjectContent);
						} else {
							listView.setVisibility(View.GONE);
							// emptyLayout.setVisibility(View.VISIBLE);
							showNoContent(emptyLayout, R.drawable.nocontent_mytiezi, "您还没有发布帖子...");
						}

					}
				});
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(listView);
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(listView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

}
