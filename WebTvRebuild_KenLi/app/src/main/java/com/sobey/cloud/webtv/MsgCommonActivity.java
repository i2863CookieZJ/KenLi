package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.adapter.CirclesCommentsAdapter;
import com.sobey.cloud.webtv.adapter.PrivateLetterAdapter;
import com.sobey.cloud.webtv.adapter.SystemMessageAdapter;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.MsgCommonBean;
import com.sobey.cloud.webtv.bean.ReplyCommentListBean;
import com.sobey.cloud.webtv.bean.TalkingContentBean;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 消息列表公共界面
 * 
 * @author Administrator
 *
 */
public class MsgCommonActivity extends BaseActivity implements IDragListViewListener {
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView titleTv;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.ac_msgcommon_listview)
	private DragListView mListView;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;

	private boolean isLoading;
	private boolean isHaveSlide;// 是否有滑出删除按钮
	private int loadPageIndex = 1;
	private int fromWho;

	private CirclesCommentsAdapter mCirclesCommentsAdapter;
	private PrivateLetterAdapter mPrivateLetterAdapter;
	private SystemMessageAdapter mSystemMessageAdapter;
	private List<MsgCommonBean> mcbList;
	private List<ReplyCommentListBean> rcbList;
	private List<GroupSubjectModel> subjectList;
	private List<TalkingContentBean> tcbList;

	@Override
	public int getContentView() {
		return R.layout.activity_mymsg_common;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		fromWho = getIntent().getIntExtra("fromWho", -1);// 从哪个跳转来的
		titleTv.setTitle(getIntent().getStringExtra("title"));
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setListener(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int index = position - 1;
				if (fromWho == 0) {
					if (isHaveSlide) {
						for (int i = 0; i < parent.getChildCount(); i++) {
							parent.getChildAt(i).scrollTo(0, 0);
						}
						isHaveSlide = false;
					} else {
						TextView msgCountTv = (TextView) view.findViewById(R.id.item_msgcommon_msgcount);
						msgCountTv.setVisibility(View.INVISIBLE);
						Intent intent = new Intent(MsgCommonActivity.this, TalkingActivity.class);
						intent.putExtra("mUserName", tcbList.get(index).getFriend_nickname());
						intent.putExtra("mUid", tcbList.get(index).getFriend_id());
						intent.putExtra("mHeadUrl", tcbList.get(index).getFriend_head());
						intent.putExtra("talkId", tcbList.get(index).getTalk_id());
						startActivity(intent);
					}
				} else if (fromWho == 1) {
					TextView msgCountTv = (TextView) view.findViewById(R.id.item_msgcommon_msgcount);
					msgCountTv.setVisibility(View.INVISIBLE);
					Intent intent = new Intent(MsgCommonActivity.this, GroupSubjectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", subjectList.get(index).groupName);
					bundle.putParcelable("mSubjectModel", subjectList.get(index));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (fromWho == 0) {
					final int index = position - 1;
					// 先关闭所有的滑出菜单
					for (int i = 0; i < parent.getChildCount(); i++) {
						parent.getChildAt(i).scrollTo(0, 0);
					}
					TextView delBtn = (TextView) view.findViewById(R.id.item_msgcommon_delitem);
					delBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							delTalk(index);
						}
					});
					view.scrollTo(delBtn.getWidth(), 0);
					isHaveSlide = true;
					return true;
				}
				return true;
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
		switch (fromWho) {
		case 0:
			getTalkList();
			break;
		case 1:
			getReplyCommentList();
			break;
		case 2:
			getSystemMsg();
			break;
		}

	}

	@Override
	public void onLoadMore() {

	}

	private void initStatus() {
		this.isLoading = false;
		this.mcbList = new ArrayList<>();
		this.tcbList = new ArrayList<>();
		this.rcbList = new ArrayList<>();
		this.subjectList = new ArrayList<>();
		this.mSystemMessageAdapter = null;
		this.mPrivateLetterAdapter = null;
		this.mCirclesCommentsAdapter = null;
	}

	/**
	 * 删除对话
	 * 
	 * @param index
	 */
	private void delTalk(final int index) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "del_talk");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("talk_id", tcbList.get(index).getTalk_id());
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "delTalk", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				JSONObject job = JSONObject.parseObject(arg0);
				if ("200".equals(job.getString("returnCode"))) {
					mcbList.remove(index);
					mPrivateLetterAdapter.notifyDataSetChanged();
					if (isHaveSlide) {
						for (int i = 0; i < mListView.getChildCount(); i++) {
							mListView.getChildAt(i).scrollTo(0, 0);
						}
						isHaveSlide = false;
					} else {
						Toast.makeText(MsgCommonActivity.this, "请重试", Toast.LENGTH_SHORT).show();
					}
				}

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(MsgCommonActivity.this, "请重试", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取私信列表
	 */
	private void getTalkList() {
		isLoading = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "get_talks");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("page", "" + loadPageIndex);
		map.put("pageSize", "20");
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getTalkList", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				JSONObject jObject = JSONObject.parseObject(arg0);
				if (jObject.getIntValue("talk_count") > 0) {
					List<TalkingContentBean> subtcbList = JSONArray.parseArray(jObject.getString("talk_list"),
							TalkingContentBean.class);
					for (TalkingContentBean tcb : subtcbList) {
						MsgCommonBean mcb = new MsgCommonBean();
						mcb.setContent(tcb.getLast_message());
						mcb.setHeadUrl(tcb.getFriend_head());
						mcb.setTime(tcb.getLast_mesage_time());
						mcb.setUserName(tcb.getFriend_nickname());
						mcb.setMsgCount(tcb.getNew_num());
						mcbList.add(mcb);
						tcbList.add(tcb);
					}
					showMsgList();
				} else {
					emptyLayout.setVisibility(View.VISIBLE);
					emptyTv.setText("无私信消息");
				}
			}

			@Override
			public void onFinish() {
				isLoading = false;
				loadingmask.setVisibility(View.GONE);
				mListView.stopLoadMore();
				mListView.stopRefresh();
			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(MsgCommonActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getReplyCommentList() {
		isLoading = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "getReplyCommentList");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("page", "" + loadPageIndex);
		map.put("pageSize", "20");
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getReplyCommentList", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				org.json.JSONObject joJsonObject;
				try {
					joJsonObject = new org.json.JSONObject(arg0);
					if (!joJsonObject.getString("total").equals("0")) {
						org.json.JSONArray jArray = joJsonObject.getJSONArray("replyList");
						List<ReplyCommentListBean> subRcbList = new ArrayList<ReplyCommentListBean>();
						for (int i = 0; i < jArray.length(); i++) {
							org.json.JSONObject jObject = jArray.getJSONObject(i);
							ReplyCommentListBean rcb = new ReplyCommentListBean();
							rcb.setHead(jObject.getString("head"));
							rcb.setMessage(jObject.getString("message"));
							rcb.setNickname(jObject.getString("nickname"));
							rcb.setSubject(jObject.getString("subject"));
							rcb.setTid(jObject.getString("tid"));
							rcb.setTime(jObject.getString("time"));
							subRcbList.add(rcb);

							GroupSubjectModel localGroupSubjectModel = new GroupSubjectModel();
							localGroupSubjectModel.groupId = jObject.getString("groupId");
							localGroupSubjectModel.groupName = jObject.getString("groupName");
							localGroupSubjectModel.groupInfo = jObject.getString("groupInfo");
							localGroupSubjectModel.groupHeadUrl = jObject.getString("headUrl");
							localGroupSubjectModel.subjectId = jObject.getString("subjectId");
							localGroupSubjectModel.subjectTitle = jObject.getString("subjectTitle");
							localGroupSubjectModel.subjectContent = jObject.getString("subjectContent");
							localGroupSubjectModel.publishUserHeadUrl = jObject.getString("publishUserHeadUrl");
							localGroupSubjectModel.publishTime = jObject.getString("publishTime");
							localGroupSubjectModel.publishUserName = jObject.getString("publishUserName");
							localGroupSubjectModel.publishUserId = jObject.getString("publishUserId");
							subjectList.add(localGroupSubjectModel);
						}
						// List<ReplyCommentListBean> subRcbList =
						// JSONArray.parseArray(
						// "[" + joJsonObject.getString("replyList") + "]",
						// ReplyCommentListBean.class);
						for (ReplyCommentListBean rcb : subRcbList) {
							MsgCommonBean mcb = new MsgCommonBean();
							mcb.setTid(rcb.getTid());
							mcb.setHeadUrl(getSharedPreferences("user_info", 0).getString("headicon", ""));
							mcb.setContent(rcb.getSubject());
							mcb.setTime(rcb.getTime());
							mcb.setUserName(PreferencesUtil.getLoggedUserName());
							mcb.setReplyContent(rcb.getMessage());
							mcb.setReplyUser(rcb.getNickname());
							mcbList.add(mcb);
							rcbList.add(rcb);

						}
						showMsgList();
					} else {
						emptyLayout.setVisibility(View.VISIBLE);
						emptyTv.setText("无圈子评论");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				isLoading = false;
				loadingmask.setVisibility(View.GONE);
				mListView.stopLoadMore();
				mListView.stopRefresh();
			}

			@Override
			public void onFail(VolleyError arg0) {

			}
		});
	}

	/**
	 * 获取系统消息
	 */
	private void getSystemMsg() {
		String sysData = FileUtil.readTextFromDataDir("system_msg");
		JSONArray jArray = JSONArray.parseArray(sysData);
		if (jArray != null) {
			for (int i = 0; i < jArray.size(); i++) {
				MsgCommonBean mcb = JSONArray.parseObject(jArray.getString(i), MsgCommonBean.class);
				mcbList.add(mcb);
			}
			showMsgList();
		} else {
			initStatus();
			loadingmask.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.VISIBLE);
			emptyTv.setText("无系统消息");
		}

	}

	private void showMsgList() {
		this.isLoading = false;
		this.mListView.stopLoadMore();
		this.mListView.stopRefresh();
		this.loadingmask.setVisibility(8);
		if (this.fromWho == 0) {
			if (this.mPrivateLetterAdapter == null) {
				this.mPrivateLetterAdapter = new PrivateLetterAdapter(this, this.mcbList);
				this.mListView.setAdapter(this.mPrivateLetterAdapter);
				return;
			}
			this.mPrivateLetterAdapter.notifyDataSetChanged();
			return;
		}
		if (this.fromWho == 1) {
			if (this.mCirclesCommentsAdapter == null) {
				this.mCirclesCommentsAdapter = new CirclesCommentsAdapter(this, this.mcbList);
				this.mListView.setAdapter(this.mCirclesCommentsAdapter);
				return;
			}
			this.mCirclesCommentsAdapter.notifyDataSetChanged();
			return;
		}
		if (this.mSystemMessageAdapter == null) {
			this.mSystemMessageAdapter = new SystemMessageAdapter(this, this.mcbList);
			this.mListView.setAdapter(this.mSystemMessageAdapter);
			return;
		}
		this.mSystemMessageAdapter.notifyDataSetChanged();
	}
}
