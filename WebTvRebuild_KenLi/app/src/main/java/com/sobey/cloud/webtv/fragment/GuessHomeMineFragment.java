package com.sobey.cloud.webtv.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.androidadvancedui.AdvancedListView;
import com.appsdk.androidadvancedui.listener.AdvancedListViewListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.utils.WeakHandler;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GuessHomeMineFragment extends Fragment {

	private static final int INIT_LISTVIEW = 1;
	private static final int REFRESH_CONTENT = 2;
	private static final int LOAD_MORE_CONTENT = 3;

	private View mFragmentView;
	private TextView mTimesTextView;
	private TextView mRightTextView;
	private AdvancedListView mAdvancedListView;

	private FragmentHandler mHandler;
	private AdvancedListViewListener mListener;
	private LayoutInflater mInflater;
	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mContentList = new ArrayList<JSONObject>();
	private boolean mIsLoading = false;
	private int mPageSize = 10;
	private int mPageIndex = 1;

	public static GuessHomeMineFragment newInstance() {
		GuessHomeMineFragment fragment = new GuessHomeMineFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mFragmentView = mInflater.inflate(R.layout.fragment_guess_home_mine, null);
		initFragment();
		mHandler = new FragmentHandler(this);
		mHandler.sendEmptyMessage(INIT_LISTVIEW);
		mHandler.sendEmptyMessage(REFRESH_CONTENT);
		return mFragmentView;
	}

	private void initFragment() {
		mTimesTextView = (TextView) mFragmentView.findViewById(R.id.guess_home_mine_times);
		mRightTextView = (TextView) mFragmentView.findViewById(R.id.guess_home_mine_right);
		mAdvancedListView = (AdvancedListView) mFragmentView.findViewById(R.id.advancedlistview);
		mAdvancedListView.initAdvancedListView(getActivity(), null, null);
	}

	private static class FragmentHandler extends WeakHandler<GuessHomeMineFragment> {
		public FragmentHandler(GuessHomeMineFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			GuessHomeMineFragment fragment = getOwner();

			switch (msg.what) {
			case INIT_LISTVIEW:
				fragment.initList();
				break;
			case REFRESH_CONTENT:
				fragment.refreshContent();
				break;
			case LOAD_MORE_CONTENT:
				fragment.loadMoreContent();
				break;
			}
			super.handleMessage(msg);
		}
	}

	private void initList() {
		// Set Adapter
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.listitem_guess_home, null);
					ViewHolder viewHolder = new ViewHolder();
					viewHolder.logoImageView = (AdvancedImageView) convertView.findViewById(R.id.logo_imageview);
					viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_textview);
					viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.summary_textview);
					viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time_text);
					viewHolder.participateTextView = (TextView) convertView.findViewById(R.id.participate_textview);
					viewHolder.participateStateImageView = (ImageView) convertView.findViewById(R.id.participate_state_imageview);
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView);
				} else {
					loadViewHolder(position, convertView);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mContentList.get(position);
			}

			@Override
			public int getCount() {
				return mContentList.size();
			}
		};
		mAdvancedListView.setAdapter(mAdapter);

		// Init listener
		mListener = new AdvancedListViewListener() {
			@Override
			public void onStopHeaderPullDown() {
				Toast.makeText(getActivity(), "Refresh complete", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStopFooterPullUp() {
				Toast.makeText(getActivity(), "Load complete", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartHeaderPullDown() {
				Toast.makeText(getActivity(), "Start Refresh ...", Toast.LENGTH_SHORT).show();
				mHandler.sendEmptyMessage(REFRESH_CONTENT);
			}

			@Override
			public void onStartFooterPullUp() {
				Toast.makeText(getActivity(), "Start Loading ...", Toast.LENGTH_SHORT).show();
				mHandler.sendEmptyMessage(LOAD_MORE_CONTENT);
			}
		};
		mAdvancedListView.setListViewListener(mListener);

		mAdvancedListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO
			}
		});
	}

	private void loadViewHolder(int position, View convertView) {
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
	}

	private void refreshContent() {
		mPageIndex = 1;
		loadContent();
	}

	private void loadMoreContent() {
		mPageIndex++;
		loadContent();
	}

	private void loadContent() {
		if (!mIsLoading) {
			mIsLoading = true;
			mTimesTextView.setText("18");
			mRightTextView.setText("9");
			News.getArticleList(0, "122", mPageSize, (mPageIndex < 1 ? 1 : mPageIndex), getActivity(), new OnJsonObjectResultListener() {
				@Override
				public void onOK(JSONObject result) {
					try {
						int total = result.getInt("total");
						JSONArray array = result.getJSONArray("articles");
						if (mPageIndex <= 1) {
							mContentList.clear();
						}
						for (int i = 0; i < array.length(); i++) {
							mContentList.add(array.getJSONObject(i));
						}
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						mAdvancedListView.stopFooterPullUp();
						mAdvancedListView.stopHeaderPullDown();
						if (mContentList.size() >= total) {
							mAdvancedListView.setFooterPullDownEnable(false);
						} else {
							mAdvancedListView.setFooterPullDownEnable(true);
						}
					} catch (Exception e) {
						mAdvancedListView.stopFooterPullUp();
						mAdvancedListView.stopHeaderPullDown();
					} finally {
						mIsLoading = false;
					}
				}

				@Override
				public void onNG(String reason) {
					mAdvancedListView.stopFooterPullUp();
					mAdvancedListView.stopHeaderPullDown();
					mIsLoading = false;
				}

				@Override
				public void onCancel() {
					mAdvancedListView.stopFooterPullUp();
					mAdvancedListView.stopHeaderPullDown();
					mIsLoading = false;
				}
			});
		}
	}

	private class ViewHolder {
		AdvancedImageView logoImageView;
		TextView titleTextView;
		TextView summaryTextView;
		TextView timeTextView;
		TextView participateTextView;
		ImageView participateStateImageView;
	}

}
