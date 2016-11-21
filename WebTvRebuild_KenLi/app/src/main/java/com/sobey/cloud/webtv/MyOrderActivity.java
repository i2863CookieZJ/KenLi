package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.adapter.MyOrderListAdapter;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.MyOrderListBean;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.widgets.CustomTitleView;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyOrderActivity extends BaseActivity implements IDragListViewListener {

	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.myOrderList)
	private DragListView myOrderList;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;

	@GinInjectView(id = R.id.nocontent_layout)
	LinearLayout noContentLayout;

	private boolean isLoading;
	private int loadPageIndex;
	private MyOrderListAdapter adapter;
	private List<MyOrderListBean> nowList = null;

	private int getType = 0;// (1-待付款,2-已支付,3-已发货,4-已完成) | int(11) | 否 | 0 |

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_ebusiness_myorder;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("我的订单");
		getType = this.getIntent().getIntExtra("getType", 0);
		emptyTv.setText(R.string.has_no_result);
		emptyLayout.setVisibility(View.GONE);
		noContentLayout.setVisibility(View.GONE);
		adapter = new MyOrderListAdapter(this);
		myOrderList.setAdapter(adapter);
		myOrderList.setPullLoadEnable(false);
		myOrderList.setPullRefreshEnable(true);
		myOrderList.setListener(this);
		myOrderList.setFooterBackgroundColor(0xffffffff);
		myOrderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (nowList == null || nowList.size() == 0) {
					return;
				}
				String payType = nowList.get(arg2 - 1).getPayType();
				String orderId = nowList.get(arg2 - 1).getId();
				String status = nowList.get(arg2 - 1).getStatus();
				// 有何意义
				// if (payType.equals("0")) {
				// status = "0";
				// }
				Intent intent = new Intent(MyOrderActivity.this, MyOrderDetailActivity.class);
				intent.putExtra("payType", payType);
				intent.putExtra("my_order_id", orderId);
				intent.putExtra("good_status", status);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}

	@Override
	public void onRefresh() {
		if (isLoading)
			return;
		loadPageIndex = 1;
		isLoading = true;
		new GetMyOrderData().execute();
	}

	@Override
	public void onLoadMore() {
		if (isLoading)
			return;
		loadPageIndex += 1;
		isLoading = true;
		new GetMyOrderData().execute();
	}

	public class GetMyOrderData extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... arg0) {
			JSONObject array = News.getMyOraderList(PreferencesUtil.getLoggedUserId(), loadPageIndex, getType);
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

	private void refreshData(JSONObject orderData) throws JSONException {
		myOrderList.stopLoadMore();
		myOrderList.stopRefresh();
		loadingmask.setVisibility(View.GONE);
		if (orderData == null) {
			return;
		}
		int code = orderData.getInt("code");
		if (code == 2000) {
			JSONArray data = orderData.getJSONArray("data");
			if (data == null || data.length() == 0) {// 数据为空的时候
				ToastUtil.showToast(this, "暂无最新订单数据");
				if (loadPageIndex > 1) {// 是加载更多的时候
					loadPageIndex -= 1;
					myOrderList.setPullLoadEnable(false);
				} else {// 不是加载更多的时候
					switch (getType) {
					case 0:
						emptyLayout.setVisibility(View.VISIBLE);
						break;
					case 1:
						noContentLayout.setVisibility(View.VISIBLE);
						showNoContent(noContentLayout, R.drawable.nocontent_waitpay, "您还没有待支付的订单...");
						break;
					case 2:
						noContentLayout.setVisibility(View.VISIBLE);
						showNoContent(noContentLayout, R.drawable.nocontent_waitsend, "您还没有待发货的订单...");
						break;
					case 3:
						noContentLayout.setVisibility(View.VISIBLE);
						showNoContent(noContentLayout, R.drawable.nocontent_waitget, "您还有没待收货的订单...");
						break;
					case 4:
						noContentLayout.setVisibility(View.VISIBLE);
						showNoContent(noContentLayout, R.drawable.nocontent_done, "您还没有已完成的订单...");
						break;
					}

					myOrderList.setVisibility(View.GONE);
					myOrderList.setPullLoadEnable(true);
				}
				return;
			}
			List<MyOrderListBean> mlbList = new ArrayList<MyOrderListBean>();
			for (int i = 0; i < data.length(); i++) {
				MyOrderListBean mlb = new MyOrderListBean();
				mlb.setRealFreight(data.getJSONObject(i).getString("real_freight"));
				mlb.setId(data.getJSONObject(i).getString("id"));
				mlb.setSendTime(data.getJSONObject(i).getString("send_time"));
				mlb.setRealAmount(data.getJSONObject(i).getString("real_amount"));
				mlb.setPayableFreight(data.getJSONObject(i).getString("payable_freight"));
				mlb.setStatus(data.getJSONObject(i).getString("completion_time"));
				mlb.setGoods(data.getJSONObject(i).getJSONArray("goods"));
				mlb.setCreateTime(data.getJSONObject(i).getString("create_time"));
				mlb.setPayableAmount(data.getJSONObject(i).getString("payable_amount"));
				mlb.setPayTime(data.getJSONObject(i).getString("pay_time"));
				mlb.setOrderNo(data.getJSONObject(i).getString("order_no"));
				mlb.setStatus(data.getJSONObject(i).getString("status"));
				mlb.setPayType(data.getJSONObject(i).getString("pay_type"));
				mlb.setOrder_comment_status(data.getJSONObject(i).getString("order_comment_status"));
				mlbList.add(mlb);
			}
			emptyLayout.setVisibility(View.GONE);
			noContentLayout.setVisibility(View.GONE);
			myOrderList.setVisibility(View.VISIBLE);
			if (loadPageIndex == 1) {
				adapter.goodsItems.clear();
				nowList = mlbList;
			} else {
				nowList.addAll(mlbList);
			}
			adapter.addGoodsItem(mlbList);
		} else {
			switch (getType) {
			case 0:
				emptyLayout.setVisibility(View.VISIBLE);
				break;
			case 1:
				noContentLayout.setVisibility(View.VISIBLE);
				showNoContent(noContentLayout, R.drawable.nocontent_waitpay, "您还没有待支付的订单...");
				break;
			case 2:
				noContentLayout.setVisibility(View.VISIBLE);
				showNoContent(noContentLayout, R.drawable.nocontent_waitsend, "您还没有待发货的订单...");
				break;
			case 3:
				noContentLayout.setVisibility(View.VISIBLE);
				showNoContent(noContentLayout, R.drawable.nocontent_waitget, "您还有没待收货的订单...");
				break;
			case 4:
				noContentLayout.setVisibility(View.VISIBLE);
				showNoContent(noContentLayout, R.drawable.nocontent_done, "您还没有已完成的订单...");
				break;
			}
			myOrderList.setVisibility(View.GONE);
		}
	}

}
