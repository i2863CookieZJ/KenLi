package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.bean.EbusinessGoodsModel;
import com.sobey.cloud.webtv.ebusiness.GoodsDetailActivity;
import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class EbusinessMyCollectAdapter extends BaseAdapter {
	private List<EbusinessGoodsModel> mGoods;
	private List<CheckBox> cbList = new ArrayList<CheckBox>();
	private Context mContext;
	private OnCheckedListener onCheckedListener;

	public EbusinessMyCollectAdapter(Context ctx) {
		this.mContext = ctx;
	}

	public void setData(List<EbusinessGoodsModel> goods) {
		this.mGoods = goods;
	}

	public List<EbusinessGoodsModel> getData() {
		return this.mGoods;
	}

	public void addData(List<EbusinessGoodsModel> goods) {
		if (null != mGoods) {
			mGoods.addAll(goods);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mGoods.size();
	}

	@Override
	public EbusinessGoodsModel getItem(int position) {
		return mGoods.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		EbusinessGoodsModel goodModel = getItem(position);
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ebusiness_mycollect_layout, null);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.mycollect_goods_checkbox);
			holder.checkBox.setTag(goodModel);
			holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					CheckBox cb = (CheckBox) buttonView;
					if (buttonView.isChecked()) {
						onCheckedListener.onChecked(cb);
					} else {
						onCheckedListener.onCancelChecked(cb);
					}
					onCheckedListener.onFinish();
				}
			});
			cbList.add(holder.checkBox);
			holder.img = (ImageView) convertView.findViewById(R.id.mycollect_goods_pic_img);
			holder.nameTv = (TextView) convertView.findViewById(R.id.mycollect_goods_name_tv);
			holder.goodsFrom = (TextView) convertView.findViewById(R.id.mycollect_goods_from);
			holder.priceTv = (TextView) convertView.findViewById(R.id.mycollect_goods_price_tv);
			holder.fakePrice = (TextView) convertView.findViewById(R.id.mycollect_goods_price_fake_tv);
			holder.addCart = (TextView) convertView.findViewById(R.id.mycollect_goods_addcart);
			final EbusinessGoodsModel goodModelTemp = goodModel;
			holder.addCart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onCheckedListener.onAddCartClick(goodModelTemp);
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().displayImage(goodModel.goodsImageURL, holder.img);
		holder.nameTv.setText(goodModel.goodsName);
		holder.priceTv.setText("￥" + goodModel.goodsFactPrice);
		holder.goodsFrom.setText("来自：" + goodModel.sellerName);
		holder.fakePrice.setText("￥" + goodModel.goodsOrginalPrice);
		holder.fakePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		convertView.setOnClickListener(new MyClickListener(goodModel));
		return convertView;
	}

	public void showCheckBox() {
		for (CheckBox cb : cbList) {
			if (cb.getVisibility() == View.VISIBLE) {
				cb.setVisibility(View.GONE);
			} else {
				cb.setVisibility(View.VISIBLE);
			}

		}
	}

	private class ViewHolder {
		CheckBox checkBox;
		ImageView img;
		TextView nameTv;
		TextView goodsFrom;
		TextView priceTv;
		TextView fakePrice;
		TextView addCart;
	}

	private class MyClickListener implements View.OnClickListener {
		EbusinessGoodsModel goodsModel;

		public MyClickListener(EbusinessGoodsModel goodsModel) {
			this.goodsModel = goodsModel;
		}

		@Override
		public void onClick(View v) {
			String url = goodsModel.goodsURL;
			Intent intent = new Intent(mContext, GoodsDetailActivity.class);
			intent.putExtra("url", url);
			mContext.startActivity(intent);
		}

	}

	public interface OnCheckedListener {
		public void onChecked(CheckBox cb);

		public void onCancelChecked(CheckBox cb);

		public void onFinish();

		public void onAddCartClick(EbusinessGoodsModel goodModel);
	}

	public void setOnchecked(OnCheckedListener onCheckedListener) {
		this.onCheckedListener = onCheckedListener;
	}
}
