package com.sobey.cloud.webtv.ebusiness;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.MyShopcarPayAcitvity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的购物车
 * 
 * @author zouxudong
 * 
 */
public class MyShopingCartActivity extends BaseActivity implements IDragListViewListener {
	// @GinInjectView(id = R.id.loading_failed_tips_tv)
	// private TextView emptyTv;
	@GinInjectView(id = R.id.empty_layout)
	private View emptyLayout;
	@GinInjectView(id = R.id.myCart)
	private DragListView myCart;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.operator_rl)
	private RelativeLayout operator_rl;
	@GinInjectView(id = R.id.operator_iv)
	private ImageView operator_iv;
	@GinInjectView(id = R.id.loadingmask)
	private View loadingmask;
	@GinInjectView(id = R.id.submit_btn)
	private TextView submit;

	@GinInjectView(id = R.id.all_price_tv)
	private TextView all_price_tv;

	@GinInjectView(id = R.id.all_shop_cart_cb)
	private CheckBox all_shop_cart_cb;

	@GinInjectView(id = R.id.all_select_ll)
	private LinearLayout all_select_ll;

	@GinInjectView(id = R.id.all_shop_cart_tv)
	private TextView all_shop_cart_tv;

	// @GinInjectView(id = R.id.nocontent_layout)
	// private View nocontentLayout;
	// @GinInjectView(id = R.id.layout_nocontent_icon)
	// private ImageView noContentIcon;
	// @GinInjectView(id = R.id.layout_nocontent_tip)
	// private TextView noContentTip;

	private boolean isLoading;

	private int loadPageIndex;

	private int maxPageCount;

	private int signleLoadCount = 10;

	private MyCartAdaptor adaptor;

	private ArrayList<CartGoodsItem> mCartGoodsItem = new ArrayList<>();

	private ArrayList<String> idList = new ArrayList<String>();

	private WebView myWebView;

	@Override
	public int getContentView() {
		return R.layout.activity_ebusiness_mycart;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		submit.setEnabled(false);
		mHeaderCtv.setTitle("我的购物车");
		// emptyTv.setText(R.string.has_no_result);
		mBackRl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});
		operator_rl.setVisibility(View.VISIBLE);
		operator_iv.setImageResource(R.drawable.righttop_del);
		operator_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				delShop();
			}
		});
		adaptor = new MyCartAdaptor(this, mCartGoodsItem, handler);
		myCart.setAdapter(adaptor);
		myCart.setListener(this);
		myCart.setPullLoadEnable(false);
		myCart.setPullRefreshEnable(true);
		myCart.setListener(this);
		myCart.setFooterBackgroundColor(0xffffffff);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyShopingCartActivity.this, MyShopcarPayAcitvity.class);
				intent.putStringArrayListExtra("ids", idList);
				startActivity(intent);
			}
		});

		all_select_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isAll = true;
				double price = 0;
				all_price_tv.setText("¥0.00");
				idList.clear();
				if (all_shop_cart_cb.isChecked()) {
					isAll = false;
					all_shop_cart_tv.setText("全选");
				} else {
					all_shop_cart_tv.setText("取消全选");
				}

				all_shop_cart_cb.setChecked(isAll);

				for (CartGoodsItem cartGoodsItem : mCartGoodsItem) {
					cartGoodsItem.isSelected = isAll;
					if (isAll) {
						price += cartGoodsItem.goodsCount * cartGoodsItem.goodsFactPrice;
						idList.add(cartGoodsItem.goodsID);
					}
				}
				all_price_tv.setText("¥" + price);
				adaptor.notifyDataSetChanged();
			}
		});

		myWebView = new WebView(this);
		onRefresh();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String id = (String) msg.obj;
			switch (msg.what) {
			case 0:// 选中商品
				idList.add(id);
				submit.setEnabled(true);
				break;
			case 1:// 未选中商品
				if (idList.contains(id)) {
					idList.remove(id);
					if (idList.size() == 0) {
						submit.setEnabled(false);
					}
				}
				break;
			case 2:
				double d = msg.getData().getDouble("totalPrice");
				String totalPrice = new DecimalFormat("0.00").format(d);
				all_price_tv.setText("¥" + totalPrice);
				break;
			}
		}

	};

	protected void refreshData(JSONObject cartData) {
		myCart.stopLoadMore();
		myCart.stopRefresh();
		loadingmask.setVisibility(View.GONE);
		JSONArray data = null;
		if (cartData != null)
			data = cartData.optJSONArray("sellers");
		else {
			loadPageIndex -= 1;
		}
		if (data == null || cartData == null || data.length() == 0) {
			// ToastUtil.showToast(this, "暂无最新购物车数据");
			if (adaptor.mCartGoodsItems.size() == 0) {
				// emptyLayout.setVisibility(View.VISIBLE);
				showNoContent(emptyLayout, R.drawable.nocontent_shopcart, "您还没有购物车的订单...");
				myCart.setVisibility(View.GONE);
			}
			return;
		}
		maxPageCount = cartData.optInt("totalPage");
		if (loadPageIndex == 1) {
			adaptor.mCartGoodsItems.clear();
		}
		ArrayList<CartItem> listCart = new ArrayList<CartItem>();
		for (int index = 0; index < data.length(); index++) {
			JSONObject itemCart = data.optJSONObject(index);
			JSONArray itemCartGoods = itemCart.optJSONArray("goods");
			if (itemCart == null || itemCartGoods == null || itemCartGoods.length() == 0)
				continue;
			CartItem cartItem = new CartItem();
			String sellerID = itemCart.optString("sellerID");
			String sellerName = itemCart.optString("sellerName");
			cartItem.sellerID = sellerID;
			cartItem.sellerName = sellerName;
			for (int cartGoodsIndex = 0; cartGoodsIndex < itemCartGoods.length(); cartGoodsIndex++) {
				JSONObject itemGoods = itemCartGoods.optJSONObject(cartGoodsIndex);
				if (itemGoods == null)
					continue;
				CartGoodsItem cartGoodsItem = new CartGoodsItem(itemGoods);
				cartItem.goods.add(cartGoodsItem);
			}
			listCart.add(cartItem);

		}
		// nocontentLayout.setVisibility(View.GONE);
		emptyLayout.setVisibility(View.GONE);
		myCart.setVisibility(View.VISIBLE);

		for (CartItem cartItem : listCart) {
			mCartGoodsItem.addAll(cartItem.goods);
		}
		// adaptor.addGoodsItem(mCartGoodsItem);
		adaptor.notifyDataSetChanged();
		if (loadPageIndex >= maxPageCount)
			myCart.setPullLoadEnable(false);
		else
			myCart.setPullLoadEnable(true);
	}

	@Override
	public void onRefresh() {
		if (isLoading)
			return;
		loadPageIndex = 1;
		isLoading = true;
		idList.clear();
		loadingmask.setVisibility(View.VISIBLE);
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

	// private void showNoContent() {
	// nocontentLayout.setVisibility(View.VISIBLE);
	// noContentIcon.setImageResource(R.drawable.nocontent_shopcart);
	// noContentTip.setText("您还没有购物车的订单...");
	// }

	public class GetEBusinessData extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			String cookie = CookieManager.getInstance()
					.getCookie("http://shop.sobeycache.com/index.php?controller=simple&action=cart");
			SharedPreferences cartInfo = getSharedPreferences("iweb_shoppingcart", 0);
			String iweb_shoppingcart = cartInfo.getString("iweb_shoppingcart", "");
			JSONObject array = News.getEBusinessMyCart(PreferencesUtil.getLoggedUserId(), loadPageIndex,
					signleLoadCount, cookie);
			return array;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			refreshData(result);
			isLoading = false;
		}

	}

	/**
	 */
	private void delShop() {

		if (idList.size() <= 0) {
			ToastUtil.showToast(this, "请选择您需要删除的商品");
			return;
		}

		StringBuffer ids = new StringBuffer();
		for (String id : idList) {
			ids.append(id).append(",");
		}
		if (TextUtils.isEmpty(ids)) {
			return;
		}
		String removeUrl = "http://shop.sobeycache.com/index.php?controller=simple&action=removeCart&type=goods&goods_id="
				+ ids.toString().substring(0, ids.length() - 1);
		myWebView.loadUrl(removeUrl);
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String cookie = CookieManager.getInstance().getCookie(url);
				Log.d("zxd", "ecshop:" + cookie);
				if (!TextUtils.isEmpty(cookie)) {
					SharedPreferences settings = MyShopingCartActivity.this.getSharedPreferences("iweb_shoppingcart",
							0);
					Editor editor = settings.edit();
					editor.putString("iweb_shoppingcart", cookie.trim());
					editor.commit();
				}
				onDataFinish(null);
			}

		});
	}

}
