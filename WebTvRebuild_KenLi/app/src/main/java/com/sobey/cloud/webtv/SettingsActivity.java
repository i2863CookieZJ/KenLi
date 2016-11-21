package com.sobey.cloud.webtv;

import com.dylan.common.utils.DataCleanManager;
import com.dylan.uiparts.views.SwitchButton;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.model.provide.personal.SettingModel;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.utils.VersionCheck;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity<SettingModel> {

	final UMSocialService mUmSocialService = UMServiceFactory.getUMSocialService(MConfig.DESCRIPTOR,
			RequestType.SOCIAL);
	private boolean isShowPicture;
	private boolean isNightMode;
	private boolean isAutoHide;
	private int mFontSize;

	/**
	 * 数据模型
	 */
	private SettingModel mMoel;

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;

	@GinInjectView(id = R.id.mSettingsNoPictureMode_old)
	SwitchButton mSettingsNoPictureMode;

	@GinInjectView(id = R.id.mSettingsNightMode)
	SwitchButton mSettingsNightMode;

	@GinInjectView(id = R.id.mSettingsAutoHide)
	SwitchButton mSettingsAutoHide;
	@GinInjectView(id = R.id.mSettingsNoPictureMode)
	ImageButton mSettingsNoPictureSwitch;
	@GinInjectView(id = R.id.mSettingsFontSizeSeekbar)
	SeekBar mSettingsFontSizeSeekbar;

	@GinInjectView(id = R.id.mSettingsClear)
	RelativeLayout mSettingsClear;

	@GinInjectView(id = R.id.mSettingsNewGuide)
	RelativeLayout mSettingsNewGuide;

	@GinInjectView(id = R.id.mSettingsAboutus)
	RelativeLayout mSettingsAboutus;

	@GinInjectView(id = R.id.mSettingsUpdate)
	RelativeLayout mSettingsUpdate;

	@GinInjectView(id = R.id.now_banben)
	TextView now_banben;
	@GinInjectView(id = R.id.mSettingsLogout)
	TextView loginout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_settings;
	}

	@Override
	public SettingModel initModel() {
		return new SettingModel(this);
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);

		mMoel = getModel();

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// close();
				finishActivity();
			}
		});
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			now_banben.setText("当前版本" + version);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedPreferences settings = this.getSharedPreferences("settings", 0);
		isShowPicture = settings.getInt("show_picture", 1) == 1 ? true : false;
		isNightMode = settings.getInt("night_mode", 0) == 1 ? true : false;
		isAutoHide = settings.getInt("autohide_footer", 0) == 1 ? true : false;
		mFontSize = settings.getInt("fontsize", MConfig.FontSizeDefault);

		mSettingsNightMode.setChecked(isNightMode);

		mSettingsAutoHide.setChecked(isAutoHide);

		mSettingsFontSizeSeekbar.setMax(MConfig.FontSizeMax - MConfig.FontSizeMin);
		mSettingsFontSizeSeekbar.setProgress(mFontSize - MConfig.FontSizeMin);
		// mSettingsNoPictureMode.setChecked(!isShowPicture);
		if (isShowPicture) {
			mSettingsNoPictureSwitch.setImageResource(R.drawable.pop_switch_on);
		} else {
			mSettingsNoPictureSwitch.setImageResource(R.drawable.pop_switch_off);
		}
		mSettingsNoPictureSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowPicture) {
					mSettingsNoPictureSwitch.setImageResource(R.drawable.pop_switch_off);
				} else {
					mSettingsNoPictureSwitch.setImageResource(R.drawable.pop_switch_on);
				}
				isShowPicture = !isShowPicture;
			}
		});
		mSettingsClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearCache();
			}
		});

		mSettingsNewGuide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, NewGuideActivity.class));
			}
		});

		mSettingsAboutus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
			}
		});

		mSettingsUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VersionCheck versionCheck = new VersionCheck();
				versionCheck.check(SettingsActivity.this, true);
			}
		});
		if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
			loginout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.mSettingsLogout:
			logout();
			break;

		default:
			break;
		}
	}

	/**
	 * 退出登录回调
	 *
	 * @param result
	 */
	public void logoutCallBack(Boolean result) {
		SharedPreferences settings = getSharedPreferences("user_info", 0);
		String loginMode = settings.getString("state", null);
		Editor editor = settings.edit();
		editor.putString("id", "");
		editor.putString("headicon", "");
		editor.putString("nickname", "");
		// editor.putString("state", "");
		editor.putString("gender", "");
		editor.putString("email", "");
		editor.putString("uid", "");
		editor.commit();

		SharedPreferences iweb_shoppingcart = getSharedPreferences("iweb_shoppingcart", 0);
		Editor iweb_shoppingcarteditor = iweb_shoppingcart.edit();
		iweb_shoppingcarteditor.putString("iweb_shoppingcart", "");
		iweb_shoppingcarteditor.commit();
		Intent intent = new Intent();
		intent.setAction(SobeyConstants.ACTION_LOG_STATE_CHANGE);
		sendBroadcast(intent);
		if (loginMode != null) {
			mUmSocialService.loginout(this, new SocializeClientListener() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
					// TODO Auto-generated method stub
				}
			});
			mUmSocialService.deleteOauth(this, SHARE_MEDIA.QQ, new SocializeClientListener() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
					// TODO Auto-generated method stub
				}
			});
			mUmSocialService.deleteOauth(this, SHARE_MEDIA.SINA, new SocializeClientListener() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
					// TODO Auto-generated method stub
				}
			});
			mUmSocialService.deleteOauth(this, SHARE_MEDIA.TENCENT, new SocializeClientListener() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onComplete(int arg0, SocializeEntity arg1) {
				}
			});
		}
	}

	private void clearCache() {
		DataCleanManager.cleanApplicationData(this);
		FileUtil.delDirectory(Environment.getExternalStorageDirectory() + MConfig.CachePath);
		FileUtil.delDirectory(FileUtil.SOBEY);
		try {
			deleteDatabase("webview.db");
			deleteDatabase("webviewCache.db");
			deleteDatabase("webviewCookiesChromium.db");
			deleteDatabase("webviewCookiesChromiumPrivate.db");
		} catch (Exception e) {
		}
		Toast.makeText(SettingsActivity.this, "清除缓存完毕！", Toast.LENGTH_SHORT).show();
	}

	private void close() {
		SharedPreferences settings = this.getSharedPreferences("settings", 0);
		Editor editor = settings.edit();
		// isShowPicture = mSettingsNoPictureMode.isChecked();
		isNightMode = mSettingsNightMode.isChecked();
		isAutoHide = mSettingsAutoHide.isChecked();
		mFontSize = mSettingsFontSizeSeekbar.getProgress() + MConfig.FontSizeMin;
		editor.putInt("show_picture", isShowPicture ? 1 : 0);
		editor.putInt("night_mode", isNightMode ? 1 : 0);
		editor.putInt("autohide_footer", isAutoHide ? 1 : 0);
		editor.putInt("fontsize", mFontSize);
		editor.commit();
		// finishActivity();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		close();
	}

	/**
	 * 退出登录
	 */
	private void logout() {
		if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
			Toast.makeText(SettingsActivity.this, "用户未登录", Toast.LENGTH_SHORT).show();
			return;
		}

		SignUtil.deleteLoginAuth(this);
		SharedPreferences settings = getSharedPreferences("user_info", 0);
		String uid = settings.getString("uid", "");
		mMoel.logout(uid);

		Toast.makeText(SettingsActivity.this, "注销成功！", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finishActivity();
			}
		}, 1000);
	}
}
