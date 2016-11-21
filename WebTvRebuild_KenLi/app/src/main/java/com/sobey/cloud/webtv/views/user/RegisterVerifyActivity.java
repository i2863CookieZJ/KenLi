package com.sobey.cloud.webtv.views.user;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sobey.cloud.webtv.RegisterModifyNicknameActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;
import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterVerifyActivity extends BaseActivity {

	private static int REGISTER_VER_NICKNAME_CODE = 2;

	private static final int COUNT_DOWN = 60;

	private int mCountDown = COUNT_DOWN;
	private String mUsername;
	private String mPassword;
	private String nickName;

	@GinInjectView(id = R.id.mRegisterVerifyNotice)
	TextView mRegisterVerifyNotice;

	@GinInjectView(id = R.id.mRegisterVerifyNumber)
	EditText mRegisterVerifyNumber;

	@GinInjectView(id = R.id.mRegisterVerifySubmitBtn)
	TextView mRegisterVerifySubmitBtn;

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mRegisterVerifyBack;

	@GinInjectView(id = R.id.mRegisterVerifyCountDownLayout)
	LinearLayout mRegisterVerifyCountDownLayout;

	@GinInjectView(id = R.id.mRegisterVerifyCountDown)
	TextView mRegisterVerifyCountDown;

	@GinInjectView(id = R.id.mRegisterVerifyResend)
	TextView mRegisterVerifyResend;

	/**
	 * 进度框
	 */
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_register_verify;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		mUsername = getIntent().getStringExtra("phone_number");
		mPassword = getIntent().getStringExtra("password");
		nickName = getIntent().getStringExtra("nick_name");
		if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(nickName)) {
			Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
		}

		mRegisterVerifyNotice.setText("您的手机号码：" + mUsername);

		mRegisterVerifyBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finishActivity();
			}
		});

		mRegisterVerifyResend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgressDialog("正在发送验证码...");
				mRegisterVerifyResend.setEnabled(false);
				if (SignUtil.flag)
					SignUtil.getMobileCaptcha(mUsername, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							dismissProgressDialog();
							String backData = null;
							try {
								backData = new String(arg2, "UTF-8");
								Log.d("zxd", "mobile captacha:" + backData);
								mRegisterVerifyResend.setEnabled(true);
								JSONObject jsonObject = new JSONObject(backData);
								if ("SUCCESS".equals(jsonObject.getString("returncode"))) {
									Toast.makeText(RegisterVerifyActivity.this, jsonObject.getString("returnmsg"),
											Toast.LENGTH_SHORT).show();
									mRegisterVerifyCountDownLayout.setVisibility(View.VISIBLE);
									mRegisterVerifyResend.setVisibility(View.GONE);
									mCountDown = COUNT_DOWN;
									mRegisterVerifyCountDown.setText(mCountDown + "秒");
								} else {
									Toast.makeText(RegisterVerifyActivity.this, jsonObject.getString("returnmsg"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
							}

						}

						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							dismissProgressDialog();
							mRegisterVerifyResend.setEnabled(true);
							Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					});
				else {
					News.getMobileCaptcha(mUsername, RegisterVerifyActivity.this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							dismissProgressDialog();
							mRegisterVerifyResend.setEnabled(true);
							try {
								if (result.getJSONObject(0).optString("returncode").trim()
										.equalsIgnoreCase("SUCCESS")) {
									Toast.makeText(RegisterVerifyActivity.this,
											result.getJSONObject(0).optString("returnmsg"), Toast.LENGTH_SHORT).show();
									mRegisterVerifyCountDownLayout.setVisibility(View.VISIBLE);
									mRegisterVerifyResend.setVisibility(View.GONE);
									mCountDown = COUNT_DOWN;
									mRegisterVerifyCountDown.setText(mCountDown + "秒");
								} else {
									Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							} catch (Exception e) {
								Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onNG(String reason) {
							dismissProgressDialog();
							mRegisterVerifyResend.setEnabled(true);
							Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onCancel() {
							dismissProgressDialog();
							mRegisterVerifyResend.setEnabled(true);
							Toast.makeText(RegisterVerifyActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});

		mRegisterVerifySubmitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String captcha = mRegisterVerifyNumber.getText().toString().trim();
				if (TextUtils.isEmpty(captcha)) {
					mRegisterVerifyNumber.setFocusable(true);
					mRegisterVerifyNumber.requestFocus();
					mRegisterVerifyNumber.selectAll();
					InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mRegisterVerifyNumber, 0);
					Toast.makeText(RegisterVerifyActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
					return;
				}
				showProgressDialog("正在校验验证码...");
				if (SignUtil.flag) {
					SignUtil.registerAccordUCenterWidthMobile(mUsername, mPassword, nickName, captcha,
							new AsyncHttpResponseHandler() {

								@Override
								public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
									dismissProgressDialog();
									String backData = null;
									try {
										backData = new String(arg2, "UTF-8");
										// 注册成功
										if (backData.indexOf("script") != -1) {
											Intent intent = getIntent();
											Bundle bundle = new Bundle();
											bundle.putString("username", mUsername);
											bundle.putString("password", mPassword);
											intent.putExtras(bundle);
											setResult(RESULT_OK, intent);
											finishActivity();
											return;
										} else {
											JSONObject jsonObject = new JSONObject(backData);
											Toast.makeText(RegisterVerifyActivity.this,
													jsonObject.getString("returnmsg"), Toast.LENGTH_SHORT).show();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
									dismissProgressDialog();
									Toast.makeText(RegisterVerifyActivity.this, "验证失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							});
				} else {
					News.mobileRegister(mUsername, mPassword, captcha, RegisterVerifyActivity.this,
							new OnJsonArrayResultListener() {
								@Override
								public void onOK(JSONArray result) {
									dismissProgressDialog();
									try {
										if (result.getJSONObject(0).optString("returncode").trim()
												.equalsIgnoreCase("SUCCESS")) {
											Intent intent = new Intent(RegisterVerifyActivity.this,
													RegisterModifyNicknameActivity.class);
											intent.putExtra("phone_number", mUsername);
											startActivityForResult(intent, REGISTER_VER_NICKNAME_CODE);
										} else {
											Toast.makeText(RegisterVerifyActivity.this, "验证失败，请输入正确的验证码",
													Toast.LENGTH_SHORT).show();
										}
									} catch (Exception e) {
										Toast.makeText(RegisterVerifyActivity.this, "验证失败，请稍后重试", Toast.LENGTH_SHORT)
												.show();
									}
								}

								@Override
								public void onNG(String reason) {
									dismissProgressDialog();
									Toast.makeText(RegisterVerifyActivity.this, "验证失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								public void onCancel() {
									dismissProgressDialog();
									Toast.makeText(RegisterVerifyActivity.this, "验证失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							});
				}
			}
		});

		mRegisterVerifyCountDownLayout.setVisibility(View.VISIBLE);
		mRegisterVerifyResend.setVisibility(View.GONE);
		new Thread(new MyThread()).start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (mCountDown > 0) {
					mRegisterVerifyCountDown.setText(mCountDown + "秒");
					mCountDown--;
				} else if (mCountDown == 0) {
					mRegisterVerifyCountDownLayout.setVisibility(View.GONE);
					mRegisterVerifyResend.setVisibility(View.VISIBLE);
					mCountDown = -1;
				} else {

				}
			}
			super.handleMessage(msg);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGISTER_VER_NICKNAME_CODE && resultCode == RESULT_OK) {

		}
	};

	public class MyThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
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
