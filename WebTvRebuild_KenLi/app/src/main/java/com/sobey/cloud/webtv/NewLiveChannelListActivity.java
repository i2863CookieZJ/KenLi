package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.JsonCache;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * 新的电台列表页
 * 
 * @author zouxudong
 *
 */
public class NewLiveChannelListActivity extends BaseActivity implements IDragListViewListener, OnItemClickListener {
	@GinInjectView(id = R.id.titlebartop)
	protected RelativeLayout layout_titlebar;
	@GinInjectView(id = R.id.titlebar_name)
	protected TextView titlebar_name;
	@GinInjectView(id = R.id.back_rl)
	protected RelativeLayout top_back;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	protected RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.channel_list)
	protected DragListView channel_list;
	/** 区分是音频 还是是视频的参数 */
	private String liveMark;
	/** 栏目id */
	private String catalogID;
	private int mPageIndex = 1;
	private int mPageSize = 20;
	private BaseAdapter channleListAdapter;
	private ArrayList<JSONObject> channelListData = new ArrayList<JSONObject>();
	protected boolean isDisposed;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_livechannel_list;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		initView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected void onDestroy() {
		isDisposed = true;
		super.onDestroy();
		channelListData.clear();
		channelListData = null;
		channleListAdapter = null;
	}

	protected void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		String title = null;
		liveMark = getIntent().getStringExtra("liveMark");
		title = getIntent().getStringExtra("title");
		catalogID = getIntent().getStringExtra("state");

		titlebar_name.setText(title);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finishActivity();
			}
		});
		channleListAdapter = new BaseAdapter() {

			@Override
			public View getView(int index, View view, ViewGroup arg2) {
				ViewHolder viewHolder;
				JSONObject itemData = channelListData.get(index);
				if (view != null) {
					viewHolder = (ViewHolder) view.getTag();
				} else {
					view = LayoutInflater.from(NewLiveChannelListActivity.this)
							.inflate(R.layout.listitem_newlivechannel, null);
					viewHolder = new ViewHolder();
					viewHolder.audio_liveing_label = (TextView) view.findViewById(R.id.audio_liveing_label);
					viewHolder.channelName = (TextView) view.findViewById(R.id.channelName);
					viewHolder.channelTypeIcon = (ImageView) view.findViewById(R.id.channelTypeIcon);
					viewHolder.image = (AdvancedImageView) view.findViewById(R.id.image);
					viewHolder.liveProgramName = (TextView) view.findViewById(R.id.liveProgramName);
					viewHolder.soonProgramName = (TextView) view.findViewById(R.id.soonProgramName);
					if ("0".equals(liveMark)) {
						viewHolder.channelTypeIcon.setImageResource(R.drawable.video_channel_icon);
					}
					view.setTag(viewHolder);
				}
				viewHolder.audio_liveing_label.setVisibility(View.GONE);
				// viewHolder.channelName.setVisibility(View.GONE);
				try {
					ImageLoader.getInstance().displayImage(itemData.getString("logo"), viewHolder.image);
					viewHolder.channelName.setText(itemData.getString("title"));
					viewHolder.channelName.setTextColor(getResources().getColor(R.color.black));
					JSONArray actlist = itemData.getJSONArray("actlist");
					String livingProgram = actlist.getJSONObject(0).getString("ctitle");
					String soonProgram = actlist.getJSONObject(0).getString("ntitle");
					String livingDate = DateParse.getDate(0, 0, 0, 0, actlist.getJSONObject(0).getString("ctime"),
							"HH:mm:ss", "HH:mm");
					String soonDate = DateParse.getDate(0, 0, 0, 0, actlist.getJSONObject(0).getString("ntime"),
							"HH:mm:ss", "HH:mm");
					viewHolder.liveProgramName.setTextColor(getResources().getColor(R.color.black));
					viewHolder.liveProgramName.setText(livingDate + " " + livingProgram);
					viewHolder.soonProgramName
							.setTextColor(getResources().getColor(R.color.new_livechannel_list_sooncolor));
					viewHolder.soonProgramName.setText(soonDate + " " + soonProgram);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("zxd", e.getMessage());
				}

				return view;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int index) {
				return channelListData.get(index);
			}

			@Override
			public int getCount() {
				return channelListData.size();
			}
		};
		channel_list.setOnItemClickListener(this);
		channel_list.setListener(this);
		channel_list.setPullLoadEnable(false);
		channel_list.setPullRefreshEnable(false);
		channel_list.setAdapter(channleListAdapter);

		// loadCatalogFromCache();
		loadCatalog();
	}

	// private void loadCatalogFromCache()
	// {
	// JsonCacheObj obj = JsonCache.getInstance().get(catalogID);
	// if(obj!=null)
	// {
	// updateChannleListData((JSONObject)obj.getContent());
	// }
	// else
	// {
	// loadCatalog();
	// }
	// }

	private void loadCatalog() {
		News.getArticleList(0, catalogID, mPageSize, mPageIndex, this, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				JsonCache.getInstance().set(catalogID, "list", result);
				updateChannleListData(result);
				channel_list.setPullRefreshEnable(true);
				channel_list.stopRefresh();
			}

			@Override
			public void onNG(String reason) {
				mLoadingIconLayout.setVisibility(View.GONE);
				channel_list.setPullRefreshEnable(true);
				channel_list.stopRefresh();
			}

			@Override
			public void onCancel() {
				mLoadingIconLayout.setVisibility(View.GONE);
				channel_list.setPullRefreshEnable(true);
				channel_list.stopRefresh();
			}
		});
	}

	protected void updateChannleListData(JSONObject result) {
		if (isDisposed)
			return;
		channelListData.clear();
		mLoadingIconLayout.setVisibility(View.GONE);
		try {
			JSONArray array = result.getJSONArray("articles");
			for (int i = 0; i < array.length(); i++) {
				channelListData.add(array.getJSONObject(i));
			}
			channleListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("zxd", e.getMessage());
		} finally {
			channel_list.setPullRefreshEnable(true);
		}
	}

	private class ViewHolder {
		public AdvancedImageView image;
		public TextView channelName;
		public TextView liveProgramName;
		public TextView soonProgramName;
		public TextView audio_liveing_label;
		public ImageView channelTypeIcon;
	}

	@Override
	public void onRefresh() {
		loadCatalog();
	}

	@Override
	public void onLoadMore() {
		loadCatalog();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		try {
			Intent intent = new Intent();
			if ("0".equals(liveMark)) {
				intent.putExtra("information",
						channelListData.get(index - channel_list.getHeaderViewsCount()).toString());
				intent.putExtra("liveMark", liveMark);
				intent.setClass(this, LiveNewsDetailActivity.class);
				startActivity(intent);
			} else {
				intent.setClass(this, NewRadioLiveChannelListview.class);
				intent.putExtra("information",
						channelListData.get(index - channel_list.getHeaderViewsCount()).toString());
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// outState.putInt("index", getIntent().getIntExtra("index", 0));
	// outState.putString("liveMark",getIntent().getStringExtra("liveMark"));
	// outState.putString("title",getIntent().getStringExtra("title"));
	// outState.putString("state",getIntent().getStringExtra("state"));
	// super.onSaveInstanceState(outState);
	// }
}
