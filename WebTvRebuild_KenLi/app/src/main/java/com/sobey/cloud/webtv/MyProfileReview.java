package com.sobey.cloud.webtv;

import org.json.JSONArray;
import org.json.JSONException;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MyProfileReview extends MyReviewFragment {
	public MyProfileReview() {
	}

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.activity_myreview);
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
		loadListView();
		loadComment();
	}

	@Override
	protected void loadComment() {
		SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(getActivity(), LoginActivity.class));
			// getActivity().finish();
		}
		mUserName = userInfo.getString("id", "");
		News.getCommentByMemberName(mUserName, getActivity(), new OnJsonArrayResultListener() {
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

		mMyReviewBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}
}
