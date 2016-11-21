package com.sobey.cloud.webtv.views.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.util.AsyncNetImageLoader;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sobey.cloud.webtv.ChangePswActivity;
import com.sobey.cloud.webtv.UploadUserInfoActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.bean.UserBean;
import com.sobey.cloud.webtv.broadcast.ECShopBroadReciver;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.model.provide.personal.LoginModel;
import com.sobey.cloud.webtv.senum.LoginMode;
import com.sobey.cloud.webtv.senum.UserGender;
import com.sobey.cloud.webtv.share.ShareControl;
import com.sobey.cloud.webtv.sharesdk.LoginApi;
import com.sobey.cloud.webtv.sharesdk.OnLoginListener;
import com.sobey.cloud.webtv.utils.BufferUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.utils.ValidateUtil;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;
import com.sobey.cloud.webtv.kenli.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

public class LoginActivity extends BaseActivity<LoginModel> {

	private static int REGISTER_CODE = 0;
	private static int CHANGE_PWD_CODE = 1;

	private LoginMode mLoginMode = LoginMode.Logout;
	private String mUserName = "";
	private String mHeadIcon = "";
	private String mNickName = "";
	private String mGender = "";
	private String mEmail = "";
	private String mSex;
	private String mPsw = "";
	private String mTelPhone = "";
	private AsyncNetImageLoader mNetImageLoader;

	@GinInjectView(id = R.id.mTabHost)
	TabHost mTabHost;
	@GinInjectView(id = R.id.mLoginLogonLayout)
	ScrollView mLoginLogonLayout;
	@GinInjectView(id = R.id.mLoginRegisterLayout)
	ScrollView mLoginRegisterLayout;
	@GinInjectView(id = R.id.mLoginSinaWBLogon)
	TextView mLoginSinaWBLogon;
	@GinInjectView(id = R.id.mLoginTencentWeiXinLogon)
	TextView mLoginTencentWeiXinLogon;
	@GinInjectView(id = R.id.mLoginTencentWeiBo)
	TextView mLoginTencentWeiBo;
	@GinInjectView(id = R.id.mLoginQQLogon)
	TextView mLoginQQLogon;
	@GinInjectView(id = R.id.mLoginLogonUsername)
	EditText mLoginLogonUsername;
	@GinInjectView(id = R.id.mLoginLogonPassword)
	EditText mLoginLogonPassword;
	@GinInjectView(id = R.id.mLoginLogonSubmitBtn)
	TextView mLoginLogonSubmitBtn;

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;

	// 2015.11.18 新UI
	@GinInjectView(id = R.id.ac_login_loginlayout)
	LinearLayout loginLayout;
	@GinInjectView(id = R.id.ac_login_registerbtn)
	TextView registerBtn;
	@GinInjectView(id = R.id.ac_login_forgotpsw)
	TextView forgoBtn;

	private String mUid;

	private final String SocialUserInfo = "SocialUserInfo";

	/**
	 * 数据模型
	 */
	private LoginModel mModel;

	/**
	 * 是不是注册后的登录 是的话引导到个人信息编辑页
	 */
	protected boolean isAfterRegistLogin;
	/**
	 * QQ登录的时候第三方平台的uid
	 */
	private String qqUID = "";

	private SignUtil.SocialUserInfo userInfo;

	private boolean savedUserInfo = false;

	private boolean loadedShopScript = false;

	/**
	 * 进度框
	 */
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	public LoginModel initModel() {
		return new LoginModel(this);
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);

		mModel = getModel();

		Log.d("ument share sdk version", SocializeConstants.SDK_VERSION);
		addSocialSupport();// 添加第三方登录支持
		setupTabBar();

