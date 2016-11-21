package com.sobey.cloud.webtv.views.group;

import com.dylan.common.animation.AnimationController;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupAdapter;
import com.sobey.cloud.webtv.adapter.GroupPersonalInfoAdapter;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.SearchType;
import com.sobey.cloud.webtv.bean.SearchResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class GroupSearchActivity extends BaseActivity4Group {

	@GinInjectView(id = R.id.group_search_edit)
	EditText mEditText;
	@GinInjectView(id = R.id.group_search_search_tv)
	TextView mSearchTv;
	@GinInjectView(id = R.id.group_search_no_record_tv)
	TextView mNoRecordTv;
	@GinInjectView(id = R.id.group_search_clear_result_tv)
	TextView mClearResultTv;
	@GinInjectView(id = R.id.group_search_listview)
	ListView mListView;
	@GinInjectView(id = R.id.group_search_radio_group)
	RadioGroup mRadioGroup;
	@GinInjectView(id = R.id.group_search_result_layout)
	LinearLayout mResultLayout;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	View mLoadingIconLayout;
	@GinInjectView(id = R.id.group_search_cha_iv)
	ImageView chaIv;

	SearchType searchType = SearchType.searchSubject;
	private GroupAdapter groupAdapter;
	private GroupPersonalInfoAdapter subjectAdapter;

	@Override
	public int getContentView() {
		return R.layout.activity_group_search_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setUpListeners();
	}

	public void setUpListeners() {

		changeViews4SubjectRecords();

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String hint = "";
				switch (checkedId) {
				case R.id.group_search_group_btn:

					searchType = SearchType.searchGroup;
					hint = getResources().getString(R.string.search_hit_for_group);

					break;
				case R.id.group_search_subject_btn:

					searchType = SearchType.searchSubject;
					hint = getResources().getString(R.string.search_hit_for_subject);

					break;

				default:
					break;
				}
				// 如果edittext为空 加载搜索记录
				if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
					mEditText.setHint(hint);
					doDeleteAction();
				} else {
					doSearchAction();
				}
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Object object = parent.getAdapter().getItem(position);
				if (object instanceof GroupModel) {
					GroupModel groupModel = (GroupModel) object;
					jump2GroupDetailActivity(groupModel);
				} else if (object instanceof String) {
					String str = (String) object;
					mEditText.setText(str);
					mEditText.setSelection(str.length());
					BaseUtil.HideKeyboard(mEditText);
					doSearchAction();
				}
			}
		});
		mSearchTv.setOnClickListener(this);
		chaIv.setOnClickListener(this);
		mClearResultTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.group_search_search_tv:

			BaseUtil.HideKeyboard(mEditText);
			doSearchAction();

			break;
		case R.id.group_search_cha_iv:

			doDeleteAction();

			break;
		case R.id.group_search_clear_result_tv:

			doClearAction();

			break;

		default:
			break;
		}
	}

	private void jump2GroupDetailActivity(GroupModel groupModel) {
		Intent intent = new Intent(mContext, GroupDetailActivity.class);
		intent.putExtra("mGroupModel", groupModel);
		startActivity(intent);
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mResultLayout);
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mResultLayout);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	private void doSearchAction() {

		Log.i(TAG, "doSearchAction--->");
		final String keyWord = mEditText.getText().toString().trim();
		if (TextUtils.isEmpty(keyWord)) {
			Toast.makeText(this, "请输入搜索关键词!", Toast.LENGTH_SHORT).show();
			return;
		}

		mOpenLoadingIcon();

		GroupRequestMananger.getInstance().search(searchType, keyWord, 1, this, new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				mCloseLoadingIcon();
				if (null != result && result instanceof SearchResult) {
					mClearResultTv.setVisibility(View.GONE);
					SearchResult searchResult = (SearchResult) result;
					switch (searchType) {
					case searchGroup:
						if (null != searchResult.groupModels && searchResult.groupModels.size() > 0) {
							if (null == groupAdapter) {
								groupAdapter = new GroupAdapter(mContext);
							}
							groupAdapter.setData(searchResult.groupModels);
							mListView.setAdapter(groupAdapter);
							mNoRecordTv.setVisibility(View.GONE);
						} else {
							mResultLayout.setVisibility(View.GONE);
							mNoRecordTv.setText(R.string.no_result);
							mNoRecordTv.setVisibility(View.VISIBLE);
						}
						break;
					case searchSubject:
						if (null != searchResult.subjectModels && searchResult.subjectModels.size() > 0) {
							if (null == subjectAdapter) {
								subjectAdapter = new GroupPersonalInfoAdapter(mContext);
							}
							subjectAdapter.setData(searchResult.subjectModels);
							subjectAdapter.setKeyword(keyWord);
							mListView.setAdapter(subjectAdapter);
							mNoRecordTv.setVisibility(View.GONE);
						} else {
							mResultLayout.setVisibility(View.GONE);
							mNoRecordTv.setText(R.string.no_result);
							mNoRecordTv.setVisibility(View.VISIBLE);
						}
						break;

					default:
						break;
					}
				} else {
					mResultLayout.setVisibility(View.GONE);
					mNoRecordTv.setText(R.string.no_result);
					mNoRecordTv.setVisibility(View.VISIBLE);
				}
			}
		});

		saveSearchRecords(keyWord);

	}

	private void saveSearchRecords(String keyword) {
		switch (searchType) {
		case searchGroup:
			String strRecords = PreferencesUtil.getString(PreferencesUtil.KEY_SEARCH_GROUP_RECORD);
			if (TextUtils.isEmpty(strRecords)) {
				PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_GROUP_RECORD, keyword);
			} else {
				String[] records = strRecords.split("/");
				for (String str : records) {
					// 如果记录中有就不添加了
					if (keyword.equals(str)) {
						return;
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append(strRecords).append("/").append(keyword);
				PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_GROUP_RECORD, sb.toString());
			}
			break;
		case searchSubject:
			strRecords = PreferencesUtil.getString(PreferencesUtil.KEY_SEARCH_SUBJECT_RECORD);
			if (TextUtils.isEmpty(strRecords)) {
				PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_SUBJECT_RECORD, keyword);
			} else {
				String[] records = strRecords.split("/");
				for (String str : records) {
					// 如果记录中有就不添加了
					if (keyword.equals(str)) {
						return;
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append(strRecords).append("/").append(keyword);
				PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_SUBJECT_RECORD, sb.toString());
			}
			break;

		default:
			break;
		}
	}

	private void changeViews4SubjectRecords() {

		String strRecords = PreferencesUtil.getString(PreferencesUtil.KEY_SEARCH_SUBJECT_RECORD);
		if (!TextUtils.isEmpty(strRecords)) {
			String[] records = strRecords.split("/");
			mResultLayout.setVisibility(View.VISIBLE);
			mClearResultTv.setVisibility(View.VISIBLE);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_textview_item,
					R.id.simple_item_text, records);
			mListView.setAdapter(arrayAdapter);
			mNoRecordTv.setVisibility(View.GONE);
		} else {
			mResultLayout.setVisibility(View.GONE);
			mNoRecordTv.setText(R.string.no_record);
			mNoRecordTv.setVisibility(View.VISIBLE);
		}
	}

	private void changeViews4GroupRecords() {

		String strRecords = PreferencesUtil.getString(PreferencesUtil.KEY_SEARCH_GROUP_RECORD);
		if (!TextUtils.isEmpty(strRecords)) {
			String[] records = strRecords.split("/");
			mResultLayout.setVisibility(View.VISIBLE);
			mClearResultTv.setVisibility(View.VISIBLE);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_textview_item,
					R.id.simple_item_text, records);
			mListView.setAdapter(arrayAdapter);
			mNoRecordTv.setVisibility(View.GONE);
		} else {
			mResultLayout.setVisibility(View.GONE);
			mNoRecordTv.setText(R.string.no_record);
			mNoRecordTv.setVisibility(View.VISIBLE);
		}
	}

	private void doDeleteAction() {
		mEditText.setText("");
		switch (searchType) {
		case searchGroup:
			changeViews4GroupRecords();
			break;
		case searchSubject:
			changeViews4SubjectRecords();
			break;

		default:
			break;
		}
	}

	private void doClearAction() {
		switch (searchType) {
		case searchGroup:

			PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_GROUP_RECORD, "");
			mResultLayout.setVisibility(View.GONE);
			mNoRecordTv.setText(R.string.no_record);
			mNoRecordTv.setVisibility(View.VISIBLE);

			break;
		case searchSubject:

			PreferencesUtil.putString(PreferencesUtil.KEY_SEARCH_SUBJECT_RECORD, "");
			mResultLayout.setVisibility(View.GONE);
			mNoRecordTv.setText(R.string.no_record);
			mNoRecordTv.setVisibility(View.VISIBLE);

			break;

		default:
			break;
		}
	}

}
