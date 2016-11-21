package com.sobey.cloud.webtv;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.ValidateUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CountDownButton;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePswActivity extends BaseActivity {

	@GinInjectView(id = R.id.ac_changpsw_telphone)
	private EditText telPhoneEt;
	@GinInjectView(id = R.id.ac_changpsw_sendyzm)
	private CountDownButton sendYzmBtn;
	@GinInjectView(id = R.id.ac_changpsw_yzm)
	private EditText yzmEt;
	@GinInjectView(id = R.id.ac_changpsw_psw)
	private EditText pswEt;
	@GinInjectView(id = R.id.ac_changpsw_submit)
	private Button submitBtn;

	/**
	 * 进度框
	 */
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		return R.layout.activity_changepsw;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
	}

	@GinOnClick(id = { R.id.ac_changpsw_sendyzm, R.id.ac_changpsw_submit })
	private void btnClick(View view) {
		switch (view.getId()) {
		case R.id.ac_changpsw_sendyzm:
			sendCaptcha();
			break;
		case R.id.ac_changpsw_submit:
			toChangePsw();
			break;
		}
	}

	private void sendCaptcha() {
		String telPhone = telPhoneEt.getText().toString();
		if (!ValidateUtil.isMobileNum(telPhone)) {
			Toast.makeText(this, "输入的手机号错误,请修改", Toast.LENGTH_SHORT).show();
			return;
		}
		sendYzmBtn.setEnabled(false);
		showProgressDialog("正在获取验证码...");
		SignUtil.getMobileCaptchaWithChangePsw(telPhone, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				dismissProgressDialog();
				String backData = null;
				try {
					backData = new String(arg2, "UTF-8");
					Log.d("zxd", "mobile captacha:" + backData);
					sendYzmBtn.setEnabled(true);
					JSONObject jsonObject = new JSONObject(backData);
					if ("SUCCESS".equals(jsonObject.getString("returncode"))) {
						sendYzmBtn.startCountDown();// 开始倒计时
					} else {
						Toast.makeText(ChangePswActivity.this, jsonObject.getString("returnmsg"), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendYzmBtn.setEnabled(true);
					Toast.makeText(ChangePswActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				dismissProgressDialog();
				sendYzmBtn.setEnabled(true);
				Toast.makeText(ChangePswActivity.this, "发送验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void toChangePsw() {
		if (checkInput()) {
			submitBtn.setEnabled(false);
			sendYzmBtn.setEnabled(false);
			String url = SignUtil.ServerURL + "?method=modifyUserPassword&userName=" + telPhoneEt.getText().toString()
					+ "&password=" + pswEt.getText().toString() + "&captcha=" + yzmEt.getText().toString();
			showProgressDialog("正在找回密码...");
			VolleyRequset.doGet(this, url, "toChangePsw", new VolleyListener() {

				@Override
				public void onSuccess(String arg0) {
					dismissProgressDialog();
					try {
						JSONObject jsonObject = new JSONObject(arg0);
						if ("FALURE".equals(jsonObject.getString("returncode"))) {
							Toast.makeText(ChangePswActivity.this, jsonObject.getString("returnmsg"),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(ChangePswActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();

							Intent intent = getIntent();
							Bundle bundle = new Bundle();
							bundle.putString("username", telPhoneEt.getText().toString());
							bundle.putString("password", pswEt.getText().toString());
							intent.putExtras(bundle);
							setResult(RESULT_OK, intent);
							finishActivity();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					submitBtn.setEnabled(true);
					sendYzmBtn.setEnabled(true);
				}

				@Override
				public void onFail(VolleyError arg0) {
					dismissProgressDialog();
					submitBtn.setEnabled(true);
					sendYzmBtn.setEnabled(true);
					Toast.makeText(ChangePswActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	/**
	 * 检查输入
	 * 
	 * @return
	 */
	private boolean checkInput() {
		String telPhone = telPhoneEt.getText().toString();
		String yzm = yzmEt.getText().toString();
		String psw = pswEt.getText().toString();
		if (!ValidateUtil.isMobileNum(telPhone)) {
			Toast.makeText(this, "输入的手机号错误,请修改", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(yzm)) {
			Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (psw.length() > 16 || psw.length() < 6) {
			Toast.makeText(this, "密码长度不符合规范,请修改", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sendYzmBtn.stopCountDown();
	}
}
