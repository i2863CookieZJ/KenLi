package com.sobey.cloud.webtv.views.group;

import java.util.ArrayList;
import java.util.List;

import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper.OnItemDeleteListener;
import com.sobey.cloud.webtv.adapter.GroupSubjectDetailAdapter;
import com.sobey.cloud.webtv.bean.GroupCommentModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.SobeyBaseResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.broadcast.LogStateChangeReceiver;
import com.sobey.cloud.webtv.ui.MyProgressDialog;
import com.sobey.cloud.webtv.utils.Base64;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;
import com.sobey.cloud.webtv.widgets.HorizontalListView;
import com.sobey.cloud.webtv.widgets.MyAlertDialog;
import com.sobey.cloud.webtv.widgets.MyAlertDialog.MyOnClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupSubjectActivity extends BaseActivity4Group implements View.OnClickListener {

	@GinInjectView(id = R.id.group_subject_detail_listView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.title_tv)
	TextView mTitleTv;
	@GinInjectView(id = R.id.operator_iv)
	ImageView mOperatorIv;
	@GinInjectView(id = R.id.group_subject_detail_like_btn)
	CheckBox likeCb;
	@GinInjectView(id = R.id.group_subject_detail_send_btn)
	Button sendBtn;
	@GinInjectView(id = R.id.group_subject_detail_add_btn)
	Button addBtn;
	@GinInjectView(id = R.id.group_subject_detail_face_btn)
	Button faceBtn;
	@GinInjectView(id = R.id.title_layout)
	LinearLayout titleLayout;
	@GinInjectView(id = R.id.more_container_layout)
	FrameLayout moreContainerLayout;

	@GinInjectView(id = R.id.more_layout)
	View moreLayout;
	@GinInjectView(id = R.id.more_layout_collect_cb)
	CheckBox collectCb;
	@GinInjectView(id = R.id.more_layout_floor_host_tv)
	TextView floorHostTv;
	@GinInjectView(id = R.id.more_layout_turn_page_tv)
	TextView turnPageTv;

	@GinInjectView(id = R.id.group_subject_detail_edit)
	EditText editText;
	@GinInjectView(id = R.id.group_subject_detail_edit_layout1)
	RelativeLayout editLayout1;
	@GinInjectView(id = R.id.group_subject_detail_edit_layout2)
	RelativeLayout editLayout2;
	@GinInjectView(id = R.id.post_comment_bottom_more_layout)
	View bottomMoreLayout;
	@GinInjectView(id = R.id.post_comment_bottom_face_layout)
	View bottomFaceLayout;
	@GinInjectView(id = R.id.post_comment_bottom_pic_layout)
	View bottomPicLayout;
	@GinInjectView(id = R.id.post_comment_bottom_content_layout)
	View bottomContainerLayout;

	@GinInjectView(id = R.id.post_comment_bottom_pictures_horizontallistview)
	HorizontalListView picturesListView;
	@GinInjectView(id = R.id.post_comment_bottom_pictures_textview)
	TextView bottomPicHitTextView;// 图片张数发生变化提示

	@GinInjectView(id = R.id.face_viewpager)
	ViewPager faceViewPager;
	@GinInjectView(id = R.id.face_indicator_layout)
	LinearLayout indicatorLayout;

	@GinInjectView(id = R.id.post_more_add_pic)
	ImageView addPicImg;// 添加图片
	@GinInjectView(id = R.id.post_more_add_add)
	ImageView addAddImg;// @好友
	@GinInjectView(id = R.id.post_more_pictures_num_layout)
	LinearLayout picNumLayout;// 图片张数顶部红色提示
	@GinInjectView(id = R.id.post_more_pictures_num_tv)
	TextView picNumTextView;// 图片张数tv

	@GinInjectView(id = R.id.group_subject_detail_edit_tv)
	TextView editTextView;// 点击次textview后显示可编辑的edittext

	@GinInjectView(id = R.id.mLoadinglayout)
	View mLoadingLayout;
	@GinInjectView(id = R.id.loading_chinese)
	TextView loadingTipsTv;

	@GinInjectView(id = R.id.mLoadingFailedLayout)
	View failedLayout;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	TextView failedTv;

	private GroupSubjectModel mSubjectModel;
	private String title;
	private GroupSubjectDetailAdapter mAdapter;
	private Animation anim_in;
	private Animation anim_out;
	private SharePopWindow mSharePopWindow;
	private boolean hasLoadSuccessed = false;
	private List<ImageButton> indicatorImg = new ArrayList<ImageButton>();
	private ArrayList<String> upLoadfiles = new ArrayList<String>();
	private List<GridView> faceGridViews = new ArrayList<GridView>();
	private ArrayList<String> uploadFileSuccessUrls = new ArrayList<String>();
	private GroupPostSubjectHorizontalListViewAdatper horizontalListViewAdapter;
	protected MyProgressDialog myProgress;

	/**
	 * 是否正在加载
	 */
	protected boolean isLoading;
	private final String PUBLISH_DATE = "publishdate";

	private int totalPage = 1;
	private int pageSize = 20;
	private int currentPage = 1;
	private Drawable collectPress;
	private Drawable collectNor;
	private String filter;
	private Drawable personNor;
	private Drawable personPress;
	private String shareUrl = "";
	private LogStateChangeReceiver logStateReceiver;
	private boolean isPosting = false;
	private TextView addPraise;
	// 评论或回复发布的次数
	private int sendCount = 0;
	private long intervalTime = 30 * 1000;// 间隔时间为30s
	private int nowPage = 0;

	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		return R.layout.activity_group_subject_deail_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle) {
			mSubjectModel = bundle.getParcelable("mSubjectModel");
			title = bundle.getString("title");
		}
		if (null != savedInstanceState) {
			mSubjectModel = savedInstanceState.getParcelable("mSubjectModel");
		}
		setUpDatas();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "onNewIntent-->");
		setIntent(intent);
		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle) {
			mSubjectModel = bundle.getParcelable("mSubjectModel");
			title = bundle.getString("title");
		}
		mTitleTv.setText(title);
		currentPage = 1;
		mOpenLoadingIcon();
		loadData(false, false, false, false);
	}

	public void setUpDatas() {

		mOpenLoadingIcon();

		initAnim();
		View contentView = findViewById(R.id.activity_group_subject_detail_layout);
		mSharePopWindow = new SharePopWindow(this, contentView);

		mTitleTv.setText(title);
		mOperatorIv.setImageResource(R.drawable.selector_btn_group_share);
		addPraise = (TextView) findViewById(R.id.group_subject_detail_addp);
		initCollectDrawable();

		initFaceViews();

		initPicViews();

		failedLayout = findViewById(R.id.mLoadingFailedLayout);
		failedTv = (TextView) findViewById(R.id.loading_failed_tips_tv);
		setListener();
		RegisterReciever();

		loadData(false, false, false, false);

	}

	private void initCollectDrawable() {
		collectPress = getResources().getDrawable(R.drawable.icon_group_subject_more_collect_press);
		collectNor = getResources().getDrawable(R.drawable.icon_group_subject_more_cellect_nor);
		personNor = getResources().getDrawable(R.drawable.icon_group_subject_more_person_nor);
		personPress = getResources().getDrawable(R.drawable.icon_group_subject_more_person_press);

		collectPress.setBounds(0, 0, collectPress.getMinimumWidth(), collectPress.getMinimumHeight());
		collectNor.setBounds(0, 0, collectNor.getMinimumWidth(), collectNor.getMinimumHeight());
		personNor.setBounds(0, 0, personNor.getMinimumWidth(), personNor.getMinimumHeight());
		personPress.setBounds(0, 0, personPress.getMinimumWidth(), personPress.getMinimumHeight());

	}

	@SuppressWarnings("deprecation")
	private void initFaceViews() {

		for (int i = 0; i < 3; i++) {
			ImageButton img = new ImageButton(this);
			LayoutParams params = new LayoutParams(BaseUtil.Dp2Px(mContext, 5), BaseUtil.Dp2Px(mContext, 5));
			params.leftMargin = 5;
			params.rightMargin = 5;
			img.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_indicator));
			img.setLayoutParams(params);
			indicatorLayout.addView(img);
			indicatorImg.add(img);
			if (i == 0) {
				img.setEnabled(false);
			}
			GridView gridView = new GridView(mContext);
			gridView.setNumColumns(6);
			gridView.setHorizontalSpacing(5);
			gridView.setVerticalSpacing(BaseUtil.Dp2Px(mContext, 15));
			gridView.setSelector(R.color.transparent);
			faceGridViews.add(gridView);

		}
		faceViewPager.setAdapter(new MyPagerAdapter());
		faceViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				GridView gridView = faceGridViews.get(arg0);
				MyFaceAdapter adapter = new MyFaceAdapter(arg0);
				if (arg0 == 0) {
					adapter.setData(FaceUtil.defaultFacesResids);
				} else if (arg0 == 1) {
					adapter.setData(FaceUtil.coolmonkeyFacesResids);
				} else if (arg0 == 2) {
					adapter.setData(FaceUtil.grapemanFacesResids);
				}
				gridView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				for (int i = 0; i < indicatorImg.size(); i++) {
					ImageView img = indicatorImg.get(i);
					if (arg0 == i) {
						img.setEnabled(false);
					} else {
						img.setEnabled(true);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		GridView gridView = faceGridViews.get(0);
		MyFaceAdapter adapter = new MyFaceAdapter(0);
		adapter.setData(FaceUtil.defaultFacesResids);
		gridView.setAdapter(adapter);
		faceViewPager.setCurrentItem(0);

	}

	private void initPicViews() {
		horizontalListViewAdapter = new GroupPostSubjectHorizontalListViewAdatper(mContext);

		if (upLoadfiles.size() < 1) {
			horizontalListViewAdapter.setData(horizontalListViewAdapter.generateBaseData());
			horizontalListViewAdapter.notifyDataSetChanged();
			picturesListView.setSelection(1);
		}
		picturesListView.setAdapter(horizontalListViewAdapter);
		horizontalListViewAdapter.setOnItemDeletedListener(new OnItemDeleteListener() {

			@Override
			public void itemDeleted(String path) {
				if (upLoadfiles.contains(path)) {
					upLoadfiles.remove(path);
					if (upLoadfiles.size() < 1) {
						horizontalListViewAdapter.setData(horizontalListViewAdapter.generateBaseData());
						horizontalListViewAdapter.notifyDataSetChanged();
						picturesListView.setSelection(1);
						picNumLayout.setVisibility(View.GONE);

						StringBuilder sb = new StringBuilder();
						sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size())
								.append("张可选");
						bottomPicHitTextView.setText(sb.toString());

						return;
					}
					horizontalListViewAdapter.setDatas(upLoadfiles);
					horizontalListViewAdapter.notifyDataSetChanged();
					picturesListView.setSelection(upLoadfiles.size());

					picNumLayout.setVisibility(View.VISIBLE);
					picNumTextView.setText(upLoadfiles.size() + "");

					StringBuilder sb = new StringBuilder();
					sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size())
							.append("张可选");
					bottomPicHitTextView.setText(sb.toString());

				}
			}
		});
	}

	private void setListener() {
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					String text = editText.getText().toString();
					if (!text.isEmpty() && text.contains("回复") && text.contains("【") && text.contains("】：")) {
						return false;
					}
					editLayout1.setVisibility(View.VISIBLE);
					editLayout2.setVisibility(View.GONE);

					bottomContainerLayout.setVisibility(View.GONE);

					break;

				default:
					break;
				}
				return false;
			}
		});

		titleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTitleTextClickAction();
				BaseUtil.HideKeyboard(editText);
			}
		});
		moreContainerLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTitleTextClickAction();
				BaseUtil.HideKeyboard(editText);
			}
		});
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);
		mListView.setListener(new IDragListViewListener() {

			@Override
			public void onRefresh() {
				loadData(false, false, false, false);
			}

			@Override
			public void onLoadMore() {
				// TODO 自动加载
				nowPage++;
				addFloor();
				mListView.setPullLoadEnable(false);
			}
		});

		editTextView.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		faceBtn.setOnClickListener(this);
		addPicImg.setOnClickListener(this);
		addAddImg.setOnClickListener(this);
		turnPageTv.setOnClickListener(this);
		floorHostTv.setOnClickListener(this);
		failedTv.setOnClickListener(this);
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					bottomContainerLayout.setVisibility(View.GONE);
				}
			}
		});

		editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					bottomContainerLayout.setVisibility(View.GONE);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	private void addFloor() {
		GroupRequestMananger.getInstance().getGroupSubjectInfo(mSubjectModel.subjectId, currentPage + nowPage, filter,
				mContext, new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						if (result instanceof GroupSubjectModel) {
							hasLoadSuccessed = true;
							GroupSubjectModel subjectModel = (GroupSubjectModel) result;
							mAdapter.addData(subjectModel);// TODO
							if (subjectModel.commentList.size() > 20) {
								mListView.setPullLoadEnable(true);
							}
						} else {
							nowPage--;
							Toast.makeText(mContext, R.string.request_data_failed, Toast.LENGTH_SHORT).show();
						}
					}

				});
	}

	/**
	 * 
	 * @param isManual
	 *            是否自动刷新请求
	 * @param isFilter
	 *            是否带过滤条件的请求
	 * @param isFromPost
	 *            是否是发布评论的请求
	 * @param isFromTurnPage
	 *            是否来自跳页的请求
	 */
	private void loadData(final boolean isManual, final boolean isFilter, final boolean isFromPost,
			final boolean isFromTurnPage) {
		if (isManual) {
			mListView.startManualRefresh();
		}
		GroupRequestMananger.getInstance().getGroupSubjectInfo(mSubjectModel.subjectId, currentPage, filter, mContext,
				new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						if (result instanceof GroupSubjectModel) {
							hasLoadSuccessed = true;
							if (null == mAdapter) {
								mAdapter = new GroupSubjectDetailAdapter(mContext, handler);
							}
							mAdapter.setPageInfo(currentPage, pageSize);
							GroupSubjectModel subjectModel = (GroupSubjectModel) result;
							shareUrl = subjectModel.subjectUrl;

							if (null == subjectModel.commentList || subjectModel.commentList.size() < 1) {
								if (isFilter) {
									floorHostTv.setCompoundDrawables(null, personNor, null, null);
									filter = null;
									Toast.makeText(mContext, "该条件下无记录", Toast.LENGTH_SHORT).show();
									if (isManual) {
										mListView.stopMannualRefresh();
									}
									return;
								}
							}

							synLikeState2File(subjectModel);
							changeCheckBoxState(subjectModel);
							caculatePageNum(subjectModel);
							mAdapter.setData(subjectModel, mSubjectModel);
							mListView.setAdapter(mAdapter);
							if (subjectModel.commentList.size() < 20) {
								mListView.setPullLoadEnable(false);
							} else {
								mListView.setPullLoadEnable(true);
							}
						} else {
							// 没成功加载过数据 显示加载失败页面
							if (!hasLoadSuccessed) {
								failedLayout.setVisibility(View.VISIBLE);
							} else {
								Toast.makeText(mContext, R.string.request_data_failed, Toast.LENGTH_SHORT).show();
							}
						}
						if (isManual) {
							mListView.stopMannualRefresh();
							mListView.setSelection(mAdapter.getCount());
						}
						if (isFromPost) {
							mListView.setSelection(mAdapter.getCount());
						}
						if (isFromTurnPage) {
							if (mAdapter.getCount() > 1) {
								mListView.setSelection(2);
							}

						}
						mListView.stopRefresh();
						mCloseLoadingIcon();
					}

				});
	}

	/**
	 * 计算分页数
	 * 
	 * @param subjectModel
	 */
	private void caculatePageNum(GroupSubjectModel subjectModel) {
		int count = subjectModel.commentCount;
		if (count < 20) {
			totalPage = 1;
		} else if (count % pageSize == 0) {
			totalPage = count / pageSize;
		} else {
			totalPage = count / pageSize + 1;
		}
	}

	/**
	 * TODO 改变点赞checkbox和收藏checkbox的状态
	 * 
	 * @param subjectModel
	 */
	private void changeCheckBoxState(GroupSubjectModel subjectModel) {
		if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
			collectCb.setEnabled(false);
			likeCb.setEnabled(false);
		} else {
			collectCb.setEnabled(true);
			likeCb.setEnabled(true);

			if (subjectModel.isCollected == 1) {
				collectCb.setChecked(true);
			} else {
				collectCb.setChecked(false);
			}
			collectCb.setOnCheckedChangeListener(new MyCheckedChangeListener(mSubjectModel.subjectId));

			int isLiked = subjectModel.isLiked;
			if (isLiked == 0) {
				// 未被点赞
				likeCb.setChecked(false);
			} else if (isLiked == 1) {
				likeCb.setChecked(true);
			}
			likeCb.setOnCheckedChangeListener(new MyCheckedChangeListener(mSubjectModel.subjectId));

		}
	}

	/**
	 * 请求数据完成后将commentlike的状态同步到本地 界面从本地取 以防发生错乱
	 * 
	 * @param subjectModel
	 */
	private void synLikeState2File(GroupSubjectModel subjectModel) {
		List<GroupCommentModel> list = subjectModel.commentList;
		for (GroupCommentModel commentModel : list) {
			PreferencesUtil.putInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT + commentModel.commentId,
					commentModel.commentLikeCount);
			int liked = commentModel.commentIsLiked;
			if (liked == 1) {
				PreferencesUtil.putBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED + commentModel.commentId, true);
			} else {
				PreferencesUtil.putBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED + commentModel.commentId, false);
			}
		}
	}

	private void doCollectAction(final boolean collect) {

		int isCollect = collect ? 1 : 0;
		GroupRequestMananger.getInstance().collectSubject(mSubjectModel.subjectId, isCollect, this,
				new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						if (result instanceof SobeyBaseResult) {
							SobeyBaseResult baseResult = (SobeyBaseResult) result;
							if (baseResult.returnCode == SobeyBaseResult.OK) {
								if (collect) {
									Toast.makeText(mContext, R.string.collect_success, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(mContext, R.string.cancel_collect_success, Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					}
				});
	}

	private void doTurnPageAction() {

		showTurnPageDialog();
		doTitleTextClickAction();
	}

	private void doFloorHostAction() {
		if (TextUtils.isEmpty(filter)) {
			filter = "master";
			floorHostTv.setCompoundDrawables(null, personPress, null, null);
		} else {
			floorHostTv.setCompoundDrawables(null, personNor, null, null);
			filter = "";
		}
		loadData(true, true, false, false);
	}

	private void showTurnPageDialog() {
		final MyAlertDialog alertDialog = new MyAlertDialog(mContext, R.layout.dialog_layout_for_turnpage, 0);
		// 当前1/1页
		StringBuilder sb = new StringBuilder();
		sb.append("当前").append(currentPage).append("/").append(totalPage).append("页");

		alertDialog.setMyOnClickListener(new MyOnClickListener() {

			@Override
			public void onConfirm() {
				String number = alertDialog.getInputText();

				if (TextUtils.isEmpty(number)) {
					Toast.makeText(mContext, R.string.page_not_exist_input_again, Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					int page = Integer.parseInt(number);
					if (page == currentPage) {
						BaseUtil.HideKeyboard(alertDialog.getEidtText());
						alertDialog.dismiss();
						return;
					}
					if (page > totalPage) {
						Toast.makeText(mContext, R.string.page_not_exist_input_again, Toast.LENGTH_SHORT).show();
					} else {
						currentPage = page;
						nowPage = 0;
						loadData(false, false, false, true);
						BaseUtil.HideKeyboard(alertDialog.getEidtText());
						alertDialog.dismiss();
					}
				} catch (NumberFormatException e) {
					Toast.makeText(mContext, R.string.page_not_exist_input_again, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onClick(View view) {

			}

			@Override
			public void onCancel() {
				alertDialog.dismiss();
				BaseUtil.HideKeyboard(editText);
			}
		});
		alertDialog.show();
		alertDialog.setPageTipsText(sb.toString());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != mSubjectModel) {
			outState.putParcelable("mSubjectModel", mSubjectModel);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, TAG + ",onActivityResult--> requestCode:" + requestCode + ",data:" + data);
		if (requestCode == GroupPostSubjectActivity.REQ_CODE_FOR_GET_PICS && data != null) {
			ArrayList<String> picPaths = data.getStringArrayListExtra("pic_paths");
			boolean isTakePhoto = data.getBooleanExtra("isTakePhoto", false);
			if (null != picPaths && picPaths.size() > 0) {
				// 如果拍照返回保留列表数据直接添加 否则清空列表
				if (isTakePhoto) {
					String path = picPaths.get(0);
					if (TextUtils.isEmpty(path)) {
						return;
					}
				} else {
					upLoadfiles.clear();
				}
				upLoadfiles.addAll(picPaths);
				horizontalListViewAdapter.setDatas(upLoadfiles);
				horizontalListViewAdapter.notifyDataSetChanged();
				picturesListView.setSelection(upLoadfiles.size());
			}
			if (upLoadfiles.size() > 0) {
				picNumLayout.setVisibility(View.VISIBLE);
				picNumTextView.setText(upLoadfiles.size() + "");

				bottomMoreLayout.setVisibility(View.GONE);
				bottomPicLayout.setVisibility(View.VISIBLE);
				bottomFaceLayout.setVisibility(View.GONE);

			}
			StringBuilder sb = new StringBuilder();
			sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size()).append("张可选");
			bottomPicHitTextView.setText(sb.toString());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_rl:
			BaseUtil.HideKeyboard(editText);
			finishActivity();
			break;
		case R.id.operator_rl:
			if (TextUtils.isEmpty(shareUrl)) {
				Toast.makeText(mContext, R.string.have_no_share_url_can_not_share, Toast.LENGTH_SHORT).show();
				return;
			}
			String[] subjectPicUrls = mSubjectModel.subjectPicUrls;
			String imageUrl = "";
			if (null != subjectPicUrls && subjectPicUrls.length > 0) {
				imageUrl = subjectPicUrls[0];
			}
			if (TextUtils.isEmpty(imageUrl)) {
				imageUrl = mSubjectModel.publishUserHeadUrl;
			}

			mSharePopWindow.showShareWindow(shareUrl, mSubjectModel.subjectTitle,
					BaseUtil.clearHtmlTag(mSubjectModel.subjectContent), imageUrl);
			break;
		case R.id.group_subject_detail_send_btn:
			// 登录校验
			if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				mContext.startActivity(intent);
				return;
			}
			// 内容为空校验
			String content = editText.getText().toString();
			if (TextUtils.isEmpty(content)) {
				ToastUtil.showToast(mContext, "内容不能为空");
				return;
			}
			if (content.contains("回复") && content.contains("【") && content.contains("】：")) {
				int index = content.indexOf("：");
				content = content.substring(index + 1, content.length());
				Log.i(TAG, "postReply:" + content);
				if (TextUtils.isEmpty(content)) {
					return;
				}
			}

			if (isPosting) {
				ToastUtil.showToast(this, getResources().getString(R.string.posting_comment_do_it_later));
				return;
			}
			upLoadFile();

			break;
		case R.id.group_subject_detail_edit_tv:

			// 登录校验
			if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				mContext.startActivity(intent);
				return;
			}
			editLayout1.setVisibility(View.GONE);
			editLayout2.setVisibility(View.VISIBLE);
			editText.requestFocus();

			bottomContainerLayout.setVisibility(View.GONE);
			BaseUtil.ShowKeyboard(editText);

			break;
		case R.id.group_subject_detail_face_btn:
			// 点击表情按钮 显示表情layout
			bottomContainerLayout.setVisibility(View.VISIBLE);
			bottomFaceLayout.setVisibility(View.VISIBLE);
			bottomMoreLayout.setVisibility(View.GONE);
			bottomPicLayout.setVisibility(View.GONE);

			BaseUtil.HideKeyboard(editText);

			break;
		case R.id.group_subject_detail_add_btn:
			// 点击+按钮
			bottomContainerLayout.setVisibility(View.VISIBLE);
			bottomFaceLayout.setVisibility(View.GONE);
			bottomMoreLayout.setVisibility(View.VISIBLE);
			bottomPicLayout.setVisibility(View.GONE);

			BaseUtil.HideKeyboard(editText);

			break;
		case R.id.post_more_add_pic:
			// 添加图片
			if (upLoadfiles.size() < 1) {
				jump2GroupPostSubjectActivity();
			} else {
				bottomContainerLayout.setVisibility(View.VISIBLE);
				bottomFaceLayout.setVisibility(View.GONE);
				bottomMoreLayout.setVisibility(View.GONE);
				bottomPicLayout.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.post_more_add_add:
			// @好友
			break;
		case R.id.more_layout_turn_page_tv:// 跳页

			doTurnPageAction();

			break;
		case R.id.more_layout_floor_host_tv:// 楼主

			doFloorHostAction();
			doTitleTextClickAction();

			break;
		case R.id.loading_failed_tips_tv:

			failedLayout.setVisibility(View.GONE);
			mOpenLoadingIcon();
			currentPage = 1;
			nowPage = 0;
			loadData(false, false, false, false);

			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	// TODO 发送条件限制
	private void doSendLimitAction() {

		if (sendCount == 1) {
			PreferencesUtil.putLong(PreferencesUtil.KEY_COMMENT_SEND_TIME + mSubjectModel.subjectId,
					System.currentTimeMillis());
		}
		long firstSendTime = PreferencesUtil.getLong(PreferencesUtil.KEY_COMMENT_SEND_TIME + mSubjectModel.subjectId);
		sendCount++;
		if (sendCount > 5) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - firstSendTime <= intervalTime) {

			}
		}
	}

	private void showProgressDialog() {
		mCustomProgressDialog = new CustomProgressDialog(mContext, "正在回复...");
		mCustomProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
			mCustomProgressDialog.dismiss();
		}
	}

	/**
	 * 回复
	 */
	private void doSendAction() {
		// loadingTipsTv.setText("正在发布...");
		showProgressDialog();
		String content = editText.getText().toString();
		if (TextUtils.isEmpty(content)) {
			return;
		}
		currentPage = totalPage;
		if (content.contains("回复") && content.contains("【") && content.contains("】：")) {
			int index = content.indexOf("：");
			content = content.substring(index + 1, content.length());
			Log.i(TAG, "postReply:" + content);
			if (TextUtils.isEmpty(content)) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(content);
			if (null != uploadFileSuccessUrls && uploadFileSuccessUrls.size() > 0) {
				for (String url : uploadFileSuccessUrls) {
					sb.append(url);
				}
			}
			postReply(sb.toString());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(content);
			if (null != uploadFileSuccessUrls && uploadFileSuccessUrls.size() > 0) {
				for (String url : uploadFileSuccessUrls) {
					sb.append(url);
				}
			}
			postComment(sb.toString());
		}
	}

	private void upLoadFile() {
		myProgress = MyProgressDialog.createLoadingDialog(this);
		myProgress.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 进入线程，表示正在发布，置为true
				isPosting = true;
				// 没有图片直接上传文本内容
				if (upLoadfiles.size() < 1) {
					handler.sendEmptyMessage(SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE);
					return;
				}
				FileUtil.uploadFile(upLoadfiles, uploadFileSuccessUrls, handler);
			}
		}).start();
	}

	// private String appendUploadFileUrl(int successId) {
	// StringBuilder sb = new StringBuilder();
	// sb.append("[attachimg]").append(successId).append("[/attachimg]");
	// return sb.toString();
	// }

	@Override
	protected void handleMessage(Message msg) {
		// 子线程任务完成后回到主线程 如果已被销毁不再进行
		if (myProgress != null) {
			myProgress.cancel();
		}
		if (isFinishing()) {
			return;
		}
		addBtn.setVisibility(View.VISIBLE);
		if (msg.what == SobeyConstants.CODE_FOR_UPLOAD_FILE_FAIL) {
			mLoadingLayout.setVisibility(View.GONE);
			Toast.makeText(mContext, "请检查您的网络稍后再试", Toast.LENGTH_SHORT).show();
			isPosting = false;
		} else if (msg.what == SobeyConstants.CODE_FOR_COMMENT_REPLY_CLICKED) {

			String text = editText.getText().toString();
			GroupCommentModel commentModel = (GroupCommentModel) msg.obj;
			Object obj = editText.getTag();
			if (null != obj && obj instanceof GroupCommentModel) {
				GroupCommentModel lastCommentModel = (GroupCommentModel) obj;
				// 上次的id和本次的id一样
				if (lastCommentModel.commentId.equals(commentModel.commentId) && text.contains("】：")) {
					return;
				}
			}

			editLayout1.setVisibility(View.GONE);
			editLayout2.setVisibility(View.VISIBLE);
			editText.requestFocus();
			bottomContainerLayout.setVisibility(View.GONE);
			editText.setTag(commentModel);
			BaseUtil.ShowKeyboard(editText);

			StringBuilder sb = new StringBuilder();
			sb.append("回复").append(" ").append(commentModel.commentUserName).append("【").append(commentModel.floor)
					.append("楼").append("】").append("：");
			editText.setText(sb);
			editText.setSelection(editText.length());
			addBtn.setVisibility(View.GONE);
		} else if (msg.what == SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE) {
			Bundle bundle = msg.getData();
			// uploadFileSuccessUrls =
			// bundle.getStringArrayList("uploadFileSuccessUrls");
			List<String> upLoadFailedPaths = bundle.getStringArrayList("upLoadFailedPaths");
			if (null != upLoadFailedPaths && upLoadFailedPaths.size() > 0) {
				String tips = getResources().getString(R.string.uploading_failed_size);
				Toast.makeText(mContext, String.format(tips, upLoadFailedPaths.size()), Toast.LENGTH_SHORT).show();
			}
			doSendAction();
		} else if (msg.what == SobeyConstants.CODE_FOR_LOG_STATE_CHANGE) {
			loadData(true, false, false, false);
		} else if (msg.what == 1) {
			mLoadingLayout.setVisibility(View.VISIBLE);
		} else if (msg.what == SobeyConstants.CODE_FOR_UPLOAD_FILE_UPLOADING) {
			String tips = getResources().getString(R.string.uploading_data_tips);
			Bundle bundle = msg.getData();
			int uploadedSize = bundle.getInt("uploadedSize");
			int totalSize = bundle.getInt("totalSize");
			String sFinalTips = String.format(tips, uploadedSize, totalSize);
			loadingTipsTv.setText(sFinalTips);
			loadingTipsTv.invalidate();
		}
	}

	@Override
	public void onBackPressed() {
		if (bottomContainerLayout.getVisibility() == View.VISIBLE) {
			bottomContainerLayout.setVisibility(View.GONE);
			return;
		}
		if (moreLayout.getVisibility() == View.VISIBLE) {
			moreLayout.startAnimation(anim_out);
			return;
		}
		if (mSharePopWindow.isShowing()) {
			mSharePopWindow.hideShareWindow();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterReciever();
	}

	private void jump2GroupPostSubjectActivity() {
		Intent intent = new Intent(mContext, GroupChoosePhotoActivity.class);
		intent.putStringArrayListExtra("choosed_photo_path", upLoadfiles);
		((Activity) mContext).startActivityForResult(intent, GroupPostSubjectActivity.REQ_CODE_FOR_GET_PICS);
	}

	private void postReply(String content) {
		Log.i(TAG, "回复的内容：" + content);
		if (null != editText.getTag() && editText.getTag() instanceof GroupCommentModel) {
			GroupCommentModel commentModel = (GroupCommentModel) editText.getTag();
			Log.i(TAG, "comment floor:" + commentModel.floor);
			GroupRequestMananger.getInstance().postReply(mSubjectModel.subjectId, commentModel.commentId,
					PreferencesUtil.getLoggedUserId(), Base64.encode(content.getBytes()), "", "", "", mContext,
					new RequestResultListner() {

						@Override
						public void onFinish(SobeyType result) {
							dismissProgressDialog();
							mLoadingLayout.setVisibility(View.GONE);
							uploadFileSuccessUrls.clear();
							upLoadfiles.clear();
							picNumLayout.setVisibility(View.GONE);
							// 发布结束
							isPosting = false;

							if (result instanceof SobeyBaseResult) {
								SobeyBaseResult baseResult = (SobeyBaseResult) result;
								if (baseResult.returnCode == SobeyBaseResult.OK) {
									Toast.makeText(mContext, R.string.reply_success, Toast.LENGTH_SHORT).show();
									bottomContainerLayout.setVisibility(View.GONE);
									BaseUtil.HideKeyboard(editText);
									editText.setText("");
									loadData(false, false, true, false);
								} else {
									Toast.makeText(mContext, R.string.reply_failed, Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(mContext, R.string.reply_failed, Toast.LENGTH_SHORT).show();
							}
						}

					});
		}

	}

	private void postComment(String content) {
		Log.i(TAG, "回复的内容：" + content);

		GroupRequestMananger.getInstance().postComment(mSubjectModel.subjectId, mSubjectModel.publishUserId,
				Base64.encode(content.getBytes()), "", mContext, new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						dismissProgressDialog();
						uploadFileSuccessUrls.clear();
						upLoadfiles.clear();
						picNumLayout.setVisibility(View.GONE);
						mLoadingLayout.setVisibility(View.GONE);
						// 发布结束
						isPosting = false;

						if (result instanceof SobeyBaseResult) {
							SobeyBaseResult baseResult = (SobeyBaseResult) result;
							if (baseResult.returnCode == SobeyBaseResult.OK) {
								Toast.makeText(mContext, R.string.post_comment_success, Toast.LENGTH_SHORT).show();
								bottomContainerLayout.setVisibility(View.GONE);
								BaseUtil.HideKeyboard(editText);
								editText.setText("");
								loadData(false, false, true, false);
								// TODO 评论成功
								// int count = adapter.getCount();
								// if (count % pageSize == 0) {
								// totalPage = count / pageSize;
								// } else {
								// totalPage = count / pageSize + 1;
								// }
								// nowPage = totalPage;
								// addFloor();
								// mListView.setPullLoadEnable(false);
							} else if (baseResult.returnCode == 500) {
								Toast.makeText(mContext, "内容含有非法关键字", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mContext, R.string.post_comment_failed, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(mContext, R.string.post_comment_failed, Toast.LENGTH_SHORT).show();
						}
					}
				});

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

	private void RegisterReciever() {

		logStateReceiver = new LogStateChangeReceiver(handler);
		IntentFilter filter = new IntentFilter(SobeyConstants.ACTION_LOG_STATE_CHANGE);
		registerReceiver(logStateReceiver, filter);

	}

	private void unRegisterReciever() {
		if (null != logStateReceiver) {
			unregisterReceiver(logStateReceiver);
		}
	}

	private void doTitleTextClickAction() {
		Log.i(TAG, "title_layuout clicked!!");
		if (moreLayout.getVisibility() == View.VISIBLE) {
			moreLayout.startAnimation(anim_out);

		} else {
			moreContainerLayout.setVisibility(View.VISIBLE);
			moreLayout.startAnimation(anim_in);
			moreLayout.setVisibility(View.VISIBLE);

		}
	}

	private void initAnim() {
		anim_in = AnimationUtils.loadAnimation(mContext, R.anim.top_in);
		anim_out = AnimationUtils.loadAnimation(mContext, R.anim.top_out);
		anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moreLayout.setVisibility(View.GONE);
				moreContainerLayout.setVisibility(View.GONE);
			}
		});
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	private class MyFaceAdapter extends BaseAdapter {
		private Integer[] resIds;
		private int mCurrentPage;

		public MyFaceAdapter(int page) {
			this.mCurrentPage = page;
		}

		public void setData(Integer[] resIds) {
			this.resIds = resIds;
		}

		@Override
		public int getCount() {
			return resIds.length;
		}

		@Override
		public Integer getItem(int position) {
			return resIds[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_face_for_gridview, null);
			ImageView img = (ImageView) view.findViewById(R.id.face_img);
			int resId = getItem(position);
			img.setBackgroundResource(resId);
			img.setOnClickListener(new MyClickListener(mCurrentPage, resId));
			return view;
		}

	}

	private class MyClickListener implements OnClickListener {
		private int mResId;
		private int mCurrentPage;

		public MyClickListener(int currentPage, int resId) {
			this.mCurrentPage = currentPage;
			this.mResId = resId;
		}

		@Override
		public void onClick(View v) {
			String faceStr = "";
			switch (mCurrentPage) {
			case 0:
				faceStr = FaceUtil.defaultEditFaces.get(mResId);
				break;
			case 1:
				faceStr = FaceUtil.coolmonkeyEditFaces.get(mResId);
				break;
			case 2:
				faceStr = FaceUtil.grapemanEditFaces.get(mResId);
				break;

			default:
				break;
			}
			int index = editText.getSelectionStart();
			Editable editable = editText.getText().insert(index, faceStr);
			editText.setText(editable.toString());
			editText.setSelection(index + faceStr.length());
		}

	}

	private class MyCheckedChangeListener implements OnCheckedChangeListener {

		private String mSubjectId;

		public MyCheckedChangeListener(String subjectId) {
			this.mSubjectId = subjectId;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.group_subject_detail_like_btn:
				// 登录校验
				if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}
				if (isChecked) {
					GroupRequestMananger.getInstance().likeSubject(mSubjectId, 1, mContext, new RequestResultListner() {

						@Override
						public void onFinish(SobeyType result) {
							if (result instanceof SobeyBaseResult) {
								SobeyBaseResult baseResult = (SobeyBaseResult) result;
								if (baseResult.returnCode == SobeyBaseResult.OK) {
									// TODO 点赞成功后 修改界面
									// Toast.makeText(GroupSubjectActivity.this,
									// "hehe", Toast.LENGTH_SHORT).show
									String uid = PreferencesUtil.getLoggedUserId();
									String userName = PreferencesUtil.getLoggedUserName();
									mAdapter.addLikedUser(uid, userName, "");
									praiseAnimAction("+1");
								}
							}

						}

					});
				} else {
					GroupRequestMananger.getInstance().likeSubject(mSubjectId, 0, mContext, new RequestResultListner() {

						@Override
						public void onFinish(SobeyType result) {
							if (result instanceof SobeyBaseResult) {
								SobeyBaseResult baseResult = (SobeyBaseResult) result;
								if (baseResult.returnCode == SobeyBaseResult.OK) {
									// 点赞成功后 修改界面
									String uid = PreferencesUtil.getLoggedUserId();
									String userName = PreferencesUtil.getLoggedUserName();
									mAdapter.delLikedUser(uid, userName, "");
									praiseAnimAction("-1");
								}
							}

						}

					});
				}

				break;
			case R.id.more_layout_collect_cb:
				// 登录校验
				if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}
				// TODO 收藏
				doCollectAction(isChecked);
				doTitleTextClickAction();
				break;
			default:
				break;
			}

		}

	}

	public void praiseAnimAction(String tag) {
		Animation anim_praise = AnimationUtils.loadAnimation(mContext, R.anim.praise_add);
		addPraise.setText(tag);
		addPraise.setVisibility(View.VISIBLE);
		addPraise.setAnimation(anim_praise);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				addPraise.setVisibility(View.GONE);
			}
		}, 1000);
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return faceGridViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GridView gridView = faceGridViews.get(position);
			container.addView(gridView);
			return gridView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}
