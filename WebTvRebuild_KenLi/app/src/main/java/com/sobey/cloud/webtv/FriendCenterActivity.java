package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.adapter.CollectionZiXunAdapter;
import com.sobey.cloud.webtv.adapter.MyTieziAdapter;
import com.sobey.cloud.webtv.bean.CollectionZiXunBean;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.kenli.R;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 好友/个人中心
 * 
 * @author Administrator
 *
 */
public class FriendCenterActivity extends BaseActivity implements IDragListViewListener {

	@GinInjectView(id = R.id.ac_fdcenter_headicon)
	private RoundImageView headIcon;
	@GinInjectView(id = R.id.ac_fdcenter_username)
	private TextView nameTv;
	@GinInjectView(id = R.id.ac_fdcenter_zxbtn)
	private RadioButton zxRbBtn;
	@GinInjectView(id = R.id.ac_fdcenter_tzbtn)
	private RadioButton tzRbBtn;
	@GinInjectView(id = R.id.ac_fdcenter_listview)
	private DragListView mListView;
	@GinInjectView(id = R.id.ac_fdcenter_addguanzhu)
	private Button addGzBtn;
	@GinInjectView(id = R.id.ac_fdcenter_delguanzhu)
	private Button delGzBtn;
	@GinInjectView(id = R.id.ac_fdcenter_sendmsg)
	private Button sendMsgBtn;

	private boolean isLoading;
	private int loadPageIndex;
	private int fromWho;
	private SharedPreferences userInfo;

	private List<CollectionZiXunBean> czxbList;// 资讯收藏集合

	private CollectionZiXunAdapter czAdapter;// 资讯适配器
	private String mUserName;
	private String mUid;
	private String mHeadUrl;
	private int isFollow;
	private List<GroupSubjectModel> groupSubjectModels = new ArrayList<GroupSubjectModel>();

	@Override
	public int getContentView() {
		return R.layout.activity_friendcenter;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		zxRbBtn.setChecked(true);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setListener(this);
		userInfo = this.getSharedPreferences("user_info", 0);
		Bundle bundle = this.getIntent().getBundleExtra("userInfos");
		if (null != savedInstanceState) {
			bundle = savedInstanceState.getBundle("userInfos");
		}
		if (null != bundle) {
			mUserName = bundle.getString("mUserName");
			mUid = bundle.getString("mUid");
			mHeadUrl = bundle.getString("mHeadUrl");
			if (TextUtils.isEmpty(mHeadUrl)) {
				headIcon.setImageResource(R.drawable.default_head);
			} else {
				Picasso.with(this).load(mHeadUrl).placeholder(R.drawable.default_head).error(R.drawable.default_head)
						.into(headIcon);
			}
			nameTv.setText(mUserName);
		}
		onRefresh();
	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		initStatus();
		getIsFollow();
		getHisTz();
	}

	@Override
	public void onLoadMore() {

	}

