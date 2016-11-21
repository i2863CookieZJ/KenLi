package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.MyGuanZhuAdapter;
import com.sobey.cloud.webtv.bean.FriendsBean;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.MyGuanZhuBean;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 我的关注界面
 * 
 * @author Administrator
 *
 */
public class MyGuanZhuActivity extends BaseActivity implements IDragListViewListener {
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView titleTv;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.ac_myguanzhu_listview)
	private DragListView mListView;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;

	private boolean isLoading;
	private int loadPageIndex;
	private SharedPreferences userInfo;

	private MyGuanZhuBean mgzb;
	private List<FriendsBean> fdList;
	private MyGuanZhuAdapter mgzAdapter;

	@Override
	public int getContentView() {
		return R.layout.activity_myguanzhu;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int index = position - 1;
				GroupUserModel userModel = new GroupUserModel();
				userModel.uid = fdList.get(index).getFriend_id();
				userModel.userName = fdList.get(index).getNickname();
				userModel.userHeadUrl = fdList.get(index).getHead();
				Intent intent = new Intent(MyGuanZhuActivity.this, FriendCenterActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("mUserName", userModel.userName);
				bundle.putString("mUid", userModel.uid);
				bundle.putString("mHeadUrl", userModel.userHeadUrl);
				intent.putExtra("userInfos", bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		initStatus();
		getMyGuanZhuUser();
	}

	@Override
	public void onLoadMore() {
		loadPageIndex++;
		mListView.setPullLoadEnable(false);
	}

	private void initStatus() {
		isLoading = false;
		mgzAdapter = null;
		loadPageIndex = 1;
		mgzb = new MyGuanZhuBean();
		fdList = new ArrayList<FriendsBean>();
	}

	/**
	 * 获取关注的好友列表
	 */
	private void getMyGuanZhuUser() {
		isLoading = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "get_follow_members");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("page", "" + loadPageIndex);
		map.put("pageSize", "20");
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getMyGuanZhuUser", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				// JSONObject jObject = JSONObject.parseObject(arg0);
				org.json.JSONObject jObject;
				try {
					jObject = new org.json.JSONObject(arg0);
					if (jObject.getInt("friend_count") > 0) {
						List<FriendsBean> subFdList = new ArrayList<FriendsBean>();
						// com.alibaba.fastjson.JSONArray
						// .parseArray(jObject.getString("friend_list"),
						// FriendsBean.class);
						org.json.JSONArray jArray = jObject.getJSONArray("friend_list");
						for (int i = 0; i < jArray.length(); i++) {
							org.json.JSONObject jsonObject = jArray.getJSONObject(i);
							FriendsBean fb = new FriendsBean();
							fb.setFriend_id(jsonObject.getString("friend_id"));
							fb.setHead(jsonObject.getString("head"));
							fb.setNickname(jsonObject.getString("nickname"));
							fb.setSex(jsonObject.getString("sex"));
							// subFdList.add(fb);
							fdList.add(fb);
						}

						// for (FriendsBean fd : subFdList) {
						// fdList.add(fd);
						// }
						showGuanZhuList();
					} else {
						noData();
						emptyTv.setText("您还未关注任何人哦");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(VolleyError arg0) {
				noData();
			}

			@Override
			public void onFinish() {
				isLoading = false;
				loadingmask.setVisibility(View.GONE);
				mListView.stopLoadMore();
				mListView.stopRefresh();
			}
		});
	}

	private void noData() {
		emptyLayout.setVisibility(View.VISIBLE);
		--loadPageIndex;
		if (loadPageIndex < 1) {
			loadPageIndex = 1;
		}
	}

	private void showGuanZhuList() {
		emptyLayout.setVisibility(View.GONE);
		mListView.setPullLoadEnable(true);
		if (mgzAdapter == null) {
			mgzAdapter = new MyGuanZhuAdapter(this, fdList);
			mListView.setAdapter(mgzAdapter);

			if (fdList.size() < 20) {
				mListView.setPullLoadEnable(false);
			}
		} else {
			mgzAdapter.notifyDataSetChanged();
		}
	}
}