		String userName = getIntent().getStringExtra("userName");
		String passWord = getIntent().getStringExtra("passWord");
		mNickName = getIntent().getStringExtra("nick_name");
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		final String loginMode = userInfo.getString("state", LoginMode.Login_Customer.toString());
		mLoginMode = LoginMode.valueOf(loginMode);
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
			mLoginMode = LoginMode.Login_Customer;
			isAfterRegistLogin = true;
			// 新加注册成功后返回用户名密码直接显示在文本框里面
			mLoginLogonUsername.setText(userName);
			mLoginLogonPassword.setText(passWord);
			login(userName, passWord);
		} else if (mLoginMode == LoginMode.Login_Customer) {
			SharedPreferences preferences = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
			mUserName = preferences.getString("uname", "");
			mPsw = preferences.getString("pw", "");
			if (!TextUtils.isEmpty(mUserName)) {
				mLoginLogonUsername.setText(mUserName);
			}
			if (!TextUtils.isEmpty(mPsw)) {
				mLoginLogonPassword.setText(mPsw);
			}
		}
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REGISTER_CODE);
			}
		});
		forgoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(LoginActivity.this, ChangePswActivity.class), CHANGE_PWD_CODE);
			}
		});
	}

	/**
	 * 登录成功
	 * 
	 * @param result
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public void loginSuccessCallBack(String result) {
		mLoginLogonSubmitBtn.setEnabled(true);
		String username = result.split("_loginSuccessCallBack_")[0];
		String returnData = result.split("_loginSuccessCallBack_")[1];
		BufferUtil.saveTextData("LOGINUC", returnData);
		Log.d("zxd", "Login return Data:" + returnData);
		if (returnData.indexOf("javascript") != -1) // 返回有js的标签 代表注册成功了
		{
			BufferUtil.saveTextData("loginSuccessCallBack", result);
			final ArrayList<String> srcList = SignUtil.getRegistScriptSrcList(returnData);
			for (int i = 0; i < srcList.size(); i++) {
				WebView webView = new WebView(LoginActivity.this);
				ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
				webView.setLayoutParams(params);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.addJavascriptInterface(this, "obj");
				webView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);
						return true;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						String cookie = CookieManager.getInstance().getCookie(url);
						Log.d("zxd", "script login cookie:" + cookie);
						if (!TextUtils.isEmpty(cookie) && url.contains("shop")) {
							Log.d("zxd", "ecshop cookie:" + cookie);
							SharedPreferences settings = getSharedPreferences("iweb_shoppingcart", 0);
							Editor editor = settings.edit();
							editor.putString("iweb_shoppingcart", cookie.trim());
							editor.commit();
							loadedShopScript = true;
							if (savedUserInfo)
								sendRedirectBroad();
						}
					}
				});
				webView.loadUrl(srcList.get(i));
			}
			ArrayList<String> codeList = SignUtil.getRegistScritpCodeValue(srcList);
			for (String item : codeList) {
				String userInfo = SignUtil.decodeLoginScriptCodeData(item);
				String[] infoSplit = userInfo.split("&");
				for (String infoItem : infoSplit) {
					String[] values = infoItem.split("=");
					if ("username".equals(values[0])) {
						mUserName = values[1];
					} else if ("uid".equals(values[0])) {
						mUid = values[1];
					}
				}
				Log.d("zxd", userInfo);
				if (isAfterRegistLogin) {
					if (mLoginMode != LoginMode.Login_Customer) {
						if (isAfterRegistLogin) {
							mHeadIcon = LoginActivity.this.userInfo.getHeadURL();
							mNickName = LoginActivity.this.userInfo.getNickName();
							if (SignUtil.SocialUserInfo.MALE.equalsIgnoreCase(LoginActivity.this.userInfo.getSex()))
								mGender = UserGender.Male.toString();
							else
								mGender = UserGender.Female.toString();
							saveSocialUserInfo();
						}
					} else {
						saveSocialUserInfo();
					}
				} else {
					getUserInfo(username);
				}
				sendLoginSuccessBroadCast();
				return;
			}
			return;
		}
		try {
			dismissProgressDialog();
			JSONObject jsonObject;
			jsonObject = new JSONObject(returnData);
			String returncode = jsonObject.getString("returncode");
			String msg = jsonObject.getString("returnmsg");
			Log.d("zxd", "invoke ucenter login interface back  returncode:" + returncode + " msg:" + msg);
			if ("FALURE".equals(returncode)) {
				Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(LoginActivity.this, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
		} finally {
			SharedPreferences settings = LoginActivity.this.getSharedPreferences("user_info", 0);
			Editor editor = settings.edit();
			editor.putString("pw", "");
			editor.commit();
		}
	}

	/**
	 * 登录失败
	 * 
	 * @param result
	 */
	public void loginFailCallBack(String result) {
		Toast.makeText(LoginActivity.this, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
		mLoginLogonSubmitBtn.setEnabled(true);
	}

	/**
	 * 初始化TAB以及用户填写表单
	 */
	private void setupTabBar() {
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {// 变化监听设置背景选中状态
					View view = (View) mTabHost.getTabWidget().getChildAt(i);
					if (mTabHost.getCurrentTab() == i) {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_focus));
						view.setBackgroundResource(R.drawable.login_tab_select);
						view.setFocusable(true);
					} else {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_normal));
						view.setBackgroundResource(R.drawable.trans);
					}
				}
			}
		});
		int width = getResources().getDisplayMetrics().widthPixels;
		LayoutParams params = new LayoutParams(width / 6, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_tabitem_text, null);
		((TextView) view.findViewById(R.id.text)).setText("登录");
		((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		view.setLayoutParams(params);
		mTabHost.addTab(mTabHost.newTabSpec("登录").setIndicator(view).setContent(R.id.mLoginLogonLayout));
		View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_tabitem_text, null);
		((TextView) view1.findViewById(R.id.text)).setText("注册");
		((TextView) view1.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		view1.setLayoutParams(params);
		mTabHost.addTab(mTabHost.newTabSpec("注册").setIndicator(view1).setContent(R.id.mLoginRegisterLayout));
		mTabHost.setCurrentTab(1);
		mTabHost.setCurrentTab(0);
	}

	/**
	 * 添加第三方授权
	 */
	private void addSocialSupport() {
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, MConfig.ShareQQAppId, MConfig.ShareQQAppKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();
		// SignUtil.initTencent(getApplicationContext());
		// 微信登录授权
		UMWXHandler wxHandler = new UMWXHandler(getApplicationContext(), MConfig.ShareWeiXinAppId,
				MConfig.SHAREWEIXINAPPSECRET_STRING);
		wxHandler.addToSocialSDK();
		if (ShareControl.addSinaSSO) {
			// SignUtil.mUmSocialService.getConfig().setSsoHandler(new
			// SinaSsoHandler());
		}
		SignUtil.mUmSocialService.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
		ShareSDK.initSDK(this);
	}

	/**
	 * 初始化第三方登录
	 */
	@GinOnClick(id = { R.id.mLoginSinaWBLogon, R.id.mLoginTencentWeiXinLogon, R.id.mLoginTencentWeiBo,
			R.id.mLoginQQLogon })
	private void initSocialLogon(View view) {

		if (view.getId() == R.id.mLoginSinaWBLogon) {
			mLoginMode = LoginMode.Login_SinaWB;
			// socialdoOauthVerify(SHARE_MEDIA.SINA);
			sinaLogin();

		} else if (view.getId() == R.id.mLoginTencentWeiXinLogon) {
			mLoginMode = LoginMode.Login_WeiXin;
			socialdoOauthVerify(SHARE_MEDIA.WEIXIN);
		} else if (view.getId() == R.id.mLoginTencentWeiBo) {
			mLoginMode = LoginMode.Login_TencentWB;
			socialdoOauthVerify(SHARE_MEDIA.TENCENT);
		} else if (view.getId() == R.id.mLoginQQLogon) {
			mLoginMode = LoginMode.Login_QQ;
			socialdoOauthVerify(SHARE_MEDIA.QQ);
		}
	}

	/**
	 * 新浪登录（因为友盟的新浪登录有问题所以用ShareSDK来登录成功获取用户资料后又交给友盟处理 呵呵呵）
	 */
	private void sinaLogin() {
		Toast.makeText(LoginActivity.this, "正在启动登陆页面，请稍等...", Toast.LENGTH_SHORT).show();
		LoginApi api = new LoginApi();
		api.setPlatform(SinaWeibo.NAME);
		api.setOnLoginListener(new OnLoginListener() {

			@Override
			public boolean onRegister(UserBean info) {
				return false;
			}

			@Override
			public boolean onLogin(String platform, HashMap<String, Object> info) {
				BufferUtil.saveTextData(SocialUserInfo + platform, info.toString());
				userInfo = SignUtil.SocialUserInfo.getSocialUserInfo(info, SHARE_MEDIA.SINA);
				if (userInfo != null) {
					Toast.makeText(LoginActivity.this, "登录中...", Toast.LENGTH_SHORT).show();
					mUserName = userInfo.getUID() + MConfig.SITE_ID;
					String[] names = SignUtil.shortUrl(mUserName);
					BufferUtil.saveTextData("mUserName.txt", names.toString());
					if (names != null && names.length > 0) {
						mUserName = names[0];
						mUserName = mUserName.toLowerCase(Locale.getDefault());
						BufferUtil.saveTextData("mUserNameToLower.txt", mUserName);
						isSocialUserExist();
					} else {
						Toast.makeText(LoginActivity.this, "登陆失败，请尝试其它登陆方式", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(LoginActivity.this, "登陆失败，请尝试其它登陆方式", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
		api.login(this);

	}

	/**
	 * 第三方登录身份验证
	 * 
	 * @param platform
	 */
	private void socialdoOauthVerify(SHARE_MEDIA platform) {
		mLoginLogonSubmitBtn.setEnabled(false);
		SignUtil.deleteLoginAuth(getApplicationContext());
		Toast.makeText(LoginActivity.this, "正在启动登陆页面，请稍等...", Toast.LENGTH_SHORT).show();
		SignUtil.mUmSocialService.doOauthVerify(LoginActivity.this, platform, new UMAuthListener() {
			@Override
			public void onStart(SHARE_MEDIA arg0) {
			}

			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				BufferUtil.saveTextData(SocialUserInfo + "SocaiaExp" + arg1.toString(), arg0.toString());
				Toast.makeText(LoginActivity.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
				mLoginLogonSubmitBtn.setEnabled(true);
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				ToastUtil.showToast(LoginActivity.this, "授权成功,正在获取个人信息...！");
				BufferUtil.saveTextData("doOauthVerifyComplete" + platform.toString(), value.toString());
				if (platform == SHARE_MEDIA.QQ) {
					try {
						qqUID = value.getString("uid");
					} catch (Exception e) {
					}
				}
				getSocialPlaformInfo(platform);
			}

			@Override
			public void onCancel(SHARE_MEDIA arg0) {
				mLoginLogonSubmitBtn.setEnabled(true);
			}
		});
	}

	/**
	 * 获取社交平台的个人信息
	 * 
	 * @param platform
	 */
	private void getSocialPlaformInfo(final SHARE_MEDIA platform) {
		Toast.makeText(LoginActivity.this, "登录中...", Toast.LENGTH_SHORT).show();
		switch (platform) {
		case SINA:
			mLoginMode = LoginMode.Login_SinaWB;
			break;
		case TENCENT:
			mLoginMode = LoginMode.Login_TencentWB;
			break;
		case QQ:
			mLoginMode = LoginMode.Login_QQ;
			break;
		case WEIXIN:
			mLoginMode = LoginMode.Login_WeiXin;
			break;
		default:
			return;
		}
		SignUtil.mUmSocialService.getPlatformInfo(LoginActivity.this, platform, new UMDataListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				BufferUtil.saveTextData(SocialUserInfo + platform, "状态码:" + status + info.toString());
				mLoginLogonSubmitBtn.setEnabled(true);
				if (status == 200 && info != null) {
					// qq登录的uid在授权成功的时候已经返回保存
					if (platform == SHARE_MEDIA.QQ) {
						info.put("uid", qqUID);
					}
					userInfo = SignUtil.SocialUserInfo.getSocialUserInfo(info, platform);
					if (userInfo != null) {
						mUserName = userInfo.getUID() + MConfig.SITE_ID;
						String[] names = SignUtil.shortUrl(mUserName);
						BufferUtil.saveTextData("mUserName.txt", names.toString());
						if (names != null && names.length > 0) {
							mUserName = names[0];
							mUserName = mUserName.toLowerCase(Locale.getDefault());
							BufferUtil.saveTextData("mUserNameToLower.txt", mUserName);
							isSocialUserExist();
						} else {
							Toast.makeText(LoginActivity.this, "登陆失败，请尝试其它登陆方式", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(LoginActivity.this, "登陆失败，请尝试其它登陆方式", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(LoginActivity.this, "登陆失败，请尝试其它登陆方式", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 是否是第三方登录退出
	 */
	// TODO cunzai
	private void isSocialUserExist() {
		News.isUserExist(mUserName, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				if (result != null)
					BufferUtil.saveTextData("SocialUserExist", result.toString());
				try {
					JSONObject object = result.getJSONObject(0);
					String returnCode = object.getString("returncode");
					if (returnCode.equalsIgnoreCase("SUCCESS")) {
						login(mUserName, mUserName);
					} else if (returnCode.equalsIgnoreCase("FALURE")) {
						String nickName = "";
						if (userInfo.getNickName() != null) {
							nickName = userInfo.getNickName();
						}
					} else {
						Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				} finally {
				}
			}

			@Override
			public void onNG(String reason) {
				Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试" + reason, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 保存用户信息
	 */
	private void saveSocialUserInfo() {
		SharedPreferences settings = LoginActivity.this.getSharedPreferences("user_info", 0);
		Editor editor = settings.edit();
		editor.putString("id", mUserName);
		editor.putString("headicon", mHeadIcon);
		if (isAfterRegistLogin) {
			if (mLoginMode != LoginMode.Login_Customer) {
				editor.putString("nickname", mNickName);
			} else
				editor.putString("nickname", mNickName);
			if (ValidateUtil.isEmail(mUserName)) {
				mEmail = mUserName;
			}
		} else
			editor.putString("nickname", mNickName);
		editor.putString("state", mLoginMode.toString());
		editor.putString("gender", mGender);
		editor.putString("email", mEmail);
		editor.putString("uid", mUid);
		editor.putString("uname", mUserName);
		editor.putString("pw", mPsw);
		editor.putString("telphone", mTelPhone);
		editor.commit();
		Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();

		savedUserInfo = true;
		sendLoginSuccessBroadCast();
		if (loadedShopScript) {
			sendRedirectBroad();
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isAfterRegistLogin) // 如果是注册后登录的 那就引导 到编辑页
				{
					Intent intent = new Intent(LoginActivity.this, UploadUserInfoActivity.class);
					// 告知那边是社交登录账号 需要同步
					if (mLoginMode != LoginMode.Login_Customer)
						intent.putExtra("issocialRegist", true);
					intent.putExtra("autolupload", true);
					startActivity(intent);
				}
				finishActivity();
			}
		}, 1000);
	}

	/**
	 * 发送登录成功的广播
	 */
	private void sendLoginSuccessBroadCast() {
		Intent intent = new Intent();
		intent.setAction(SobeyConstants.ACTION_LOG_STATE_CHANGE);
		sendBroadcast(intent);
	}

	private void syncUserInfoInCms() {
		SignUtil.modifyUserInfoFromUCenter(mUserName, mNickName, mSex, mEmail, mHeadIcon,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						Toast.makeText(LoginActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						Toast.makeText(LoginActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * 从UCenter登录
	 * 
	 * @param userName
	 * @param Password
	 */
	private void loginFromUCenter(final String userName, final String Password) {
		mModel.login(MConfig.SITE_ID, userName, Password);
	}

	public void login(final String username, String password) {
		showProgressDialog("正在登录...");
		mPsw = password;
		mLoginLogonSubmitBtn.setEnabled(false);
		if (SignUtil.flag) {
			loginFromUCenter(username, password);
		} else {
			dismissProgressDialog();
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param username
	 */
	private void getUserInfo(String username) {
		final String test = username;
		News.getUserInfo(username, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				BufferUtil.saveTextData("getuserInfoMethod", result.toString());
				try {
					if (result == null || result.length() == 0) {
						saveSocialUserInfo();
						return;
					}
					JSONObject object = result.getJSONObject(0);
					if (object != null) {
						mUserName = object.getString("username");
						mNickName = object.getString("nickname");
						mEmail = object.getString("email");
						mHeadIcon = object.getString("logo");
						mTelPhone = object.getString("telphone");
						String sex = object.getString("sex");
						if (sex.equalsIgnoreCase("男")) {
							mGender = UserGender.Male.toString();
						} else if (sex.equalsIgnoreCase("女")) {
							mGender = UserGender.Female.toString();
						} else {
							mGender = UserGender.Undefined.toString();
						}
						// mLoginMode = LoginMode.Login_Customer;
						saveSocialUserInfo();
					} else {
						Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNG(String reason) {
				Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void sendRedirectBroad() {
		Intent intent = new Intent();
		intent.setAction(ECShopBroadReciver.REDIRECT_AFTERLOGIN);
		intent.putExtra("msg", ECShopBroadReciver.REDIRECT_AFTERLOGIN);
		sendBroadcast(intent);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGISTER_CODE || requestCode == CHANGE_PWD_CODE) {
			if (resultCode != RESULT_OK) {
				return;
			}
			Bundle bundle = data.getExtras();
			String username = bundle.getString("username");
			String password = bundle.getString("password");
			mLoginLogonUsername.setText(username);
			mLoginLogonPassword.setText(password);
			login(username, password);
		} else {
			/** 使用SSO授权必须添加如下代码 */
			UMSsoHandler ssoHandler = SignUtil.mUmSocialService.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	/**
	 * 通过账号密码登录
	 */
	@GinOnClick(id = R.id.mLoginLogonSubmitBtn)
	private void loginByUserNameAndPassword(View view) {
		String username = mLoginLogonUsername.getText().toString().trim();
		String password = mLoginLogonPassword.getText().toString().trim();
		if (username.length() < 1) {
			Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.length() < 1) {
			Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		mLoginMode = LoginMode.Login_Customer;
		login(username, password);
	}

	@GinOnClick(id = { R.id.back_rl })
	public void back(View view) {
		finishActivity();
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
