package com.sobey.cloud.webtv.ebusiness;

import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.adapter.UniversalAdapter;
import com.sobey.cloud.webtv.adapter.UniversalViewHolder;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的购物车
 * 
 * @author zouxudong
 *
 */
public class MyCartAdaptor extends UniversalAdapter<CartGoodsItem> {
	protected Context context;
	public final ArrayList<CartGoodsItem> mCartGoodsItems;
	private Handler handler;

	public MyCartAdaptor(Context paramContext, ArrayList<CartGoodsItem> paramList, Handler paramHandler) {
		super(paramContext, paramList, R.layout.item_ebusiness_mycart_item);
		this.mCartGoodsItems = paramList;
		this.handler = paramHandler;
	}

	public void addGoodsItem(ArrayList<CartGoodsItem> items) {
		mCartGoodsItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public void convert(UniversalViewHolder holder, CartGoodsItem cartGoodsItem) {
		TextView goodsTitle = holder.findViewById(R.id.goodsTitle);
		TextView goodsPrice = holder.findViewById(R.id.goodsPrice);
		AdvancedImageView goodsHeaderIcon = holder.findViewById(R.id.goodsHeaderIcon);
		TextView goodsCount = holder.findViewById(R.id.goodsCount);
		TextView goodsCountSub = holder.findViewById(R.id.goodsCountSub);
		TextView goodsCountPlus = holder.findViewById(R.id.goodsCountPlus);
		CheckBox shopCartCb = holder.findViewById(R.id.shop_card_cb);
		RelativeLayout selectRl = holder.findViewById(R.id.select_rl);

		goodsTitle.setText(cartGoodsItem.goodsName);
		goodsPrice.setText("¥" + cartGoodsItem.goodsFactPrice);
		goodsCount.setText(cartGoodsItem.goodsCount + "");

		ImageLoader.getInstance().displayImage(cartGoodsItem.goodsImageURL, goodsHeaderIcon);

		shopCartCb.setChecked(cartGoodsItem.isSelected);

		goodsCountSub.setTag(cartGoodsItem);
		goodsCountSub.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CartGoodsItem cartGoodsItem = (CartGoodsItem) v.getTag();
				cartGoodsItem.goodsCount = cartGoodsItem.goodsCount - 1;
				changeTotalPrice(calcAllPrice());
				notifyDataSetChanged();
			}
		});

		goodsCountPlus.setTag(cartGoodsItem);
		goodsCountPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CartGoodsItem cartGoodsItem = (CartGoodsItem) v.getTag();
				cartGoodsItem.goodsCount = cartGoodsItem.goodsCount + 1;
				changeTotalPrice(calcAllPrice());
				notifyDataSetChanged();
			}
		});

		selectRl.setTag(cartGoodsItem);
		selectRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CartGoodsItem cartGoodsItem = (CartGoodsItem) v.getTag();
				if (cartGoodsItem.isSelected) {
					cartGoodsItem.isSelected = false;
					changeIDList(cartGoodsItem.goodsID, 1);
				} else {
					cartGoodsItem.isSelected = true;
					changeIDList(cartGoodsItem.goodsID, 0);
				}
				changeTotalPrice(calcAllPrice());
				notifyDataSetChanged();
			}
		});
	}

	private void changeTotalPrice(double paramDouble) {
		Message localMessage = this.handler.obtainMessage(2);
		Bundle localBundle = new Bundle();
		localBundle.putDouble("totalPrice", paramDouble);
		localMessage.setData(localBundle);
		this.handler.sendMessage(localMessage);
	}

	private void changeIDList(String id, int type) {
		Message localMessage = this.handler.obtainMessage(type);
		localMessage.obj = id;
		this.handler.sendMessage(localMessage);
	}

	private double calcAllPrice() {
		double totalPrice = 0;
		for (CartGoodsItem cartGoodsItem : mCartGoodsItems) {
			if (cartGoodsItem.isSelected) {
				totalPrice += cartGoodsItem.goodsCount * cartGoodsItem.goodsFactPrice;
			}
		}
		return totalPrice;
	}

}
