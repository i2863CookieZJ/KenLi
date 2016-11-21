package com.sobey.cloud.webtv.utils;

import com.dylan.uiparts.views.SwitchButton;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.SearchActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MorePopWindow {
	private Context context;
	private PopupWindow mMorePopupWindow = null;
	private View attachView;
	private TextView mPopwindowMoreCancel;
	private TextView mPopwindowSearch;
	private TextView mPopwindowCollection;
	private SwitchButton mPopwindowShowPictureButton_Old;
	private ImageButton mPopwindowShowPictureButton;
	private SwitchButton mPopwindowNightModeButton;
	private SeekBar mPopwindowFontSizeSeekbar;
	private FontSizeChangeListener mFontSizeChangeListener;
	private CollectionClickListener mCollectionClickListener;
	private LinearLayout mPopwindowMoreShareLl;
	private LinearLayout mPopwindowMoreSearchLl;
	private LinearLayout mPopwindowMoreCollectionLl;
	private ImageView popwindow_more_collection_iv;
	private boolean isShowPicture;
	private boolean isNightMode;
	private boolean isCollectionSet = false;
	private int mFontSize;
	private int mHeight;
	private int mScreenWidth;

	public MorePopWindow(final Context context, View attachView, FontSizeChangeListener listener,
			CollectionClickListener listener2) {
		this.context = context;
		this.attachView = attachView;
		this.mFontSizeChangeListener = listener;
		this.mCollectionClickListener = listener2;
		View mMoreView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_more, null);
		mMorePopupWindow = new PopupWindow(mMoreView);
		mPopwindowMoreCancel = (TextView) mMoreView.findViewById(R.id.popwindow_more_cancel);
		mPopwindowSearch = (TextView) mMoreView.findViewById(R.id.popwindow_more_search);
		mPopwindowCollection = (TextView) mMoreView.findViewById(R.id.popwindow_more_collection);
		mPopwindowShowPictureButton = (ImageButton) mMoreView.findViewById(R.id.popwindow_more_showpicture_btn);
		mPopwindowNightModeButton = (SwitchButton) mMoreView.findViewById(R.id.popwindow_more_nightmode_btn);
		mPopwindowFontSizeSeekbar = (SeekBar) mMoreView.findViewById(R.id.popwindow_more_fontsize_seekbar);
		mPopwindowMoreShareLl = (LinearLayout) mMoreView.findViewById(R.id.popwindow_more_share_ll);
		mPopwindowMoreSearchLl = (LinearLayout) mMoreView.findViewById(R.id.popwindow_more_search_ll);
		mPopwindowMoreCollectionLl = (LinearLayout) mMoreView.findViewById(R.id.popwindow_more_collection_ll);
		popwindow_more_collection_iv = (ImageView) mMoreView.findViewById(R.id.popwindow_more_collection_iv);
		mPopwindowShowPictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowPicture) {
					mPopwindowShowPictureButton.setImageResource(R.drawable.pop_switch_off);
					isShowPicture = false;
				} else {
					mPopwindowShowPictureButton.setImageResource(R.drawable.pop_switch_on);
					isShowPicture = true;
				}
			}
		});
		mPopwindowMoreCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMoreWindow();
			}
		});

		mPopwindowMoreSearchLl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMoreWindow();
				Intent intent = new Intent(context, SearchActivity.class);
				context.startActivity(intent);
			}
		});

		mPopwindowMoreCollectionLl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCollectionClickListener != null) {
					mCollectionClickListener.onClick(isCollectionSet);
				}
			}
		});

		mPopwindowFontSizeSeekbar.setMax(MConfig.FontSizeMax - MConfig.FontSizeMin);
		mPopwindowFontSizeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (mFontSizeChangeListener != null) {
					mFontSizeChangeListener.onChange(progress);
				}
			}
		});

		mPopwindowMoreShareLl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideMoreWindow();
				if (context instanceof GeneralNewsDetailActivity) {
					((GeneralNewsDetailActivity) context).showSharePopWindow();
				} else if (context instanceof VideoNewsDetailActivity) {
					((VideoNewsDetailActivity) context).showSharePopWindow();
				} else if (context instanceof PhotoNewsDetailActivity) {
					((PhotoNewsDetailActivity) context).showSharePopWindow();
				}
			}
		});

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mHeight = metrics.heightPixels;// ScaleConversion.dip2px(context, 320);
		mScreenWidth = metrics.widthPixels;
	}

	public void showMoreWindow() {
		if (!mMorePopupWindow.isShowing()) {
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			isShowPicture = settings.getInt("show_picture", 1) == 1 ? true : false;
			isNightMode = settings.getInt("night_mode", 0) == 1 ? true : false;
			mFontSize = settings.getInt("fontsize", MConfig.FontSizeDefault);
			if (isShowPicture) {
				mPopwindowShowPictureButton.setImageResource(R.drawable.pop_switch_on);
			} else {
				mPopwindowShowPictureButton.setImageResource(R.drawable.pop_switch_off);
			}
			// mPopwindowShowPictureButton.setChecked(!isShowPicture);
			mPopwindowNightModeButton.setChecked(isNightMode);
			mPopwindowFontSizeSeekbar.setProgress(mFontSize - MConfig.FontSizeMin);
			mMorePopupWindow.showAtLocation(attachView, Gravity.BOTTOM, 0, 0);
			mMorePopupWindow.update(0, 0, mScreenWidth, mHeight);
		}
	}

	public void hideMoreWindow() {
		if (mMorePopupWindow.isShowing()) {
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			Editor editor = settings.edit();
			// isShowPicture = mPopwindowShowPictureButton.isChecked();
			isNightMode = mPopwindowNightModeButton.isChecked();
			mFontSize = mPopwindowFontSizeSeekbar.getProgress() + MConfig.FontSizeMin;
			editor.putInt("show_picture", isShowPicture ? 1 : 0);
			editor.putInt("night_mode", isNightMode ? 1 : 0);
			editor.putInt("fontsize", mFontSize);
			editor.commit();
			mMorePopupWindow.dismiss();
		}
	}

	public boolean isShowing() {
		return mMorePopupWindow.isShowing();
	}

	public void setCollection(boolean isCollectionSet) {
		this.isCollectionSet = isCollectionSet;
		if (isCollectionSet) {
			mPopwindowCollection.setText("取消收藏");
			popwindow_more_collection_iv.setImageResource(R.drawable.pop_shoucang);
		} else {
			mPopwindowCollection.setText("收藏");
			popwindow_more_collection_iv.setImageResource(R.drawable.pop_noshoucang);
		}
	}

	public interface FontSizeChangeListener {
		public void onChange(int progress);
	}

	public interface CollectionClickListener {
		public void onClick(boolean addFlag);
	}
}
