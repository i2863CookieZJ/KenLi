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

public class SettingModel extends CommonModel {

	/**
	 * Handler
	 */
	private Handler mHandler;

	/**
	 * 进度提示
	 */
	private CustomProgressDialog mProgressDialog;

	/**
	 * 退出登陆
	 */
	private static String LOGOUT = "logout";

	public SettingModel(GinIControl<?> control) {
		super(control);
		mHandler = new Handler(this);
	}

	/**
	 * 退出登陆
	 *
	 * @param uid
	 */
	public void logout(String uid) {
		try {
			LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
			param.put("uid", uid);

			ServerData.logout(param, new IResponse<Object>() {
				@Override
				public void onResponse(Object data) {
					boolean result = false;
					int errorCode = ServerConfig.SUCCESS_CODE;
					String errorMsg = "";
					try {
						JSONObject obj = (JSONObject) data;
						result = obj.getInt("status") == 1;
						if (result) {
							boolean isSuccess = obj.getBoolean("isSuccess");
							if (isSuccess) {
								result = false;
							}
						} else {
							errorCode = obj.getInt("error_code");
							errorMsg = obj.getString("error_msg");
						}
					} catch (Exception e) {
						errorCode = ServerConfig.ERROR_CODE;
					}
					Message message = new Message();
					message.obj = LOGOUT;
					message.what = errorCode;
					Bundle bundle = new Bundle();
					bundle.putString("errorMsg", errorMsg);
					message.setData(bundle);
					mHandler.sendMessage(message);
					dataCallback(result, "logoutCallBack");
				}

				@Override
				public void onError(Object error) {
					sendErrorMessage((VolleyError) error, LOGOUT);
					dataCallback(true, "logoutCallBack");
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
