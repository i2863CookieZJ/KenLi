package com.sobey.cloud.webtv.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.sobey.cloud.webtv.LiveNewsListActivity;
import com.sobey.cloud.webtv.adapter.LiveNewsAdapter;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.BufferUtil;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 直播
 * 
 * @author zouxudong
 * 
 */
public class LiveFragment extends BaseFragment implements IDragListViewListener {

	@GinInjectView(id = R.id.newsList)
	private DragListView newsMessageList;
	@GinInjectView(id = R.id.news_loader_mask)
	private RelativeLayout news_loader_mask;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	/***
	 * 后台返回 来的栏目id 这个是根据cms后台配置里面的
	 */
	protected String[] catalogIDs;

	protected HashMap<String, String> cataLogNameMap = new HashMap<String, String>();
	/**
	 * 具体某个栏目取最大的新闻条数
	 */
	protected HashMap<String, Integer> cataLogMaxNewsCount = new HashMap<String, Integer>();

	/**
	 * 栏目id缓存
	 */
	protected final String catalogIDBufferFile = "catalogid_buffer_data";
	/**
	 * 首页资讯缓存
	 */
	protected final String newsBufferFile = "home_news_buffer_data";
	protected int needInvokeTime = 0;
	protected HashMap<String, JSONObject> catatlogDetailData = new HashMap<String, JSONObject>();
	/**
	 * 装缓存的数据
	 */
	protected List<View> dynamicViews = new ArrayList<View>();
	private List<NewsItem> newsItems = new ArrayList<LiveFragment.NewsItem>();
	private NewsListAdaptor adaptor = new NewsListAdaptor();
	/**
	 * 一个栏目最大取多少条新闻数据
	 */
	protected int maxItemNewCount = 2;
	protected boolean addHeader = false;

