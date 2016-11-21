package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.utils.CommonMethod;
import com.sobey.cloud.webtv.views.activity.CampaignOffLineDetailActivity;
import com.sobey.cloud.webtv.views.activity.CampaignShowDetailActivity;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 活动
 * 
 * @author lgx
 *
 */
public class MyActivitiesFragment extends BaseActivity implements IDragListViewListener {
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
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.myactivity_nocontent)
	private View noContentLayout;

	private int state = 0;
	private String mUserName;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_frame;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("我的活动");
		emptyTv.setText(R.string.has_no_result);
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// View v = inflater.inflate(R.layout.activity_frame,
	// container, false);
	// ViewUtils.inject(this, v);
	//
	// return v;
	// }
	@GinOnClick(id = R.id.user_login)
	public void userLogin(View view) {
		Intent intent = new Intent(MyActivitiesFragment.this, PersonalCenterActivity.class);
		startActivity(intent);
	}

	@GinOnClick(id = R.id.back_rl)
	public void back(View view) {
		finishActivity();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	public void init() {
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(MyActivitiesFragment.this, LoginActivity.class));
			finish();
		}
		mUserName = userInfo.getString("id", "");
		// try {
		// mCatalogObj = MConfig.CatalogList.get(8);
		// mCatalogId = mCatalogObj.id;
		// if (TextUtils.isEmpty(mCatalogId)) {
		//
		// }
		// } catch (Exception e) {
		// if (e != null) {
		// Log.i("dzy", e.toString());
		// }
		// finish();
		// }

		inflater = LayoutInflater.from(MyActivitiesFragment.this);
		initContent();
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	private void initContent() {
		if (state == 0) {
			mOpenLoadingIcon();
			state = 1;
		}
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xfff9f9f9);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xfff9f9f9);
		mListView.setBackgroundColor(0xfff9f9f9);

		mAdapter = new ActivitiesAdpater(mArticles, MyActivitiesFragment.this);
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					switch (Integer.valueOf(mArticles.get(position - 1).optString("Type"))) {
					case 1:
						Intent intent1 = new Intent(MyActivitiesFragment.this, CampaignShowDetailActivity.class);
						intent1.putExtra("information", mArticles.get(position - 1).toString());
						startActivity(intent1);
						break;

					case 2:
						Intent intent2 = new Intent(MyActivitiesFragment.this, CampaignOffLineDetailActivity.class);
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
		loadMore();
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
		if (!CommonMethod.checkNetwork(this)) {
			mCloseLoadingIcon();
			showNoContent(noContentLayout, R.drawable.noconten_myactivity, "您还没有参与活动...");
		}
		isLoading = true;
		News.getActivityList(MyActivitiesFragment.this, mUserName, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0 && !TextUtils.isEmpty(result.optJSONObject(0).optString("ID"))) {
						mArticles.clear();
						for (int i = 0; i < result.length(); i++) {
							mArticles.add(result.optJSONObject(i));
						}
						mAdapter.notifyDataSetChanged();
					} else {
						// emptyLayout.setVisibility(View.VISIBLE);
						showNoContent(noContentLayout, R.drawable.noconten_myactivity, "您还没有参与活动...");
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

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

	}
}
