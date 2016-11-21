package com.sobey.cloud.webtv.views.group;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper;
import com.sobey.cloud.webtv.adapter.GroupPostSubjectHorizontalListViewAdatper.OnItemDeleteListener;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.SobeyBaseResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.ui.MyProgressDialog;
import com.sobey.cloud.webtv.utils.Base64;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;
import com.sobey.cloud.webtv.widgets.HorizontalListView;
import com.sobey.cloud.webtv.widgets.SmiliesEditText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder")
public class GroupPostSubjectActivity extends BaseActivity4Group implements OnClickListener {

	@GinInjectView(id = R.id.post_subject_bottom_content_layout)
	LinearLayout postSubjectBottomLayout;
	@GinInjectView(id = R.id.post_subject_pictures_num_layout)
	LinearLayout numLayout;
	@GinInjectView(id = R.id.post_subject_bottom_pic_layout)
	View picLayout;
	@GinInjectView(id = R.id.post_comment_bottom_pictures_horizontallistview)
	HorizontalListView picturesListView;
	@GinInjectView(id = R.id.post_comment_bottom_pictures_textview)
	TextView bottomPicHitTextView;
	@GinInjectView(id = R.id.post_subject_content_edit)
	SmiliesEditText contentEdit;
	@GinInjectView(id = R.id.post_subject_faces_btn)
	Button facesBtn;
	@GinInjectView(id = R.id.post_subject_link_btn)
	Button linkBtn;
	@GinInjectView(id = R.id.post_subject_pictures_btn)
	Button picturesBtn;
	@GinInjectView(id = R.id.post_subject_title_edit)
	EditText titleEdit;
	@GinInjectView(id = R.id.post_subject_pictures_num_tv)
	TextView numTv;
	@GinInjectView(id = R.id.activity_layout)
	RelativeLayout activityLayout;
	@GinInjectView(id = R.id.face_indicator_layout)
	LinearLayout indicatorLayout;
	@GinInjectView(id = R.id.face_viewpager)
	ViewPager faceViewPager;
	@GinInjectView(id = R.id.post_subject_bottom_faces_layout)
	View facesLayout;

	@GinInjectView(id = R.id.mLoadinglayout)
	View mLoadingLayout;
	@GinInjectView(id = R.id.loading_chinese)
	TextView loadingTipsTv;

	MyProgressDialog myProgress;

	public static int REQ_CODE_FOR_GET_PICS = 10010;
	private ArrayList<String> upLoadfiles = new ArrayList<String>();
	private GroupPostSubjectHorizontalListViewAdatper adapter;
	private GroupModel mGroupModel;
	private List<ImageButton> indicatorImg = new ArrayList<ImageButton>();
	private List<GridView> faceGridViews = new ArrayList<GridView>();
	private ArrayList<String> uploadFileSuccessUrls = new ArrayList<String>();
	private boolean isPost;

	/**
	 * 进度框
	 */
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		return R.layout.activity_group_post_subject_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mGroupModel = this.getIntent().getParcelableExtra("mGroupModel");
		setUpData();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if(null != savedInstanceState){
		// photoModels =
		// savedInstanceState.getParcelableArrayList("photoModels");
		// }
	}