	public LiveFragment() {
	}

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.fragment_newmessage_live);
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
		LayoutParams params = newsMessageList.getLayoutParams();
		// recommendView.setLayoutParams(params);
		newsMessageList.setAdapter(adaptor);
		addHeader = true;
		newsMessageList.startManualRefresh();
		news_loader_mask.setVisibility(View.GONE);
		newsMessageList.setVisibility(View.VISIBLE);
		newsMessageList.stopLoadMore();
		newsMessageList.stopRefresh();
		newsMessageList.setPullRefreshEnable(true);
		newsMessageList.setPullLoadEnable(false);
		newsMessageList.setListener(this);
		newsMessageList.setFooterBackgroundColor(0xffffffff);

		emptyLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				newsMessageList.startManualRefresh();
				emptyLayout.setVisibility(View.GONE);
				new GetDataTask().execute();
			}
		});
		
		// readBufferData();
		new GetDataTask().execute();
	}

	protected void readBufferData() {
		String catalogBuffer = BufferUtil.getTextData(catalogIDBufferFile);// 分类
		String newsBuffer = BufferUtil.getTextData(newsBufferFile);
		if (TextUtils.isEmpty(catalogBuffer) || TextUtils.isEmpty(newsBuffer)) {
			news_loader_mask.setVisibility(View.VISIBLE);
		} else {
			try {
				String ids = catalogBuffer;
				String[] pairsValue = ids.split(",");
				catalogIDs = new String[pairsValue.length];
				needInvokeTime = catalogIDs.length;
				for (int i = 0; i < pairsValue.length; i++) {
					String[] item = pairsValue[i].split(":");
					if (item.length > 1) {
						cataLogNameMap.put(item[0], item[1]);
						catalogIDs[i] = item[0];
					} else {
						cataLogNameMap.clear();
						catalogIDs = new String[0];
						break;
					}
				}
				JSONObject buffer = new JSONObject(newsBuffer);
				Iterator<String> iterator = buffer.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					catatlogDetailData.put(key, buffer.getJSONObject(key));
				}
				pushCatalogoData();

			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				news_loader_mask.setVisibility(View.GONE);
			}
		}
	}

	protected class GetDataTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... args) {
			catatlogDetailData.clear();
			// JSONObject jsonObject = News.getNewsCatalogIDs();
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject("{\"Status\":\"ok\",\"Msg\":\"" + MConfig.LIVE_360 + ","
						+ MConfig.LIVE_GUANGDIAN + "," + MConfig.LIVE_ACTIVITY + "\"}");
			} catch (JSONException e1) {
			}

			String ids = jsonObject.optString("Msg");
			String[] pairsValue = ids.split(",");
			catalogIDs = new String[pairsValue.length];
			needInvokeTime = catalogIDs.length;
			if (pairsValue.length > 0) {
				cataLogMaxNewsCount.clear();
				cataLogNameMap.clear();
			}
			for (int i = 0; i < pairsValue.length; i++) {
				String[] item = pairsValue[i].split(":");
				if (item.length == 1) {
					catalogIDs = new String[0];
					cataLogNameMap.clear();
					cataLogMaxNewsCount.clear();
					// Toast.makeText(getActivity(),
					// "暂无最新数据!",Toast.LENGTH_SHORT).show();
					return 0;
				}
				cataLogNameMap.put(item[0], item[1]);
				if (item.length >= 3)
					cataLogMaxNewsCount.put(item[0], Integer.valueOf(item[2]));
				else
					cataLogMaxNewsCount.put(item[0], maxItemNewCount);
				catalogIDs[i] = item[0];
			}
			// BufferUtil.saveTextData(catalogIDBufferFile, ids);
			JSONObject buffer = new JSONObject();
			for (int i = 0; i < catalogIDs.length; i++) {
				final String catalogId = catalogIDs[i];
				int maxCount = maxItemNewCount;
				if (cataLogMaxNewsCount.containsKey(catalogId)) {
					maxCount = cataLogMaxNewsCount.get(catalogId);
				}
				jsonObject = News.getArticleList(0, catalogId, maxCount, 1, getActivity(), null);
				if (jsonObject != null) {
					catatlogDetailData.put(catalogId, jsonObject);
					try {
						buffer.put(catalogId, jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			// BufferUtil.saveTextData(newsBufferFile, buffer.toString());
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			news_loader_mask.setVisibility(View.GONE);
			newsMessageList.stopMannualRefresh();
			newsMessageList.stopRefresh();
			newsMessageList.stopLoadMore();
			newsMessageList.setPullRefreshEnable(true);
			if (catatlogDetailData.size() > 0) {
				pushCatalogoData();
			} else {
				// Toast.makeText(getActivity(), "暂无最新数据!",
				// Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void pushCatalogoData() {
		Log.d("zxd", "begin pushCatalogoData");
		newsItems.clear();
		for (int index = 0; index < catalogIDs.length; index++) {
			if (catatlogDetailData.containsKey(catalogIDs[index])) {
				NewsItem item = new NewsItem(catatlogDetailData.get(catalogIDs[index]), catalogIDs[index],
						cataLogNameMap.get(catalogIDs[index]));
				JSONArray jsonArray = item.itemNewsDetail.optJSONArray("articles");
				if (jsonArray == null || jsonArray.length() == 0)
					continue;
				newsItems.add(item);
				// View view=getView(item, null, null);
				// // dynamicViews.add(view);
				// // newsMessageList.addView(view);
				// Message msg=new Message();
				// msg.obj=view;
				// handler.sendMessage(msg);
			}
		}
		adaptor.notifyDataSetChanged();
		if (newsItems.size() > 0) {
			emptyLayout.setVisibility(View.GONE);
		} else {
			emptyLayout.setVisibility(View.VISIBLE);
			emptyTv.setText("暂无直播，点击刷新");
		}
		Log.d("zxd", "after pushCatalogoData");
	}

	/**
	 * 新闻列表项
	 * 
	 * @author zouxudong
	 * 
	 */
	class NewsItem {
		public final String catalogName;
		public final String catalogID;
		public final JSONObject itemNewsDetail;

		public NewsItem(JSONObject itemNewsDetail, String catalogID, String catalogName) {
			this.catalogName = catalogName;
			this.catalogID = catalogID;
			this.itemNewsDetail = itemNewsDetail;
		}
	}

	class NewsListAdaptor extends BaseAdapter {

		@Override
		public int getCount() {
			return newsItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			return newsItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int width = displayMetrics.widthPixels;
			ItemNewsHolder holder;
			// LiveNewsAdaptor.Holder
			// itemNewsOneHolder,itemNewsTwoHolder;
			LiveNewsAdapter.Holder itemNewsHolder;
			NewsItem item = newsItems.get(index);
			JSONArray jsonArray = item.itemNewsDetail.optJSONArray("articles");
			List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObjects.add(jsonArray.optJSONObject(i));
			}
			ArrayList<View> viewList;
			LiveNewsAdapter itemNewsAdaptor = new LiveNewsAdapter(jsonObjects, getActivity(), item.catalogID,
					item.catalogName, width);
			if (view == null) {
				holder = new ItemNewsHolder();
				view = LayoutInflater.from(getActivity()).inflate(R.layout.item_home_live, null);
				GinInjector.manualInjectView(holder, view);
				view.setTag(holder);
				viewList = new ArrayList<View>();
				for (int itemIndex = 0; itemIndex < jsonObjects.size(); itemIndex++) {
					itemNewsHolder = new LiveNewsAdapter.Holder();
					View itemNewsView = LayoutInflater.from(getActivity()).inflate(R.layout.item_news_live, null);
					holder.newsItemContainer.addView(itemNewsView);
					viewList.add(itemNewsView);
					GinInjector.manualInjectView(itemNewsHolder, itemNewsView);
					itemNewsView.setTag(itemNewsHolder);
					holder.newsItemContainer.setTag(viewList);
					itemNewsHolder.itemBottomLine.setVisibility(View.VISIBLE);
				}
			} else {
				holder = (ItemNewsHolder) view.getTag();
				viewList = (ArrayList<View>) holder.newsItemContainer.getTag();
				if (viewList.size() < jsonObjects.size()) {
					for (int itemIndex = viewList.size(); itemIndex < jsonObjects.size(); itemIndex++) {
						itemNewsHolder = new LiveNewsAdapter.Holder();
						View itemNewsView = LayoutInflater.from(getActivity()).inflate(R.layout.item_news_live, null);
						holder.newsItemContainer.addView(itemNewsView);
						viewList.add(itemNewsView);
						GinInjector.manualInjectView(itemNewsHolder, itemNewsView);
						itemNewsView.setTag(itemNewsHolder);
						holder.newsItemContainer.setTag(viewList);
						itemNewsHolder.itemBottomLine.setVisibility(View.VISIBLE);
					}
				}
			}

			NewsItemHeaderClickListener listener = (NewsItemHeaderClickListener) holder.newsItemTitleRl.getTag();
			if (listener == null) {
				listener = new NewsItemHeaderClickListener();
				holder.newsItemTitleRl.setOnClickListener(listener);
				holder.newsItemTitleRl.setTag(listener);
			}
			listener.parentId = item.catalogID;
			listener.catalogName = item.catalogName;
			holder.newsItemTitle.setText(item.catalogName);
			for (View itemView : viewList) {
				itemView.setVisibility(View.VISIBLE);
			}
			for (int dataIndex = 0; dataIndex < viewList.size(); dataIndex++) {
				View itemView = viewList.get(dataIndex);
				if (dataIndex < itemNewsAdaptor.getCount()) {
					itemNewsHolder = (LiveNewsAdapter.Holder) itemView.getTag();
					itemNewsAdaptor.setViewData(itemNewsHolder, dataIndex, itemView);
				} else {
					itemView.setVisibility(View.GONE);
				}
			}
			return view;
		}

	}

	class ItemNewsHolder {
		@GinInjectView(id = R.id.newsItemTitle)
		public TextView newsItemTitle;

		@GinInjectView(id = R.id.newsItemTitleRl)
		private RelativeLayout newsItemTitleRl;

		// @ViewInject(R.id.newsItemContent)
		// public MyListView newsItemContent;
		// @ViewInject(R.id.itemNewsOne)
		// public View itemNewsOne;
		// @ViewInject(R.id.itemNewsTwo)
		// public View itemNewsTwo;
		@GinInjectView(id = R.id.newsItemContainer)
		public LinearLayout newsItemContainer;
	}

	class NewsItemHeaderClickListener implements OnClickListener {
		public String parentId;
		public String catalogName;

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getActivity(), LiveNewsListActivity.class);
			intent.putExtra("type", MConfig.TypeNews);
			intent.putExtra("ids", parentId);
			intent.putExtra("title", catalogName);
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute();
	}

	@Override
	public void onLoadMore() {

	}

}
