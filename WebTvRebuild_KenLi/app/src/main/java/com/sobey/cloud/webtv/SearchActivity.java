package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.uiparts.edittext.SearchBox;
import com.dylan.uiparts.keywordflow.KeywordsFlow;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.ViewHolderText;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends BaseActivity {

	public static final int TYPE_ALL = 0;

	private SearchBox searchBox;
	private ImageView selectType;
	private KeywordsFlow keywordsFlow;
	private LinearLayout mSearchHeaderLayout;
	private TextView mSearchHeaderText;
	private ListView mListView;
	private BaseAdapter mAdapter;

	private JSONArray mSearchResultArray;
	private String mSearchContent;
	private int mSearchType = TYPE_ALL;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_search;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		/* setup searchBox */
		searchBox = (SearchBox) findViewById(R.id.mSearchBox);
		searchBox.setStyle(true, false, true, false, "请输入搜索内容");
		// add left icon
		setSearchBoxLeftIcon(0);
		// set edittext active
		searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
		searchBox.setCursorVisible(true);
		searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		// set edittext touch listner
		searchBox.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					int x = (int) event.getX();
					int width = v.getWidth();
					// select search type
					if(x >= 0 && x <= 80) {
						if(selectType.getVisibility() == View.VISIBLE) {
							selectType.setVisibility(View.GONE);
						} else {
							selectType.setVisibility(View.VISIBLE);
						}
					}
					// search
					else if(x >= width - 60 && x <= width) {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
						searchBox.setInputType(InputType.TYPE_NULL);
						mSearchContent = String.valueOf(searchBox.getText()).trim();
						mOnSearchBoxClick();
					}
					// input text
					else {
						searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
						searchBox.setCursorVisible(true);
						searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
					}
				}
				return false;
			}
		});

		/* setup keywordFlow */
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsflow_framework);
		keywordsFlow.setShowType(2);
		keywordsFlow.setType2TextSize(15);
		keywordsFlow.setOnItemClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchContent = ((TextView) v).getText().toString().trim();
				searchBox.setText(mSearchContent);
				mOnSearchBoxClick();
			}
		});

		News.getHotword(15, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					ArrayList<String> strList = new ArrayList<String>();
					for(int i = 0; i < result.length(); i++) {
						strList.add(result.getJSONObject(i).getString("hotword"));
					}
					keywordsFlow.feedKeywordAll(strList);
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNG(String reason) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}
		});

		initKeyClickListener();

		mListView = (ListView) findViewById(R.id.search_resultlist);
		mListView.setHeaderDividersEnabled(false);
		mListView.setBackgroundColor(0xffffffff);

		mAdapter = new BaseAdapter() {

			private LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderText viewHolder = null;
				if(convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_searchresult, null);
					viewHolder = new ViewHolderText();
					viewHolder.setTextView((TextView) convertView.findViewById(R.id.title));
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView, viewHolder);
				} else {
					loadViewHolder(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				if(mSearchResultArray == null) {
					return 0;
				}
				return mSearchResultArray.length();
			}
		};
		mSearchHeaderLayout = new LinearLayout(this);
		mSearchHeaderLayout.setGravity(Gravity.CENTER);
		mSearchHeaderText = new TextView(this);
		mSearchHeaderLayout.addView(mSearchHeaderText);
		mListView.addHeaderView(mSearchHeaderLayout);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					JSONObject obj = mSearchResultArray.getJSONObject(position - 1);
					openDetailActivity(Integer.valueOf(obj.getString("type")), obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSearchBoxLeftIcon(int typeNum) {
		Drawable left;
		switch (typeNum){
		case 0:
			left = this.getResources().getDrawable(R.drawable.searchbox_left_all_icon);
			break;
		case 1:
			left = this.getResources().getDrawable(R.drawable.searchbox_left_video_icon);
			break;
		case 2:
			left = this.getResources().getDrawable(R.drawable.searchbox_left_news_icon);
			break;
		case 3:
			left = this.getResources().getDrawable(R.drawable.searchbox_left_picture_icon);
			break;
		default:
			left = this.getResources().getDrawable(R.drawable.searchbox_left_all_icon);
			break;
		}
		left.setBounds(0, 0, 90, 60);
		Drawable[] icons = searchBox.getCompoundDrawables();
		searchBox.setCompoundDrawables(left, icons[1], icons[2], icons[3]);
		searchBox.setCompoundDrawablePadding(0);
	}

	private void initKeyClickListener() {
		Button cancelButton = (Button) findViewById(R.id.search_cancel_btn);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
			}
		});

		selectType = (ImageView) findViewById(R.id.search_selecttype);
		selectType.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					int width = v.getWidth() / 2;
					int height = v.getHeight() / 2;
					// all
					if(x >= 0 && x <= width && y >= 0 && y <= height) {
						setSearchBoxLeftIcon(0);
						mSearchType = TYPE_ALL;
					}
					// video
					else if(x > width && y >= 0 && y <= height) {
						setSearchBoxLeftIcon(1);
						mSearchType = MConfig.TypeVideo;
					}
					// news
					else if(x >= 0 && x <= width && y > height) {
						setSearchBoxLeftIcon(2);
						mSearchType = MConfig.TypeNews;
					}
					// picture
					else if(x > width && y > height) {
						setSearchBoxLeftIcon(3);
						mSearchType = MConfig.TypePicture;
					}
					selectType.setVisibility(View.GONE);
				}
				return false;
			}
		});
	}

	protected void mOnSearchBoxClick() {
		keywordsFlow.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		setCommentsTitle("搜索中...");
		
		if(mSearchContent.length() < 1) {
			Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			return;
		} else {
			News.searchArticleList(0, mSearchContent, mSearchType, this, new OnJsonObjectResultListener() {
				@Override
				public void onOK(JSONObject result) {
					try {
						mSearchResultArray = result.getJSONArray("articles");
						mAdapter.notifyDataSetChanged();
						if (mSearchResultArray.length() < 1) {
							setCommentsTitle("没有相关内容");
						} else {
							setCommentsTitle("");
						}
					} catch (JSONException e) {
						setCommentsTitle("暂时无法搜索相关内容");
					}
				}

				@Override
				public void onNG(String reason) {
					setCommentsTitle("暂时无法搜索相关内容");
				}

				@Override
				public void onCancel() {
					setCommentsTitle("暂时无法搜索相关内容");
				}
			});
		}
	}
	
	private void loadViewHolder(int position, View convertView, ViewHolderText viewHolder) {
		viewHolder = (ViewHolderText) convertView.getTag();
		if(mSearchResultArray.length() > position) {
			try {
				viewHolder.getTextView().setText(mSearchResultArray.getJSONObject(position).getString("title"));
			} catch (JSONException e) {
				viewHolder = null;
			}
		} else {
			viewHolder = null;
		}
	}
	
	private void openDetailActivity(int type, String information) {
		switch (type){
		case MConfig.TypePicture:
			Intent intent = new Intent(SearchActivity.this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			SearchActivity.this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(SearchActivity.this);
			Intent intent1 = new Intent(SearchActivity.this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			SearchActivity.this.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(SearchActivity.this, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			SearchActivity.this.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(SearchActivity.this);
			Intent intent4 = new Intent(SearchActivity.this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			SearchActivity.this.startActivity(intent4);
			break;
		}
	}
	
	private void close() {
		View view = getWindow().peekDecorView();
		if(view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				finishActivity();
			}
		}, 200);
	}
	
	private void setCommentsTitle(String title) {
		mSearchHeaderText.setVisibility(View.GONE);
		if (!title.equalsIgnoreCase("")) {
			mSearchHeaderText.setText(title);
			mSearchHeaderText.setTextSize(18);
			mSearchHeaderText.setTextColor(Color.BLACK);
			mSearchHeaderText.setGravity(Gravity.CENTER);
			mSearchHeaderText.setPadding(10, 10, 0, 10);
			mSearchHeaderText.setVisibility(View.VISIBLE);
		}
	}
}
