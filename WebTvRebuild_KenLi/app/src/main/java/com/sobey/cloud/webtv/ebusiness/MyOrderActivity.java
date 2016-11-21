package com.sobey.cloud.webtv.ebusiness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/***
 * 我的订单
 * 
 * @author zouxudong
 *
 */
public class MyOrderActivity extends BaseActivity implements IDragListViewListener {
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.myOrderList)
	private DragListView myOrderList;
	@GinInjectView(id = R.id.top_back)
	private View backBtn;
	@GinInjectView(id = R.id.titlebar_name)
	private TextView titlebar_name;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;

	private boolean isLoading;

	private int loadPageIndex;

	private int maxPageCount;

	private int signleLoadCount = 10;

	private MyOrderAdapter adaptor;

	@Override
	public int getContentView() {
		return R.layout.activity_ebusiness_myorder;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		titlebar_name.setText("我的订单");
		emptyTv.setText(R.string.has_no_result);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MyOrderActivity.this.finish();
			}
		});
		emptyLayout.setVisibility(View.GONE);

		adaptor = new MyOrderAdapter(this);
		myOrderList.setAdapter(adaptor);
		myOrderList.setOnItemClickListener(adaptor);

		myOrderList.setPullLoadEnable(false);
		myOrderList.setPullRefreshEnable(true);
		myOrderList.setListener(this);
		myOrderList.setFooterBackgroundColor(0xffffffff);
		onRefresh();
	}

	protected void refreshData(JSONObject orderData) {
		myOrderList.stopLoadMore();
		myOrderList.stopRefresh();
		loadingmask.setVisibility(View.GONE);
		JSONArray data = null;
		if (orderData != null)
			data = orderData.optJSONArray("orders");
		else {
			loadPageIndex -= 1;
		}
		if (orderData == null || data == null || data.length() == 0) {
			ToastUtil.showToast(this, "暂无最新订单数据");
			if (adaptor.goodsItems.size() == 0) {
				emptyLayout.setVisibility(View.VISIBLE);
				myOrderList.setVisibility(View.GONE);
			}
			return;
		}
		maxPageCount = orderData.optInt("totalPage");
		if (loadPageIndex == 1) {
			adaptor.goodsItems.clear();
		}
		ArrayList<OrderItem> listOrder = new ArrayList<OrderItem>();
		for (int j = 0; j < data.length(); j++) {
			JSONObject jsonObject = data.optJSONObject(j);
			if (jsonObject == null)
				continue;
			JSONArray orderGoods = jsonObject.optJSONArray("goods");
			if (orderGoods == null)
				continue;
			OrderItem itemOrder = new OrderItem(jsonObject);
			ArrayList<OrderGoodsItem> list = new ArrayList<OrderGoodsItem>();

			int max = orderGoods.length();
			for (int i = 0; i < max; i++) {
				JSONObject goodsItem = orderGoods.optJSONObject(i);
				OrderGoodsItem item = new OrderGoodsItem(goodsItem);
				list.add(item);
			}
			itemOrder.goods.addAll(list);
			listOrder.add(itemOrder);
		}
		emptyLayout.setVisibility(View.GONE);
		myOrderList.setVisibility(View.VISIBLE);
		Collections.sort(listOrder, new CompareOrderDate());
		adaptor.addGoodsItem(listOrder);
		if (loadPageIndex >= maxPageCount)
			myOrderList.setPullLoadEnable(false);
		else
			myOrderList.setPullLoadEnable(true);
	}

	class CompareOrderDate implements Comparator<OrderItem> {
		public CompareOrderDate() {

		}

		@Override
		public int compare(OrderItem item1, OrderItem item2) {
			Date item1Date = DateParse.parseDate(item1.orderDate, null);
			Date item2Date = DateParse.parseDate(item2.orderDate, null);
			if (item1Date.after(item2Date))
				return -1;
			return 1;
		}

	}

	public class GetEBusinessData extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject array = News.getEBusinessMyOrader(PreferencesUtil.getLoggedUserId(), loadPageIndex,
					signleLoadCount);
			return array;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			refreshData(result);
			isLoading = false;
		}

	}

	@Override
	public void onRefresh() {
		if (isLoading)
			return;
		loadPageIndex = 1;
		isLoading = true;
		new GetEBusinessData().execute();
	}

	@Override
	public void onLoadMore() {
		if (isLoading)
			return;
		loadPageIndex += 1;
		isLoading = true;
		new GetEBusinessData().execute();
	}
}
