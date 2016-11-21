package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.androidadvancedui.AdvancedListView;
import com.appsdk.androidadvancedui.listener.AdvancedListViewFooter;
import com.appsdk.androidadvancedui.listener.AdvancedListViewHeader;
import com.appsdk.androidadvancedui.listener.AdvancedListViewListener;
import com.baidu.mobstat.StatService;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.ui.ListViewFooter;
import com.sobey.cloud.webtv.ui.ListViewHeader;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CampaignArticleListActivity extends BaseActivity {
	private static final int mPageSize = 10;
	@SuppressWarnings("unused")
	private static final int mTextNewsType = 8;
	private static final int mPicNewsType = 3;
	private static final int mVideoNewsType = 2;
	private static final int mLiveNewsType = 6;

	private LayoutInflater mInflater;
	private JSONObject mInformation;
	private int mPageNum = 1;
	private int mNewsType = 1;
	private boolean mLoading = false;
	private boolean mRefreshFlag = true;
	private AdvancedListViewHeader mHeader;
	private AdvancedListViewFooter mFooter;
	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mArticleList = new ArrayList<JSONObject>();

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mCampaignArticleListBack;

	@GinInjectView(id = R.id.mCampaignArticleListTitle)
	TextView mCampaignArticleListTitle;

	@GinInjectView(id = R.id.mCampaignArticleListView)
	AdvancedListView mCampaignArticleListView;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_article_list;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		try {
			String str = getIntent().getStringExtra("information");
			if (!TextUtils.isEmpty(str)) {
				mInformation = new JSONObject(str);
				mNewsType = Integer.valueOf(mInformation.optString("appstyle"));
			} else {
				finishActivity();
			}
		} catch (Exception e) {
			finishActivity();
		}

		mInflater = LayoutInflater.from(this);

		mCampaignArticleListBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.listitem_campaign_article_list, null);
					viewHolder = new ViewHolder();
					viewHolder.textNewsLayout = (RelativeLayout) convertView.findViewById(R.id.text_news_layout);
					viewHolder.textNewsTextView = (TextView) convertView.findViewById(R.id.text_news_textview);
					viewHolder.picNewsLayout = (LinearLayout) convertView.findViewById(R.id.pic_news_layout);
					viewHolder.picNewsTitleTextView = (TextView) convertView.findViewById(R.id.pic_news_title);
					viewHolder.picNewsImageView1 = (AdvancedImageView) convertView.findViewById(R.id.pic_news_image1);
					viewHolder.picNewsImageView2 = (AdvancedImageView) convertView.findViewById(R.id.pic_news_image2);
					viewHolder.picNewsImageView3 = (AdvancedImageView) convertView.findViewById(R.id.pic_news_image3);
					viewHolder.videoNewsLayout = (LinearLayout) convertView.findViewById(R.id.video_news_layout);
					viewHolder.videoNewsTitleTextView = (TextView) convertView.findViewById(R.id.video_news_title);
					viewHolder.videoNewsImageView = (AdvancedImageView) convertView
							.findViewById(R.id.video_news_imageview);
					viewHolder.videoNewsDurationTextView = (TextView) convertView
							.findViewById(R.id.video_news_duration);
					viewHolder.videoNewsCommentTextView = (TextView) convertView.findViewById(R.id.video_news_comments);
					viewHolder.videoNewsPlayCountTextView = (TextView) convertView
							.findViewById(R.id.video_news_playcount);
					viewHolder.liveNewsLayout = (RelativeLayout) convertView.findViewById(R.id.live_news_layout);
					viewHolder.liveNewsImageView = (AdvancedImageView) convertView
							.findViewById(R.id.live_news_imageview);
					viewHolder.liveNewsTitleTextView = (TextView) convertView.findViewById(R.id.live_news_title);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView);
				} else {
					loadViewHolder(position, convertView);
				}
				return convertView;
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
				return mArticleList.size();
			}
		};

		mCampaignArticleListTitle.setText(mInformation.optString("catalogname"));
		mHeader = new ListViewHeader(this);
		mFooter = new ListViewFooter(this);
		mCampaignArticleListView.initAdvancedListView(this, mHeader, mFooter);
		mCampaignArticleListView.setAdapter(mAdapter);
		mCampaignArticleListView.setListViewListener(new AdvancedListViewListener() {
			@Override
			public void onStopHeaderPullDown() {
			}

			@Override
			public void onStopFooterPullUp() {
			}

			@Override
			public void onStartHeaderPullDown() {
				refreshContent();
			}

			@Override
			public void onStartFooterPullUp() {
				loadMore();
			}
		});
		mCampaignArticleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				try {
					if (arg2 > 0 && arg2 <= mArticleList.size()) {
						JSONObject obj = mArticleList.get(arg2 - 1);
						switch (Integer.valueOf(obj.optString("type"))) {
						case mPicNewsType:
							Intent intent0 = new Intent(CampaignArticleListActivity.this,
									PhotoNewsDetailActivity.class);
							intent0.putExtra("information", obj.toString());
							startActivity(intent0);
							break;
						case mVideoNewsType:
							HuiZhouSarft.disposeVideoComponent(CampaignArticleListActivity.this);
							Intent intent1 = new Intent(CampaignArticleListActivity.this,
									VideoNewsDetailActivity.class);
							intent1.putExtra("information", obj.toString());
							startActivity(intent1);
							break;
						case mLiveNewsType:
							Intent intent2 = new Intent(CampaignArticleListActivity.this,
									CampaignArticleLiveActivity.class);
							obj.put("activity_id", mInformation.optString("activity_id"));
							intent2.putExtra("information", obj.toString());
							startActivity(intent2);
							break;
						default:
							Intent intent3 = new Intent(CampaignArticleListActivity.this,
									GeneralNewsDetailActivity.class);
							intent3.putExtra("information", obj.toString());
							startActivity(intent3);
							break;
						}
					}
				} catch (Exception e) {
				}
			}
		});
		mCampaignArticleListView.startHeaderPullDown();
	}

	private void loadViewHolder(int position, View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		try {
			JSONObject obj = mArticleList.get(position);
			viewHolder.picNewsImageView1.setVisibility(View.INVISIBLE);
			viewHolder.picNewsImageView2.setVisibility(View.INVISIBLE);
			viewHolder.picNewsImageView3.setVisibility(View.INVISIBLE);
			switch (mNewsType) {
			case mPicNewsType:
				viewHolder.textNewsLayout.setVisibility(View.GONE);
				viewHolder.picNewsLayout.setVisibility(View.VISIBLE);
				viewHolder.videoNewsLayout.setVisibility(View.GONE);
				viewHolder.liveNewsLayout.setVisibility(View.GONE);
				viewHolder.picNewsTitleTextView.setText(obj.optString("title"));
				viewHolder.picNewsImageView1.setNetImage(obj.optString("logo"));
				viewHolder.picNewsImageView1.setVisibility(View.VISIBLE);
				JSONArray array = obj.optJSONArray("content");
				String url1, url2, url3;
				if (array != null && array.length() > 0) {
					url1 = array.getJSONObject(0).getString("filepath");
					viewHolder.picNewsImageView1.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(url1, viewHolder.picNewsImageView1);
					if (array.length() >= 3) {
						url3 = array.getJSONObject(2).getString("filepath");
						viewHolder.picNewsImageView2.setVisibility(View.VISIBLE);
						viewHolder.picNewsImageView3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(url3, viewHolder.picNewsImageView3);
						url2 = array.getJSONObject(1).getString("filepath");
						ImageLoader.getInstance().displayImage(url2, viewHolder.picNewsImageView2);
					} else if (array.length() == 2) {
						url2 = array.getJSONObject(1).getString("filepath");
						viewHolder.picNewsImageView2.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(url2, viewHolder.picNewsImageView2);
					}
				}
				// if (array.length() > 0 &&
				// !TextUtils.isEmpty(array.optJSONObject(0).optString("filepath")))
				// {
				// viewHolder.picNewsImageView2.setNetImage(array.optJSONObject(0).optString("filepath"));
				// if (array.length() > 1 &&
				// !TextUtils.isEmpty(array.optJSONObject(1).optString("filepath")))
				// {
				// viewHolder.picNewsImageView3.setNetImage(array.optJSONObject(1).optString("filepath"));
				// }
				// }
				break;
			case mVideoNewsType:
				viewHolder.textNewsLayout.setVisibility(View.GONE);
				viewHolder.picNewsLayout.setVisibility(View.GONE);
				viewHolder.videoNewsLayout.setVisibility(View.VISIBLE);
				viewHolder.liveNewsLayout.setVisibility(View.GONE);
				viewHolder.videoNewsTitleTextView.setText(obj.optString("title"));
				viewHolder.videoNewsImageView.setNetImage(obj.optString("logo"));
				viewHolder.videoNewsDurationTextView.setText(obj.optString("duration"));
				viewHolder.videoNewsCommentTextView.setText(obj.optString("commcount"));
				viewHolder.videoNewsPlayCountTextView.setText(obj.optString("hitcount"));
				break;
			case mLiveNewsType:
				viewHolder.textNewsLayout.setVisibility(View.GONE);
				viewHolder.picNewsLayout.setVisibility(View.GONE);
				viewHolder.videoNewsLayout.setVisibility(View.GONE);
				viewHolder.liveNewsLayout.setVisibility(View.VISIBLE);
				viewHolder.liveNewsImageView.setNetImage(obj.optString("logo"));
				viewHolder.liveNewsTitleTextView.setText(obj.optString("title"));
				break;
			default:
				viewHolder.textNewsLayout.setVisibility(View.VISIBLE);
				viewHolder.picNewsLayout.setVisibility(View.GONE);
				viewHolder.videoNewsLayout.setVisibility(View.GONE);
				viewHolder.liveNewsLayout.setVisibility(View.GONE);
				viewHolder.textNewsTextView.setText(obj.optString("title"));
				break;
			}
		} catch (Exception e) {
		}
	}

	private void refreshContent() {
		mRefreshFlag = true;
		mPageNum = 1;
		loadContent();
	}

	private void loadMore() {
		mRefreshFlag = false;
		mPageNum++;
		loadContent();
	}

	private void loadContent() {
		if (!mLoading) {
			mLoading = true;
			News.getArticleList(mInformation.optString("id"), mPageNum, mPageSize, this,
					new OnJsonObjectResultListener() {
						@Override
						public void onOK(JSONObject result) {
							try {
								if (mPageNum < 2) {
									mArticleList.clear();
								}
								int total = Integer.valueOf(result.optString("total"));
								JSONArray array = result.optJSONArray("articles");
								for (int i = 0; i < array.length(); i++) {
									mArticleList.add(array.optJSONObject(i));
								}
								if (total <= mArticleList.size()) {
									mCampaignArticleListView.setFooterPullDownEnable(false);
									mFooter.setVisibility(View.GONE);
								} else {
									mCampaignArticleListView.setFooterPullDownEnable(true);
									mFooter.setVisibility(View.VISIBLE);
								}
								mAdapter.notifyDataSetChanged();
							} catch (Exception e) {
							} finally {
								mLoading = false;
								if (mRefreshFlag) {
									mCampaignArticleListView.stopHeaderPullDown();
								} else {
									mCampaignArticleListView.stopFooterPullUp();
								}
							}
						}

						@Override
						public void onNG(String reason) {
							mLoading = false;
							if (mRefreshFlag) {
								mCampaignArticleListView.stopHeaderPullDown();
							} else {
								mCampaignArticleListView.stopFooterPullUp();
							}
						}

						@Override
						public void onCancel() {
							mLoading = false;
							if (mRefreshFlag) {
								mCampaignArticleListView.stopHeaderPullDown();
							} else {
								mCampaignArticleListView.stopFooterPullUp();
							}
						}
					});
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

	private class ViewHolder {
		RelativeLayout textNewsLayout;
		TextView textNewsTextView;
		LinearLayout picNewsLayout;
		TextView picNewsTitleTextView;
		AdvancedImageView picNewsImageView1;
		AdvancedImageView picNewsImageView2;
		AdvancedImageView picNewsImageView3;
		LinearLayout videoNewsLayout;
		AdvancedImageView videoNewsImageView;
		TextView videoNewsTitleTextView;
		TextView videoNewsDurationTextView;
		TextView videoNewsCommentTextView;
		TextView videoNewsPlayCountTextView;
		RelativeLayout liveNewsLayout;
		AdvancedImageView liveNewsImageView;
		TextView liveNewsTitleTextView;
	}
}
