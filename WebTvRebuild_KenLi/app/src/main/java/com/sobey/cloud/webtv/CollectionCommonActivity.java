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
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.CollectionQuanZiAdapter;
import com.sobey.cloud.webtv.adapter.CollectionZiXunAdapter;
import com.sobey.cloud.webtv.bean.CollectionZiXunBean;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 资讯&帖子收藏公共界面
 * 
 * @author Administrator
 *
 */
public class CollectionCommonActivity extends BaseActivity implements IDragListViewListener {

	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView titleTv;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.ac_collectioncommon_listview)
	private DragListView mListView;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;

	private boolean isLoading;
	private int loadPageIndex;
	private int fromWho;
	private SharedPreferences userInfo;

	private List<CollectionZiXunBean> czxbList;// 资讯收藏集合
	private List<GroupSubjectModel> gsmList;// 圈子收藏集合

	private CollectionZiXunAdapter czAdapter;// 资讯适配器
	private CollectionQuanZiAdapter cqAdapter;// 帖子适配器

	@Override
	public int getContentView() {
		return R.layout.activity_collection_common;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		fromWho = getIntent().getIntExtra("fromWho", -1);// 从哪个跳转来的
		titleTv.setTitle(getIntent().getStringExtra("title"));
		userInfo = this.getSharedPreferences("user_info", 0);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这个onItemClick的positon默认从1开始、fuck
				int index = position - 1;
				switch (fromWho) {
				case 0: {
					// 把原先的id的字段的值改成它aid的值，别TM问我为什么，之前的人是这么个意思
					czxbList.get(index).setId(czxbList.get(index).getAid());
					openDetailActivity(Integer.valueOf(czxbList.get(index).getType()),
							JSONObject.toJSONString(czxbList.get(index)));
				}
					break;

				case 1: {
					Intent intent = new Intent(CollectionCommonActivity.this, GroupSubjectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", gsmList.get(index).groupName);
					bundle.putParcelable("mSubjectModel", gsmList.get(index));
					intent.putExtras(bundle);
					CollectionCommonActivity.this.startActivity(intent);
				}

					break;
				}

			}
		});
		// 点击重试
		emptyLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emptyLayout.setVisibility(View.GONE);
				loadingmask.setVisibility(View.VISIBLE);
				onRefresh();
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
			getZiXunCollection();
			break;
		case 1:
			getQuanZiCollection();
			break;
		}

	}

	@Override
	public void onLoadMore() {

	}

	/**
	 * 初始化基本状态
	 */
	private void initStatus() {
		isLoading = false;
		czAdapter = null;
		cqAdapter = null;
		czxbList = new ArrayList<CollectionZiXunBean>();
		gsmList = new ArrayList<GroupSubjectModel>();
	}

	/**
	 * 获取资讯收藏
	 */
	private void getZiXunCollection() {
		isLoading = true;
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", userInfo.getString("id", ""));
		params.put("method", "getCollect");
		params.put("type", "1");
		params.put("siteId", "" + MConfig.SITE_ID);
		VolleyRequset.doPost(this, MConfig.mServerUrl, "getZiXunCollection", params, new VolleyListener() {

			@Override
			public void onSuccess(String result) {
				List<CollectionZiXunBean> CollectionZiXunBean = JSONArray.parseArray(result, CollectionZiXunBean.class);
				for (CollectionZiXunBean czb : CollectionZiXunBean) {
					czxbList.add(czb);
				}
				showZiXunCollectionList();
			}

			@Override
			public void onFail(VolleyError arg0) {
				initStatus();
				loadingmask.setVisibility(View.GONE);
				emptyLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 获取帖子收藏
	 */
	private void getQuanZiCollection() {
		isLoading = true;
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "getcollectedSubjectList");
		params.put("uid", PreferencesUtil.getLoggedUserId());
		VolleyRequset.doPost(this, MConfig.mQuanZiApiUrl, "getQuanZiCollection", params, new VolleyListener() {

			@Override
			public void onSuccess(String result) {
				try {
					org.json.JSONObject jsonObject = new org.json.JSONObject(result);
					org.json.JSONArray jArray = jsonObject.getJSONArray("subjectList");
					for (int i = 0; i < jArray.length(); i++) {
						org.json.JSONObject jObject = jArray.getJSONObject(i);
						GroupSubjectModel subjectModel = new GroupSubjectModel();
						subjectModel.groupId = jObject.getString("groupId");
						subjectModel.groupInfo = jObject.getString("groupInfo");
						subjectModel.groupName = jObject.getString("groupName");
						subjectModel.publishTime = jObject.getString("publishTime");
						subjectModel.publishUserHeadUrl = jObject.getString("publishUserHeadUrl");
						subjectModel.publishUserId = jObject.getString("publishUserId");
						subjectModel.publishUserName = jObject.getString("publishUserName");
						subjectModel.subjectContent = jObject.getString("subjectContent");
						subjectModel.subjectId = jObject.getString("subjectId");
						subjectModel.subjectLikeCount = jObject.getString("subjectLikeCount");
						subjectModel.subjectReplyCount = jObject.getString("subjectReplyCount");
						subjectModel.subjectTitle = jObject.getString("subjectTitle");
						String subjectPicUrls = jObject.getString("subjectPicUrls");
						if (!TextUtils.isEmpty(subjectPicUrls)) {
							subjectModel.subjectPicUrls = subjectPicUrls.split(",");
							for (int j = 0; j < subjectModel.subjectPicUrls.length; j++) {
								subjectModel.subjectPicUrls[j] = MConfig.QZ_DOMAIN
										+ subjectModel.subjectPicUrls[j];
							}
						}
						gsmList.add(subjectModel);
						showQuanZiCollectionList();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(VolleyError arg0) {
				initStatus();
				loadingmask.setVisibility(View.GONE);
				emptyLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 显示资讯收藏列表
	 */
	private void showZiXunCollectionList() {
		isLoading = false;
		loadingmask.setVisibility(View.GONE);
		emptyLayout.setVisibility(View.GONE);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		if (czAdapter == null) {
			czAdapter = new CollectionZiXunAdapter(this, czxbList);
			mListView.setAdapter(czAdapter);
		} else {
			czAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 显示帖子收藏列表
	 */
	private void showQuanZiCollectionList() {
		isLoading = false;
		loadingmask.setVisibility(View.GONE);
		emptyLayout.setVisibility(View.GONE);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		if (cqAdapter == null) {
			cqAdapter = new CollectionQuanZiAdapter(this, gsmList);
			mListView.setAdapter(cqAdapter);
		} else {
			cqAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 根据资讯type打开对应的新闻类型
	 * 
	 * @param type
	 *            新闻类型
	 * @param information
	 *            该条新闻的json信息
	 */
	private void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(this);
			Intent intent1 = new Intent(this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			this.startActivity(intent1);
			break;
		case MConfig.TypeBroke:
			Intent intent2 = new Intent(this, com.sobey.cloud.webtv.broke.BrokeNewsDetailActivity.class);
			intent2.putExtra("information", information);
			this.startActivity(intent2);
			break;
		case MConfig.TypeNews:
			Intent intent3 = new Intent(this, GeneralNewsDetailActivity.class);
			intent3.putExtra("information", information);
			this.startActivity(intent3);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(this);
			Intent intent4 = new Intent(this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			this.startActivity(intent4);
			break;
		}
	}
}
