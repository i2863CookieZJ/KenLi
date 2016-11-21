package com.sobey.cloud.webtv.ebusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simple.eventbus.EventBus;

import com.android.volley.VolleyError;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.adapter.EbusinessMyCollectAdapter;
import com.sobey.cloud.webtv.adapter.EbusinessMyCollectAdapter.OnCheckedListener;
import com.sobey.cloud.webtv.bean.EbusinessGoodsModel;
import com.sobey.cloud.webtv.bean.EbusinessRequestMananger;
import com.sobey.cloud.webtv.bean.EbusinessRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GetEbCollectListResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 特卖收藏
 * 
 * @author Administrator
 *
 */
public class MyCollectActivity extends BaseActivity implements OnClickListener, OnCheckedListener {

	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.operator_rl)
	private RelativeLayout operator_rl;
	@GinInjectView(id = R.id.operator_iv)
	private ImageView operator_iv;
	@GinInjectView(id = R.id.activity_my_collect_listview)
	DragListView mListView;
	@GinInjectView(id = R.id.loadingmask)
	View mLoadingLayout;
	@GinInjectView(id = R.id.empty_layout)
	View failedLayout;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	TextView failedTv;
	private EbusinessMyCollectAdapter adapter;
	private int totalPage = 1;
	private int pagesize = 20;
	private int currentPage = 1;

	private List<CheckBox> cbCheckedList = new ArrayList<CheckBox>();
	private StringBuffer goodsIds = null;
	private WebView myWebView;
	String cookie = "";

	@Override
	public int getContentView() {
		return R.layout.activity_ebusiness_mycollect;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setUpDatas();
	}

	public void setUpDatas() {
		EventBus.getDefault().register(this);
		mHeaderCtv.setTitle("特卖收藏");
		mBackRl.setOnClickListener(this);
		operator_rl.setVisibility(View.VISIBLE);
		operator_rl.setOnClickListener(this);
		operator_iv.setImageResource(R.drawable.rightntop_chooseall);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		// mListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// EbusinessGoodsModel goodsModel = (EbusinessGoodsModel)
		// parent.getAdapter().getItem(position);
		// if (goodsModel == null) {
		// return;
		// }
		// String url = goodsModel.goodsURL;
		// Intent intent = new Intent(MyCollectActivity.this,
		// GoodsDetailActivity.class);
		// intent.putExtra("url", url);
		// startActivity(intent);
		// }
		// });
		mListView.setListener(new IDragListViewListener() {

			@Override
			public void onRefresh() {

			}

			@Override
			public void onLoadMore() {
				currentPage++;
				if (currentPage > totalPage) {
					mListView.setPullLoadEnable(false);
					return;
				}
				getData();
			}
		});
		myWebView = new WebView(this);
		getData();
	}

	private void getData() {
		EbusinessRequestMananger.getInstance().getMyCollect(currentPage, pagesize, this, new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				mCloseLoadingIcon();
				if (null != result && result instanceof GetEbCollectListResult) {
					GetEbCollectListResult collectListResult = (GetEbCollectListResult) result;
					totalPage = collectListResult.totalPage;
					if (totalPage <= 1) {
						mListView.setPullLoadEnable(false);
					}
					if (null == collectListResult.goods || collectListResult.goods.size() < 1) {
						// 数据为空
						// failedLayout.setVisibility(View.VISIBLE);
						// failedTv.setText(R.string.has_no_result);
						showNoContent(failedLayout, R.drawable.nocontent_shouc, "您暂时没有收藏任何商品...");
						return;
					}
					if (adapter == null) {
						adapter = new EbusinessMyCollectAdapter(MyCollectActivity.this);
					}
					if (adapter.getData() != null) {
						adapter.addData(collectListResult.goods);
					} else {
						adapter.setData(collectListResult.goods);
					}
					adapter.setOnchecked(MyCollectActivity.this);
					mListView.setAdapter(adapter);
				} else {
					// failedLayout.setVisibility(View.VISIBLE);
					// failedTv.setText(R.string.has_no_result);
					showNoContent(failedLayout, R.drawable.nocontent_shouc, "您暂时没有收藏任何商品...");
				}
				mListView.stopLoadMore();
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		case R.id.operator_rl:
			operatorAction();
			break;
		default:
			break;
		}
	}

	/*
	 * 右上角按钮操作
	 */
	private void operatorAction() {
		if (operator_rl.getTag() == null) {
			adapter.showCheckBox();
		} else {
			if ("del".equals(operator_rl.getTag().toString())) {
				delShop();
			} else {
				adapter.showCheckBox();
			}
		}

	}

	/**
	 * 删除收藏商品
	 */
	private void delShop() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "delfavorite");
		map.put("uid", PreferencesUtil.getLoggedUserId());
		map.put("goodsids", goodsIds.toString());
		VolleyRequset.doPost(this, MConfig.ECSHOP_CARTAPI, "deteleShop", map, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				adapter = null;
				getData();
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(MyCollectActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 把收藏商品加入购物车
	 */
	private void addShop() {
		String url = "http://shop.sobeycache.com/index.php?controller=simple&action=joincart&type=goods&goods_num=1&goods_id="
				+ goodsIds.toString();
		myWebView.loadUrl(url);
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Toast.makeText(MyCollectActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
				String cookie = CookieManager.getInstance().getCookie(url);
				if (!TextUtils.isEmpty(cookie)) {
					SharedPreferences settings = MyCollectActivity.this.getSharedPreferences("iweb_shoppingcart", 0);
					Editor editor = settings.edit();
					editor.putString("iweb_shoppingcart", cookie.trim());
					editor.commit();
				}
			}

		});
	}

	public void mOpenLoadingIcon() {
		if (mLoadingLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingLayout, 1000, 0);
		}
	}

	/*
	 * 批量选中的状态check
	 */
	@Override
	public void onChecked(CheckBox cb) {
		cbCheckedList.add(cb);
	}

	@Override
	public void onCancelChecked(CheckBox cb) {
		cbCheckedList.remove(cb);
	}

	@Override
	public void onFinish() {
		if (cbCheckedList.size() > 0) {
			goodsIds = new StringBuffer();
			for (CheckBox cb : cbCheckedList) {
				EbusinessGoodsModel goodsModle = (EbusinessGoodsModel) cb.getTag();
				goodsIds.append(goodsModle.goodsID).append(",");
			}
			operator_iv.setImageResource(R.drawable.righttop_del);
			operator_rl.setTag("del");
		} else {
			operator_iv.setImageResource(R.drawable.rightntop_chooseall);
			operator_rl.setTag("notdel");
		}
	}

	@Override
	public void onAddCartClick(EbusinessGoodsModel goodModel) {
		goodsIds = new StringBuffer();
		goodsIds.append(goodModel.goodsID);
		addShop();
	}
}
