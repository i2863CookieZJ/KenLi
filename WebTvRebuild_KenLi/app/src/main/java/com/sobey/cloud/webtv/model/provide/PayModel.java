package com.sobey.cloud.webtv.model.provide;

import java.util.LinkedHashMap;

import org.json.JSONObject;

import com.higgses.griffin.core.inf.GinIControl;
import com.higgses.griffin.volley.VolleyError;
import com.sobey.cloud.webtv.config.ServerConfig;
import com.sobey.cloud.webtv.model.provide.common.CommonModel;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class PayModel extends CommonModel {

	/**
	 * Handler
	 */
	private Handler mHandler;

	/**
	 * 进度提示
	 */
	private CustomProgressDialog mProgressDialog;

	/**
	 * 支付
	 */
	private static String PAY = "pay";

	public PayModel(GinIControl<?> control) {
		super(control);
	}

	/**
	 * 取消订单
	 * 
	 * @param uid
	 * @param orderId
	 */
	public void orderCancel(String uid, String orderId) {
		pay(ServerConfig.ORDERCANCEL, uid, orderId);
	}

	/**
	 * 下订单
	 * 
	 * @param uid
	 * @param orderId
	 */
	public void doDelivery(String uid, String orderId) {
		pay(ServerConfig.DODELIVERY, uid, orderId);
	}

	/**
	 * 支付
	 *
	 */
	private void pay(String action, String uid, String orderId) {
		try {
			LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
			param.put("action", action);
			param.put("uid", uid);
			param.put("orderId", orderId);

			ServerData.pay(param, new IResponse<Object>() {
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
					message.obj = PAY;
					message.what = errorCode;
					Bundle bundle = new Bundle();
					bundle.putString("errorMsg", errorMsg);
					message.setData(bundle);
					mHandler.sendMessage(message);
					dataCallback(result, "payCallBack");
				}

				@Override
				public void onError(Object error) {
					sendErrorMessage((VolleyError) error, PAY);
					dataCallback(true, "payCallBack");
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
		if (PAY.equals(obj)) {
			if (what == ServerConfig.SUCCESS_CODE) {
				ToastUtil.showToast(getContext(), "操作成功");
			} else if (what == ServerConfig.ERROR_CODE) {
				ToastUtil.showToast(getContext(), "操作失败");
			} else {
				ToastUtil.showToast(getContext(), msg.getData().getString("errorMsg"));
			}
		}
		return false;
	}
}
