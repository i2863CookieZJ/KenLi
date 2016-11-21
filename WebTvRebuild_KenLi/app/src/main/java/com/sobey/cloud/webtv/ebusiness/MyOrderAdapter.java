package com.sobey.cloud.webtv.ebusiness;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我的订单
 * 
 * @author zouxudong
 *
 */
public class MyOrderAdapter extends BaseAdapter implements OnItemClickListener, OnScrollListener {

	private Context context;
	public final ArrayList<OrderItem> goodsItems;

	public MyOrderAdapter(Context context) {
		this.context = context;
		goodsItems = new ArrayList<OrderItem>();
	}

	public void addGoodsItem(ArrayList<OrderItem> items) {
		goodsItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return goodsItems.size();
	}

	@Override
	public OrderItem getItem(int index) {
		return goodsItems.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_ebusiness_myorder, null);
			Holder holder = new Holder();
			GinInjector.manualInjectView(holder, view);
			view.setTag(holder);
		}
		loadViewHolder(view, index);
		return view;
	}

	protected void loadViewHolder(View view, int index) {
		Log.d("zxd", "列表:" + index);
		Holder holder = (Holder) view.getTag();
		OrderItem item = getItem(index);
		if (item.goods.size() > 1) {
			showMutipleGoodsViewData(item, holder);
		} else {
			showSigleGoodsViewData(item, holder, item.carriage);
		}
	}

	/**
	 * 多件商品一个定单的显示
	 * 
	 * @param list
	 * @param holder
	 */
	protected void showMutipleGoodsViewData(OrderItem item, Holder holder) {
		final List<OrderGoodsItem> list = item.goods;
		holder.singleGoodsContainer.setVisibility(View.GONE);
		holder.mutipleGoodsListContainer.setVisibility(View.VISIBLE);
		// holder.goodsImageContainer.removeAllViews();
		int childImageComponentCount = holder.goodsImageContainer.getChildCount();
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		double price = item.carriage;
		if (childImageComponentCount > 0) {
			boolean enough = childImageComponentCount <= list.size();
			int startIndex = 0;
			View[] view = new View[childImageComponentCount];
			for (int i = 0; i < childImageComponentCount; i++) {
				view[i] = holder.goodsImageContainer.getChildAt(i);
				GoodsImageHolder imageHolder = (GoodsImageHolder) view[i].getTag();
				view[i].setVisibility(View.VISIBLE);
				if (i >= list.size()) {
					view[i].setVisibility(View.GONE);
				} else {
					ItemGoodsListenerUtil.setViewListener(list.get(i).status, list.get(i).goodsURL, context, view[i]);
					price += list.get(i).goodsCount * list.get(i).goodsFactPrice;
					if (!TextUtils.isEmpty(list.get(i).goodsImageURL) && isShowPicture) {
						ImageLoader.getInstance().displayImage(list.get(i).goodsImageURL, imageHolder.goodsHeaderIcon);
					}
				}
				startIndex = i + 1;
			}
			if (!enough) {
				for (int i = startIndex; i < list.size(); i++) {
					View newView = LayoutInflater.from(context).inflate(R.layout.item_ebusiness_myorder_imagelist,
							null);
					GoodsImageHolder imageHolder = new GoodsImageHolder();
					GinInjector.manualInjectView(imageHolder, newView);
					newView.setTag(imageHolder);
					if (!TextUtils.isEmpty(list.get(i).goodsImageURL) && isShowPicture) {
						ImageLoader.getInstance().displayImage(list.get(i).goodsImageURL, imageHolder.goodsHeaderIcon);
					}
					price += list.get(i).goodsCount * list.get(i).goodsFactPrice;
					holder.goodsImageContainer.addView(newView);
					ItemGoodsListenerUtil.setViewListener(list.get(i).status, list.get(i).goodsURL, context, newView);
				}
			}
			holder.mutipleGoodsImageList.scrollTo(0, 0);
		} else {
			for (int i = 0; i < list.size(); i++) {
				View view = LayoutInflater.from(context).inflate(R.layout.item_ebusiness_myorder_imagelist, null);
				GoodsImageHolder imageHolder = new GoodsImageHolder();
				GinInjector.manualInjectView(imageHolder, view);
				view.setTag(imageHolder);
				if (!TextUtils.isEmpty(list.get(i).goodsImageURL) && isShowPicture) {
					ImageLoader.getInstance().displayImage(list.get(i).goodsImageURL, imageHolder.goodsHeaderIcon);
				}
				price += list.get(i).goodsCount * list.get(i).goodsFactPrice;
				holder.goodsImageContainer.addView(view);
				ItemGoodsListenerUtil.setViewListener(list.get(i).status, list.get(i).goodsURL, context, view);
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		String allPrice = df.format(price);
		holder.mutipleGoodsPrice.setText("¥" + allPrice);
		holder.mutipleGoodsTradeDate.setText(item.orderDate);
		// holder.goodsImageContainer.invalidate();
	}

	/**
	 * 一个定单只有一件商品的显示
	 * 
	 * @param item
	 * @param holder
	 */
	protected void showSigleGoodsViewData(OrderItem orderitem, Holder holder, double carriage) {
		OrderGoodsItem item = orderitem.goods.get(0);
		holder.singleGoodsContainer.setVisibility(View.VISIBLE);
		holder.mutipleGoodsListContainer.setVisibility(View.GONE);
		holder.goodsHeaderIcon.clear();
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		if (!TextUtils.isEmpty(item.goodsImageURL) && isShowPicture) {
			ImageLoader.getInstance().displayImage(item.goodsImageURL, holder.goodsHeaderIcon);
		}
		holder.goodsTitle.setText(item.goodsName);
		DecimalFormat df = new DecimalFormat("0.00");
		String price = df.format(item.goodsFactPrice + carriage);
		holder.goodsPrice.setText("¥" + price);
		holder.tradeDate.setText(orderitem.orderDate);
		ItemGoodsListenerUtil.setViewListener(item.status, item.goodsURL, context, holder.singleGoodsContainer);
	}

	private class Holder {
		@GinInjectView(id = R.id.singleGoodsContainer)
		protected View singleGoodsContainer;

		@GinInjectView(id = R.id.mutipleGoodsListContainer)
		protected View mutipleGoodsListContainer;

		@GinInjectView(id = R.id.mutipleGoodsImageList)
		protected HorizontalScrollView mutipleGoodsImageList;
		@GinInjectView(id = R.id.goodsImageContainer)
		protected LinearLayout goodsImageContainer;

		@GinInjectView(id = R.id.mutipleGoodsPrice)
		protected TextView mutipleGoodsPrice;

		@GinInjectView(id = R.id.mutipleGoodsTradeDate)
		protected TextView mutipleGoodsTradeDate;

		@GinInjectView(id = R.id.goodsHeaderIcon)
		protected AdvancedImageView goodsHeaderIcon;

		@GinInjectView(id = R.id.rightContentLayout)
		protected View rightContentLayout;

		@GinInjectView(id = R.id.goodsTitle)
		protected TextView goodsTitle;

		@GinInjectView(id = R.id.goodsPrice)
		protected TextView goodsPrice;

		@GinInjectView(id = R.id.tradeDate)
		protected TextView tradeDate;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		// 点击 打开具体某个 订单或是商品页
	}

	private class MutiplGoodsImageListAdaptor extends BaseAdapter {
		public List<OrderGoodsItem> list = new ArrayList<OrderGoodsItem>();

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public OrderGoodsItem getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			GoodsImageHolder holder;
			if (view == null) {
				holder = new GoodsImageHolder();
				view = LayoutInflater.from(context).inflate(R.layout.item_ebusiness_myorder_imagelist, null);
				GinInjector.manualInjectView(holder, view);
				view.setTag(holder);
			} else {
				holder = (GoodsImageHolder) view.getTag();
			}
			OrderGoodsItem item = getItem(arg0);
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			CheckNetwork network = new CheckNetwork(context);
			boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
					|| network.getWifiState(false) == State.CONNECTED;
			if (!TextUtils.isEmpty(item.goodsImageURL) && isShowPicture) {
				ImageLoader.getInstance().displayImage(item.goodsImageURL, holder.goodsHeaderIcon);
			}
			return view;
		}

	}

	public class GoodsImageHolder {
		@GinInjectView(id = R.id.goodsHeaderIcon)
		private AdvancedImageView goodsHeaderIcon;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
