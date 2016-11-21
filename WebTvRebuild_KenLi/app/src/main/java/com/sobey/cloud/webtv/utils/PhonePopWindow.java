package com.sobey.cloud.webtv.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhonePopWindow {
	private Context context;
	private PopupWindow mPhonePopupWindow = null;
	private View attachView;
	private RelativeLayout mPopwindowPhoneLayout;
	private TextView mPopwindowPhoneOK;
	private TextView mPopwindowPhoneCancel;
	private EditText mPopwindowPhoneNumber;
	private String mPhoneNumber = null;
	private PhonePopWindowCloseListener mCloseListener;
	private int mHeight;
	private int mScreenWidth;

	public PhonePopWindow(final Context context, View attachView, PhonePopWindowCloseListener listener) {
		this.context = context;
		this.attachView = attachView;
		View mPhoneView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_phone, null);
		mPhonePopupWindow = new PopupWindow(mPhoneView);
		mPopwindowPhoneLayout = (RelativeLayout) mPhoneView.findViewById(R.id.popwindow_phone_layout);
		mPopwindowPhoneOK = (TextView) mPhoneView.findViewById(R.id.popwindow_phone_ok);
		mPopwindowPhoneCancel = (TextView) mPhoneView.findViewById(R.id.popwindow_phone_cancel);
		mPopwindowPhoneNumber = (EditText) mPhoneView.findViewById(R.id.popwindow_phone_number);
		mCloseListener = listener;
		
		mPopwindowPhoneLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hidePhoneWindow();
			}
		});

		mPopwindowPhoneOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPhoneNumber = mPopwindowPhoneNumber.getText().toString();
				if (TextUtils.isEmpty(mPhoneNumber) || !isMobileNum(mPhoneNumber)) {
					Toast.makeText(context, "请输入正确的手机号码以便提供奖励", Toast.LENGTH_SHORT).show();
					mPopwindowPhoneNumber.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mPopwindowPhoneNumber, 0);
					return;
				} else {
					hidePhoneWindow();
					mCloseListener.onClose(true, mPhoneNumber);
				}
			}
		});

		mPopwindowPhoneCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hidePhoneWindow();
				mCloseListener.onClose(false, null);
			}
		});
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mHeight = metrics.heightPixels;//ScaleConversion.dip2px(context, 320);
		mScreenWidth = metrics.widthPixels;
	}

	public void showPhoneWindow() {
		if (!mPhonePopupWindow.isShowing()) {
			mPhonePopupWindow.setFocusable(true);
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			mPhoneNumber = settings.getString("phone_number", null);
			mPopwindowPhoneNumber.setText(mPhoneNumber);
			mPhonePopupWindow.showAtLocation(attachView, Gravity.BOTTOM, 0, 0);
			mPhonePopupWindow.update(0, 0, mScreenWidth, mHeight);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mPopwindowPhoneNumber.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mPopwindowPhoneNumber, 0);
				}
			}, 200);
		}
	}

	public void hidePhoneWindow() {
		if (mPhonePopupWindow.isShowing()) {
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			Editor editor = settings.edit();
			editor.putString("phone_number", mPhoneNumber);
			editor.commit();
			if (mPopwindowPhoneNumber != null) {
				InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(mPopwindowPhoneNumber.getWindowToken(), 0);
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mPhonePopupWindow.dismiss();
				}
			}, 200);
		}
	}

	public boolean isShowing() {
		return mPhonePopupWindow.isShowing();
	}
	
	private boolean isMobileNum(String number) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }

	public interface PhonePopWindowCloseListener {
		void onClose(boolean buttonFlag, String number);
	}
}
