package com.sobey.cloud.webtv.utils;

import java.util.ArrayList;

import org.json.JSONObject;

import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
//import com.sobey.cloud.webtv.NewTopicDetailActivity;
import com.sobey.cloud.webtv.LiveNewsDetailActivity;
import com.sobey.cloud.webtv.LiveNewsDetailActivity2;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;

import android.content.Context;
import android.content.Intent;

public class NewsItemClickUtil {
	@SuppressWarnings("serial")
	/**
	 * 直播栏目id
	 */
	public static ArrayList<String> VideoLiveCatalogIDS = new ArrayList<String>() {
		{
			add(MConfig.VIDEO_LIVE_ID);
		}
	};
	/**
	 * 专题栏目id
	 */
	public static ArrayList<String> SpecialTopicCatalogIDS = new ArrayList<String>();

	public static void OpenDetailPage(int type, JSONObject information, Context context, int index, String parentID) {
		Intent intent = null;
		switch (type) {
		// 專題
		case 6:
			// intent= new Intent(context, NewTopicDetailActivity.class);
			// intent.putExtra("id", information.optString("id"));
			// intent.putExtra("title", information.optString("title"));
			// context.startActivity(intent);
			break;
		case MConfig.TypePicture:
			intent = new Intent(context, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information.toString());
			context.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(context);

			intent = new Intent(context, VideoNewsDetailActivity.class);
			intent.putExtra("information", information.toString());
			context.startActivity(intent);
			break;
		case MConfig.TypeLive:
			HuiZhouSarft.disposeVideoComponent(context);

			if (VideoLiveCatalogIDS.indexOf(parentID) != -1) {// 视频直播
				String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\""
						+ MConfig.VIDEO_LIVE_ID + "\"}";
				intent = new Intent();
				intent.putExtra("information", string);
				intent.putExtra("liveMark", "0");
				intent.setClass(context, LiveNewsDetailActivity.class);
				context.startActivity(intent);
			} else {// 音频直播
				String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\"" + parentID
						+ "\",\"title\":\"" + information.optString("title") + "\"}";
				intent = new Intent();
				intent.putExtra("information", string);
				intent.putExtra("index", index);
				intent.putExtra("liveMark", "-1");
				intent.setClass(context, LiveNewsDetailActivity2.class);
				context.startActivity(intent);
			}

			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(context, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information.toString());
			context.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(context);
			Intent intent3 = new Intent(context, VideoNewsDetailActivity.class);
			intent3.putExtra("information", information.toString());
			context.startActivity(intent3);
			break;
		}
	}
}
