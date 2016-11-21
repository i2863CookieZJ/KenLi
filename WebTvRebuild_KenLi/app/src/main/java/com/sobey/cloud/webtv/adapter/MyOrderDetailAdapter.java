package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.MyOrderDetailGoodsBean;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 我的订单
 */
public class MyOrderDetailAdapter extends BaseAdapter implements OnScrollListener {

	private Context context;
	public final List<MyOrderDetailGoodsBean> goodsItems;
	private DisplayImageOptions imageOptions;

	public MyOrderDetailAdapter(Context context) {
		this.context = context;
		goodsItems = new ArrayList<MyOrderDetailGoodsBean>();
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_thumbnail_banner)
				.showImageOnLoading(R.drawable.default_thumbnail_banner);
		imageOptions = builder.build();
	}

	public void addGoodsItem(List<MyOrderDetailGoodsBean> items) {
		goodsItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return goodsItems.size();
	}

	@Override
	public MyOrderDetailGoodsBean getItem(int index) {
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
			view = LayoutInflater.from(context).inflate(R.layout.myorderdetail_item, null);
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
		String shopName = goodsItems.get(index).getShopName();
		if (shopName != null) {
			holder.shopName.setText(shopName);
		}

		String goodImgUrl = MConfig.ORDER_PIC_HEAD + goodsItems.get(index).getGoodsImg();
		if (goodImgUrl != null) {
			ImageLoader.getInstance().displayImage(goodImgUrl, holder.goodIv, imageOptions);
		}

		String goodTitle = goodsItems.get(index).getGoodsName();
		if (goodTitle != null) {
			holder.goodTitle.setText(goodTitle);
		}

		String goodNum = goodsItems.get(index).getGoodsNums();
		if (goodNum != null) {
			holder.goodNum.setText("x" + goodNum);
		}

		String realPrice = goodsItems.get(index).getRealPrice();
		if (realPrice != null) {
			holder.realamountTv.setText("￥" + realPrice);
		}

	}

	private class Holder {
		@GinInjectView(id = R.id.modi_shopname)
		TextView shopName;
		@GinInjectView(id = R.id.modi_goodprice)
		TextView realamountTv;
		@GinInjectView(id = R.id.modi_good_iv)
		ImageView goodIv;
		@GinInjectView(id = R.id.modi_goodTitle)
		TextView goodTitle;
		@GinInjectView(id = R.id.modi_goodnum)
		TextView goodNum;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
