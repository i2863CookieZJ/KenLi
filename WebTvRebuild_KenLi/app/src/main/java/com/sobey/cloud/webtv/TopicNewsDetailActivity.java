package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.ViewHolderTopicNewsDetail;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopicNewsDetailActivity extends BaseActivity {

	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private LayoutInflater inflater;
	private JSONObject mInformation;
	private JSONObject information;
	private String mVoteId;
	private String mVoteTitle;
	private String mVoters;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.mTopicdetailBack)
	ImageButton mTopicdetailBack;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_topicnews_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);

		inflater = LayoutInflater.from(this);

		mTopicdetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

		String str = getIntent().getStringExtra("information");
		try {
			mInformation = new JSONObject(str);
			mOpenLoadingIcon();
			News.getSpecialArticleDetail(String.valueOf(mInformation.optString("id")), this,
					new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								information = result.optJSONObject(0);
								mArticles.clear();
								JSONObject bannerObj = new JSONObject();
								bannerObj.put("style", 1);
								bannerObj.put("logo", information.optString("logo"));
								mArticles.add(bannerObj);
								JSONObject summaryObj = new JSONObject();
								summaryObj.put("style", 2);
								summaryObj.put("summary", information.optString("prop1"));
								mArticles.add(summaryObj);
								for (int i = 0; i < information.optJSONArray("catalogs").length(); i++) {
									JSONObject catalogObj = information.optJSONArray("catalogs").optJSONObject(i);
									JSONObject sectionObj = new JSONObject();
									sectionObj.put("style", 3);
									sectionObj.put("name", catalogObj.optString("name"));
									mArticles.add(sectionObj);
									for (int j = 0; j < catalogObj.optJSONArray("articles").length(); j++) {
										JSONObject articleObj = catalogObj.optJSONArray("articles").optJSONObject(j);
										switch (Integer.valueOf(articleObj.getString("type"))) {
										case MConfig.TypeVideo:
											articleObj.put("style", 5);
											break;
										case MConfig.TypePicture:
											articleObj.put("style", 6);
											break;
										default:
											articleObj.put("style", 4);
											break;
										}
										mArticles.add(articleObj);
									}
								}
								loadContent();
								getVote(information.optString("id"));
							} catch (Exception e) {
							}
							try {
								News.increaseHitCount("Catalog", information.getString("id"),
										information.getString("id"));
							} catch (Exception e) {
							}
						}

						@Override
						public void onNG(String reason) {
							loadContent();
						}

						@Override
						public void onCancel() {
							loadContent();
						}
					});
		} catch (Exception e) {
			loadContent();
		}
	}

	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(TopicNewsDetailActivity.this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			TopicNewsDetailActivity.this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(TopicNewsDetailActivity.this);
			Intent intent1 = new Intent(TopicNewsDetailActivity.this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			TopicNewsDetailActivity.this.startActivity(intent1);
			break;
		// Vote
		case 0:
			Intent intent2 = new Intent(TopicNewsDetailActivity.this, VoteDetailActivity.class);
			intent2.putExtra("vote_id", mVoteId);
			intent2.putExtra("vote_title", mVoteTitle);
			TopicNewsDetailActivity.this.startActivity(intent2);
			break;
		case MConfig.TypeNews:
			Intent intent3 = new Intent(TopicNewsDetailActivity.this, GeneralNewsDetailActivity.class);
			intent3.putExtra("information", information);
			TopicNewsDetailActivity.this.startActivity(intent3);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(TopicNewsDetailActivity.this);
			Intent intent4 = new Intent(TopicNewsDetailActivity.this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			TopicNewsDetailActivity.this.startActivity(intent4);
			break;
		}
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

	private void loadContent() {
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderTopicNewsDetail viewHolderTopicNewsDetail = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_topicnews_detail, null);
					viewHolderTopicNewsDetail = new ViewHolderTopicNewsDetail();
					viewHolderTopicNewsDetail.setBanner((AdvancedImageView) convertView.findViewById(R.id.banner));
					viewHolderTopicNewsDetail
							.setSummaryLayout((RelativeLayout) convertView.findViewById(R.id.summary_layout));
					viewHolderTopicNewsDetail.setSummary((TextView) convertView.findViewById(R.id.summary));
					viewHolderTopicNewsDetail.setSectionTitle((TextView) convertView.findViewById(R.id.section_title));
					viewHolderTopicNewsDetail
							.setNormalLayout((LinearLayout) convertView.findViewById(R.id.normal_layout));
					viewHolderTopicNewsDetail.setNormalTitle((TextView) convertView.findViewById(R.id.normal_title));
					viewHolderTopicNewsDetail
							.setNormalSummary((TextView) convertView.findViewById(R.id.normal_summary));
					viewHolderTopicNewsDetail
							.setNormalComment((TextView) convertView.findViewById(R.id.normal_comment));
					viewHolderTopicNewsDetail
							.setNormalLogo((AdvancedImageView) convertView.findViewById(R.id.normal_logo));
					viewHolderTopicNewsDetail.setNormalType((ImageView) convertView.findViewById(R.id.normal_type));
					viewHolderTopicNewsDetail
							.setVideoLayout((LinearLayout) convertView.findViewById(R.id.video_layout));
					viewHolderTopicNewsDetail
							.setVideoDuration((TextView) convertView.findViewById(R.id.video_duration));
					viewHolderTopicNewsDetail.setVideoTitle((TextView) convertView.findViewById(R.id.video_title));
					viewHolderTopicNewsDetail.setVideoComment((TextView) convertView.findViewById(R.id.video_comment));
					viewHolderTopicNewsDetail
							.setVideoPlaycount((TextView) convertView.findViewById(R.id.video_playcount));
					viewHolderTopicNewsDetail
							.setVideoLogo((AdvancedImageView) convertView.findViewById(R.id.video_logo));
					viewHolderTopicNewsDetail
							.setPictureLayout((LinearLayout) convertView.findViewById(R.id.picture_layout));
					viewHolderTopicNewsDetail.setPictureTitle((TextView) convertView.findViewById(R.id.picture_title));
					viewHolderTopicNewsDetail
							.setPictureComment((TextView) convertView.findViewById(R.id.picture_comment));
					viewHolderTopicNewsDetail
							.setPictureLogo1((AdvancedImageView) convertView.findViewById(R.id.picture_logo1));
					viewHolderTopicNewsDetail
							.setPictureLogo2((AdvancedImageView) convertView.findViewById(R.id.picture_logo2));
					viewHolderTopicNewsDetail
							.setPictureLogo3((AdvancedImageView) convertView.findViewById(R.id.picture_logo3));
					viewHolderTopicNewsDetail.setVoteLayout((LinearLayout) convertView.findViewById(R.id.vote_layout));
					viewHolderTopicNewsDetail.setVoteTitle((TextView) convertView.findViewById(R.id.vote_title));
					viewHolderTopicNewsDetail.setVoters((TextView) convertView.findViewById(R.id.voters));
					convertView.setTag(viewHolderTopicNewsDetail);
					loadViewHolder(position, convertView, viewHolderTopicNewsDetail);
				} else {
					loadViewHolder(position, convertView, viewHolderTopicNewsDetail);
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
				return mArticles.size();
			}
		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position - 1;
				JSONObject obj = mArticles.get(pos);
				try {
					openDetailActivity(Integer.valueOf(obj.optString("type")), obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mAdapter.notifyDataSetChanged();
		mCloseLoadingIcon();
	}

	private void loadViewHolder(int position, View convertView, ViewHolderTopicNewsDetail viewHolderTopicNewsDetail) {
		viewHolderTopicNewsDetail = (ViewHolderTopicNewsDetail) convertView.getTag();
		try {
			int pos = position;
			JSONObject obj = mArticles.get(pos);
			switch (obj.optInt("style")) {
			case 1:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getBanner().setNetImage(obj.optString("logo"));
				break;
			case 2:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummary().setText(obj.optString("summary").trim());
				break;
			case 3:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setText(obj.optString("name"));
				break;
			case 4:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getNormalType().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalTitle().setText(obj.optString("title"));
				viewHolderTopicNewsDetail.getNormalSummary().setText(obj.optString("summary"));
				viewHolderTopicNewsDetail.getNormalComment().setText(obj.optString("commcount"));
				viewHolderTopicNewsDetail.getNormalLogo().setNetImage(obj.optString("logo"));
				break;
			case 5:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getNormalType().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalTitle().setText(obj.optString("title"));
				viewHolderTopicNewsDetail.getNormalSummary().setText(obj.optString("summary"));
				viewHolderTopicNewsDetail.getNormalComment().setText(obj.optString("commcount"));
				viewHolderTopicNewsDetail.getNormalLogo().setNetImage(obj.optString("logo"));
				break;
			case 6:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureTitle().setText(obj.getString("title"));
				viewHolderTopicNewsDetail.getPictureComment().setText(obj.getString("commcount"));
				JSONArray pictureArray = obj.getJSONArray("content");
				int pictureSum = pictureArray.length();
				viewHolderTopicNewsDetail.getPictureLogo1().setNetImage(obj.getString("logo"));
				if (pictureSum > 1) {
					viewHolderTopicNewsDetail.getPictureLogo2()
							.setNetImage(((JSONObject) pictureArray.get(0)).getString("filepath"));
					viewHolderTopicNewsDetail.getPictureLogo3()
							.setNetImage(((JSONObject) pictureArray.get(1)).getString("filepath"));
				}
				DisplayMetrics metrics = TopicNewsDetailActivity.this.getResources().getDisplayMetrics();
				int width = (int) ((metrics.widthPixels - 50.0) / 3.0);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 3.0 / 4.0));
				params.setMargins(3, 3, 3, 3);
				viewHolderTopicNewsDetail.getPictureLogo1().setLayoutParams(params);
				viewHolderTopicNewsDetail.getPictureLogo2().setLayoutParams(params);
				viewHolderTopicNewsDetail.getPictureLogo3().setLayoutParams(params);
				break;
			case 7:
				viewHolderTopicNewsDetail.getBanner().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSummaryLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getSectionTitle().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getNormalLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVideoLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getPictureLayout().setVisibility(View.GONE);
				viewHolderTopicNewsDetail.getVoteLayout().setVisibility(View.VISIBLE);
				viewHolderTopicNewsDetail.getVoteTitle().setText(mVoteTitle);
				viewHolderTopicNewsDetail.getVoters().setText(mVoters + " 人参加");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getVote(String catalogId) {
		if (TextUtils.isEmpty(catalogId)) {
			return;
		}
		News.getCatalogRelaVote(catalogId, this, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					showVote(result.optString("ID"), result.optString("Total"), result.optString("Title"));
				} catch (Exception e) {
					useCatalogVote();
				}
			}

			@Override
			public void onNG(String reason) {
				useCatalogVote();
			}

			@Override
			public void onCancel() {
				useCatalogVote();
			}
		});
	}

	private void showVote(String id, String voters, String title) {
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(title)) {
			return;
		}
		if (TextUtils.isEmpty(voters)) {
			voters = "0";
		}
		mVoteId = id;
		mVoteTitle = title;
		mVoters = voters;
		try {
			JSONObject voteObj = new JSONObject();
			voteObj.put("style", 7);
			voteObj.put("type", "0");
			mArticles.add(2, voteObj);
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
		}
	}

	private void useCatalogVote() {
		try {
			String str = getIntent().getStringExtra("vote");
			JSONObject result = new JSONObject(str);
			showVote(result.optString("ID"), result.optString("Total"), result.optString("Title"));
		} catch (Exception e) {
		}
	}
}