	@GinOnClick(id = { R.id.ac_fdcenter_zxbtn, R.id.ac_fdcenter_tzbtn, R.id.ac_fdcenter_addguanzhu,
			R.id.ac_fdcenter_delguanzhu, R.id.ac_fdcenter_sendmsg, R.id.back_rl })
	private void btnClick(View view) {
		switch (view.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		case R.id.ac_fdcenter_zxbtn:
			initStatus();
			break;
		case R.id.ac_fdcenter_tzbtn:
			initStatus();
			getHisTz();
			break;
		case R.id.ac_fdcenter_addguanzhu:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				break;
			}
			addOrCancelFollow(1);
			break;
		case R.id.ac_fdcenter_delguanzhu:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				break;
			}
			addOrCancelFollow(0);
			break;
		case R.id.ac_fdcenter_sendmsg:
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				break;
			}
			if (isFollow == 0) {
				Toast.makeText(this, "请先关注", Toast.LENGTH_SHORT).show();
				break;
			}
			getTalkId();
			break;
		}
	}

	/**
	 * 初始化基本状态
	 */
	private void initStatus() {
		isLoading = false;
		czAdapter = null;
		czxbList = new ArrayList<CollectionZiXunBean>();
	}

	private void getHisZx2() {
		isLoading = true;
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", userInfo.getString("id", ""));
		params.put("method", "getCollect");
		params.put("type", "1");
		params.put("siteId", "" + MConfig.SITE_ID);
		VolleyRequset.cancelQueue("getHis");
		VolleyRequset.doPost(this, MConfig.mServerUrl, "getHis", params, new VolleyListener() {

			@Override
			public void onSuccess(String result) {
				List<CollectionZiXunBean> CollectionZiXunBean = JSONArray.parseArray(result, CollectionZiXunBean.class);
				for (CollectionZiXunBean czb : CollectionZiXunBean) {
					czxbList.add(czb);
				}
				showHisZxList();
			}

			@Override
			public void onFail(VolleyError arg0) {
				initStatus();
				// loadingmask.setVisibility(View.GONE);
				// emptyLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getTalkId() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "get_talk");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("friend_id", mUid);
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getTalkId", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				try {
					com.alibaba.fastjson.JSONObject job = com.alibaba.fastjson.JSONObject.parseObject(arg0);
					if ("200".equals(job.getString("returnCode"))) {
						Intent intent = new Intent(FriendCenterActivity.this, TalkingActivity.class);
						intent.putExtra("mUserName", mUserName);
						intent.putExtra("mUid", mUid);
						intent.putExtra("mHeadUrl", mHeadUrl);
						intent.putExtra("talkId", job.getString("talk_id"));
						startActivity(intent);
					} else {
						Toast.makeText(FriendCenterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {

				}
			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(FriendCenterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 是否已经关注过该人
	 */
	private void getIsFollow() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "check_follow_member");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("friend_id", mUid);
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getIsFollow", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				try {
					JSONObject jObject = new JSONObject(arg0);
					if ("200".equals(jObject.getString("returnCode"))) {
						if ("1".equals(jObject.getString("is_follow"))) {// 已经关注
							addGzBtn.setVisibility(View.GONE);
							delGzBtn.setVisibility(View.VISIBLE);
							isFollow = 1;
						} else {
							isFollow = 0;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(VolleyError arg0) {

			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 关注动作
	 */
	private void addOrCancelFollow(final int is_follow) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "follow_member");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("friend_id", mUid);
		map.put("siteid", "" + MConfig.SITE_ID);
		map.put("is_follow", "" + is_follow);
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "addFollow", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				String returnCode;
				try {
					returnCode = new JSONObject(arg0).getString("returnCode");
					if ("200".equals(returnCode)) {
						switch (is_follow) {
						case 0:
							Toast.makeText(FriendCenterActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
							isFollow = 0;
							delGzBtn.setVisibility(View.GONE);
							addGzBtn.setVisibility(View.VISIBLE);
							break;

						case 1:
							Toast.makeText(FriendCenterActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
							isFollow = 1;
							addGzBtn.setVisibility(View.GONE);
							delGzBtn.setVisibility(View.VISIBLE);
							break;
						}

					} else {
						Toast.makeText(FriendCenterActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(FriendCenterActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {

			}
		});
	}

	/**
	 * 获得帖子的信息
	 */
	public void getHisTz() {
		GroupRequestMananger.getInstance().getUserInfo(mUid, this, new RequestResultListner() {
			// GroupRequestMananger.getInstance().getUserInfo("1", this,
			// new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				// mCloseLoadingIcon();
				if (result instanceof GroupUserModel) {
					GroupUserModel groupUserModel = (GroupUserModel) result;
					groupSubjectModels = groupUserModel.postedSubjectList;
					if (groupSubjectModels.size() > 0) {
						MyTieziAdapter adapter = new MyTieziAdapter(FriendCenterActivity.this);
						adapter.setData(groupSubjectModels);
						mListView.setAdapter(adapter);
					} else {
						mListView.setVisibility(View.INVISIBLE);
						// emptyLayout.setVisibility(View.VISIBLE);
						// showNoContent(emptyLayout,
						// R.drawable.nocontent_mytiezi, "您还没有发布帖子...");
					}
					// Log.v("myGroupList",
					// groupUserModel.postedSubjectList.get(0).subjectContent);
				} else {
					mListView.setVisibility(View.INVISIBLE);
					// emptyLayout.setVisibility(View.VISIBLE);
					// showNoContent(emptyLayout,
					// R.drawable.nocontent_mytiezi, "您还没有发布帖子...");
				}
				mListView.stopLoadMore();
				mListView.stopRefresh();
			}
		});
	}

	private void showHisZxList() {
		isLoading = false;
		// loadingmask.setVisibility(View.GONE);
		// emptyLayout.setVisibility(View.GONE);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		if (czAdapter == null) {
			czAdapter = new CollectionZiXunAdapter(this, czxbList);
			mListView.setAdapter(czAdapter);
		} else {
			czAdapter.notifyDataSetChanged();
		}
	}
}
