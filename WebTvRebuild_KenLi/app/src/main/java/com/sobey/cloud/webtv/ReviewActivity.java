package com.sobey.cloud.webtv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.ViewHolderVideoNewsDetailComments;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReviewActivity extends BaseActivity {

	private JSONObject information;
	private JSONArray mCommentsArray = null;
	private ListView mListView;
	private LinearLayout mCommentHeaderLayout;
	private TextView mCommentHeaderText;
	private BaseAdapter mAdapter;
	private String mArticleId;
	private String mCatalogId;
	private String mTitle;
	

	@GinInjectView(id = R.id.mReviewBack)
	ImageButton mReviewBack;
	@GinInjectView(id = R.id.mReviewLeavemessage)
	TextView mReviewLeavemessage;
	@GinInjectView(id = R.id.mReviewTitle)
	TextView mReviewTitle;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_review;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		loadListView();
		loadComment();
	}

	private void loadComment() {
		try {
			information = new JSONObject(getIntent().getStringExtra("information"));
			mArticleId = information.getString("id");
			mCatalogId = information.getString("catalogid");
			mTitle = information.getString("title");
			mReviewTitle.setText(mTitle);
			News.getCommentByArticleId(mArticleId, mCatalogId, this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					JSONArray jsonArray = new JSONArray();
					for (int i = (result.length() - 1); i >= 0; i--) {
						try {
							jsonArray.put(result.get(i));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					mCommentsArray = jsonArray;
					mAdapter.notifyDataSetChanged();
					if (mCommentsArray.length() < 1) {
						setCommentsTitle("暂无评论");
					} else {
						setCommentsTitle("");
					}
				}

				@Override
				public void onNG(String reason) {
					setCommentsTitle("暂时无法获取评论信息");
				}

				@Override
				public void onCancel() {
					setCommentsTitle("暂时无法获取评论信息");
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mReviewLeavemessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences userInfo = ReviewActivity.this.getSharedPreferences("user_info", 0);
				if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
					ReviewActivity.this.startActivity(new Intent(ReviewActivity.this, LoginActivity.class));
					return;
				}
				Intent intent = new Intent(ReviewActivity.this, CommentActivity.class);
				intent.putExtra("information", information.toString());
				ReviewActivity.this.startActivity(intent);
				finishActivity();
			}
		});
		mReviewBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
	}

	private void loadListView() {
		mListView = (ListView) findViewById(R.id.review_list);
		mListView.setHeaderDividersEnabled(false);
		mListView.setBackgroundColor(0xffffffff);
		mCommentHeaderLayout = new LinearLayout(this);
		mCommentHeaderLayout.setGravity(Gravity.CENTER);
		mCommentHeaderText = new TextView(this);
		mCommentHeaderLayout.addView(mCommentHeaderText);
		mListView.addHeaderView(mCommentHeaderLayout);
		setCommentsTitle("正在获取评论信息。。。");

		mAdapter = new BaseAdapter() {
			private LayoutInflater inflater = LayoutInflater.from(ReviewActivity.this);

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderVideoNewsDetailComments viewHolder = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_review, null);
					viewHolder = new ViewHolderVideoNewsDetailComments();
					viewHolder.setUser((TextView) convertView.findViewById(R.id.user));
					viewHolder.setComments((TextView) convertView.findViewById(R.id.comments));
					viewHolder.setDate((TextView) convertView.findViewById(R.id.date));
					viewHolder.setThumbnail((AdvancedImageView) convertView.findViewById(R.id.image));
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView, viewHolder);
				} else {
					loadViewHolder(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				if (mCommentsArray != null) {
					return mCommentsArray.length();
				} else {
					return 0;
				}
			}
		};

		mListView.setAdapter(mAdapter);
	}

	private void loadViewHolder(int position, View convertView, ViewHolderVideoNewsDetailComments viewHolder) {
		viewHolder = (ViewHolderVideoNewsDetailComments) convertView.getTag();
		if (mCommentsArray.length() > position) {
			try {
				int pos = mCommentsArray.length() - position - 1;
				viewHolder.getUser().setText(mCommentsArray.getJSONObject(pos).getString("commentuser"));
//				viewHolder.getUser().setText("匿名用户");
				viewHolder.getDate().setText(DateParse.individualTime(mCommentsArray.getJSONObject(pos).getString("addtime"), null));
				viewHolder.getComments().setText(mCommentsArray.getJSONObject(pos).getString("content"));
				viewHolder.getThumbnail().setNetImage(mCommentsArray.getJSONObject(pos).getString("logo"));
			} catch (JSONException e) {
				viewHolder = null;
			}
		} else {
			viewHolder = null;
		}
	}

	private void setCommentsTitle(String title) {
		mCommentHeaderText.setVisibility(View.GONE);
		if (!title.equalsIgnoreCase("")) {
			mCommentHeaderText.setText(title);
			mCommentHeaderText.setTextSize(18);
			mCommentHeaderText.setTextColor(Color.BLACK);
			mCommentHeaderText.setGravity(Gravity.CENTER);
			mCommentHeaderText.setPadding(10, 10, 0, 10);
			mCommentHeaderText.setVisibility(View.VISIBLE);
		}
	}

}
