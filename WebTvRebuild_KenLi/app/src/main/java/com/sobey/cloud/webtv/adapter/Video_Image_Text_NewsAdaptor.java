package com.sobey.cloud.webtv.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.NewsItemClickUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 图片 视频 图集类的新闻列表项通用适配器 用于惠州台
 * 
 * @author zouxudong
 *
 */
public class Video_Image_Text_NewsAdaptor extends RegularNewsAdapter {

	public int filterLen = 0;
	protected List<JSONObject> dataList;

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Video_Image_Text_NewsAdaptor(List<JSONObject> list, Context context, String parentid, int width) {
		super();
		this.dataList = list;
		this.parentid = parentid;
		this.width = width;
		this.context = context;
		inflater = LayoutInflater.from(context);
		filterImageListNews();
	}

	public void filterImageListNews() {
		for (JSONObject item : dataList) {
			if ((MConfig.TypePicture + "").equals(item.optString("type"))) {
				JSONArray jsonArray = item.optJSONArray("content");
				if (jsonArray.length() == 0) {
					dataList.remove(item);
					filterLen++;
					filterImageListNews();
					break;
				}
			}

		}
	}

	public void setAdaptorData(List<JSONObject> list, String parentid, int width) {
		this.dataList = list;
		this.parentid = parentid;
		this.width = width;
		filterImageListNews();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.item_news_video_image_text, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		setViewData(holder, position, convertView);
		return convertView;
	}

	public void setViewData(Holder holder, final int position, View convertView) {
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		holder.title.setText(dataList.get(position).optString("title").trim());
		// holder.summary.setText(dataList.get(position).getSummary().trim());
		holder.summary.setText(formatString(dataList.get(position).optString("summary").trim(), 20));
		Log.i("context",dataList.get(position).optString("summary"));
		// 取控件textView当前的布局参数
		// ViewGroup.LayoutParams linearParams =
		// holder.previewImageContainer.getLayoutParams();
		// linearParams.width = (int) (width * 0.3215);
		holder.previewImageContainer.getLayoutParams();
		holder.comments.setVisibility(View.VISIBLE);
		holder.previewImageContainer.setVisibility(View.VISIBLE);
		holder.comments.setVisibility(View.VISIBLE);
		holder.title.setVisibility(View.VISIBLE);
		holder.summary.setVisibility(View.GONE);
		holder.comments.setText(dataList.get(position).optString("publishdate"));
		holder.photoAlbumContainer.setVisibility(View.GONE);
		holder.previewImage.clear();
		// 组图显示方式和其它一样
		if (dataList.get(position).optString("type").equals(MConfig.TypeVideo + "")) {
			holder.shipin.setVisibility(View.VISIBLE);
			holder.shipinIcon.setVisibility(View.VISIBLE);
		} else {
			holder.shipin.setVisibility(View.INVISIBLE);
			holder.shipinIcon.setVisibility(View.GONE);
		}
		Log.v("message", "视频");
		holder.comments.setVisibility(View.VISIBLE);
		// holder.shipin.setVisibility(View.VISIBLE);
		holder.shipin.setText(dataList.get(position).optString("hitcount"));
		holder.comments.setText(dataList.get(position).optString("publishdate"));
		holder.bf_time.setVisibility(View.VISIBLE);
		holder.bf_time.setText(dataList.get(position).optString("duration"));
		if (TextUtils.isEmpty(dataList.get(position).optString("duration"))) {
			holder.bf_time.setVisibility(View.GONE);
		}

		// } else {
		// holder.bf_time.setVisibility(View.GONE);
		// holder.shipin.setVisibility(View.GONE);
		// if
		// (dataList.get(position).optString("type").equals(MConfig.TypePicture
		// + "")) {
		// holder.previewImageContainer.setVisibility(View.GONE);
		// holder.comments.setVisibility(View.GONE);
		// holder.title.setVisibility(View.GONE);
		// holder.summary.setVisibility(View.GONE);
		// holder.photoAlbumContainer.setVisibility(View.VISIBLE);
		// holder.albumTitle.setText(dataList.get(position).optString("title").trim());
		// holder.picture_image1.clear();
		// holder.picture_image2.clear();
		// holder.picture_image3.clear();
		// String url1, url2, url3;
		// try {
		// JSONArray jsonArray = dataList.get(position).getJSONArray("content");
		// if (jsonArray.length() > 0) {
		// url1 = jsonArray.getJSONObject(0).getString("filepath");
		// if (isShowPicture)
		// ImageLoader.getInstance().displayImage(url1, holder.picture_image1);
		// holder.picture_image2.setVisibility(View.INVISIBLE);
		// holder.picture_image3.setVisibility(View.INVISIBLE);
		// if (jsonArray.length() >= 3) {
		// url3 = jsonArray.getJSONObject(2).getString("filepath");
		// if (isShowPicture)
		// ImageLoader.getInstance().displayImage(url3, holder.picture_image3);
		// url2 = jsonArray.getJSONObject(1).getString("filepath");
		// if (isShowPicture)
		// ImageLoader.getInstance().displayImage(url2, holder.picture_image2);
		// holder.picture_image2.setVisibility(View.VISIBLE);
		// holder.picture_image3.setVisibility(View.VISIBLE);
		// } else if (jsonArray.length() == 2) {
		// url2 = jsonArray.getJSONObject(1).getString("filepath");
		// if (isShowPicture)
		// ImageLoader.getInstance().displayImage(url2, holder.picture_image2);
		// holder.picture_image2.setVisibility(View.VISIBLE);
		// holder.picture_image3.setVisibility(View.INVISIBLE);
		// }
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// }
		// }

		// 新图片框架
		if (isShowPicture)
			ImageLoader.getInstance().displayImage(dataList.get(position).optString("logo"), holder.previewImage);
		// holder.summary.setVisibility(View.GONE);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					JSONObject jsonObject = new JSONObject(dataList.get(position).toString());
					jsonObject.put("parentid", parentid);
					// openDetailActivity(Integer.valueOf(jsonObject.optString("type")),
					// jsonObject.toString());

					NewsItemClickUtil.OpenDetailPage(Integer.valueOf(jsonObject.optString("type")), jsonObject, context,
							position, parentid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static class Holder {
		@GinInjectView(id = R.id.title)
		public TextView title;
		@GinInjectView(id = R.id.bf_count)
		public TextView shipin;
		@GinInjectView(id = R.id.pl_count)
		public TextView comments;
		@GinInjectView(id = R.id.previewImage)
		public AdvancedImageView previewImage;
		@GinInjectView(id = R.id.summary)
		public TextView summary;
		@GinInjectView(id = R.id.bf_time)
		public TextView bf_time;
		@GinInjectView(id = R.id.previewImageContainer)
		public RelativeLayout previewImageContainer;
		@GinInjectView(id = R.id.photoAlbumContainer)
		public LinearLayout photoAlbumContainer;
		@GinInjectView(id = R.id.albumTitle)
		public TextView albumTitle;
		@GinInjectView(id = R.id.picture_image1)
		public AdvancedImageView picture_image1;
		@GinInjectView(id = R.id.picture_image2)
		public AdvancedImageView picture_image2;
		@GinInjectView(id = R.id.picture_image3)
		public AdvancedImageView picture_image3;
		@GinInjectView(id = R.id.itemBottomLine)
		public View itemBottomLine;
		@GinInjectView(id = R.id.shipin_icon)
		public ImageView shipinIcon;
	}
}