package com.sobey.cloud.webtv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.adapter.TalkingAdapter;
import com.sobey.cloud.webtv.adapter.TalkingAdapter.OnMessageDelListener;
import com.sobey.cloud.webtv.bean.SpeakMsgBean;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.CommonMethod;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;
import com.sobey.cloud.webtv.kenli.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 私信聊天界面
 * 
 * @author Administrator
 *
 */
public class TalkingActivity extends BaseActivity
		implements IDragListViewListener, OnMessageDelListener, View.OnClickListener {
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView titleTv;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.ac_talking_listview)
	private DragListView mListView;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;
	@GinInjectView(id = R.id.ac_talking_msgedit)
	private EditText msgEt;
	@GinInjectView(id = R.id.ac_talking_sendbtn)
	private Button sendBtn;
	@GinInjectView(id = R.id.ac_talking_mainview)
	private RelativeLayout mainLayout;

	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;

	private boolean isLoading;
	private boolean isPull;
	private int loadPageIndex = 1;
	private int offset = 1;
	private int lastVis;
	private String mUserName;
	private String mUid;
	private String mHeadUrl;
	private String talkId;
	private TalkingAdapter mAdapter;
	private List<SpeakMsgBean> smbList = new ArrayList<SpeakMsgBean>();

	@Override
	public int getContentView() {
		return R.layout.acitivty_talking;

	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		loadingmask.setVisibility(View.GONE);
		mUserName = getIntent().getStringExtra("mUserName");
		mUid = getIntent().getStringExtra("mUid");
		mHeadUrl = getIntent().getStringExtra("mHeadUrl");
		talkId = getIntent().getStringExtra("talkId");
		titleTv.setTitle(mUserName);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setListener(this);
		initData();
		getMessage();

		mBackRl.setOnClickListener(this);
	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		isPull = true;
		initData();
		getMessage();
	}

	@Override
	public void onLoadMore() {

	}

	private void initData() {
		isLoading = false;
		mAdapter = null;
	}

	@GinOnClick(id = { R.id.ac_talking_sendbtn })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.ac_talking_sendbtn:
			sendLetter();
			break;

		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;

		default:
			break;
		}
	}

	/**
	 * 检查输入
	 */
	private boolean checkInput() {
		String content = msgEt.getText().toString();
		// 不能为空
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "不能发送空信息", Toast.LENGTH_SHORT).show();
			return false;
		}
		// String regEx = "[/\\:*?<>|\"\n\t]"; // 要过滤掉的字符
		// Pattern p = Pattern.compile(regEx);
		// Matcher m = p.matcher(content);
		// return m.replaceAll("").trim();
		return true;
	}

	/**
	 * 发私信
	 * 
	 */
	private void sendLetter() {
		if (!checkInput()) {
			return;
		}
		isLoading = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "post_friend_message");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("friend_id", mUid);
		String content = msgEt.getText().toString();
		try {
			map.put("message", URLEncoder.encode(content, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			map.put("message", content);
		}
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "sendLetter", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				JSONObject jObject = JSONObject.parseObject(arg0);
				if ("200".equals(jObject.getString("returnCode"))) {
					SpeakMsgBean smb = new SpeakMsgBean();
					smb.setMessage(msgEt.getText().toString());
					smb.setTime(CommonMethod.getNowTime());
					smb.setFrom_type("0");
					SharedPreferences userInfo = getSharedPreferences("user_info", 0);
					if (userInfo != null) {
						smb.setHead(userInfo.getString("headicon", ""));
					}
					smbList.add(smb);
					showLetterList();
					mListView.setSelection(smbList.size() - 1);
					msgEt.setText("");
				}
			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(TalkingActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				isLoading = false;
				mListView.stopLoadMore();
				mListView.stopRefresh();
			}
		});
	}

	/**
	 * 获取私信消息列表
	 */
	private void getMessage() {
		isLoading = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "get_messages");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("talk_id", talkId);
		map.put("offset", "" + (smbList.size() + 1));
		map.put("limit", "20");
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getMessage", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				JSONObject jOb = JSONObject.parseObject(arg0);
				int msgCount = jOb.getIntValue("message_count");
				if (msgCount > 0) {
					List<SpeakMsgBean> subSmbList = JSONArray.parseArray(jOb.getString("message_list"),
							SpeakMsgBean.class);
					if (subSmbList != null) {
						for (int i = 0; i < subSmbList.size(); i++) {
							SpeakMsgBean smb = subSmbList.get(i);
							smbList.add(0, smb);
						}
						showLetterList();
					}
				} else {
					emptyLayout.setVisibility(View.VISIBLE);
					emptyTv.setText("暂无消息");
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

	private void delMsg(final SpeakMsgBean smb) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "del_message");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("message_ids", smb.getMessage_id());
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "delMsg", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				JSONObject jOb = JSONObject.parseObject(arg0);
				if ("200".equals(jOb.getString("returnCode"))) {
					smbList.remove(smb);
					mAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(TalkingActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onFinish() {

			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(TalkingActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onMessageDel(SpeakMsgBean smb) {
		delMsg(smb);
	}

	private void showLetterList() {
		emptyLayout.setVisibility(View.GONE);
		if (mAdapter == null) {
			mAdapter = new TalkingAdapter(this, smbList);
			mAdapter.setOnMessageDelListener(this);
			mListView.setAdapter(mAdapter);
			if (!isPull) {
				mListView.setSelection(smbList.size() - 1);
			}
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

}
