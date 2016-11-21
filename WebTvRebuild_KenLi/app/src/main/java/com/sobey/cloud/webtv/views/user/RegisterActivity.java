package com.sobey.cloud.webtv.views.user;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.util.AsyncNetImageLoader;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.model.provide.personal.LoginModel;
import com.sobey.cloud.webtv.senum.LoginMode;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.ValidateUtil;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;
import com.sobey.cloud.webtv.kenli.R;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.sso.UMSsoHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity<LoginModel> {

	private static int REGISTER_VER_CODE = 1;

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

	@GinInjectView(id = R.id.mLoginRegisterPhoneLayout)
	LinearLayout mLoginRegisterPhoneLayout;
	@GinInjectView(id = R.id.mLoginRegisterPhoneChange)
	TextView mLoginRegisterPhoneChange;
	@GinInjectView(id = R.id.mLoginRegisterPhoneUsername)
	EditText mLoginRegisterPhoneUsername;
	@GinInjectView(id = R.id.mLoginRegisterPhonePassword)
	EditText mLoginRegisterPhonePassword;
	@GinInjectView(id = R.id.mLoginRegisterPhoneSubmitBtn)
	TextView mLoginRegisterPhoneSubmitBtn;
	@GinInjectView(id = R.id.mLoginRegisterEmailLayout)
	LinearLayout mLoginRegisterEmailLayout;
	@GinInjectView(id = R.id.mLoginRegisterEmailChange)
	TextView mLoginRegisterEmailChange;
	@GinInjectView(id = R.id.mLoginRegisterEmailUsername)
	EditText mLoginRegisterEmailUsername;
	@GinInjectView(id = R.id.mLoginRegisterEmailPassword)
	EditText mLoginRegisterEmailPassword;
	@GinInjectView(id = R.id.mLoginRegisterEmailSubmitBtn)
	TextView mLoginRegisterEmailSubmitBtn;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.mLoginRegister_nick_name)
	EditText nickNamePhoneEt;
	EditText nickNameEmailEt;

	// 2015.11.18 新UI
	@GinInjectView(id = R.id.ac_register_registerlayout)
	LinearLayout registerLayout;

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
		return R.layout.activity_register;
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
		setupTabBar();
		initRegister();

		String userName = getIntent().getStringExtra("userName");
		String passWord = getIntent().getStringExtra("passWord");
		mNickName = getIntent().getStringExtra("nick_name");
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		final String loginMode = userInfo.getString("state", LoginMode.Login_Customer.toString());
		mLoginMode = LoginMode.valueOf(loginMode);
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
			mLoginMode = LoginMode.Login_Customer;
			isAfterRegistLogin = true;
			login(userName, passWord);
		} else if (mLoginMode == LoginMode.Login_Customer) {
			SharedPreferences preferences = getSharedPreferences("user_info", Activity.MODE_PRIVATE);
			mUserName = preferences.getString("uname", "");
			mPsw = preferences.getString("pw", "");
			if (!TextUtils.isEmpty(mUserName)) {
			}
			if (!TextUtils.isEmpty(mPsw)) {
			}
		}
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
		View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_tabitem_text, null);
		((TextView) view.findViewById(R.id.text)).setText("登录");
		((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		view.setLayoutParams(params);
		mTabHost.addTab(mTabHost.newTabSpec("登录").setIndicator(view).setContent(R.id.mLoginLogonLayout));
		View view1 = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_tabitem_text, null);
		((TextView) view1.findViewById(R.id.text)).setText("注册");
		((TextView) view1.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		view1.setLayoutParams(params);
		mTabHost.addTab(mTabHost.newTabSpec("注册").setIndicator(view1).setContent(R.id.mLoginRegisterLayout));
		mTabHost.setCurrentTab(1);
		mTabHost.setCurrentTab(0);
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
		mPsw = password;
		if (SignUtil.flag) {
			loginFromUCenter(username, password);
		}
	}

	/**
	 * 初始化注册
	 */
	private void initRegister() {
		mLoginRegisterPhoneLayout.setVisibility(View.VISIBLE);
		mLoginRegisterEmailLayout.setVisibility(View.GONE);
		mLoginRegisterPhoneChange.setOnClickListener(new OnClickListener() {// 变化电话与电子右键输入框
			@Override
			public void onClick(View arg0) {
				mLoginRegisterPhoneLayout.setVisibility(View.GONE);
				mLoginRegisterEmailLayout.setVisibility(View.VISIBLE);
			}
		});
		mLoginRegisterPhoneSubmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				registerByPhone();
				// Intent intent = new Intent(RegisterActivity.this,
				// RegisterVerifyActivity.class);
				// intent.putExtra("phone_number", "13908064279");
				// intent.putExtra("password", "258369");
				// intent.putExtra("nick_name", "彬彬");
				// startActivityForResult(intent, REGISTER_VER_CODE);
			}
		});
		addTextWatcher();
	}

	private void addTextWatcher() {
		mLoginRegisterPhoneUsername.addTextChangedListener(mTextWatcher);
		mLoginRegisterPhonePassword.addTextChangedListener(mTextWatcher);
		mLoginRegisterEmailUsername.addTextChangedListener(mTextWatcher);
		mLoginRegisterEmailPassword.addTextChangedListener(mTextWatcher);
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(mLoginRegisterPhoneUsername.getText())
					&& !TextUtils.isEmpty(mLoginRegisterPhonePassword.getText())) {
				mLoginRegisterPhoneSubmitBtn.setEnabled(true);
			} else {
				mLoginRegisterPhoneSubmitBtn.setEnabled(false);
			}
			if (!TextUtils.isEmpty(mLoginRegisterEmailUsername.getText())
					&& !TextUtils.isEmpty(mLoginRegisterEmailPassword.getText())) {
				mLoginRegisterEmailSubmitBtn.setEnabled(true);
			} else {
				mLoginRegisterEmailSubmitBtn.setEnabled(false);
			}
		}
	};

	/**
	 * 注册
	 * 
	 * @param username
	 * @param password
	 * @param nickName
	 * @param head
	 * @param sex
	 */
	private void register(String username, String password, String nickName, String head, String sex) {
		mLoginRegisterEmailSubmitBtn.setEnabled(false);
		if (SignUtil.flag) {
			registFromUCenter(username, password, nickName, head, sex);
		} else {
			News.register(username, password, this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					try {
						JSONObject object = result.getJSONObject(0);
						String returnCode = object.getString("returncode");
						if (returnCode.equalsIgnoreCase("SUCCESS")) {
							if (mLoginMode == LoginMode.Login_Customer) {
								Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
								mTabHost.setCurrentTab(0);
							} else {
								// uploadSocialUserInfo();
							}
						} else {
							Toast.makeText(RegisterActivity.this, object.getString("returnmsg"), Toast.LENGTH_SHORT)
									.show();
						}
					} catch (JSONException e) {
						Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onNG(String reason) {
					Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancel() {
					Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * 从ucenter注册的
	 * 
	 * @param userName
	 * @param passWord
	 */
	private void registFromUCenter(final String userName, final String passWord, final String nickName,
			final String head, final String sex) {
		SignUtil.registAccordUCtenterWithEMail(userName, passWord, nickName, head, sex, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				mLoginRegisterEmailSubmitBtn.setEnabled(true);
			}

			@SuppressLint("SetJavaScriptEnabled")
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mLoginRegisterEmailSubmitBtn.setEnabled(true);
				String returnData = null;
				try {
					returnData = new String(arg2, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				if (returnData.indexOf("javascript") != -1) // 返回有js的标签
															// 代表注册成功了
				{
					WebView webView = new WebView(RegisterActivity.this);
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
					});
					webView.loadUrl(returnData);

					mTabHost.setCurrentTab(0);
					if (mLoginMode == LoginMode.Login_Customer) {
						Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
					} else {
					}
					isAfterRegistLogin = true;
					login(userName, passWord);
					return;
				}
				try {
					JSONObject jsonObject;
					jsonObject = new JSONObject(returnData);
					String returncode = jsonObject.getString("returncode");
					String msg = jsonObject.getString("returnmsg");
					Log.d("zxd", "invoke ucenter regist interface back  returncode:" + returncode + " msg:" + msg);
					Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGISTER_VER_CODE && resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finishActivity();
		} else {
			/** 使用SSO授权必须添加如下代码 */
			UMSsoHandler ssoHandler = SignUtil.mUmSocialService.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	/**
	 * 通过手机号注册
	 */
	// @GinOnClick(id = { R.id.mLoginRegisterPhoneSubmitBtn })
	private void registerByPhone() {
		final String username = mLoginRegisterPhoneUsername.getText().toString().trim();
		final String password = mLoginRegisterPhonePassword.getText().toString().trim();
		final String nickName = nickNamePhoneEt.getText().toString().trim();
		if (!ValidateUtil.isMobileNum(username)) {
			Toast.makeText(RegisterActivity.this, "输入的手机号错误,请修改", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mLoginRegisterPhoneUsername.setFocusable(true);
					mLoginRegisterPhoneUsername.requestFocus();
					mLoginRegisterPhoneUsername.selectAll();
				}
			}, 500);
			return;
		}
		if (password.length() > 16 || password.length() < 6) {
			Toast.makeText(RegisterActivity.this, "密码长度不符合规范,请修改", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mLoginRegisterPhonePassword.setFocusable(true);
					mLoginRegisterPhonePassword.requestFocus();
					mLoginRegisterPhonePassword.selectAll();
				}
			}, 500);
			return;
		}
		if (nickName.length() > 10 || nickName.length() < 2) {
			Toast.makeText(RegisterActivity.this, "昵称长度不符合规范,请修改", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					nickNamePhoneEt.setFocusable(true);
					nickNamePhoneEt.requestFocus();
					nickNamePhoneEt.selectAll();
				}
			}, 500);
			return;
		}

		showProgressDialog("正在注册...");
		mLoginMode = LoginMode.Login_Customer;
		mLoginRegisterPhoneSubmitBtn.setEnabled(false);
		if (SignUtil.flag) {
			SignUtil.getMobileCaptchaTwo(username, nickName, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					dismissProgressDialog();
					String backData = null;
					try {
						backData = new String(arg2, "UTF-8");
						Log.d("zxd", "mobile captacha:" + backData);
						mLoginRegisterPhoneSubmitBtn.setEnabled(true);
						JSONObject jsonObject = new JSONObject(backData);
						if ("SUCCESS".equals(jsonObject.getString("returncode"))) {
							Toast.makeText(RegisterActivity.this, jsonObject.getString("returnmsg"), Toast.LENGTH_SHORT)
									.show();
							if (jsonObject.getString("returnmsg").contains("已注册")) {
								return;
							}
							Intent intent = new Intent(RegisterActivity.this, RegisterVerifyActivity.class);
							intent.putExtra("phone_number", username);
							intent.putExtra("password", password);
							intent.putExtra("nick_name", nickName);
							startActivityForResult(intent, REGISTER_VER_CODE);
						} else {
							Toast.makeText(RegisterActivity.this, jsonObject.getString("returnmsg"), Toast.LENGTH_SHORT)
									.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					dismissProgressDialog();
					mLoginRegisterPhoneSubmitBtn.setEnabled(true);
					Toast.makeText(RegisterActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			News.getMobileCaptchaTwo(username, nickName, RegisterActivity.this, new OnJsonArrayResultListener() {
				@Override
				public void onOK(JSONArray result) {
					dismissProgressDialog();
					mLoginRegisterPhoneSubmitBtn.setEnabled(true);
					try {
						if (result.getJSONObject(0).optString("returncode").trim().equalsIgnoreCase("SUCCESS")) {
							Toast.makeText(RegisterActivity.this, result.getJSONObject(0).optString("returnmsg"),
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(RegisterActivity.this, RegisterVerifyActivity.class);
							intent.putExtra("phone_number", username);
							intent.putExtra("password", password);
							intent.putExtra("nick_name", nickName);
							startActivityForResult(intent, REGISTER_VER_CODE);
							finishActivity();
						} else {
							Toast.makeText(RegisterActivity.this, result.getJSONObject(0).optString("returnmsg"),
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Toast.makeText(RegisterActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onNG(String reason) {
					dismissProgressDialog();
					mLoginRegisterPhoneSubmitBtn.setEnabled(true);
					Toast.makeText(RegisterActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancel() {
					dismissProgressDialog();
					mLoginRegisterPhoneSubmitBtn.setEnabled(true);
					Toast.makeText(RegisterActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@GinOnClick(id = { R.id.back_rl })
	public void back(View view) {
		setResult(RESULT_CANCELED);
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
