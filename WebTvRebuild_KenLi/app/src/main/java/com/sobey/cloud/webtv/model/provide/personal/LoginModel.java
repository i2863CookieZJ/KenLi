package com.sobey.cloud.webtv.model.provide.personal;

import java.util.LinkedHashMap;

import org.json.JSONObject;

import com.higgses.griffin.core.inf.GinIControl;
import com.higgses.griffin.volley.VolleyError;
import com.sobey.cloud.webtv.config.ServerConfig;
import com.sobey.cloud.webtv.model.provide.ServerData;
import com.sobey.cloud.webtv.model.provide.common.CommonModel;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 登录
 * 
 * @author lazy
 *
 */
public class LoginModel extends CommonModel {

	/**
	 * Handler
	 */
	private Handler mHandler;

	/**
	 * 进度提示
	 */
	private CustomProgressDialog mProgressDialog;

	/**
	 * 登录
	 */
	private static String LOGIN = "login";

	public LoginModel(GinIControl<?> control) {
		super(control);
		mHandler = new Handler(this);
	}

	/**
	 * 登录
	 * 
	 * @param siteId
	 * @param userName
	 * @param Password
	 */
	public void login(int siteId, final String userName, String Password) {
		try {
			LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
			param.put("siteId", String.valueOf(siteId));
			param.put("userName", userName);
			param.put("Password", Password);

			ServerData.login(param, new IResponse<Object>() {
				@Override
				public void onResponse(Object data) {
					Message message = new Message();
					message.obj = LOGIN;
					dataCallback(userName + "_loginSuccessCallBack_" + String.valueOf(data), "loginSuccessCallBack");
				}

				@Override
				public void onError(Object error) {
					sendErrorMessage((VolleyError) error, LOGIN);
					dataCallback("loginFail", "loginFailCallBack");
				}
			});
		} catch (Exception e) {

		}

	}

	/**
	 * 发送错误消息
	 *
	 * @param error
	 * @param obj
	 */
	private void sendErrorMessage(VolleyError error, Object obj) {
		int errorCode;
		String errorMsg = "";
		try {
			VolleyError volleyError = (VolleyError) error;
			String errorJson = new String(volleyError.networkResponse.data);
			JSONObject errorJsonObject = new JSONObject(errorJson);
			errorCode = errorJsonObject.getInt("error_code");
			errorMsg = errorJsonObject.getString("error_msg");
		} catch (Exception e) {
			errorCode = ServerConfig.ERROR_CODE;
		}
		Message message = new Message();
		message.obj = obj;
		message.what = errorCode;
		Bundle bundle = new Bundle();
		bundle.putString("errorMsg", errorMsg);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	@Override
	public boolean handleMessage(Message msg) {
		super.handleMessage(msg);
		if (null != mProgressDialog) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}

		int what = msg.what;
		Object obj = msg.obj;

		return false;
	}
}