	public void setUpData() {
		adapter = new GroupPostSubjectHorizontalListViewAdatper(mContext);

		if (upLoadfiles.size() < 1) {
			adapter.setData(adapter.generateBaseData());
			adapter.notifyDataSetChanged();
			picturesListView.setSelection(1);
		}
		picturesListView.setAdapter(adapter);
		adapter.setOnItemDeletedListener(new OnItemDeleteListener() {

			@Override
			public void itemDeleted(String path) {
				if (upLoadfiles.contains(path)) {
					upLoadfiles.remove(path);
					if (upLoadfiles.size() < 1) {
						adapter.setData(adapter.generateBaseData());
						adapter.notifyDataSetChanged();
						picturesListView.setSelection(1);
						numLayout.setVisibility(View.GONE);

						StringBuilder sb = new StringBuilder();
						sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size())
								.append("张可选");
						bottomPicHitTextView.setText(sb.toString());
						return;
					}
					adapter.setDatas(upLoadfiles);
					adapter.notifyDataSetChanged();
					picturesListView.setSelection(upLoadfiles.size());

					numLayout.setVisibility(View.VISIBLE);
					numTv.setText(upLoadfiles.size() + "");
					StringBuilder sb = new StringBuilder();
					sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size())
							.append("张可选");
					bottomPicHitTextView.setText(sb.toString());
				}
			}
		});

		picturesBtn.setOnClickListener(this);
		linkBtn.setOnClickListener(this);
		facesBtn.setOnClickListener(this);
		// titleEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		// postSubjectBottomLayout.setVisibility(View.GONE);
		// facesBtn.setEnabled(false);
		// picturesBtn.setEnabled(false);
		// linkBtn.setEnabled(false);
		//
		// //当点击标题后发图 发表情 @好友置为不可用状态
		// facesBtn.setBackgroundResource(R.drawable.btn_pb_add_expressions_d);
		// picturesBtn.setBackgroundResource(R.drawable.btn_pb_add_pic_d);
		// linkBtn.setBackgroundResource(R.drawable.btn_pb_add_at_d);
		// }
		// }
		// });
		titleEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					postSubjectBottomLayout.setVisibility(View.GONE);
					facesBtn.setEnabled(false);
					picturesBtn.setEnabled(false);
					linkBtn.setEnabled(false);

					// 当点击标题后发图 发表情 @好友置为不可用状态
					facesBtn.setBackgroundResource(R.drawable.btn_pb_add_expressions_d);
					picturesBtn.setBackgroundResource(R.drawable.btn_pb_add_pic_d);
					linkBtn.setBackgroundResource(R.drawable.btn_pb_add_at_d);
					break;

				default:
					break;
				}
				return false;
			}
		});
		// contentEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		// postSubjectBottomLayout.setVisibility(View.GONE);
		// facesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_faces);
		// picturesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_picture);
		// linkBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_link);
		//
		// facesBtn.setEnabled(true);
		// picturesBtn.setEnabled(true);
		// linkBtn.setEnabled(true);
		// }
		// }
		// });
		contentEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					postSubjectBottomLayout.setVisibility(View.GONE);
					facesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_faces);
					picturesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_picture);
					linkBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_link);

					facesBtn.setEnabled(true);
					picturesBtn.setEnabled(true);
					linkBtn.setEnabled(true);
					break;

				default:
					break;
				}
				return false;
			}
		});

		initFaceViews();
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

	// 隐藏虚拟键盘
	private static void HideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

		}
	}

	// 显示虚拟键盘
	@SuppressWarnings("unused")
	private static void ShowKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.post_subject_pictures_btn:

			HideKeyboard(contentEdit);
			changeListViewState();

			break;
		case R.id.post_subject_faces_btn:

			HideKeyboard(contentEdit);
			changeGridViewState();

			break;
		case R.id.post_subject_link_btn:

			HideKeyboard(contentEdit);

			break;
		case R.id.back_rl:
			finishActivity();
			break;
		case R.id.title_right:

			postSubject();

			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (postSubjectBottomLayout.getVisibility() == View.VISIBLE) {
			postSubjectBottomLayout.setVisibility(View.GONE);

			postSubjectBottomLayout.setVisibility(View.GONE);
			facesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_faces);
			picturesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_picture);
			linkBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_link);

			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, TAG + ",onActivityResult--> requestCode:" + requestCode + ",data:" + data);
		if (requestCode == REQ_CODE_FOR_GET_PICS && data != null) {
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
				adapter.setDatas(upLoadfiles);
				adapter.notifyDataSetChanged();
				picturesListView.setSelection(upLoadfiles.size());
			}
			if (upLoadfiles.size() > 0) {
				numLayout.setVisibility(View.VISIBLE);
				numTv.setText(upLoadfiles.size() + "");
			}
			StringBuilder sb = new StringBuilder();
			sb.append("已选").append(upLoadfiles.size()).append("张，还剩").append(10 - upLoadfiles.size()).append("张可选");
			bottomPicHitTextView.setText(sb.toString());
		}
	}

	private void changeListViewState() {

		postSubjectBottomLayout.setVisibility(View.VISIBLE);
		postSubjectBottomLayout.requestFocus();
		picLayout.setVisibility(View.VISIBLE);
		facesLayout.setVisibility(View.GONE);
		picturesBtn.setBackgroundResource(R.drawable.btn_pb_add_pic_s);
		facesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_faces);
	}

	private void changeGridViewState() {

		postSubjectBottomLayout.setVisibility(View.VISIBLE);
		postSubjectBottomLayout.requestFocus();
		picLayout.setVisibility(View.GONE);
		facesLayout.setVisibility(View.VISIBLE);
		facesBtn.setBackgroundResource(R.drawable.btn_pb_add_expressions_s);
		picturesBtn.setBackgroundResource(R.drawable.selector_btn_post_subject_picture);
	}

	private void upLoadFile() {
		// myProgress = MyProgressDialog.createLoadingDialog(this);
		// myProgress.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (upLoadfiles.size() < 1) {
					handler.sendEmptyMessage(999);
					handler.sendEmptyMessage(SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE);
					return;
				}
				FileUtil.uploadFile(upLoadfiles, uploadFileSuccessUrls, handler);
			}
		}).start();
	}

	// 发布主题
	private void postSubject() {
		// String title = titleEdit.getText().toString().trim();
		// String content =
		// parseSendContent(contentEdit.getText().toString().trim());
		// if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
		// Toast.makeText(mContext, "您尚未输入标题或内容", Toast.LENGTH_SHORT).show();
		// return;
		// }
		// if (isPost) {
		// return;
		// }
		// isPost = true;
		// upLoadFile();

		String title = titleEdit.getText().toString().trim();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(mContext, "您尚未输入标题", Toast.LENGTH_SHORT).show();
			return;
		}
		if (isPost) {
			return;
		}
		isPost = true;

		upLoadFile();
	}

	// TODO
	private void doPostSubject() {

		if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
			Toast.makeText(mContext, "您还未登录", Toast.LENGTH_SHORT).show();
			isPost = false;
			return;
		}

		String title = titleEdit.getText().toString();
		String content = parseSendContent(contentEdit.getText().toString());
		StringBuilder sb = new StringBuilder();
		sb.append(content);
		if (null != uploadFileSuccessUrls && uploadFileSuccessUrls.size() > 0) {
			for (String url : uploadFileSuccessUrls) {
				sb.append(url);
			}
		}

		GroupRequestMananger.getInstance().postSubject(mGroupModel.groupId, title,
				Base64.encode(sb.toString().getBytes()), "", "", "", this, new RequestResultListner() {

					@Override
					public void onFinish(SobeyType result) {
						dismissProgressDialog();
						Log.i(TAG, "result:" + result);
						if (result instanceof SobeyBaseResult) {
							SobeyBaseResult sobeyBaseResult = (SobeyBaseResult) result;
							if (sobeyBaseResult.returnCode == SobeyBaseResult.OK) {
								Toast.makeText(mContext, "发布成功！", Toast.LENGTH_SHORT).show();

								Intent intent = new Intent();
								intent.setAction(SobeyConstants.ACTION_POST_SUBJECT);
								sendBroadcast(intent);
								finishActivity();
								// isPost = false;
							} else if (sobeyBaseResult.returnCode == 500) {
								Toast.makeText(mContext, "含有非法关键字", Toast.LENGTH_SHORT).show();
								isPost = false;
							} else {
								Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
								isPost = false;
							}
						} else {
							Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
							isPost = false;
						}
						mLoadingLayout.setVisibility(View.GONE);
					}
				});

	}

	// private String appendUploadFileUrl(int successId){
	// StringBuilder sb = new StringBuilder();
	// sb.append("[attachimg]").append(successId).append("[/attachimg]");
	// return sb.toString();
	// }
	private String parseSendContent(String content) {
		Pattern pattern = null;
		pattern = Pattern.compile("\\[(\\S+?)\\]");
		Matcher matcher = pattern.matcher(content);
		int id = 0;
		String newChar = "";
		while (matcher.find()) {
			String str = matcher.group(1);
			Log.i(TAG, "str:" + str);
			if (str.contains("default")) {
				// 根据表情名找id
				id = FaceUtil.defaultFaces.get(str.replace("default_", ""));
				// 根据id找表情特殊符
				newChar = FaceUtil.defaultEditFaces.get(id);
			} else if (str.contains("coolmonkey")) {
				id = FaceUtil.coolmonkeyFaces.get(str.replace("coolmonkey_", ""));
				newChar = FaceUtil.coolmonkeyEditFaces.get(id);
			} else if (str.contains("grapeman")) {
				id = FaceUtil.grapemanFaces.get(str.replace("grapeman_", ""));
				newChar = FaceUtil.grapemanEditFaces.get(id);
			} else {
				// 如果没有表情内容文本不往下执行
				continue;
			}
			String oldChar = "[" + str + "]";
			content = content.replace(oldChar, newChar);
		}
		Log.i(TAG, "要发送的内容为：" + content);
		return content;
	}

	@Override
	protected void handleMessage(Message msg) {
		super.handleMessage(msg);
		// if (myProgress != null) {
		// myProgress.cancel();
		// }
		if (msg.what == SobeyConstants.CODE_FOR_UPLOAD_FILE_DONE) {
			Bundle bundle = msg.getData();
			if (null != bundle.getStringArrayList("uploadFileSuccessUrls")) {
				uploadFileSuccessUrls = bundle.getStringArrayList("uploadFileSuccessUrls");
			}
			if (null != bundle.getStringArrayList("upLoadFailedPaths")) {
				List<String> upLoadFailedPaths = bundle.getStringArrayList("upLoadFailedPaths");
				if (upLoadFailedPaths.size() > 0) {
					String tips = getResources().getString(R.string.uploading_failed_size);
					Toast.makeText(mContext, String.format(tips, upLoadFailedPaths.size()), Toast.LENGTH_SHORT).show();
				}
			}

			loadingTipsTv.setText(R.string.uploading_data);
			doPostSubject();
		} else if (msg.what == 1) {
			mLoadingLayout.setVisibility(View.VISIBLE);
		} else if (msg.what == SobeyConstants.CODE_FOR_UPLOAD_FILE_UPLOADING) {
			String tips = getResources().getString(R.string.uploading_data_tips);
			Bundle bundle = msg.getData();
			int uploadedSize = bundle.getInt("uploadedSize");
			int totalSize = bundle.getInt("totalSize");
			String sFinalTips = String.format(tips, uploadedSize, totalSize);
			loadingTipsTv.setText(sFinalTips);
		} else if (msg.what == 999) {
			showProgressDialog("正在发布帖子...");
		}
	}

	@SuppressLint("InflateParams")
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
			String faceShowStr = "";
			String faceType = "";
			switch (mCurrentPage) {
			case 0:
				faceShowStr = FaceUtil.defaultAppendFaces.get(mResId);
				faceType = "default";
				break;
			case 1:
				faceShowStr = FaceUtil.coolmonkeyAppendFaces.get(mResId);
				faceType = "coolmonkey";
				break;
			case 2:
				faceShowStr = FaceUtil.grapemanAppendFaces.get(mResId);
				faceType = "grapeman";
				break;

			default:
				break;
			}
			contentEdit.insertIcon(faceType + "_" + faceShowStr);
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

	/**
	 * 显示进度框
	 * 
	 * @param message
	 */
	private void showProgressDialog(String message) {
		mCustomProgressDialog = new CustomProgressDialog(this, message);
		mCustomProgressDialog.show();
	}

	/**
	 * 关闭进度框
	 * 
	 * @param message
	 */
	private void dismissProgressDialog() {
		if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
			mCustomProgressDialog.dismiss();
		}
	}
}
