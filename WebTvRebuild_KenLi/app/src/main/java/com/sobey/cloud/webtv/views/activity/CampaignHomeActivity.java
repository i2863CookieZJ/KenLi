package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CampaignHomeActivity extends BaseActivity implements IDragListViewListener {
	private CatalogObj mCatalogObj;
	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private boolean isLoading = false;
	private LayoutInflater inflater;
	private String mCatalogId = null;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	
	@Override
	public int getContentView() {
		return R.layout.activity_campaign_home;
	}
	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}
	public void init() {
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
			initSliding(false);
			setTitle(mCatalogObj.name);
			setModuleMenuSelectedItem(mCatalogObj.index);
			mCatalogId = mCatalogObj.id;
			if (TextUtils.isEmpty(mCatalogId)) {
				finishActivity();
			}
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
			finishActivity();
		}

		inflater = LayoutInflater.from(this);
		initContent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@Override
	public void onResume() {
		StatService.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	private void initContent() {
		mOpenLoadingIcon();
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xfff9f9f9);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xfff9f9f9);
		mListView.setBackgroundColor(0xfff9f9f9);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_campaign, null);
					viewHolder = new ViewHolder();
					viewHolder.logo = (AdvancedImageView) convertView.findViewById(R.id.campaign_logo_imageview);
					viewHolder.title = (TextView) convertView.findViewById(R.id.campaign_title_textview);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView, viewHolder);
				} else {
					loadViewHolder(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return 0;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return mArticles.size();
			}
		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					switch (Integer.valueOf(mArticles.get(position - 1).optString("Type"))) {
					case 1:
						Intent intent1 = new Intent(CampaignHomeActivity.this, CampaignShowDetailActivity.class);
						intent1.putExtra("information", mArticles.get(position - 1).toString());
						startActivity(intent1);
						break;
					case 2:
						Intent intent2 = new Intent(CampaignHomeActivity.this, CampaignOffLineDetailActivity.class);
						intent2.putExtra("information", mArticles.get(position - 1).toString());
						startActivity(intent2);
						break;
					}
				} catch (Exception e) {
				}
			}
		});
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);

		mArticles.clear();
		mAdapter.notifyDataSetChanged();
		loadMore();
	}

	private void loadViewHolder(int position, View convertView, ViewHolder viewHolder) {
		viewHolder = (ViewHolder) convertView.getTag();
		try {
			JSONObject obj = mArticles.get(position);
			viewHolder.title.setText(obj.optString("Name"));
			viewHolder.logo.setNetImage(obj.optString("PosterUrl"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMore() {
		isLoading = true;
		News.getActivityList(this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0 && !TextUtils.isEmpty(result.optJSONObject(0).optString("ID"))) {
						mArticles.clear();
						for (int i = 0; i < result.length(); i++) {
							mArticles.add(result.optJSONObject(i));
						}
						mAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
				} finally {
					mListView.stopRefresh();
					mCloseLoadingIcon();
					isLoading = false;
				}
			}

			@Override
			public void onNG(String reason) {
				mListView.stopRefresh();
				mCloseLoadingIcon();
				isLoading = false;
			}

			@Override
			public void onCancel() {
				mListView.stopRefresh();
				mCloseLoadingIcon();
				isLoading = false;
			}
		});
	}

	@Override
	public void onRefresh() {
		if (!isLoading) {
			loadMore();
		}
	}

	@Override
	public void onLoadMore() {
	}

	private class ViewHolder {
		public AdvancedImageView logo;
		public TextView title;
	}
}
