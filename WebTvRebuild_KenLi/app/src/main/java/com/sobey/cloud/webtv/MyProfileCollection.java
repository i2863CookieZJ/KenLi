package com.sobey.cloud.webtv;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyProfileCollection extends CollectionFragment {

	protected void loadCollection() {
		SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(getActivity(), LoginActivity.class));
			// getActivity().finish();
		}
		mUserName = userInfo.getString("id", "");

		mCollectionHeaderTitle.setText(mSelectList[0]);
		mCollectionHeaderBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		mCollectionHeaderSelectLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mInitSelectFlag) {
					LayoutInflater inflater = LayoutInflater.from(getActivity());
					for (int i = 0; i < mSelectList.length; i++) {
						TextView view = (TextView) inflater.inflate(R.layout.listitem_broke_capture_dropbox, null);
						view.setText(mSelectList[i]);
						mSelectListLayout.addView(view);
						SelectItemClickListener listener = new SelectItemClickListener(i);
						view.setOnClickListener(listener);
					}
					mInitSelectFlag = true;
				}
				if (mSelectLayout.getVisibility() == View.VISIBLE) {
					mSelectLayout.setVisibility(View.GONE);
				} else {
					mSelectLayout.setVisibility(View.VISIBLE);
				}
			}
		});

		loadMore();
	}
}
