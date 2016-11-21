package com.sobey.cloud.webtv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.widgets.CustomTitleView;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 一个订单包含多个商品时的评价列表
 * 
 * @author Administrator
 *
 */
public class ShopPingJiaActivity extends BaseActivity {

	@GinInjectView(id = R.id.ac_pjlist_myOrderList)
	private ListView myOrderList;
	@GinInjectView(id = R.id.back_rl)
	private RelativeLayout mBackRl;
	@GinInjectView(id = R.id.ac_pjlist_header_ctv)
	private CustomTitleView mHeaderCtv;

	private MyAdapter adapter;
	private JSONArray JsonArray;

	@Override
	public int getContentView() {
		return R.layout.activity_pingjialist;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("商品评价");
		mBackRl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});
		String goodsInfo = (String) getIntent().getStringExtra("goodList");
		if (TextUtils.isEmpty(goodsInfo)) {
			return;
		} else {
			try {
				JsonArray = new JSONArray(goodsInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			adapter = new MyAdapter();
			myOrderList.setAdapter(adapter);
			myOrderList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(ShopPingJiaActivity.this, SendPingJiaActivity.class);
					try {
						intent.putExtra("goodList", "[" + JsonArray.getString(position) + "]");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					startActivity(intent);
				}
			});
		}

	}

	private class MyAdapter extends BaseAdapter {

		public MyAdapter() {
		}

		@Override
		public int getCount() {
			return JsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(ShopPingJiaActivity.this).inflate(R.layout.item_shoppj, null);
				holder.shopImg = (ImageView) convertView.findViewById(R.id.item_shoppj_shopimg);
				holder.shopDes = (TextView) convertView.findViewById(R.id.item_shoppj_shopdes);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				JSONObject jObject = JsonArray.getJSONObject(position);
				holder.shopDes.setText(jObject.getString("goods_name"));
				Picasso.with(ShopPingJiaActivity.this).load(MConfig.ORDER_PIC_HEAD + jObject.getString("goods_img"))
						.error(R.drawable.default_thumbnail_banner).placeholder(R.drawable.default_thumbnail_banner)
						.into(holder.shopImg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}

		private class ViewHolder {
			private ImageView shopImg;
			private TextView shopDes;
		}
	}

}
