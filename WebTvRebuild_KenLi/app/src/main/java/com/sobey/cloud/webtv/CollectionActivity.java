package com.sobey.cloud.webtv;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CollectionActivity extends BaseActivity implements IDragListViewListener {

	private JSONArray mCollectionImageArray = null;
	private JSONArray mCollectionNormalArray = null;
	private CollectionType mCollectionType = CollectionType.NORMAL;
	private BaseAdapter mAdapterImage;
	private BaseAdapter mAdapterNormal;
	private String mUserName;
	private boolean mInitSelectFlag = false;
	private int mScreenWidth;
	private long mStartTime = 0;
	private boolean mFirstFlag = true;

	private enum CollectionType {
		IMAGE, NORMAL
	}

	private String[] mSelectList = { "新闻收藏", "图片收藏" };

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mCollectionHeaderBackBtn)
	ImageButton mCollectionHeaderBackBtn;
	@GinInjectView(id = R.id.mCollectionHeaderSelectLayout)
	LinearLayout mCollectionHeaderSelectLayout;
	@GinInjectView(id = R.id.mCollectionHeaderTitle)
	TextView mCollectionHeaderTitle;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.mSelectLayout)
	RelativeLayout mSelectLayout;
	@GinInjectView(id = R.id.mSelectListLayout)
	LinearLayout mSelectListLayout;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			if (mFirstFlag) {
				mFirstFlag = false;
			} else {
				loadMore();
			}
		}
	}

	@Override
	public int getContentView() {
		return R.layout.activity_collection;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupReviewActivity();
	}

	public void setupReviewActivity() {
		loadListView();
		loadCollection();
	}

	private void loadCollection() {
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(CollectionActivity.this, LoginActivity.class));
			finish();
		}
		mUserName = userInfo.getString("id", "");

		mCollectionHeaderTitle.setText(mSelectList[0]);
		mCollectionHeaderBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CollectionActivity.this.finish();
			}
		});
		mCollectionHeaderSelectLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mInitSelectFlag) {
					LayoutInflater inflater = LayoutInflater.from(CollectionActivity.this);
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

	private void loadListView() {
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);

		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		mAdapterImage = new BaseAdapter() {
			private LayoutInflater inflater = LayoutInflater.from(CollectionActivity.this);

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderCollectionImage viewHolder = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_collection_image, null);
					viewHolder = new ViewHolderCollectionImage();
					viewHolder.horizontalLayout = (HorizontalScrollView) convertView
							.findViewById(R.id.horizontal_layout);
					viewHolder.image = (AdvancedImageView) convertView.findViewById(R.id.image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.title);
					viewHolder.count = (TextView) convertView.findViewById(R.id.count);
					viewHolder.actionBtn = (Button) convertView.findViewById(R.id.action_btn);
					((LinearLayout) convertView.findViewById(R.id.content_layout))
							.getLayoutParams().width = mScreenWidth;
					ActionBtnClass actionBtnClass = new ActionBtnClass();
					actionBtnClass.postion = position;
					actionBtnClass.horizontalScrollView = viewHolder.horizontalLayout;
					viewHolder.actionBtn.setTag(actionBtnClass);
					convertView.setTag(viewHolder);
					loadViewHolderImage(position, convertView, viewHolder);
				} else {
					loadViewHolderImage(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				if (mCollectionImageArray != null) {
					return mCollectionImageArray.length();
				} else {
					return 0;
				}
			}
		};

		mAdapterNormal = new BaseAdapter() {
			private LayoutInflater inflater = LayoutInflater.from(CollectionActivity.this);

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderCollectionNormal viewHolder = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_collection_normal, null);
					viewHolder = new ViewHolderCollectionNormal();
					viewHolder.horizontalLayout = (HorizontalScrollView) convertView
							.findViewById(R.id.horizontal_layout);
					viewHolder.title = (TextView) convertView.findViewById(R.id.title);
					viewHolder.summary = (TextView) convertView.findViewById(R.id.summary);
					viewHolder.actionBtn = (Button) convertView.findViewById(R.id.action_btn);
					((LinearLayout) convertView.findViewById(R.id.content_layout))
							.getLayoutParams().width = mScreenWidth;
					ActionBtnClass actionBtnClass = new ActionBtnClass();
					actionBtnClass.postion = position;
					actionBtnClass.horizontalScrollView = viewHolder.horizontalLayout;
					viewHolder.actionBtn.setTag(actionBtnClass);
					convertView.setTag(viewHolder);
					loadViewHolderNormal(position, convertView, viewHolder);
				} else {
					loadViewHolderNormal(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				if (mCollectionNormalArray != null) {
					return mCollectionNormalArray.length();
				} else {
					return 0;
				}
			}
		};

		setMyAdapter();
		mListView.setAsOuter();
	}

	private void setMyAdapter() {
		if (mListView != null) {
			if (mCollectionType == CollectionType.IMAGE) {
				if (mCollectionImageArray == null) {
					mOpenLoadingIcon();
					loadMore();
				}
				mListView.setAdapter(mAdapterImage);
			} else {
				if (mCollectionNormalArray == null) {
					mOpenLoadingIcon();
					loadMore();
				}
				mListView.setAdapter(mAdapterNormal);
			}
		}
	}

	private void loadMore() {
		if (mCollectionType == CollectionType.IMAGE) {
			News.getCollect(mUserName, "2", CollectionActivity.this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					if (result != null) {
						mCollectionImageArray = result;
						mAdapterImage.notifyDataSetChanged();
						closeLoading();
					} else {
						closeLoading();
					}
				}

				@Override
				public void onNG(String reason) {
					closeLoading();
				}

				@Override
				public void onCancel() {
					closeLoading();
				}
			});
		} else {
			News.getCollect(mUserName, "", CollectionActivity.this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					if (result != null) {
						mCollectionNormalArray = result;
						mAdapterNormal.notifyDataSetChanged();
						closeLoading();
					} else {
						closeLoading();
					}
				}

				@Override
				public void onNG(String reason) {
					closeLoading();
				}

				@Override
				public void onCancel() {
					closeLoading();
				}
			});
		}
	}

	private void closeLoading() {
		mListView.setPullRefreshEnable(true);
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mCloseLoadingIcon();
	}

	private void loadViewHolderImage(int position, View convertView, ViewHolderCollectionImage viewHolderImage) {
		if (convertView != null && mCollectionImageArray != null && mCollectionImageArray.length() > position) {
			try {
				viewHolderImage = (ViewHolderCollectionImage) convertView.getTag();
				final JSONObject object = mCollectionImageArray.getJSONObject(position);
				viewHolderImage.image.setNetImage(object.getString("logo"));
				viewHolderImage.title.setText(object.getString("title"));
				viewHolderImage.count.setText(object.getString("count") + "张");

				convertView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						ViewHolderCollectionImage viewHolderImage = (ViewHolderCollectionImage) v.getTag();
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setPressed(true);
							if (viewHolderImage.horizontalLayout.getScrollX() > 0) {
								viewHolderImage.horizontalLayout.smoothScrollTo(0, 0);
								mStartTime = 0;
							} else {
								mStartTime = System.currentTimeMillis();
							}
							return true;
						case MotionEvent.ACTION_UP:
							v.setPressed(false);
							int scrollX = viewHolderImage.horizontalLayout.getScrollX();
							int actionW = viewHolderImage.actionBtn.getWidth();
							if (scrollX > 0 && scrollX < actionW * 0.8) {
								viewHolderImage.horizontalLayout.smoothScrollTo(0, 0);
							} else if (scrollX <= 0) {
								if (mStartTime > 0) {
									long difTime = System.currentTimeMillis() - mStartTime;
									if (difTime > 0 && difTime < 500) {
										try {
											JSONObject obj = new JSONObject();
											obj.put("id", object.getString("aid"));
											openDetailActivity(MConfig.TypePicture, obj.toString());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							} else {
								viewHolderImage.horizontalLayout.smoothScrollTo(actionW, 0);
							}
							mStartTime = 0;
							return true;
						case MotionEvent.ACTION_MOVE:
							if (viewHolderImage.horizontalLayout.getScrollX() <= 0 && mStartTime > 0) {
								long difTime = System.currentTimeMillis() - mStartTime;
								if (difTime >= 500 && difTime < 2000) {
									viewHolderImage.horizontalLayout
											.smoothScrollTo(viewHolderImage.actionBtn.getWidth(), 0);
									mStartTime = 0;
									return true;
								}
							}
							break;
						}
						return false;
					}
				});

				viewHolderImage.actionBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ActionBtnClass actionBtnClass = (ActionBtnClass) v.getTag();
						actionBtnClass.horizontalScrollView.smoothScrollTo(0, 0);
						deleteItem(actionBtnClass.postion);
					}
				});
			} catch (Exception e) {
			}
		}
	}

	private void loadViewHolderNormal(int position, View convertView, ViewHolderCollectionNormal viewHolderNormal) {
		if (convertView != null && mCollectionNormalArray != null && mCollectionNormalArray.length() > position) {
			try {
				viewHolderNormal = (ViewHolderCollectionNormal) convertView.getTag();
				final JSONObject object = mCollectionNormalArray.getJSONObject(position);
				viewHolderNormal.title.setText(object.getString("title"));
				viewHolderNormal.summary.setText(object.getString("summary"));

				convertView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						ViewHolderCollectionNormal viewHolderNormal = (ViewHolderCollectionNormal) v.getTag();
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setPressed(true);
							if (viewHolderNormal.horizontalLayout.getScrollX() > 0) {
								viewHolderNormal.horizontalLayout.smoothScrollTo(0, 0);
								mStartTime = 0;
							} else {
								mStartTime = System.currentTimeMillis();
							}
							return true;
						case MotionEvent.ACTION_UP:
							v.setPressed(false);
							int scrollX = viewHolderNormal.horizontalLayout.getScrollX();
							int actionW = viewHolderNormal.actionBtn.getWidth();
							if (scrollX > 0 && scrollX < actionW * 0.8) {
								viewHolderNormal.horizontalLayout.smoothScrollTo(0, 0);
							} else if (scrollX <= 0) {
								if (mStartTime > 0) {
									long difTime = System.currentTimeMillis() - mStartTime;
									if (difTime > 0 && difTime < 500) {
										try {
											JSONObject obj = new JSONObject();
											obj.put("id", object.getString("aid"));
											openDetailActivity(object.getInt("type"), obj.toString());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							} else {
								viewHolderNormal.horizontalLayout.smoothScrollTo(actionW, 0);
							}
							mStartTime = 0;
							return true;
						case MotionEvent.ACTION_MOVE:
							if (viewHolderNormal.horizontalLayout.getScrollX() <= 0 && mStartTime > 0) {
								long difTime = System.currentTimeMillis() - mStartTime;
								if (difTime >= 500 && difTime < 2000) {
									viewHolderNormal.horizontalLayout
											.smoothScrollTo(viewHolderNormal.actionBtn.getWidth(), 0);
									mStartTime = 0;
									return true;
								}
							}
							break;
						}
						return false;
					}
				});

				viewHolderNormal.actionBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ActionBtnClass actionBtnClass = (ActionBtnClass) v.getTag();
						actionBtnClass.horizontalScrollView.smoothScrollTo(0, 0);
						deleteItem(actionBtnClass.postion);
					}
				});
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onRefresh() {
		loadMore();
	}

	@Override
	public void onLoadMore() {
	}

	private void deleteItem(int position) {
		String articleId;
		try {
			if (mCollectionType == CollectionType.IMAGE) {
				articleId = mCollectionImageArray.getJSONObject(position).getString("aid");
			} else {
				articleId = mCollectionNormalArray.getJSONObject(position).getString("aid");
			}
		} catch (Exception e) {
			Toast.makeText(CollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(articleId)) {
			Toast.makeText(CollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(CollectionActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
		News.deleteCollect(mUserName, articleId, CollectionActivity.this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.getJSONObject(0).getString("returncode").equalsIgnoreCase("SUCCESS")) {
						loadMore();
						Toast.makeText(CollectionActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(CollectionActivity.this, result.getJSONObject(0).getString("returnmsg"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(CollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNG(String reason) {
				Toast.makeText(CollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(CollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(CollectionActivity.this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			CollectionActivity.this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(CollectionActivity.this);
			Intent intent1 = new Intent(CollectionActivity.this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			CollectionActivity.this.startActivity(intent1);
			break;
		case MConfig.TypeBroke:
			Intent intent2 = new Intent(CollectionActivity.this,
					com.sobey.cloud.webtv.broke.BrokeNewsDetailActivity.class);
			intent2.putExtra("information", information);
			CollectionActivity.this.startActivity(intent2);
			break;
		case MConfig.TypeNews:
			Intent intent3 = new Intent(CollectionActivity.this, GeneralNewsDetailActivity.class);
			intent3.putExtra("information", information);
			CollectionActivity.this.startActivity(intent3);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(CollectionActivity.this);
			Intent intent4 = new Intent(CollectionActivity.this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			CollectionActivity.this.startActivity(intent4);
			break;
		}
	}

	private void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	private void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	private class ViewHolderCollectionImage {
		HorizontalScrollView horizontalLayout;
		AdvancedImageView image;
		TextView title;
		TextView count;
		Button actionBtn;
	}

	private class ViewHolderCollectionNormal {
		HorizontalScrollView horizontalLayout;
		TextView title;
		TextView summary;
		Button actionBtn;
	}

	private class ActionBtnClass {
		public int postion;
		public HorizontalScrollView horizontalScrollView;
	}

	private class SelectItemClickListener implements OnClickListener {
		private int mPosition;

		public SelectItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			mCollectionHeaderTitle.setText(mSelectList[mPosition]);
			mSelectLayout.setVisibility(View.GONE);
			if (mPosition == 0) {
				mCollectionType = CollectionType.NORMAL;
			} else {
				mCollectionType = CollectionType.IMAGE;
			}
			setMyAdapter();
		}
	}

}
