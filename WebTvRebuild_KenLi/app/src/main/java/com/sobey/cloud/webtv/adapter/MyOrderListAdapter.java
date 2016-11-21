package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.VolleyError;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.MyOrderActivity;
import com.sobey.cloud.webtv.PayDialogActivity;
import com.sobey.cloud.webtv.SendPingJiaActivity;
import com.sobey.cloud.webtv.ShopPingJiaActivity;
import com.sobey.cloud.webtv.bean.MyOrderListBean;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.Display;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的订单
 */
public class MyOrderListAdapter extends BaseAdapter implements OnScrollListener, View.OnClickListener {

	private Context context;
	public final List<MyOrderListBean> goodsItems;
	private DisplayImageOptions imageOptions;

	public MyOrderListAdapter(Context context) {
		this.context = context;
		goodsItems = new ArrayList<MyOrderListBean>();
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_thumbnail_banner)
				.showImageOnLoading(R.drawable.default_thumbnail_banner);
		imageOptions = builder.build();
	}

	public void addGoodsItem(List<MyOrderListBean> items) {
		goodsItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return goodsItems.size();
	}

	@Override
	public MyOrderListBean getItem(int index) {
		return goodsItems.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(R.layout.myoderlist_item, null);
			GinInjector.manualInjectView(holder, view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		try {
			setMainData(holder, index);
		} catch (JSONException e) {
		}
		return view;
	}

	private void setMainData(Holder holder, int index) throws JSONException {
		String oderNo = goodsItems.get(index).getOrderNo();
		if (oderNo != null) {
			holder.orderNoTv.setText("订单号：" + oderNo);
		}

		String orderId = ((MyOrderListBean) this.goodsItems.get(index)).getId();
		holder.payStatusTv.setTag(orderId);
		holder.payStatusTv.setOnClickListener(this);
		holder.cancelStatusTv.setTag(orderId);
		holder.cancelStatusTv.setOnClickListener(this);

		String status = goodsItems.get(index).getStatus();
		int statusRes = getStatusRes(status);
		if (statusRes != -1 && statusRes != 0) {
			if (status.equals("1") && (goodsItems.get(index).getPayType().equals("0"))) {// 是货到付款的情况
				holder.statusTv.setText("货到付款");
			} else {
				holder.pjStatus.setVisibility(View.GONE);
				holder.alreadyPayTv.setVisibility(View.GONE);
				holder.unPayLl.setVisibility(View.GONE);
				holder.statusTv.setText(statusRes);
				holder.receiptStatusTv.setVisibility(View.GONE);
				if (status.equals("5")) {// 订单完成显示评价按钮
					holder.pjStatus.setVisibility(View.VISIBLE);
				} else if (status.equals("1")) {
					holder.unPayLl.setVisibility(View.VISIBLE);
				} else if (status.equals("2")) {
					holder.receiptStatusTv.setVisibility(View.VISIBLE);
				}
			}
		}
		String realAmount = goodsItems.get(index).getRealAmount();
		if (realAmount != null) {
			holder.realamountTv.setText("金额：￥" + realAmount);
		}
		// 下面放入需要显示的商品信息
		final JSONArray goodJsonArray = goodsItems.get(index).getGoods();
		if (goodJsonArray.length() == 1) {// 只有一件商品的情况
			holder.singleGoodLayout.setVisibility(View.VISIBLE);
			holder.goodsLayout.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(
					MConfig.ORDER_PIC_HEAD + goodJsonArray.getJSONObject(0).getString("goods_img"), holder.goodIv,
					imageOptions);
			holder.goodTitle.setText(goodJsonArray.getJSONObject(0).getString("goods_name"));
		} else {// 多件商品的情况
			holder.goodIvContent.removeAllViews();
			holder.singleGoodLayout.setVisibility(View.GONE);
			holder.goodsLayout.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Display.dip2px(context, 75),
					Display.dip2px(context, 75));
			lp.setMargins(15, 0, 0, 0);
			for (int i = 0; i < goodJsonArray.length(); i++) {
				holder.singleGoodLayout.setVisibility(View.GONE);
				holder.goodsLayout.setVisibility(View.VISIBLE);
				ImageView iv = new ImageView(context);
				iv.setLayoutParams(lp);
				holder.goodIvContent.addView(iv);
				ImageLoader.getInstance().displayImage(
						MConfig.ORDER_PIC_HEAD + goodJsonArray.getJSONObject(i).getString("goods_img"), iv,
						imageOptions);
			}
		}
		final int itemId = index;
		String order_comment_status = goodsItems.get(index).getOrder_comment_status();
		if ("0".equals(order_comment_status)) {
			holder.pjStatus.setText("评价");
		} else {
			holder.pjStatus.setText("已评价");
		}
		holder.pjStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = null;
				MyOrderListBean mob = goodsItems.get(itemId);
				if (goodJsonArray.length() == 1) {
					intent = new Intent(context, SendPingJiaActivity.class);
				} else {
					intent = new Intent(context, ShopPingJiaActivity.class);
				}
				intent.putExtra("goodList", mob.getGoods().toString());
				context.startActivity(intent);
			}
		});
	}

	private int getStatusRes(String status) {
		int resID = -1;
		try {
			String ImageName;
			ImageName = "oder_status" + status;
			resID = context.getResources().getIdentifier(ImageName, "string", context.getPackageName());
		} catch (Exception e) {
			return resID;
		}
		return resID;
	}

	private class Holder {
		@GinInjectView(id = R.id.mol_oderno)
		TextView orderNoTv;
		@GinInjectView(id = R.id.mol_status)
		TextView statusTv;
		@GinInjectView(id = R.id.mol_singlegoods_layout)
		RelativeLayout singleGoodLayout;
		@GinInjectView(id = R.id.mol_manygoods_layout)
		HorizontalScrollView goodsLayout;
		@GinInjectView(id = R.id.mol_realamount)
		TextView realamountTv;
		@GinInjectView(id = R.id.mols_good_iv)
		ImageView goodIv;
		@GinInjectView(id = R.id.mols_title_iv)
		TextView goodTitle;
		@GinInjectView(id = R.id.mol_goodsiv_content)
		LinearLayout goodIvContent;
		@GinInjectView(id = R.id.mol_pjstatus)
		TextView pjStatus;

		@GinInjectView(id = R.id.alreay_pay_tv)
		private TextView alreadyPayTv;

		@GinInjectView(id = R.id.mol_cancelstatus)
		TextView cancelStatusTv;

		@GinInjectView(id = R.id.top_line_view)
		private View topLineView;

		@GinInjectView(id = R.id.un_pay_ll)
		private LinearLayout unPayLl;

		@GinInjectView(id = R.id.mol_paystatus)
		TextView payStatusTv;

		@GinInjectView(id = R.id.mol_receiptStatus)
		TextView receiptStatusTv;

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mol_paystatus:
			pay((String) v.getTag());
			return;
		case R.id.mol_cancelstatus:
			cancelOrder((String) v.getTag());
			return;
		case R.id.mol_receiptStatus:
			confirmReceived((String) v.getTag());
		}
	}

	private void pay(String paramString) {
		Intent localIntent = new Intent(this.context, PayDialogActivity.class);
		localIntent.putExtra("my_order_id", paramString);
		this.context.startActivity(localIntent);
		((Activity) context).overridePendingTransition(R.anim.dialog_in, 0);
	}

	private void confirmReceived(String paramString) {
		Toast.makeText(this.context, "操作中", 0).show();
		RequestParams localRequestParams = new RequestParams();
		localRequestParams.put("action", "confirmReceipt");
		localRequestParams.put("uid", PreferencesUtil.getLoggedUserId());
		localRequestParams.put("orderId", paramString);
		new AsyncHttpClient().get("http://shop.sobeycache.com/index.php?controller=appservice", localRequestParams,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] paramArrayOfHeader, byte[] paramArrayOfByte,
							Throwable paramThrowable) {
					}

					@Override
					public void onSuccess(int arg0, Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {
						ToastUtil.showToast(context, "操作成功");
						((MyOrderActivity) context).onRefresh();
					}
				});
	}

	/**
	 * 取消订单
	 */
	private void cancelOrder(String orderID) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "orderCancel");
		params.put("uid", PreferencesUtil.getLoggedUserId());
		params.put("orderId", orderID);
		VolleyRequset.doPost(context, MConfig.SHOP_URL, "cancelOrder", params, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				ToastUtil.showToast(context, "操作成功");
				((MyOrderActivity) context).onRefresh();
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFail(VolleyError arg0) {
				ToastUtil.showToast(context, "操作失败");
			}
		});
	}
}
