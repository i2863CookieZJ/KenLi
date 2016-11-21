package com.sobey.cloud.webtv.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.adapter.NewAttentionListAdaptor;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.GetGroupListResult;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.RequestResultParser;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.utils.AutoPlayUtil;
import com.sobey.cloud.webtv.utils.BufferUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

/**
 * 首页新关注
 * 
 * @version 2015-05-11 按照最新的瀑布流方式来显示 暂时不管便民这种类型的新闻 按普通新闻来显示
 * @author zouxudong
 * 
 */
public class NewAttentionFragment extends BaseFragment
		implements IDragListViewListener, OnItemClickListener, OnScrollListener {
	@GinInjectView(id = R.id.attentionList)
	private DragListView attentionListView;
	@GinInjectView(id = R.id.attention_loader_mask)
	private RelativeLayout attention_loader_mask;
	@GinInjectView(id = R.id.back_top)
	private View back_top;
	/**
	 * 当前加载的是第几页的
	 */
	private int loadPageIndex = 0;

	/**
	 * 单次加载多少条数据
	 */
	private int singleLoadCount = 10;

	/**
	 * 用来存置顶的
	 */
	private List<View> headerViews = new ArrayList<View>();

	private NewAttentionListAdaptor attentionListAdaptor;

	protected ArrayList<JSONObject> topNewsArray = new ArrayList<JSONObject>();
	/**
	 * 是否正在加载
	 */
	protected boolean isLoading;
	private final String PUBLISH_DATE = "publishdate";

	private static final String ATTENTION_NORMAL_NEWS = "att_normal_news_bf";

	/**
	 * 圈子
	 */
	public static final int GroupType = 0;

	private HashMap<String, GroupModel> attentionGroup = new HashMap<String, GroupModel>();

	/**
	 * 常规新闻的缓存
	 */
	private HashMap<String, ArrayList<JSONObject>> regularNews = new HashMap<String, ArrayList<JSONObject>>();
	/**
	 * 加载新闻数量缓存
	 */
	private HashMap<String, Integer> regularNewsLoadSize = new HashMap<String, Integer>();

	/**
	 * 新闻和圈子加载页数索引
	 */
	private HashMap<String, Integer> regularNewsLoadPageIndex = new HashMap<String, Integer>();
	/**
	 * 用来表示所有的新闻列表项是不是都已经到最大页数了
	 */
	private HashMap<String, Boolean> itemNewsHadLoadMaxPage = new HashMap<String, Boolean>();

	private boolean notMore = false;
	private View recommendView;
	private AutoPlayUtil apu;

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
		if (isUseCache()) {
			return;
		}
		setupActivity();
	}

	private void setupActivity() {
		recommendView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend_news, null);// 顶部轮播
		attentionListView.addHeaderView(recommendView);
		attentionListView.stopLoadMore();
		attentionListView.stopRefresh();
		attentionListView.setPullRefreshEnable(true);
		attentionListView.setPullLoadEnable(true);
		attentionListView.setListener(this);
		attentionListView.setFooterBackgroundColor(0xffffffff);
		attentionListView.setVisibility(View.GONE);
		attentionListView.setOnScrollListener(this);
		attention_loader_mask.setVisibility(View.VISIBLE);
		attentionListAdaptor = new NewAttentionListAdaptor(attentionListView, mInflater, getActivity());
		attentionListView.setAdapter(attentionListAdaptor);
		attentionListView.setOnItemClickListener(this);
		apu = new AutoPlayUtil(recommendView, getActivity(), new AutoPlayUtil.LoadImageListener() {

			@Override
			public void loadComplete() {
				if (recommendView.getTag() != null && "noPage".equals(recommendView.getTag().toString())) {// 没轮播的时候不显示
					attentionListView.removeHeaderView(recommendView);
				}
			}
		});

		loadDataFromBuffer();
		loadCMSNewsItemList();
		attentionListView.setEnabled(false);
		back_top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				attentionListView.setSelection(0);
			}
		});
	}

	/**
	 * 从cms中取新闻列表
	 */
	protected void loadCMSNewsItemList() {
		attentionListView.startManualRefresh();
		onRefresh();
	}

	/**
	 * 取缓存数据
	 */
	protected void loadDataFromBuffer() {
		String normalData = BufferUtil.getTextData(ATTENTION_NORMAL_NEWS);
		if (!TextUtils.isEmpty(normalData)) {
			attentionListView.setVisibility(View.VISIBLE);
			attention_loader_mask.setVisibility(View.GONE);
			decodeNormlNews(normalData);
		}
	}

	protected void loadUCenterAttenionData() {

	}

	protected void saveTopList(String topData) {

	}

	protected void saveReguralNews(String data) {
	}

	@Override
	public void onResume() {
		super.onResume();
		attentionListView.setEnabled(true);
	}

	@Override
	public void onRefresh() {
		if (!isLoading) {
			isLoading = true;
			loadPageIndex = 1;
			// getTopNews();
			// new GetTopicNewsDataTask().execute();
			apu.refreshRecommendList();
			regularNews.clear();
			regularNewsLoadPageIndex.clear();
			itemNewsHadLoadMaxPage.clear();
			regularNewsLoadSize.clear();
			attentionListView.setPullRefreshEnable(true);
			attentionListView.stopRefresh();
			new GetNormaAndAttentionNewsDataTask().executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
		}
	}

	@Override
	public void onLoadMore() {
		if (isLoading)
			return;
		isLoading = true;
		loadPageIndex++;
		// getNormalNewsList();
		new GetNormaAndAttentionNewsDataTask().execute();
	}

	/**
	 * 解析一般新闻
	 * 
	 * @param data
	 */
	protected void decodeNormlNews(String data) {
		// int totalCount=0;
		// int currentPageSize=0;
		try {
			JSONObject jsonObject = new JSONObject(data);
			// totalCount=jsonObject.getInt("total");
			JSONArray news = jsonObject.getJSONArray("articles");
			ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			// currentPageSize=news.length();
			outloop: for (int i = 0; i < news.length(); i++) {
				JSONObject item = news.getJSONObject(i);
				String normalNewsType = item.optString("type");
				if (NewsType.LIVE.equals(normalNewsType)) // 过滤直播新闻
				{
					continue outloop;
				}
				innerloop: for (JSONObject topObject : topNewsArray) {
					String topItemType = item.optString("type");
					if (GroupType == Integer.valueOf(topItemType)) // 过滤置顶圈子
					{
						continue innerloop;
					}
					String topItemID = topObject.optString("id");
					if (topItemID.equals(item.optString("id"))) // 过滤重复的置顶新闻
					{
						continue outloop;
					}
				}
				// String type = item.optString("type");
				// String date = item.optString("publishdate");
				// if ("0".equals(type)) {
				// if (date.contains("2015-08-31")) {
				// jsonObjects.add(item);
				// }
				// } else {
				jsonObjects.add(item);
				// }
			}
			if (loadPageIndex == 1) {
				attentionListAdaptor.getNewsListData().clear();
			}
			// Collections.sort(jsonObjects, new CompareNewsDate(PUBLISH_DATE));
			attentionListAdaptor.addNewsList(jsonObjects);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			attentionListView.stopLoadMore();
			attentionListView.stopRefresh();

			Iterator<String> iterator = itemNewsHadLoadMaxPage.keySet().iterator();
			// 还有没加载完的项
			boolean allLoadComplete = false;

			while (iterator.hasNext()) {
				String key = iterator.next();
				allLoadComplete = itemNewsHadLoadMaxPage.get(key);
				if (!allLoadComplete) {
					break;
				}
			}

			// 如果已经达到了总数量 或是当前数量已经取来小于单页的数量 就不能加载更多了
			// if(attentionListAdaptor.getNewsListData().size()==totalCount||currentPageSize<singleLoadCount)
			if (notMore) {
				attentionListView.setPullLoadEnable(false);
			} else if (allLoadComplete) {
				attentionListView.setPullLoadEnable(false);
			} else {
				attentionListView.setPullLoadEnable(true);
			}
			isLoading = false;
			attentionListView.setVisibility(View.VISIBLE);
			attention_loader_mask.setVisibility(View.GONE);
		}
	}

	class CompareNewsDate implements Comparator<JSONObject> {
		private String key;

		public CompareNewsDate(String datekey) {
			this.key = datekey;
		}

		@Override
		public int compare(JSONObject item1, JSONObject item2) {
			Date item1Date = DateParse.parseDate(item1.optString(key), null);
			Date item2Date = DateParse.parseDate(item2.optString(key), null);
			if (item1Date.after(item2Date))
				return -1;
			return 1;
		}

	}

	/**
	 * 清除置顶新闻
	 */
	private void clearTopNews() {
		for (View view : headerViews) {
			attentionListView.removeHeaderView(view);
		}
	}

	// TODO
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		JSONObject jsonObject = null;
		int headerCount = attentionListView.getHeaderViewsCount();
		if (position >= headerCount) {
			position = position - headerCount;
			jsonObject = attentionListAdaptor.getNewsListData().get(position);
		}
		// else {
		// position -= 1;
		// jsonObject = topNewsArray.get(position);
		// }
		try {
			if (GroupType == jsonObject.optInt("type")) {
				GroupSubjectModel subjectModel = RequestResultParser.parseSubjectModel(jsonObject);
				Intent intent = new Intent(getActivity(), GroupSubjectActivity.class);

				String groupId = jsonObject.optString("groupId");
				String groupInfo = jsonObject.optString("groupInfo");
				String headUrl = jsonObject.optString("headUrl");
				subjectModel.groupId = groupId;
				subjectModel.groupInfo = groupInfo;
				subjectModel.groupHeadUrl = headUrl;
				subjectModel.publishUserHeadUrl = jsonObject.optString("publishUserHeadUrl");
				Bundle bundle = new Bundle();
				bundle.putString("title", jsonObject.optString("groupName"));
				bundle.putParcelable("mSubjectModel", subjectModel);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
				return;
			}
			String info = "{\"id\":\"" + jsonObject.getString("id") + "\",\"parentid\":\"" + "\"}";
			openDetailActivity(jsonObject.getInt("type"), info);
		} catch (JSONException e) {
			e.printStackTrace();
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
	 * 取置顶新闻数据
	 * 
	 * @author zouxudong
	 * 
	 */
	class GetTopicNewsDataTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			regularNews.clear();
			regularNewsLoadPageIndex.clear();
			itemNewsHadLoadMaxPage.clear();
			regularNewsLoadSize.clear();
			SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", Activity.MODE_PRIVATE);
			String uid = userInfo.getString("uid", null);
			JSONObject groupList = null;
			List<JSONObject> topicGroupData = new ArrayList<JSONObject>();
			JSONObject maxtTopicCount = News.getAttentionTopNewsMaxCount();
			final int MAX_TOPNEWS_SIZE;
			if (maxtTopicCount != null) {
				String msg = maxtTopicCount.optString("Msg");
				if (msg != null && !msg.equals("0")) {
					Pattern pattern = Pattern.compile("[0-9]*");
					Matcher isNum = pattern.matcher(msg);
					if (!isNum.matches()) {
						MAX_TOPNEWS_SIZE = 5;
					} else {
						MAX_TOPNEWS_SIZE = Integer.valueOf(msg);
					}
				} else
					MAX_TOPNEWS_SIZE = 5;
			} else
				MAX_TOPNEWS_SIZE = 5;
			// String msg=maxtTopicCount.optString("Msg");
			// final int MAX_TOPNEWS_SIZE;
			// if(msg!=null&&!msg.equals("0"))
			// {
			// MAX_TOPNEWS_SIZE=Integer.valueOf(msg);
			// }
			// else
			// MAX_TOPNEWS_SIZE=5;
			JSONObject cmsTopicNews = News.getTopNewsFromCMS(MConfig.SITE_ID, 1, MAX_TOPNEWS_SIZE, getActivity(), null);
			try {
				if (!TextUtils.isEmpty(uid)) {
					groupList = News.getGroupList(uid, getActivity(), null);
					attentionGroup.clear();
					GetGroupListResult resultGroupList = RequestResultParser.parseGroupsModel(groupList);
					if (groupList != null) // 把用户关注过的圈子取出来
					{
						Iterator<GroupModel> iterator = resultGroupList.followedGroupList.iterator();
						int i = 0;
						while (iterator.hasNext()) {
							GroupModel groupModel = iterator.next();
							attentionGroup.put(groupModel.groupId, groupModel);
							if (++i < MAX_TOPNEWS_SIZE) {
								JSONObject jsonObject = News.getGroupInfo(uid, groupModel.groupId, 1, "publish",
										"heats", getActivity(), null);
								JSONArray topicSubjectListArray = jsonObject.optJSONArray("topicSubjectList");
								if (topicSubjectListArray != null) {
									for (int j = 0; j < topicSubjectListArray.length(); j++) {
										JSONObject item = topicSubjectListArray.optJSONObject(j);
										item.put(PUBLISH_DATE, item.optString("publishTime"));
										item.put("title", item.optString("subjectTitle"));
										item.put("groupName", attentionGroup.get(groupModel.groupId).groupName);
										item.put("type", GroupType);
										topicGroupData.add(item);
									}
								}
							}
						}
					}
				}

				JSONArray topsNews = cmsTopicNews.optJSONArray("articles");
				for (int i = 0; i < topsNews.length(); i++) {
					JSONObject item = topsNews.optJSONObject(i);
					topicGroupData.add(i, item);
				}
				Collections.sort(topicGroupData, new CompareNewsDate(PUBLISH_DATE));

				Iterator<JSONObject> iterator = topicGroupData.iterator();
				JSONArray orderArray = new JSONArray();
				while (iterator.hasNext()) {
					orderArray.put(iterator.next());
					if (orderArray.length() >= MAX_TOPNEWS_SIZE)
						break;
				}
				cmsTopicNews.put("articles", orderArray);
			} catch (Exception e) {
			}
			return cmsTopicNews;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null) {
				new GetNormaAndAttentionNewsDataTask()
						.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
			} else {
				attentionListView.setPullRefreshEnable(true);
				attentionListView.stopRefresh();
				new GetNormaAndAttentionNewsDataTask()
						.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());
			}
		}

	}

	/**
	 * 取普通新闻数据
	 * 
	 * @author zouxudong
	 * 
	 */
	class GetNormaAndAttentionNewsDataTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			attentionListView.setPullRefreshEnable(true);
			attentionListView.stopRefresh();
			notMore = false;
			Iterator<String> iterator = regularNewsLoadPageIndex.keySet().iterator();
			regularNews.clear();
			while (iterator.hasNext()) {
				String key = iterator.next();
				regularNewsLoadPageIndex.put(key, regularNewsLoadPageIndex.get(key) + 1);// 每次加载更多的时候默认让他的页数加1
			}
			// 加缓存cms新闻
			if (regularNews.containsKey(ATTENTION_NORMAL_NEWS) == false) {
				regularNews.put(ATTENTION_NORMAL_NEWS, new ArrayList<JSONObject>());
			}
			// 加cms新闻的页数索引
			if (regularNewsLoadPageIndex.containsKey(ATTENTION_NORMAL_NEWS) == false) {
				regularNewsLoadPageIndex.put(ATTENTION_NORMAL_NEWS, 1);
			}
			if (regularNewsLoadSize.containsKey(ATTENTION_NORMAL_NEWS) == false) {
				regularNewsLoadSize.put(ATTENTION_NORMAL_NEWS, 0);
			}
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			// 这注释 了代表是第几页就是加载 10的页数倍数条 暂时用10条 用倍数条有点多
			// int
			// cmsLoadCount=singleLoadCount*regularNewsLoadPageIndex.get(ATTENTION_NORMAL_NEWS);
			int cmsLoadCount = singleLoadCount;

			int pageIndex = -1;
			// 先看把cms里面的新闻 取完没有 没加载完才去取
			boolean loadCMSComplete = !itemNewsHadLoadMaxPage.containsKey(ATTENTION_NORMAL_NEWS) ? false
					: itemNewsHadLoadMaxPage.get(ATTENTION_NORMAL_NEWS);
			if (!loadCMSComplete) {
				pageIndex = regularNewsLoadPageIndex.get(ATTENTION_NORMAL_NEWS);
				Log.d("zxd", "加载cms新闻页索引:" + pageIndex);
				// TODO 取cms新闻
				JSONObject cmsNews = News.getNormaNewsFromCMS(MConfig.SITE_ID, pageIndex, cmsLoadCount, getActivity(),
						null);
				// Log.d("zxd",
				// "加载cms新闻数据:"+cmsNews!=null?""+cmsNews.optJSONArray("articles").length():"");
				if (cmsNews != null) {
					int cmsTotal = cmsNews.optInt("total");
					Log.d("zxd", "加载cms新闻页总条数:" + cmsTotal);
					JSONArray news = cmsNews.optJSONArray("articles");
					regularNewsLoadSize.put(ATTENTION_NORMAL_NEWS,
							regularNewsLoadSize.get(ATTENTION_NORMAL_NEWS) + news.length());
					Log.d("zxd", "CMS新闻已经加载了" + regularNewsLoadSize.get(ATTENTION_NORMAL_NEWS) + "条数据");
					// ArrayList<JSONObject> sortList=new
					// ArrayList<JSONObject>();
					// 加到新闻缓存里面
					for (int index = 0; index < news.length(); index++) {
						// sortList.add(news.optJSONObject(index));
						regularNews.get(ATTENTION_NORMAL_NEWS).add(news.optJSONObject(index));
					}
					// 判断是不是已经把所有新闻都加载完了
					if (regularNewsLoadSize.get(ATTENTION_NORMAL_NEWS) >= cmsTotal) {
						itemNewsHadLoadMaxPage.put(ATTENTION_NORMAL_NEWS, true);
						Log.d("zxd", "CMS新闻已经全部加载完");
					} else {
						itemNewsHadLoadMaxPage.put(ATTENTION_NORMAL_NEWS, false);
					}
				} else {
					Log.d("zxd", "取cms新闻返回为空");
					regularNewsLoadPageIndex.put(ATTENTION_NORMAL_NEWS,
							regularNewsLoadPageIndex.get(ATTENTION_NORMAL_NEWS) - 1);
				}
				Log.d("zxd", "加载cms新闻缓存条数:" + regularNews.get(ATTENTION_NORMAL_NEWS).size());
			} else {
				itemNewsHadLoadMaxPage.put(ATTENTION_NORMAL_NEWS, true);
				Log.d("zxd", "CMS新闻已经全部加载完  不再重取");
			}

			// 再根据用户登录没有 再来判断是否去取圈子的数据
			JSONObject groupList = null;
			SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", Activity.MODE_PRIVATE);
			String uid = userInfo.getString("uid", null);
			attentionGroup.clear();
			if (!TextUtils.isEmpty(uid)) {
				groupList = News.getGroupList(uid, getActivity(), null);
				GetGroupListResult resultGroupList = RequestResultParser.parseGroupsModel(groupList);
				if (groupList != null && resultGroupList.followedGroupList.size() > 0) // 把用户关注过的圈子取出来
				{

					Iterator<GroupModel> iterator = resultGroupList.followedGroupList.iterator();
					while (iterator.hasNext()) {
						GroupModel groupModel = iterator.next();
						attentionGroup.put(groupModel.groupId, groupModel);
						// 加圈子的页数索引
						if (regularNewsLoadPageIndex.containsKey(groupModel.groupId) == false) {
							regularNewsLoadPageIndex.put(groupModel.groupId, 1);
						}
						if (regularNewsLoadSize.containsKey(groupModel.groupId) == false) {
							regularNewsLoadSize.put(groupModel.groupId, 0);
						}
						// 先看把cms里面的新闻 取完没有 没加载完才去取
						boolean loadItemGroupComplete = !itemNewsHadLoadMaxPage.containsKey(groupModel.groupId) ? false
								: itemNewsHadLoadMaxPage.get(groupModel.groupId);
						// 如果没加载完圈子里面的帖子
						if (!loadItemGroupComplete) {
							pageIndex = regularNewsLoadPageIndex.get(groupModel.groupId);
							// 取精华帖和热点帖 不进行过滤
							JSONObject jsonObject = News.getGroupInfo(uid, groupModel.groupId,
									regularNewsLoadPageIndex.get(groupModel.groupId), "publish", null, getActivity(),
									null);
							if (jsonObject == null || jsonObject.has("subjectCount") == false) {
								regularNewsLoadPageIndex.put(groupModel.groupId,
										regularNewsLoadPageIndex.get(groupModel.groupId) - 1);
								continue;
							}
							// 这个圈子下的帖子总数
							int gropItemTotalSize = Integer.valueOf(jsonObject.optString("subjectCount"));
							JSONArray subjectList = jsonObject.optJSONArray("subjectList");
							if (subjectList == null) {
							}
							// 把对应id圈子的数据存进来
							if (regularNews.containsKey(groupModel.groupId) == false) {
								regularNews.put(groupModel.groupId, new ArrayList<JSONObject>());
							}

							if (subjectList != null) // 把圈子加到相应id的缓存下
							{
								for (int index = 0; index < subjectList.length(); index++) {
									JSONObject item = subjectList.optJSONObject(index);
									try {

										item.put(PUBLISH_DATE, item.optString("publishTime"));
										item.put("title", item.optString("subjectTitle"));
										item.put("groupId", groupModel.groupId);
										item.put("groupInfo", groupModel.groupInfo);
										item.put("headUrl", groupModel.headUrl);
										item.put("groupName", attentionGroup.get(groupModel.groupId).groupName);
										item.put("type", GroupType);
										item.put("catalogimage", groupModel.headUrl);
										item.put("logo", groupModel.headUrl);
										item.put("commcount", item.optString("subjectReplyCount"));
										item.put("catalogname", item.optString("publishUserName"));
										item.put("groupName", groupModel.groupName);
										item.put("publishUserHeadUrl", item.optString("publishUserHeadUrl"));
										// 只添加今天的圈子内容
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
										String date = df.format(new Date());// 获取今天日期
										if (item.optString("publishdate").contains(date)) {
											regularNews.get(groupModel.groupId).add(item);
										}
									} catch (Exception e) {
									}
								}
								regularNewsLoadSize.put(groupModel.groupId,
										regularNewsLoadSize.get(groupModel.groupId) + subjectList.length());
							}
							// 判断是不是已经把所有新闻都加载完了
							if (regularNewsLoadSize.get(groupModel.groupId) >= gropItemTotalSize) {
								itemNewsHadLoadMaxPage.put(groupModel.groupId, true);
							} else if (subjectList == null) {
								itemNewsHadLoadMaxPage.put(groupModel.groupId, true);
							} else {
								itemNewsHadLoadMaxPage.put(groupModel.groupId, false);
							}
						} else {
							itemNewsHadLoadMaxPage.put(groupModel.groupId, true);
						}
					}
				} else {
					clearGroupBufferKey();
				}
			} else// 如果没有登录的话 就直接把一些缓存数据给清理了
			{
				clearGroupBufferKey();
			}

			List<JSONObject> allData = new ArrayList<JSONObject>();
			Iterator<String> iterator = regularNews.keySet().iterator();
			while (iterator.hasNext()) {
				ArrayList<JSONObject> item = regularNews.get(iterator.next());
				// TODO guolu
				if (item != null)
					allData.addAll(item);
			}
			Collections.sort(allData, new CompareNewsDate(PUBLISH_DATE));
			Log.d("zxd", "所有缓存新闻 圈子数据总条数:" + allData.size());
			Iterator<JSONObject> allDataIterator = allData.iterator();
			HashMap<String, String> tempBufferKey = new HashMap<String, String>();

			while (allDataIterator.hasNext()) {
				JSONObject item = allDataIterator.next();
				if (GroupType != item.optInt("type")) {
					String id = item.optString("id");
					if (tempBufferKey.containsKey(id)) {
						Log.d("zxd", "allDataIterator 去掉ID为:" + id + "的新闻,索引位置" + allData.indexOf(item));
						allDataIterator.remove();
						continue;
					}
					tempBufferKey.put(id, id);
				} else {
					String id = item.optString("subjectId");
					if (tempBufferKey.containsKey(id)) {
						Log.d("zxd", "allDataIterator 去掉ID为:" + id + "的帖子,索引位置" + allData.indexOf(item));
						allDataIterator.remove();
						continue;
					}
					tempBufferKey.put(id, id);
				}
			}
			tempBufferKey.clear();
			// int start = (loadPageIndex - 1) * singleLoadCount;
			int start = 0;
			if (start > allData.size()) {
				// notMore=true;
				return null;
			}
			int end = loadPageIndex * singleLoadCount;
			end = singleLoadCount;
			// if (end > allData.size()) {
			end = allData.size();
			// notMore = true;
			// }
			Log.d("zxd", "当前加载页数:" + loadPageIndex + "  准备从缓存中取第:" + start + " 条开始到 第:" + end + " 条结束数据");
			List<JSONObject> sliceData = allData.subList(start, end);
			Log.d("zxd", "截取后的条数:" + sliceData.size());
			JSONObject list = new JSONObject();
			try {
				list.put("total", sliceData.size());
				list.put("articles", new JSONArray(sliceData.toString()));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return list;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			int a = loadPageIndex;
			super.onPostExecute(result);
			JSONObject normalNews = result;
			isLoading = false;
			if (normalNews != null) {
				if (loadPageIndex == 1) {
					BufferUtil.saveTextData(ATTENTION_NORMAL_NEWS, normalNews.toString());
				}
				decodeNormlNews(normalNews.toString());
			} else {
				if (loadPageIndex > 1) {
					loadPageIndex--;
				}
				attentionListView.stopLoadMore();
				attentionListView.stopRefresh();
				attentionListView.setVisibility(View.VISIBLE);
				attention_loader_mask.setVisibility(View.GONE);
				Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();

				Iterator<String> iterator = itemNewsHadLoadMaxPage.keySet().iterator();
				// 还有没加载完的项
				boolean allLoadComplete = false;

				while (iterator.hasNext()) {
					String key = iterator.next();
					allLoadComplete = itemNewsHadLoadMaxPage.get(key);
					if (!allLoadComplete) {
						break;
					}
				}

				// 如果已经达到了总数量 或是当前数量已经取来小于单页的数量 就不能加载更多了
				// if(attentionListAdaptor.getNewsListData().size()==totalCount||currentPageSize<singleLoadCount)
				if (notMore) {
					attentionListView.setPullLoadEnable(false);
				} else if (allLoadComplete) {
					attentionListView.setPullLoadEnable(false);
				} else {
					attentionListView.setPullLoadEnable(true);
				}
			}
		}

		/**
		 * 清圈子相关的key
		 */
		protected void clearGroupBufferKey() {
			Set<String> loadMaxPageKeySet = itemNewsHadLoadMaxPage.keySet();
			Iterator<String> iterator = loadMaxPageKeySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (ATTENTION_NORMAL_NEWS.equals(key) == false) // 没有登录的情况
																// 下清所有与非cms新闻相关的新闻
																// 加载索引
				{
					itemNewsHadLoadMaxPage.remove(key);
					if (regularNews.containsKey(key) && regularNews.get(key) != null) {
						regularNews.get(key).clear();
						regularNews.remove(key);
					}
					if (regularNewsLoadPageIndex.containsKey(key))
						regularNewsLoadPageIndex.remove(key);
				}
			}
		}

	}

	/**
	 * 新闻类型
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class NewsType {
		public static final String NORMAL = "1";
		public static final String IMAGE = "2";
		public static final String LIVE = "3";
		public static final String VIDEO_VOD = "5";
		public static final String RADIO_VOD = "6";
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		Log.d("zxd", "firstVisibleItem" + firstVisibleItem);
		Log.d("zxd", "visibleItemCount" + visibleItemCount);
		Log.d("zxd", "totalItemCount" + totalItemCount);
		if (firstVisibleItem - attentionListView.getHeaderViewsCount() + 1 >= singleLoadCount) {
			back_top.setVisibility(View.VISIBLE);
		} else {
			back_top.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}
}
