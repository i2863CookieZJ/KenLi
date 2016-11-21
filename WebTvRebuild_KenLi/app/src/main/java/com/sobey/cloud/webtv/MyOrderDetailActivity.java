package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sobey.cloud.webtv.adapter.MyOrderDetailAdapter;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.MyOrderDetailBean;
import com.sobey.cloud.webtv.bean.MyOrderDetailGoodsBean;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyOrderDetailActivity extends BaseActivity implements IDragListViewListener {

	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.myOrderList)
	private DragListView myOrderList;
	// @GinInjectView(id = R.id.top_back)
	// private View backBtn;
	// @GinInjectView(id = R.id.titlebar_name)
	// private TextView titlebar_name;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;
	@GinInjectView(id = R.id.submit_btn)
	private TextView submitBtn;

	private boolean isLoading;
	private String orderID;
	private String status;
	private String payType;
	private String distribution_status;
	private View headView;
	private View bottomView;

	private TextView orderNoTv;
	private TextView expressNoTv;
	private TextView expressCompanyTv;
	private TextView acceptNameTv;
	private TextView userPhoneTv;
	private TextView userAddressTv;
	private MyOrderDetailAdapter moda;

	private TextView totalGoodPrice;
	private TextView shipment;
	private TextView totalPrice;
	private TextView orderTime;
	private TextView orderCancer;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_ebusiness_myorder;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("订单详情");
		emptyTv.setText("网络开小差了，请稍后再试");
		orderID = getIntent().getStringExtra("my_order_id");
		payType = getIntent().getStringExtra("payType");
		status = getIntent().getStringExtra("good_status");
		headView = LayoutInflater.from(this).inflate(R.layout.orderdetail_headview, null);
		bottomView = LayoutInflater.from(this).inflate(R.layout.oderdetail_bottomview, null);
		orderNoTv = (TextView) headView.findViewById(R.id.odhv_orderno);
		expressNoTv = (TextView) headView.findViewById(R.id.express_no_tv);
		expressCompanyTv = (TextView) headView.findViewById(R.id.express_company_tv);
		orderCancer = (TextView) headView.findViewById(R.id.odhv_cancelorder);
		acceptNameTv = (TextView) headView.findViewById(R.id.odhv_username);
		userPhoneTv = (TextView) headView.findViewById(R.id.odhv_phonenum);
		userAddressTv = (TextView) headView.findViewById(R.id.odhv_address);

		totalGoodPrice = (TextView) bottomView.findViewById(R.id.total_goods_price);
		shipment = (TextView) bottomView.findViewById(R.id.shipment);
		totalPrice = (TextView) bottomView.findViewById(R.id.realcount);
		orderTime = (TextView) bottomView.findViewById(R.id.order_time);

		moda = new MyOrderDetailAdapter(this);
		myOrderList.addHeaderView(headView);
		myOrderList.addFooterView(bottomView);
		myOrderList.setAdapter(moda);
		myOrderList.setPullLoadEnable(false);
		myOrderList.setPullRefreshEnable(false);
		myOrderList.setListener(this);
		myOrderList.setFooterBackgroundColor(0xffffffff);
		// onRefresh();
		// backBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
		orderCancer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelOrder();
			}
		});
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tag = submitBtn.getTag().toString();
				switch (Integer.valueOf(tag)) {
				case 0:// 确认收货
					confirmReceived();
					break;
				case 1:// 支付
					Intent intent = new Intent(MyOrderDetailActivity.this, PayDialogActivity.class);
					intent.putExtra("my_order_id", orderID);
					startActivity(intent);
					overridePendingTransition(R.anim.dialog_in, 0);
					break;
				}
			}
		});
	}

	private void confirmReceived() {
		Toast.makeText(this, "操作中", Toast.LENGTH_SHORT).show();
		RequestParams params = new RequestParams();
		params.put("action", "confirmReceipt");
		params.put("uid", PreferencesUtil.getLoggedUserId());
		params.put("orderId", orderID);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(MConfig.SHOP_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Toast.makeText(MyOrderDetailActivity.this, "操作完成", Toast.LENGTH_SHORT).show();
				submitBtn.setVisibility(View.GONE);
			}

		});
	}

	private void refreshData(JSONObject orderData) throws JSONException {
		if (orderData == null) {
			loadingmask.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.VISIBLE);
			emptyTv.setText("获取数据失败");
			return;
		}
		myOrderList.stopLoadMore();
		myOrderList.stopRefresh();
		loadingmask.setVisibility(View.GONE);
		int code = orderData.getInt("code");
		status = orderData.getJSONObject("data").getString("status");
		payType = orderData.getJSONObject("data").getString("pay_type");
		distribution_status = orderData.getJSONObject("data").getString("distribution_status");
		if (code == 2000) {
			JSONObject data = orderData.getJSONObject("data");
			if (data == null) {
				emptyLayout.setVisibility(View.VISIBLE);
				myOrderList.setVisibility(View.GONE);
				myOrderList.setPullLoadEnable(true);
				return;
			}
			MyOrderDetailBean modb = new MyOrderDetailBean();
			modb.setOrderNo(data.getString("order_no"));
			modb.setAcceptName(data.getString("accept_name"));
			modb.setMobile(data.getString("mobile"));
			modb.setAddress(data.getString("address"));
			modb.setProvince(data.getString("province"));
			modb.setArea(data.getString("area"));
			modb.setCity(data.getString("city"));

			modb.setPayableAmount(data.getString("payable_amount"));// 商品总价
			modb.setPayableFreight(data.getString("payable_freight"));// 商品运费
			modb.setCreateTime(data.getString("create_time"));// 时间
			modb.setOrderAmount(data.getString("order_amount"));// 订单总价格

			orderNoTv.setText("订单号：" + modb.getOrderNo());

			if (data.has("delivery_code") && !"".equals(data.getString("delivery_code"))) {
				expressNoTv.setText("快递单号：" + data.getString("delivery_code"));
				expressCompanyTv.setText("快递公司：" + data.getString("freight_name"));
			} else {
				expressNoTv.setText("快递单号：暂无物流信息!");
				expressCompanyTv.setText("快递公司：暂无物流信息!");
			}

			acceptNameTv.setText(modb.getAcceptName());
			userPhoneTv.setText(modb.getMobile());
			String province = modb.getProvince();
			String area = modb.getArea();
			String city = modb.getCity();
			String address = modb.getAddress();
			String showAddress = "";
			if (province != null) {
				showAddress += province;
			}
			if (area != null) {
				showAddress += area;
			}
			if (city != null) {
				showAddress += city;
			}
			if (address != null) {
				showAddress += address;
			}
			userAddressTv.setText(showAddress);

			totalGoodPrice.setText("￥" + modb.getPayableAmount());
			shipment.setText("￥" + modb.getPayableFreight());
			totalPrice.setText("￥" + modb.getOrderAmount());
			orderTime.setText("下单时间：" + modb.getCreateTime());
			modb.setGoods(data.getJSONArray("goods"));
			List<MyOrderDetailGoodsBean> modgList = new ArrayList<MyOrderDetailGoodsBean>();
			for (int i = 0; i < modb.getGoods().length(); i++) {
				MyOrderDetailGoodsBean modgb = new MyOrderDetailGoodsBean();
				modgb.setShopName(modb.getGoods().getJSONObject(i).getString("shop_name"));
				modgb.setGoodsImg(modb.getGoods().getJSONObject(i).getString("goods_img"));
				modgb.setGoodsName(modb.getGoods().getJSONObject(i).getString("goods_name"));
				modgb.setGoodsNums(modb.getGoods().getJSONObject(i).getString("goods_nums"));
				modgb.setRealPrice(modb.getGoods().getJSONObject(i).getString("real_price"));
				modgList.add(modgb);
			}
			emptyLayout.setVisibility(View.GONE);
			myOrderList.setVisibility(View.VISIBLE);
			int statusInt = Integer.valueOf(status);
			// 根据paytype和status的值来确定按钮状态
			if (payType.equals("0")) {// 货到付款
				if (statusInt < 5) {
					submitBtn.setVisibility(View.VISIBLE);
					submitBtn.setText("确认收货");
					submitBtn.setTag(0);
					orderCancer.setVisibility(View.GONE);
				}
			} else {// 非货到付款
				if (statusInt == 1) {// 1.标识代付款
					submitBtn.setVisibility(View.VISIBLE);
					submitBtn.setText("支付");
					submitBtn.setTag(1);
					orderCancer.setVisibility(View.VISIBLE);
				} else if (statusInt == 2) {
					if ("0".equals(distribution_status)) {// 待发货
						submitBtn.setVisibility(View.GONE);
					} else {// 待收获
						submitBtn.setVisibility(View.VISIBLE);
						submitBtn.setText("确认收货");
					}
					submitBtn.setTag(0);
					orderCancer.setVisibility(View.GONE);
				} else {// 其它情况不显示按钮
					submitBtn.setVisibility(View.GONE);
					orderCancer.setVisibility(View.GONE);
				}

			}

			// if (status.equals("1")) {
			// submitBtn.setVisibility(View.VISIBLE);
			// submitBtn.setText("结算");
			// }
			moda.goodsItems.clear();
			moda.addGoodsItem(modgList);
		} else {
			emptyLayout.setVisibility(View.VISIBLE);
			myOrderList.setVisibility(View.GONE);
		}
	}

	class GetMyOderDetail extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject array = News.getMyOrderDetail(PreferencesUtil.getLoggedUserId(), orderID);
			return array;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			try {
				refreshData(result);
			} catch (JSONException e) {
			}
			isLoading = false;
		}
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onRefresh() {
		if (isLoading)
			return;
		isLoading = true;
		new GetMyOderDetail().execute();
	}

	@Override
	public void onResume() {
		onRefresh();
		super.onResume();
	}

	/**
	 * 取消订单
	 */
	private void cancelOrder() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "orderCancel");
		params.put("uid", PreferencesUtil.getLoggedUserId());
		params.put("orderId", orderID);
		VolleyRequset.doPost(this, MConfig.SHOP_URL, "cancelOrder", params, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				ToastUtil.showToast(MyOrderDetailActivity.this, "操作成功");
				submitBtn.setVisibility(View.GONE);
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFail(VolleyError arg0) {
				ToastUtil.showToast(MyOrderDetailActivity.this, "操作失败");
			}
		});
	}
}
