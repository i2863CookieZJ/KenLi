package com.sobey.cloud.webtv.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.fragment.WebPageFragment;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.CatalogType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CatalogControl {

	private static boolean hasInitCatalog = false;

	public static void startFirstActivity(Activity activity) {
		Intent intent=new Intent(activity, WebPageFragment.class);
//		switch (MConfig.CatalogList.get(0).type) {
//		case News:
//		case Photo:
//		case Video:
//			intent = new Intent(activity, HomeActivity.class);
//			break;
//		case Live:
//			intent = new Intent(activity, LiveNewsHomeActivity.class);
//			break;
//		case Broke:
//			intent = new Intent(activity, BrokeNewsHomeActivity.class);
//			break;
//		case Recommend:
//			intent = new Intent(activity, RecommendNewsHomeActivity.class);
//			break;
//		default:
//			intent = new Intent(activity, HomeActivity.class);
//			break;
//		}
		intent.putExtra("index", 0);
		activity.startActivity(intent);
	}

	public static void setCatalogList(Context context, JSONArray jsonArray) throws Exception {
		if (!hasInitCatalog) {
			SharedPreferences catalogList = context.getSharedPreferences("catalog_list", 0);
			if (jsonArray == null) {
				try {
					jsonArray = new JSONArray(catalogList.getString("json", null));
				} catch (Exception e) {
					return;
				}
			}
			int index = 0;
			if (MConfig.HaveRecommendCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "", "推荐", R.drawable.default_recomment_icon, CatalogType.Recommend));
				index++;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				CatalogObj catalogObj = new CatalogObj();
				catalogObj.index = index;
				catalogObj.id = object.optString("id");
				catalogObj.name = object.optString("name");
				catalogObj.resId = MConfig.CatalogDefaultIconResId;
				catalogObj.url = object.optString("logo");
				int type = 1;
				try {
					type = Integer.valueOf(object.optString("appstyle"));
				} catch (Exception e) {
					type = 1;
				}
				switch (type) {
				case 5:
				case 6:
					catalogObj.type = CatalogType.Live;
					//5是音频直播 6是视频直播 如果是视频直播 把直播栏目的id加进去
					if(type==6)
						NewsItemClickUtil.VideoLiveCatalogIDS.add(catalogObj.id);
					break;
				case 7:
					catalogObj.type = CatalogType.Topic;
					break;
				default:
					catalogObj.type = CatalogType.News;
					break;
				}
				MConfig.CatalogList.add(catalogObj);
				index++;
			}
			if (MConfig.HaveCouponCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "", "优惠", R.drawable.default_coupon_icon, CatalogType.Coupon));
				index++;
			}
			if (MConfig.HaveBrokeCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "", "爆料", R.drawable.default_broke_icon, CatalogType.Broke));
				index++;
			}
			if (MConfig.HaveChargeMobileFeeCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "http://wvs.m.taobao.com/", "充值中心", R.drawable.default_charge_mobile_fee, CatalogType.ChargeMobileFee));
				index++;
			}
			if (MConfig.HaveSearchBusLineCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "http://map.baidu.com/mobile/webapp/index/index/&tab=line/", "公交线路", R.drawable.default_search_bus_line, CatalogType.SearchBusLine));
				index++;
			}
			if (MConfig.HaveSearchIllegalCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "http://www.aneee.com/wap.html", "违章查询", R.drawable.default_search_illegal, CatalogType.SearchIllegal));
				index++;
			}
			if (MConfig.HaveLifeAroundCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "http://map.baidu.com/mobile/webapp/index/more/", "周边生活", R.drawable.default_life_around, CatalogType.LifeAround));
				index++;
			}
			if (MConfig.HaveTakeTaxiCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "http://pay.xiaojukeji.com/api/v2/webapp?maptype=baidu&channel=1272", "打车", R.drawable.default_take_taxi, CatalogType.TakeTaxi));
				index++;
			}
			if (MConfig.HaveGuessCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "", "竞猜", R.drawable.default_life_around, CatalogType.Guess));
				index++;
			}
			if (MConfig.HaveCampaignCatalog && jsonArray != null) {
				MConfig.CatalogList.add(new CatalogObj(index, "198", "活动", R.drawable.default_life_around, CatalogType.Campaign));
				index++;
			}
			Editor editor = catalogList.edit();
			editor.putString("json", jsonArray.toString());
			editor.commit();
			hasInitCatalog = true;
		}
	}
}
