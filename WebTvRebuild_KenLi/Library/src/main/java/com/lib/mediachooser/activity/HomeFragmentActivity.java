package com.lib.mediachooser.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lib.mediachooser.MediaChooser;
import com.lib.mediachooser.MediaChooserConstants;
import com.lib.mediachooser.MediaModel;
import com.lib.mediachooser.fragment.ImageFragment;
import com.lib.mediachooser.fragment.VideoFragment;
import com.third.library.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragmentActivity extends FragmentActivity implements ImageFragment.OnImageSelectedListener, VideoFragment.OnVideoSelectedListener {

	private FragmentTabHost mTabHost;
	private TextView mHeaderBarTitle;
	private TextView mFooterBarDone;
	private TextView mFooterBarCancel;

	private int mImageCount = 0;
	private int mVideoCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_media_chooser);

		mHeaderBarTitle = (TextView) findViewById(R.id.headerbar_title_text);
		mFooterBarDone = (TextView) findViewById(R.id.footerbar_add_text);
		mFooterBarCancel = (TextView) findViewById(R.id.footerbar_cancel_text);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		mFooterBarDone.setText(MediaChooserConstants.footerBarOkString);
		mFooterBarCancel.setText(MediaChooserConstants.footerBarCancelString);
		mFooterBarDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
				VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag("tab2");
				if (videoFragment != null || imageFragment != null) {
					if (videoFragment != null) {
						if (videoFragment.getSelectedVideoList() != null && videoFragment.getSelectedVideoList().size() > 0) {
							Intent videoIntent = new Intent();
							videoIntent.setAction(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
							videoIntent.putStringArrayListExtra("list", videoFragment.getSelectedVideoList());
							sendBroadcast(videoIntent);
						}
					}
					if (imageFragment != null) {
						if (imageFragment.getSelectedImageList() != null && imageFragment.getSelectedImageList().size() > 0) {
							Intent imageIntent = new Intent();
							imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
							imageIntent.putStringArrayListExtra("list", imageFragment.getSelectedImageList());
							sendBroadcast(imageIntent);
						}
					}
					finish();
				} else {
					Toast.makeText(HomeFragmentActivity.this, getString(R.string.plaese_select_file), Toast.LENGTH_SHORT).show();
				}
			}
		});
		mFooterBarCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
		if (getIntent() != null && (getIntent().getBooleanExtra("isFromBucket", false))) {
			if (getIntent().getBooleanExtra("isImage", true)) {
				setImageTitle();
				Bundle bundle = new Bundle();
				bundle.putString("name", getIntent().getStringExtra("name"));
				mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), ImageFragment.class, bundle);
			} else {
				setVideoTitle();
				Bundle bundle = new Bundle();
				bundle.putString("name", getIntent().getStringExtra("name"));
				mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), VideoFragment.class, bundle);
			}
			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
				mTabHost.getTabWidget().setVisibility(View.GONE);
			}
		} else if (getIntent() != null && (getIntent().getStringExtra("path") != null)) {
			if (getIntent().getBooleanExtra("isImage", true)) {
				setImageTitle();
				Bundle bundle = new Bundle();
				bundle.putString("path", getIntent().getStringExtra("path"));
				mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), ImageFragment.class, bundle);
			} else {
				setVideoTitle();
				Bundle bundle = new Bundle();
				bundle.putString("path", getIntent().getStringExtra("path"));
				mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), VideoFragment.class, bundle);
			}
			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
				mTabHost.getTabWidget().setVisibility(View.GONE);
			}
		} else {
			if (MediaChooserConstants.fileType.equalsIgnoreCase("image")) {
				setImageTitle();
				mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), ImageFragment.class, null);
			} else if (MediaChooserConstants.fileType.equalsIgnoreCase("video")) {
				setVideoTitle();
				mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), VideoFragment.class, null);
			} else {
				setImageTitle();
				mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), ImageFragment.class, null);
				mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), VideoFragment.class, null);
			}
		}

		if (MediaChooserConstants.fileType.equalsIgnoreCase("all")) {
			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
				TextView textView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
				if (textView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
					RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) textView.getLayoutParams();
					params.addRule(RelativeLayout.CENTER_HORIZONTAL);
					params.addRule(RelativeLayout.CENTER_VERTICAL);
					params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
					params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
					mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title).setLayoutParams(params);
					mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 100;
				} else if (textView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
					LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
					params.gravity = Gravity.CENTER;
					mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title).setLayoutParams(params);
				}
			}
		} else {
			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
				mTabHost.getTabWidget().setVisibility(View.GONE);
			}
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				onTabChange(tabId);
			}
		});
	}

	private void onTabChange(String tabId) {
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
		VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag("tab2");
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (tabId.equalsIgnoreCase("tab1")) {
			setImageTitle();
			if (imageFragment != null) {
				if (videoFragment != null) {
					fragmentTransaction.hide(videoFragment);
				}
				fragmentTransaction.show(imageFragment);
			}
		} else {
			setVideoTitle();
			if (videoFragment != null) {
				if (imageFragment != null) {
					fragmentTransaction.hide(imageFragment);
				}
				fragmentTransaction.show(videoFragment);
				if (videoFragment.getAdapter() != null) {
					videoFragment.getAdapter().notifyDataSetChanged();
				}
			}
		}
		fragmentTransaction.commit();
	}

	@Override
	public void onImageSelected(int count) {
		mImageCount = count;
		setImageTitle();
	}

	@Override
	public void onImageClick(int position) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onVideoSelected(int count) {
		mVideoCount = count;
		setVideoTitle();
	}

	@Override
	public void onVideoClick(int position) {
		// TODO Auto-generated method stub
	}

	private void setImageTitle() {
		if (MediaChooserConstants.SHOW_SELECTED_NUM) {
			mHeaderBarTitle.setText("��ѡ" + mImageCount + "��ͼƬ");
		} else {
			mHeaderBarTitle.setText("����ѡ" + (MediaChooserConstants.MAX_IMAGE_LIMIT - mImageCount) + "��ͼƬ");
		}
	}

	private void setVideoTitle() {
		if (MediaChooserConstants.SHOW_SELECTED_NUM) {
			mHeaderBarTitle.setText("��ѡ" + mVideoCount + "����Ƶ");
		} else {
			mHeaderBarTitle.setText("����ѡ" + (MediaChooserConstants.MAX_VIDEO_LIMIT - mVideoCount) + "����Ƶ");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
			try {
				String str = data.getStringExtra("information");
				JSONArray information = new JSONArray(str);
				ArrayList<MediaModel> list = new ArrayList<MediaModel>();
				for (int i = 0; i < information.length(); i++) {
					JSONObject object = information.getJSONObject(i);
					list.add(new MediaModel(object.getString("filepath"), object.getBoolean("status")));
				}
				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
				if(list.size()>0 && imageFragment !=null) {
					imageFragment.refreshImageFragment(list);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
