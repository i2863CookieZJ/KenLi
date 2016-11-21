package com.sobey.cloud.webtv;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.VolleyError;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.model.provide.PayModel;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class PayDialogActivity extends BaseActivity<PayModel> implements OnClickListener {
	private LinearLayout topLayout;
	private Button cancelOrderBtn;
	private Button ylPayBtn;
	private Button alipayBtn;
	private Button getpayBtn;
	private Button exitBtn;
	private String orderId;

	/**
	 * 数据模型
	 */
	private PayModel mMoel;

	@Override
	public int getContentView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		return R.layout.activity_paydialog;

	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mMoel = getModel();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏O
		initUI();
		initData();
	}

	/**
	 * 支付回调
	 * 
	 * @param result
	 */
	public void payCallBack(Boolean result) {
		if (result.booleanValue()) {

		}
	}

	private void initUI() {
		topLayout = (LinearLayout) findViewById(R.id.ac_paydilog_toplayout);
		topLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finishActivity();
				return false;
			}
		});
		cancelOrderBtn = (Button) findViewById(R.id.ac_paydilog_cancelorder);
		cancelOrderBtn.setOnClickListener(this);
		ylPayBtn = (Button) findViewById(R.id.ac_paydilog_ylpay);
		ylPayBtn.setOnClickListener(this);
		alipayBtn = (Button) findViewById(R.id.ac_paydilog_alipay);
		alipayBtn.setOnClickListener(this);
		getpayBtn = (Button) findViewById(R.id.ac_paydilog_getpay);
		getpayBtn.setOnClickListener(this);
		exitBtn = (Button) findViewById(R.id.ac_paydilog_exit);
		exitBtn.setOnClickListener(this);
	}

	private void initData() {
		orderId = this.getIntent().getStringExtra("my_order_id");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ac_paydilog_cancelorder:
			ToastUtil.showToast(this, "正在操作");
			// mMoel.orderCancel(PreferencesUtil.getLoggedUserId(), orderId);
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "orderCancel");
			params.put("uid", PreferencesUtil.getLoggedUserId());
			params.put("orderId", orderId);
			doPost(params);
			break;
		case R.id.ac_paydilog_ylpay:
			Intent intent = new Intent(this, MyOderWBActivity.class);
			intent.putExtra("url", "http://shop.sobeycache.com/index.php?controller=block&action=doPay&order_id="
					+ orderId + "&uid=" + PreferencesUtil.getLoggedUserId() + "&payment_id=" + 14);
			intent.putExtra("my_order_id", orderId);
			startActivity(intent);
			finishActivity();
			break;
		case R.id.ac_paydilog_alipay:
			Intent intent2 = new Intent(this, MyOderWBActivity.class);
			intent2.putExtra("url", "http://shop.sobeycache.com/index.php?controller=block&action=doPay&order_id="
					+ orderId + "&uid=" + PreferencesUtil.getLoggedUserId() + "&payment_id=" + 12);
			intent2.putExtra("my_order_id", orderId);
			startActivity(intent2);
			finishActivity();
			break;
		case R.id.ac_paydilog_getpay:
			ToastUtil.showToast(this, "正在操作");
			// mMoel.doDelivery(PreferencesUtil.getLoggedUserId(), orderId);
			ToastUtil.showToast(this, "正在操作");
			Map<String, String> params2 = new HashMap<String, String>();
			params2.put("action", "doDelivery");
			params2.put("uid", PreferencesUtil.getLoggedUserId());
			params2.put("orderId", orderId);
			doPost(params2);
			break;
		case R.id.ac_paydilog_exit:
			finishActivity();
			break;
		}
	}

	private void doPost(Map<String, String> map) {
		VolleyRequset.doPost(this, MConfig.SHOP_URL, "", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				ToastUtil.showToast(PayDialogActivity.this, "操作成功");
			}

			@Override
			public void onFinish() {
				finishActivity();
			}

			@Override
			public void onFail(VolleyError arg0) {
				ToastUtil.showToast(PayDialogActivity.this, "操作失败");
			}
		});
	}

}
