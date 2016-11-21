package com.sobey.cloud.webtv.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.LiveNewsDetailActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.NewRadioLiveChannelListview;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.broadcast.DestoryRadioInstanceReciver;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.utils.AdBanner;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 惠州广电
 * 
 * @author zouxudong
 *
 */
public class HuiZhouSarft extends BaseFragment {
	@GinInjectView(id = R.id.channel_container)
	private LinearLayout channel_container;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	private RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.tvchannel_title)
	private TextView tvchannel_title;
	@GinInjectView(id = R.id.radiochannel_title)
	private TextView radiochannel_title;
	@GinInjectView(id = R.id.tvchannel_linearlayout)
	private LinearLayout tvchannel_linearlayout;
	@GinInjectView(id = R.id.radiochannel_linearlayout)
	private LinearLayout radiochannel_linearlayout;

	@GinInjectView(id = R.id.mAdLayout)
	private RelativeLayout advLayout;
	@GinInjectView(id = R.id.mAdCloseBtn)
	private ImageButton advCloseBtn;
	@GinInjectView(id = R.id.mAdImage)
	private AdvancedImageCarousel advImage;

	private HuiZhouSarftBroadcast broadcast;
	protected String[] catalogIDs;
	private LayoutInflater inflater;
	private int MAX_Alpha = 255;
	private int Base_Little = 35;
	private int loadIndex = 0;

	private List<View> radioList = new ArrayList<View>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = LayoutInflater.from(getActivity());
		View view = getCacheView(inflater, R.layout.fragment_huizhou_sarft);
		// this.inflater=inflater;
		// View view=inflater.inflate(R.layout.fragment_huizhou_sarft,
		// container,false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}
		setupActivity();
	}

	private void setupActivity() {
		initBroadcast();
		channel_container.setVisibility(View.GONE);
		mLoadingIconLayout.setVisibility(View.VISIBLE);
		invokeChannelCatatlogData(MConfig.VIDEO_LIVE_ID);
		invokeChannelCatatlogData(MConfig.AUDIO_LIVE_ID);
		new AdBanner(getActivity(), String.valueOf(MConfig.ZHIBOID), advLayout, advImage, advCloseBtn, true);
	}

	protected void initBroadcast() {
		broadcast = new HuiZhouSarftBroadcast();
		IntentFilter intentFilter = new IntentFilter(HuiZhouSarftBroadcast.HuiZhouSarftPageReciver);
		getActivity().registerReceiver(broadcast, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(broadcast);
		broadcast = null;
	}

	/**
	 * 请求视频直播和音频直播频道信息
	 * 
	 * @param catalogID
	 *            所属栏目id
	 */
	protected void invokeChannelCatatlogData(final String catalogID) {
		int pageNum = 20;
		int pageSize = 1;
		News.getArticleList(0, catalogID, pageNum, pageSize, getActivity(), new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				Log.d("zxd", result.toString());
				if (MConfig.VIDEO_LIVE_ID.equals(catalogID)) // 视频的
				{
					setVideoChannelData(result);
				} else {
					setAudioChannelData(result);
				}
				loadEnd();
			}

			@Override
			public void onNG(String reason) {
				loadEnd();
			}

			@Override
			public void onCancel() {
				loadEnd();
			}
		});
	}

	protected void loadEnd() {
		if (++loadIndex >= 2) {
			channel_container.setVisibility(View.VISIBLE);
			mLoadingIconLayout.setVisibility(View.GONE);
		}
	}

	@SuppressWarnings("deprecation")
	protected void setAudioChannelData(JSONObject result) {
		// radiochannel_linearlayout
		radiochannel_title.setText("惠州电台");

		try {
			final JSONArray jsonArray = result.getJSONArray("articles");
			for (int index = 0; index < jsonArray.length(); index++) {
				Holder holder = new Holder();

				ImageView lineGap = new ImageView(getActivity());
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						Utility.dpToPx(getActivity(), 2F));
				lineGap.setLayoutParams(layoutParams);
				Drawable background = getResources().getDrawable(R.drawable.huizhousarft_channleitem_gapline);
				lineGap.setImageDrawable(background);
				if (index != 0) {
					radiochannel_linearlayout.addView(lineGap);
				}

				View view = inflater.inflate(R.layout.itemchannel_huizhou_sarft, null);
				view.setTag(holder);
				GinInjector.manualInjectView(holder, view);
				int alpha = MAX_Alpha - (index * Base_Little);
				Drawable drawable = null;

				if (index == 0) {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_topshape);
					if (jsonArray.length() == 1) {
						drawable = getResources().getDrawable(R.drawable.huizhousarft_singleshape);
					}
					// holder.channelitem_gapline.setVisibility(View.GONE);
				} else if (index == jsonArray.length() - 1) {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_bottomshape);
				} else {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_rectshape);
				}
				drawable.setAlpha(alpha);
				view.setBackgroundDrawable(drawable);
				JSONObject item = jsonArray.getJSONObject(index);
				holder.channel_name.setText(item.getString("title"));
				ImageLoader.getInstance().displayImage(item.getString("logo"), holder.channel_logo);
				holder.channel_watch_icon.setImageResource(R.drawable.erji_icon);
				radiochannel_linearlayout.addView(view);

				radioList.add(view);
				final int itemIndex = index;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(itemIndex);
							String string = "{\"id\":\"" + jsonObject.getString("id") + "\",\"parentid\":\""
									+ MConfig.AUDIO_LIVE_ID + "\",\"title\":\"" + jsonObject.getString("title") + "\"}";
							Intent intent = new Intent();
							intent.putExtra("information", string);
							intent.putExtra("index", itemIndex);
							intent.setClass(getActivity(), NewRadioLiveChannelListview.class);
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	protected void setVideoChannelData(JSONObject result) {
		tvchannel_title.setText("惠州电视台");
		try {
			final JSONArray jsonArray = result.getJSONArray("articles");
			for (int index = 0; index < jsonArray.length(); index++) {
				Holder holder;
				View view = inflater.inflate(R.layout.itemchannel_huizhou_sarft, null);
				holder = new Holder();
				view.setTag(holder);
				GinInjector.manualInjectView(holder, view);
				int alpha = MAX_Alpha - (index * Base_Little);
				Drawable drawable = null;

				ImageView lineGap = new ImageView(getActivity());
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
						Utility.dpToPx(getActivity(), 2F));
				lineGap.setLayoutParams(layoutParams);
				Drawable background = getResources().getDrawable(R.drawable.huizhousarft_channleitem_gapline);
				lineGap.setImageDrawable(background);
				if (index != 0) {
					tvchannel_linearlayout.addView(lineGap);
				}

				if (index == 0) {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_topshape);
					if (jsonArray.length() == 1) {
						drawable = getResources().getDrawable(R.drawable.huizhousarft_singleshape);
					}
					// holder.channelitem_gapline.setVisibility(View.GONE);
				} else if (index == jsonArray.length() - 1) {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_bottomshape);
				} else {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_rectshape);
				}
				drawable.setAlpha(alpha);
				view.setBackgroundDrawable(drawable);
				JSONObject item = jsonArray.getJSONObject(index);
				holder.channel_name.setText(item.getString("title"));
				ImageLoader.getInstance().displayImage(item.getString("logo"), holder.channel_logo);
				holder.channel_watch_icon.setImageResource(R.drawable.playvideo_icon);
				final int itemIndex = index;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							Intent broad = new Intent(DestoryRadioInstanceReciver.DestoryRadioInstance);
							broad.putExtra("msg", DestoryRadioInstanceReciver.Destory);
							getActivity().sendBroadcast(broad);
							JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(itemIndex);
							String string = "{\"id\":\"" + jsonObject.getString("id") + "\",\"parentid\":\""
									+ MConfig.VIDEO_LIVE_ID + "\"}";
							Intent intent = new Intent();
							intent.putExtra("information", string);
							intent.putExtra("liveMark", "0");
							intent.setClass(getActivity(), LiveNewsDetailActivity.class);
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				tvchannel_linearlayout.addView(view);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	class Holder {
		// @GinInjectView(id =R.id.itemchannel_topbackground)
		// private RelativeLayout itemchannel_topbackground;
		//
		// @GinInjectView(id =R.id.channelitem_gapline)
		// private View channelitem_gapline;

		@GinInjectView(id = R.id.channel_logo)
		private ImageView channel_logo;

		@GinInjectView(id = R.id.channel_name)
		private TextView channel_name;

		@GinInjectView(id = R.id.channel_alias)
		private TextView channel_alias;

		@GinInjectView(id = R.id.channel_watch_icon)
		private ImageView channel_watch_icon;
		@GinInjectView(id = R.id.playStatusLabel)
		private TextView playStatusLabel;
	}

	/**
	 * 收到广播后的处理
	 * 
	 * @param intent
	 */
	private void recivBroadcastHandle(Intent intent) {
		String msg = intent.getStringExtra("msg");
		int index = intent.getIntExtra("index", -1);
		if (HuiZhouSarftBroadcast.RESUME_PLAY.equals(msg)) {
			for (int i = 0; i < radioList.size(); i++) {
				View view = radioList.get(i);
				Holder holder = (Holder) view.getTag();
				if (index == i) {
					holder.playStatusLabel.setText("正在播放");
					holder.playStatusLabel.setVisibility(View.VISIBLE);
					holder.channel_watch_icon.setVisibility(View.GONE);
				} else {
					holder.playStatusLabel.setVisibility(View.GONE);
					holder.channel_watch_icon.setVisibility(View.VISIBLE);
				}
			}
		} else {
			for (int i = 0; i < radioList.size(); i++) {
				View view = radioList.get(i);
				Holder holder = (Holder) view.getTag();
				holder.playStatusLabel.setVisibility(View.GONE);
				holder.channel_watch_icon.setVisibility(View.VISIBLE);
			}
		}
	}

	public static void disposeVideoComponent(Context context) {
		Intent broad = new Intent(DestoryRadioInstanceReciver.DestoryRadioInstance);
		broad.putExtra("msg", DestoryRadioInstanceReciver.Destory);
		context.sendBroadcast(broad);
	}

	public class HuiZhouSarftBroadcast extends BroadcastReceiver {
		public static final String STOP_PLAY = "stop_play";
		public static final String RESUME_PLAY = "resume_play";
		public static final String HuiZhouSarftPageReciver = "HuiZhouSarftPageReciver";

		@Override
		public void onReceive(Context context, Intent intent) {
			recivBroadcastHandle(intent);
		}

	}
}
