package com.sobey.cloud.webtv.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.adapter.HomeHeadLinesAdapter;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.HomeHeadLineBean;
import com.sobey.cloud.webtv.bean.RequestResultParser;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.AutoPlayUtil;
import com.sobey.cloud.webtv.utils.CommonMethod;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import libcore.io.DiskLruCache;

/**
 * 首页-头条 16/1/4
 * 
 * @author Administrator
 *
 */
public class HomeHeadLines extends BaseFragment
		implements IDragListViewListener, OnItemClickListener, OnScrollListener {

	@GinInjectView(id = R.id.attentionList)
	private DragListView attentionListView;
	@GinInjectView(id = R.id.attention_loader_mask)
	private RelativeLayout attention_loader_mask;
	@GinInjectView(id = R.id.back_top)
	private View back_top;
	private View topNewsView;
	private boolean isLoading;
	private int loadPageIndex;
	private int singleLoadCount = 10;

	private List<HomeHeadLineBean> bottomNewsList;
	private List<GroupSubjectModel> qzModelList;
	private HomeHeadLinesAdapter mAdapter;
	private SharedPreferences userInfo;
	private AutoPlayUtil apu;

	private File cacheDir = null;
	private DiskLruCache mDiskLruCache = null;// diskLruCache缓存
	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.fragment_newattention_home);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupActivity();
	}

	private void setupActivity() {
		topNewsView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend_news, null);// 顶部轮播
		attentionListView.addHeaderView(topNewsView);
		attentionListView.stopLoadMore();
		attentionListView.stopRefresh();
		attentionListView.setPullRefreshEnable(true);
		attentionListView.setPullLoadEnable(true);
		attentionListView.setListener(this);
		attentionListView.setFooterBackgroundColor(0xffffffff);
		attentionListView.setVisibility(View.GONE);
		attentionListView.setOnScrollListener(this);
		attention_loader_mask.setVisibility(View.VISIBLE);
		attentionListView.setOnItemClickListener(this);
		apu = new AutoPlayUtil(topNewsView, getActivity(), new AutoPlayUtil.LoadImageListener() {

			@Override
			public void loadComplete() {
				if (topNewsView.getTag() != null && "noPage".equals(topNewsView.getTag().toString())) {// 没轮播的时候不显示
					attentionListView.removeHeaderView(topNewsView);
				}
			}
		});

		initData();
		openDiskCache();
		loadByDiskCache();
		attentionListView.setEnabled(false);
		back_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				attentionListView.setSelection(0);
			}
		});
	}

	private void initData() {
		if (!CommonMethod.checkNetwork(getActivity())) {
			Toast.makeText(getActivity(), "请检查您的网络", Toast.LENGTH_SHORT).show();
			loadingDone();
			return;
		}
		mAdapter = null;
		isLoading = false;
		loadPageIndex = 1;
		userInfo = getActivity().getSharedPreferences("user_info", Activity.MODE_PRIVATE);
		bottomNewsList = new ArrayList<HomeHeadLineBean>();
		qzModelList = new ArrayList<GroupSubjectModel>();

	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		isLoading = true;
		apu.refreshRecommendList();
		initData();
		attentionListView.setPullRefreshEnable(true);
		attentionListView.setPullLoadEnable(true);
		attentionListView.stopRefresh();
		getFollowQz();
	}

	@Override
	public void onLoadMore() {
		if (isLoading) {
			return;
		}
		isLoading = true;
		loadPageIndex++;
		getNormalNews();
	}

	@Override
	public void onResume() {
		super.onResume();
		attentionListView.setEnabled(true);
	}

	/**
	 * 创建缓存目录
	 */
	private void openDiskCache() {
		try {
			cacheDir = CommonMethod.getDiskCacheDir(getActivity(), "toutiaojson");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(cacheDir, CommonMethod.getAppVersion(getActivity()), 1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从缓存取数据
	 */
	private void loadByDiskCache() {
		if (cacheDir != null) {
			try {
				DiskLruCache.Snapshot snapShot = mDiskLruCache.get("qzJSONString");
				String uid = userInfo.getString("uid", null);
				if (snapShot != null && !TextUtils.isEmpty(uid)) {
					String qzJsonString = snapShot.getString(0);
					decodeQzJson(qzJsonString);
				}
				snapShot = mDiskLruCache.get("normalJSONString");
				if (snapShot != null) {
					String normalJsonString = snapShot.getString(0);
					decodeNormalNews(new JSONObject(normalJsonString));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				attentionListView.startManualRefresh();
				onRefresh();
			}
		}
	}

	/**
	 * 取用户关注的圈子信息
	 */
	private void getFollowQz() {
		String uid = userInfo.getString("uid", null);
		if (!TextUtils.isEmpty(uid)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("action", "getTopGroupInfo");
			map.put("uid", uid);
			map.put("siteid", "" + MConfig.SITE_ID);
			map.put("page", "1");
			VolleyRequset.doPost(getActivity(), MConfig.mQuanZiApiUrl, "getFollowQz", map, new VolleyListener() {

				@Override
				public void onSuccess(String arg0) {
					decodeQzJson(arg0);
					getNormalNews();
				}

				@Override
				public void onFinish() {
					loadingDone();
				}

				@Override
				public void onFail(VolleyError arg0) {
					getNormalNews();
				}
			});
		} else {
			getNormalNews();
		}
	}

	/**
	 * 解析圈子数据
	 * 
	 * @param json
	 */
	private void decodeQzJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jArray = jsonObject.getJSONArray("topicGroupList");
			if (jArray != null && jArray.length() > 0) {
				mDiskLruCache.remove("qzJSONString");
				DiskLruCache.Editor editor = mDiskLruCache.edit("qzJSONString");
				if (editor != null) {
					editor.set(0, json);
					editor.commit();
				}
				mDiskLruCache.flush();
			} else {
				return;
			}
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObject = jArray.getJSONObject(i);
				GroupSubjectModel subjectModel = RequestResultParser.parseSubjectModel(jObject);
				qzModelList.add(subjectModel);
				// 将数据转移到HomeHeadLineBean中用于显示list
				HomeHeadLineBean qzNews = new HomeHeadLineBean();
				qzNews.setLogo1(subjectModel.publishUserHeadUrl);
				qzNews.setTitle(subjectModel.subjectTitle);
				qzNews.setSummary(subjectModel.publishUserName);
				qzNews.setPublishdate(subjectModel.publishTime);
				qzNews.setType(NewsType.QZ);
				bottomNewsList.add(qzNews);
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取普通新闻
	 */
	private void getNormalNews() {
		new Thread() {
			public void run() {
				JSONObject cmsNews = News.getNormaNewsFromCMS(MConfig.SITE_ID, loadPageIndex, 10, getActivity(), null);
				if (cmsNews != null) {
					handler.obtainMessage(0x100, cmsNews).sendToTarget();
				} else {
					handler.obtainMessage(0x101).sendToTarget();
				}
			};
		}.start();

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loadingDone();
			switch (msg.what) {
			case 0x100:
				decodeNormalNews((JSONObject) msg.obj);
				break;
			case 0x101:
				attentionListView.setPullLoadEnable(false);
				Toast.makeText(getActivity(), "已无更多数据", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	/**
	 * 解析普通新闻
	 * 
	 * @param cmsNews
	 */
	private void decodeNormalNews(JSONObject cmsNews) {
		try {
			JSONArray jArray = cmsNews.getJSONArray("articles");
			if (jArray != null && jArray.length() > 0) {
				// 缓存第一页数据
				if (loadPageIndex == 1) {
					mDiskLruCache.remove("normalJSONString");
					DiskLruCache.Editor editor = mDiskLruCache.edit("normalJSONString");
					if (editor != null) {
						editor.set(0, cmsNews.toString());
						editor.commit();
					}
					mDiskLruCache.flush();
				}
			} else {
				Toast.makeText(getActivity(), "已无更多数据", Toast.LENGTH_SHORT).show();
				attentionListView.setPullLoadEnable(false);
				return;
			}
			for (int i = 0; i < jArray.length(); i++) {
				HomeHeadLineBean normalbean = com.alibaba.fastjson.JSONObject.parseObject(jArray.getString(i),
						HomeHeadLineBean.class);
				bottomNewsList.add(normalbean);
			}
			showList();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载完成
	 */
	private void loadingDone() {
		isLoading = false;
		attentionListView.stopLoadMore();
		attentionListView.stopRefresh();
		attentionListView.setEnabled(true);
		attentionListView.setVisibility(View.VISIBLE);
		attention_loader_mask.setVisibility(View.GONE);
	}

	/**
	 * 显示新闻列表
	 */
	private void showList() {
		if (mAdapter == null) {
			mAdapter = new HomeHeadLinesAdapter(getActivity(), bottomNewsList);
			attentionListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem - attentionListView.getHeaderViewsCount() + 1 >= singleLoadCount) {
			back_top.setVisibility(View.VISIBLE);
		} else {
			back_top.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int headerCount = attentionListView.getHeaderViewsCount();
		if (position >= headerCount) {
			position = position - headerCount;
		}
		if (NewsType.QZ.equals(bottomNewsList.get(position).getType())) {
			GroupSubjectModel subjectModel = qzModelList.get(position);
			Intent intent = new Intent(getActivity(), GroupSubjectActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("title", subjectModel.groupName);
			bundle.putParcelable("mSubjectModel", subjectModel);
			intent.putExtras(bundle);
			getActivity().startActivity(intent);
		} else {
			String info = "{\"id\":\"" + bottomNewsList.get(position).getId() + "\",\"parentid\":\"" + "\"}";
			openDetailActivity(Integer.valueOf(bottomNewsList.get(position).getType()), info);
		}
	}

	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(getActivity(), PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			getActivity().startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(getActivity());
			Intent intent1 = new Intent(getActivity(), VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			getActivity().startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(getActivity(), GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			getActivity().startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(getActivity());
			Intent intent3 = new Intent(getActivity(), VideoNewsDetailActivity.class);
			intent3.putExtra("information", information);
			getActivity().startActivity(intent3);
			break;
		}
	}

	/**
	 * 新闻类型
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class NewsType {
		public static final String QZ = "-1";
		public static final String NORMAL = "1";
		public static final String IMAGE = "2";
		public static final String LIVE = "3";
		public static final String VIDEO_VOD = "5";
		public static final String RADIO_VOD = "6";
	}
}
