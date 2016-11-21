package com.sobey.cloud.webtv.personal;

import java.util.ArrayList;
import java.util.List;

import com.dylan.uiparts.listview.DragListView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.ChatAdapter;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper.OnItemDeleteListener;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.LetterModel;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.views.group.BaseActivity4Group;
import com.sobey.cloud.webtv.views.group.GroupChoosePhotoActivity;
import com.sobey.cloud.webtv.views.group.GroupPostSubjectActivity;
import com.sobey.cloud.webtv.widgets.HorizontalListView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostPrivtateLetterActivity extends BaseActivity4Group implements OnClickListener {
	@GinInjectView(id = R.id.group_subject_detail_listView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.title)
	TextView titleNameTv;
	@GinInjectView(id = R.id.title_right)
	Button titleRightBtn;
	@GinInjectView(id = R.id.group_subject_detail_like_btn)
	CheckBox likeCb;
	@GinInjectView(id = R.id.group_subject_detail_send_btn)
	Button sendBtn;
	@GinInjectView(id = R.id.group_subject_detail_add_btn)
	Button addBtn;
	@GinInjectView(id = R.id.group_subject_detail_face_btn)
	Button faceBtn;

	@GinInjectView(id = R.id.group_subject_detail_edit)
	EditText editText;
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
	private Animation anim_in;
	private Animation anim_out;
	private SharePopWindow mSharePopWindow;
	private boolean hasLoadSuccessed = false;

	private List<ImageButton> indicatorImg = new ArrayList<ImageButton>();
	private ArrayList<String> upLoadfiles = new ArrayList<String>();
	private List<GridView> faceGridViews = new ArrayList<GridView>();
	private ArrayList<String> uploadFileSuccessUrls = new ArrayList<String>();
	private GroupPostSubjectHorizontalListViewAdatper horizontalListViewAdapter;

	private int totalPage = 1;
	private int pageSize = 20;
	private int currentPage = 1;
	private Drawable collectPress;
	private Drawable collectNor;
	private String filter;
	private Drawable personNor;
	private Drawable personPress;
	private String shareUrl = "";

	private GroupUserModel mUserModel;
	private ChatAdapter adapter;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != mUserModel) {
			outState.putParcelable("groupUserModel", mUserModel);
		}
	}

	@Override
	public int getContentView() {
		return R.layout.activity_post_private_letter_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);

		mUserModel = this.getIntent().getParcelableExtra("groupUserModel");
		if (null != savedInstanceState) {
			mUserModel = savedInstanceState.getParcelable("groupUserModel");
		}

		setUpData();
	}

	public void setUpData() {
		titleRightBtn.setVisibility(View.GONE);
		if (null != mUserModel) {
			titleNameTv.setText(mUserModel.userName);
		}

		mListView.setPullLoadEnable(false);
		adapter = new ChatAdapter(this);
		adapter.generateDatas(mUserModel.uid, mUserModel.userHeadUrl);
		mListView.setAdapter(adapter);
		mListView.setSelection(adapter.getCount());
		faceBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		addPicImg.setOnClickListener(this);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					BaseUtil.HideKeyboard(editText);
					break;

				default:
					break;
				}
				return false;
			}
		});
		initFaceViews();
		initPicViews();

		editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					bottomContainerLayout.setVisibility(View.GONE);
					break;

				default:
					break;
				}
				return false;
			}
		});
		// 私信不支持发图片
		addBtn.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.group_subject_detail_send_btn:
			// 内容为空校验
			String content = editText.getText().toString();
			if (TextUtils.isEmpty(content)) {
				Toast.makeText(this, "您尚未输入内容！", Toast.LENGTH_SHORT).show();
				return;
			}
			final String letterId = "54";
			LetterModel letterModel = new LetterModel();
			letterModel.letterPublishUserId = PreferencesUtil.getLoggedUserId();
			letterModel.letterContent = content;
			letterModel.letterId = letterId;
			letterModel.sendSuccess = 0;
			adapter.addData(letterModel);
			adapter.notifyDataSetChanged();
			mListView.setSelection(adapter.getCount());
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(10000);
						Message msg = new Message();
						msg.what = 3333;
						msg.obj = letterId;
						mHandler.sendMessage(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			editText.setText("");

			// upLoadFile();

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
		default:
			break;
		}
	}

	private void upLoadFile() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 没有图片直接上传文本内容
				if (upLoadfiles.size() < 1) {
					mHandler.sendEmptyMessage(SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE);
					return;
				}
				FileUtil.uploadFile(upLoadfiles, uploadFileSuccessUrls, mHandler);
			}
		}).start();
	}

	private void jump2GroupPostSubjectActivity() {
		Intent intent = new Intent(this, GroupChoosePhotoActivity.class);
		intent.putStringArrayListExtra("choosed_photo_path", upLoadfiles);
		startActivityForResult(intent, GroupPostSubjectActivity.REQ_CODE_FOR_GET_PICS);
	}

	@SuppressWarnings("deprecation")
	private void initFaceViews() {

		for (int i = 0; i < 3; i++) {
			ImageButton img = new ImageButton(this);
			LayoutParams params = new LayoutParams(BaseUtil.Dp2Px(this, 5), BaseUtil.Dp2Px(this, 5));
			params.leftMargin = 5;
			params.rightMargin = 5;
			img.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_indicator));
			img.setLayoutParams(params);
			indicatorLayout.addView(img);
			indicatorImg.add(img);
			if (i == 0) {
				img.setEnabled(false);
			}
			GridView gridView = new GridView(this);
			gridView.setNumColumns(6);
			gridView.setHorizontalSpacing(5);
			gridView.setVerticalSpacing(BaseUtil.Dp2Px(this, 15));
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
		horizontalListViewAdapter = new GroupPostSubjectHorizontalListViewAdatper(this);

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

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == 3333) {
			String letterId = (String) msg.obj;
			if (!TextUtils.isEmpty(letterId)) {
				List<LetterModel> letterModels = adapter.getData();
				for (LetterModel letterModel : letterModels) {
					if (letterModel.letterId.equals(letterId)) {
						letterModel.sendSuccess = -1;
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
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
			View view = LayoutInflater.from(PostPrivtateLetterActivity.this).inflate(R.layout.item_face_for_gridview,
					null);
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
