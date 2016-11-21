package com.sobey.cloud.webtv.broke;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.DateParse;
import com.dylan.common.utils.Random;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.lib.mediachooser.MediaChooser;
import com.lib.mediachooser.activity.BucketHomeFragmentActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.broke.util.BrokeCaptureAdapter;
import com.sobey.cloud.webtv.broke.util.BrokeCaptureAdapter.OnDeleteIconClickListener;
import com.sobey.cloud.webtv.broke.util.BrokeCaptureGridView;
import com.sobey.cloud.webtv.broke.util.BrokeTaskUploadControl;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CacheDataBrokeTask;
import com.sobey.cloud.webtv.senum.BrokeTaskStatus;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PhonePopWindow;
import com.sobey.cloud.webtv.utils.PhonePopWindow.PhonePopWindowCloseListener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BrokeCaptureActivity extends BaseActivity {
	private static final int MAX_IMAGE_NUM = 10;
	private static final int MAX_VIDEO_NUM = 2;
	private static final int MAX_COUNT = 150;

	private ArrayList<String> mVideoFileList;
	private ArrayList<String> mImageFileList;
	private BrokeCaptureAdapter mAdapter;
	private LocationClient mLocClient;
	private MyLocationListener mMyLocationListener;
	private String mUserName;
	private String mAutoLocation;
	private PhonePopWindow mPhonePopWindow;

	private ArrayList<String> mCatalogNameList = new ArrayList<String>();
	private ArrayList<String> mCatalogIdList = new ArrayList<String>();
	private int mCatalogSelectedIndex = -1;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderBackBtn)
	ImageButton mBrokeCaptureHeaderBackBtn;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderBackBtn)
	ImageButton mBrokeCaptureHeaderOKBtn;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderBackBtn)
	ImageButton mBrokeCaptureFooterCameraBtn;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderBackBtn)
	ImageButton mBrokeCaptureFooterVideoBtn;

	@GinInjectView(id = R.id.mBrokeCaptureFooterLocalBtn)
	ImageButton mBrokeCaptureFooterLocalBtn;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderMoreLayout)
	LinearLayout mBrokeCaptureHeaderMoreLayout;

	@GinInjectView(id = R.id.mBrokeCaptureHeaderTitle)
	TextView mBrokeCaptureHeaderTitle;

	@GinInjectView(id = R.id.mBrokeCaptureTitleEditText)
	EditText mBrokeCaptureTitleEditText;

	@GinInjectView(id = R.id.mBrokeCaptureLocationLayout)
	LinearLayout mBrokeCaptureLocationLayout;

	@GinInjectView(id = R.id.mBrokeCaptureLocationImage)
	ImageView mBrokeCaptureLocationImage;

	@GinInjectView(id = R.id.mBrokeCaptureLocationEditText)
	EditText mBrokeCaptureLocationEditText;

	@GinInjectView(id = R.id.mBrokeCaptureGridView)
	BrokeCaptureGridView mBrokeCaptureGridView;

	@GinInjectView(id = R.id.mSelectLayout)
	RelativeLayout mSelectLayout;

	@GinInjectView(id = R.id.mSelectListLayout)
	LinearLayout mSelectListLayout;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	@Override
	public int getContentView() {
		return R.layout.activity_broke_capture;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		mOpenLoadingIcon();
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(BrokeCaptureActivity.this, com.sobey.cloud.webtv.views.user.LoginActivity.class));
			finish();
		}
		mUserName = userInfo.getString("id", "");
		loadCatalog();
		initHeader();
		initFooter();
		initMediaChooser();
		initGridView();
		initGPS();
		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_broke_capture_detail_layout);
		mPhonePopWindow = new PhonePopWindow(this, mActivityLayoutView, new PhonePopWindowCloseListener() {
			@Override
			public void onClose(boolean buttonFlag, String number) {
				headerOkClick(number);
			}
		});

		mBrokeCaptureTitleEditText.addTextChangedListener(mTextWatcher);
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mBrokeCaptureTitleEditText.getSelectionStart();
			editEnd = mBrokeCaptureTitleEditText.getSelectionEnd();
			// 先去掉监听器，否则会出现栈溢出
			mBrokeCaptureTitleEditText.removeTextChangedListener(mTextWatcher);
			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			if (calculateLength(s.toString()) > MAX_COUNT) {
				Toast.makeText(BrokeCaptureActivity.this, "已达最大字数限制", Toast.LENGTH_SHORT).show();
			}
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			mBrokeCaptureTitleEditText.setSelection(editStart);
			// 恢复监听器
			mBrokeCaptureTitleEditText.addTextChangedListener(mTextWatcher);
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	};

	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	private void loadCatalog() {
		News.getBrokeCatalogList(MConfig.BrokeTypeId, 0, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					for (int i = 0; i < result.length(); i++) {
						mCatalogIdList.add(result.optJSONObject(i).getString("id"));
						mCatalogNameList.add(result.optJSONObject(i).getString("name"));
					}
					initSelectLayout();
					mCloseLoadingIcon();
				} catch (JSONException e) {
					finish();
				}
			}

			@Override
			public void onNG(String reason) {
				finish();
			}

			@Override
			public void onCancel() {
				finish();
			}
		});
	}

	private void initGPS() {
		mLocClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(3000);
		option.setNeedDeviceDirect(false);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.requestPoi();
		}
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			setLocationInfo(location);
		}

		@Override
		public void onReceivePoi(BDLocation location) {
			setLocationInfo(location);
		}
	}

	private void setLocationInfo(BDLocation location) {
		if (location.getLocType() == 161 && !TextUtils.isEmpty(location.getAddrStr())) {
			if (TextUtils.isEmpty(mBrokeCaptureLocationEditText.getText().toString())) {
				mBrokeCaptureLocationEditText.setHint(location.getAddrStr());
				mAutoLocation = location.getAddrStr();
				mBrokeCaptureLocationImage.setImageResource(R.drawable.broke_capture_location_icon);
				mLocClient.stop();
				mBrokeCaptureLocationEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						if (arg1) {
							mBrokeCaptureLocationEditText.setHint("插入位置");
						} else {
							if (TextUtils.isEmpty(mBrokeCaptureLocationEditText.getText().toString())) {
								mBrokeCaptureLocationEditText.setHint(mAutoLocation);
							}
						}
					}
				});
			}
		}
	}

	private void initHeader() {
		mBrokeCaptureHeaderBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
		mBrokeCaptureHeaderOKBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = mBrokeCaptureTitleEditText.getText().toString();
				String location = mBrokeCaptureLocationEditText.getText().toString();
				if (TextUtils.isEmpty(location)) {
					location = TextUtils.isEmpty(mAutoLocation) ? "" : mAutoLocation;
				}
				if (TextUtils.isEmpty(title)) {
					Toast.makeText(BrokeCaptureActivity.this, "请输入新闻内容", Toast.LENGTH_SHORT).show();
					mBrokeCaptureTitleEditText.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mBrokeCaptureTitleEditText, 0);
					return;
				}
				if (mImageFileList.size() < 1 && mVideoFileList.size() < 1) {
					Toast.makeText(BrokeCaptureActivity.this, "请拍摄或选择要上传的视频、图片", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mCatalogSelectedIndex < 0) {
					Toast.makeText(BrokeCaptureActivity.this, "请选择上传内容所属的类目", Toast.LENGTH_SHORT).show();
					mSelectLayout.setVisibility(View.VISIBLE);
					return;
				}
				mPhonePopWindow.showPhoneWindow();
			}
		});
		mBrokeCaptureHeaderMoreLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectLayout.getVisibility() == View.VISIBLE) {
					mSelectLayout.setVisibility(View.GONE);
				} else {
					mSelectLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@SuppressLint("DefaultLocale")
	private void headerOkClick(String number) {
		try {
			String title = mBrokeCaptureTitleEditText.getText().toString();
			String location = mBrokeCaptureLocationEditText.getText().toString();
			if (TextUtils.isEmpty(location)) {
				location = TextUtils.isEmpty(mAutoLocation) ? "" : mAutoLocation;
			}
			if (TextUtils.isEmpty(title)) {
				Toast.makeText(BrokeCaptureActivity.this, "请输入新闻内容", Toast.LENGTH_SHORT).show();
				mBrokeCaptureTitleEditText.requestFocus();
				InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mBrokeCaptureTitleEditText, 0);
				return;
			}
			if (mImageFileList.size() < 1 && mVideoFileList.size() < 1) {
				Toast.makeText(BrokeCaptureActivity.this, "请拍摄或选择要上传的视频、图片", Toast.LENGTH_SHORT).show();
				return;
			}
			if (mCatalogSelectedIndex < 0) {
				Toast.makeText(BrokeCaptureActivity.this, "请选择上传内容所属的类目", Toast.LENGTH_SHORT).show();
				mSelectLayout.setVisibility(View.VISIBLE);
				return;
			}

			CacheDataBrokeTask brokeTask = new CacheDataBrokeTask();
			ArrayList<JSONObject> filePathList = new ArrayList<JSONObject>();
			String headIcon = "";
			long time = System.currentTimeMillis();
			if (mImageFileList.size() > 0) {
				for (int i = 0; i < mImageFileList.size(); i++) {
					String fileName = mImageFileList.get(i);
					JSONObject filePath = new JSONObject();
					String guid = Random.nextString(32);
					filePath.put("path", fileName);
					filePath.put("url",
							MConfig.mFtpRemoteImagePath + DateParse.getNowDate("yyyy") + "/"
									+ DateParse.getNowDate("MM") + "/" + DateParse.getNowDate("dd") + "/" + (time + i)
									+ fileName.substring(fileName.lastIndexOf(".")));
					filePath.put("guid", guid);
					filePath.put("type", "image");
					filePathList.add(filePath);
					if (TextUtils.isEmpty(headIcon)) {
						headIcon = mImageFileList.get(i);
					}
				}
			}
			if (mVideoFileList.size() > 0) {
				for (int i = 0; i < mVideoFileList.size(); i++) {
					String fileName = mVideoFileList.get(i);
					MediaPlayer mMediaPlayer = new MediaPlayer();
					mMediaPlayer.setDataSource(fileName);
					int duration = mMediaPlayer.getDuration();
					JSONObject filePath = new JSONObject();
					String guid = Random.nextString(32).toLowerCase();
					filePath.put("path", fileName);
					filePath.put("url",
							MConfig.mFtpRemoteVideoPath + DateParse.getNowDate("yyyy") + "/"
									+ DateParse.getNowDate("MM") + "/" + DateParse.getNowDate("dd") + "/" + (time + i)
									+ fileName.substring(fileName.lastIndexOf(".")));
					filePath.put("guid", guid);
					filePath.put("type", "video");
					filePath.put("duration", duration);
					filePathList.add(filePath);
					if (TextUtils.isEmpty(headIcon)) {
						headIcon = mVideoFileList.get(i);
					}
				}
			}
			String xmlFilePath = createXMLFile(time, filePathList, title, mCatalogIdList.get(mCatalogSelectedIndex));
			JSONObject filePath = new JSONObject();
			filePath.put("path", xmlFilePath);
			filePath.put("url", "/" + DateParse.getNowDate("yyyy") + "/" + DateParse.getNowDate("MM") + "/"
					+ DateParse.getNowDate("dd") + "/" + time + ".xml");
			filePath.put("type", "xml");
			filePath.put("guid", "");
			filePathList.add(filePath);
			Log.i("dzy", xmlFilePath + ";" + mUserName + ";" + mCatalogIdList.get(mCatalogSelectedIndex));
			brokeTask.setUsername(mUserName);
			brokeTask.setIndex(String.valueOf(getIntent().getIntExtra("index", 0)));
			brokeTask.setId(String.valueOf(System.currentTimeMillis()));
			brokeTask.setCatalogId(mCatalogIdList.get(mCatalogSelectedIndex));
			brokeTask.setLogo(headIcon);
			brokeTask.setTitle(title);
			brokeTask.setPhone(number);
			brokeTask.setLocation(location);
			brokeTask.setImagecount(mImageFileList.size());
			brokeTask.setVideocount(mVideoFileList.size());
			brokeTask.setFilePathList(filePathList);
			brokeTask.setStatus(BrokeTaskStatus.WAITING);
			brokeTask.setProgress(0);
			BrokeTaskUploadControl.saveUploadTask(BrokeCaptureActivity.this, brokeTask);
			BrokeTaskUploadControl.startUploadService(BrokeCaptureActivity.this, brokeTask);

			View view = getWindow().peekDecorView();
			if (view != null) {
				InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					SharedPreferences settings = BrokeCaptureActivity.this.getSharedPreferences("lauch_mode", 0);
					Editor editor = settings.edit();
					editor.putBoolean("broke_task_home", true);
					editor.commit();
					BrokeCaptureActivity.this.finish();
				}
			}, 200);
		} catch (Exception e) {
		}
	}

	private void initFooter() {
		mBrokeCaptureFooterCameraBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BrokeCaptureActivity.this, BrokeCameraTakeActivity.class);
				intent.putExtra("camera_take_mode", "picture");
				BrokeCaptureActivity.this.startActivity(intent);
			}
		});
		mBrokeCaptureFooterVideoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BrokeCaptureActivity.this, BrokeCameraTakeActivity.class);
				intent.putExtra("camera_take_mode", "video");
				BrokeCaptureActivity.this.startActivity(intent);
			}
		});
		mBrokeCaptureFooterLocalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaChooser.showImageVideoTab();
				Intent intent = new Intent(BrokeCaptureActivity.this, BucketHomeFragmentActivity.class);
				startActivity(intent);
			}
		});
		mBrokeCaptureLocationEditText.addTextChangedListener(mLocationWatcher);
	}

	private TextWatcher mLocationWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String string = s.toString();
			if (TextUtils.isEmpty(string) && TextUtils.isEmpty(mAutoLocation)) {
				mBrokeCaptureLocationImage.setImageResource(R.drawable.broke_capture_location_icon_grey);
			} else {
				mBrokeCaptureLocationImage.setImageResource(R.drawable.broke_capture_location_icon);
			}
		}
	};

	private void initSelectLayout() {
		mBrokeCaptureHeaderTitle.setText("爆料");
		LayoutInflater inflater = LayoutInflater.from(BrokeCaptureActivity.this);
		for (int i = 0; i < mCatalogNameList.size(); i++) {
			TextView view = (TextView) inflater.inflate(R.layout.listitem_broke_capture_dropbox, null);
			view.setText(mCatalogNameList.get(i));
			mSelectListLayout.addView(view);
			SelectItemClickListener listener = new SelectItemClickListener(i);
			view.setOnClickListener(listener);
		}
		mSelectLayout.setVisibility(View.VISIBLE);
	}

	private void initMediaChooser() {
		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(mVideoBroadcastReceiver, videoIntentFilter);
		IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(mImageBroadcastReceiver, imageIntentFilter);
		mVideoFileList = new ArrayList<String>();
		mImageFileList = new ArrayList<String>();
		MediaChooser.showSelectedNum(false);
		MediaChooser.setSelectionImageLimit(MAX_IMAGE_NUM);
		MediaChooser.setSelectionVideoLimit(MAX_VIDEO_NUM);
	}

	private BroadcastReceiver mVideoBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<String> list = intent.getStringArrayListExtra("list");
			int remainSum = MAX_VIDEO_NUM - mVideoFileList.size();
			for (int i = 0; i < (list.size() > remainSum ? remainSum : list.size()); i++) {
				if (mVideoFileList.contains(list.get(i))) {
					continue;
				}
				mVideoFileList.add(list.get(i));
				MediaChooser.setSelectionVideoLimit(MAX_VIDEO_NUM - mVideoFileList.size());
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	private BroadcastReceiver mImageBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<String> list = intent.getStringArrayListExtra("list");
			int remainSum = MAX_IMAGE_NUM - mImageFileList.size();
			for (int i = 0; i < (list.size() > remainSum ? remainSum : list.size()); i++) {
				if (mImageFileList.contains(list.get(i))) {
					continue;
				}
				mImageFileList.add(list.get(i));
				MediaChooser.setSelectionImageLimit(MAX_IMAGE_NUM - mImageFileList.size());
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	private void initGridView() {
		mAdapter = new BrokeCaptureAdapter(this, mImageFileList, mVideoFileList, new OnDeleteIconClickListener() {
			@Override
			public void onClick(int position) {
				if (position < mVideoFileList.size()) {
					mVideoFileList.remove(position);
					MediaChooser.setSelectionVideoLimit(MAX_VIDEO_NUM - mVideoFileList.size());
				} else {
					mImageFileList.remove(position - mVideoFileList.size());
					MediaChooser.setSelectionImageLimit(MAX_IMAGE_NUM - mImageFileList.size());
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		mBrokeCaptureGridView.setAdapter(mAdapter);
	}

	private String createXMLFile(long time, ArrayList<JSONObject> filePathList, String title, String catalogId) {
		try {
			String dirPath = Environment.getExternalStorageDirectory().getPath() + MConfig.XmlFilePath;
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String filePath = Environment.getExternalStorageDirectory().getPath() + MConfig.XmlFilePath + "/" + time
					+ ".xml";
			File file = new File(filePath);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, true);
			String uuid = Random.nextString(32);
			String msg = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
			msg += "<signal xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" version=\"0\" device=\"Android\" uuid=\""
					+ uuid + "\">\n";
			msg += "<metadata>\n<siteid>" + MConfig.mVmsSiteId + "</siteid>\n<username>" + mUserName
					+ "</username>\n<title>" + title + "</title>\n<catalogid>" + MConfig.mVmsCatalogId
					+ "</catalogid>\n</metadata>\n";
			msg += "<files>\n";
			for (int i = 0; i < filePathList.size(); i++) {
				JSONObject object = filePathList.get(i);
				String guid = object.optString("guid");
				String url = object.optString("url");
				int start = url.lastIndexOf("/");
				String path = url.substring(0, (start < 0 ? 0 : start) + 1);
				String name = url.substring((start < 0 ? 0 : start) + 1);
				if (object.optString("type").equalsIgnoreCase("video")) {
					msg += "<file id=\"" + i + "\">\n<guid>" + guid + "</guid>\n<type>video</type>\n<storagepath>"
							+ path + "</storagepath>\n<rawFileName>" + name + "</rawFileName>\n</file>\n";
				} else {
					msg += "<file id=\"" + i + "\">\n<guid>" + guid + "</guid>\n<type>image</type>\n<storagepath>"
							+ path + "</storagepath>\n<rawFileName>" + name + "</rawFileName>\n</file>\n";
				}
			}
			msg += "</files>\n</signal>";
			out.write(msg.getBytes("UTF-8"));
			out.close();
			return filePath;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
		}
		if (mImageBroadcastReceiver != null) {
			unregisterReceiver(mImageBroadcastReceiver);
		}
		if (mImageBroadcastReceiver != null) {
			unregisterReceiver(mVideoBroadcastReceiver);
		}
		super.onDestroy();
	}

	private class SelectItemClickListener implements OnClickListener {
		private int mPosition;

		public SelectItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			mBrokeCaptureHeaderTitle.setText(mCatalogNameList.get(mPosition));
			mSelectLayout.setVisibility(View.GONE);
			mCatalogSelectedIndex = mPosition;
		}
	}

	private void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingIconLayout);
		}
	}

	private void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	private void close() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				BrokeCaptureActivity.this.finish();
			}
		}, 200);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPhonePopWindow.isShowing()) {
				mPhonePopWindow.hidePhoneWindow();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
