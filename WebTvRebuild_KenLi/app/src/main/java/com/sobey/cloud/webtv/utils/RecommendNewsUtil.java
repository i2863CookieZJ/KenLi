package com.sobey.cloud.webtv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.kenli.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 推荐新闻 用于那种要滚动效果的
 * 
 * @author zouxudong
 * 
 */
public class RecommendNewsUtil {
	protected static final long INTERVAL = 3000;
	private TopRecommendHolder holder;
	private final String ShareName = "RecommendNews";
	private final String ShareKey = "newsData";
	private final String LastTime = "lastRecommendPushTime";
	private Context context;
	private String lastTime;
	public List<AdvancedImageView> imageList = new ArrayList<AdvancedImageView>();
	private JSONArray recommendDataArray;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private int currentIndex = 0;
	private Handler switchHandler;
	private boolean runFlag = false;
	private List<RecommendNewsItem> recommendList = new ArrayList<RecommendNewsItem>();
	private ViewPageListener listener = new ViewPageListener();
	private DisplayImageOptions imageOptions;

	private int pageTotalNum;
	private View headView;

	private LoadImageListener mLoadImageListener;

	public RecommendNewsUtil() {

	}

	public RecommendNewsUtil(View view, Context context, LoadImageListener loadImageListener) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_loading_banner)
				.showImageOnLoading(R.drawable.default_loading_banner);
		mLoadImageListener = loadImageListener;
		imageOptions = builder.build();
		holder = new TopRecommendHolder();
		GinInjector.manualInjectView(holder, view);
		this.context = context;
		this.headView = view;
		LayoutParams layoutParams = holder.advImage.getLayoutParams();
		layoutParams.height = context.getResources().getDisplayMetrics().heightPixels * 2 / 7;
		holder.advImage.setLayoutParams(layoutParams);
		holder.advImage.setAdapter(new ImageAdaptor());

		holder.advImage.setOnTouchListener(listener);
		holder.advImage.setOnPageChangeListener(listener);
		lastTime = DateParse.getDate(0, 0, 0, 0, null, null, "yyyy-MM-dd HH:mm:ss");

		switchHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (holder.advImage.getAdapter().getCount() == 0)
					return false;
				for (int index = 0; index < pageTotalNum; index++) {
					// ImageView view=dots.get(index);
					// LinearLayout.LayoutParams
					// layoutParams=(android.widget.LinearLayout.LayoutParams)
					// view.getLayoutParams();
					if (msg.what == currentIndex && msg.what == index) {
						// view.setBackgroundResource(R.drawable.dot_recommend_item_focused);
						// holder.recommendTitle.setText(recommendList.get(index).getArticleTitle());
						holder.advImage.setCurrentItem(index, true);
					} else {
						// view.setBackgroundResource(R.drawable.dot_normal);
					}
					// view.setLayoutParams(layoutParams);
				}
				return false;
			}
		});

		readBuffer();
		refreshRecommendList();
		holder.advImage.setCurrentItem(pageTotalNum / 2);
	}

	protected void readBuffer() {
		SharedPreferences preferences = context.getSharedPreferences(ShareName, Activity.MODE_PRIVATE);
		String newsData = preferences.getString("newsData", "[]");
		lastTime = preferences.getString(LastTime, null);
		try {
			recommendList.clear();
			recommendDataArray = new JSONArray(newsData);
			for (int i = 0; i < recommendDataArray.length(); i++) {
				recommendList.add(createNewsItem(recommendDataArray.optJSONObject(i)));
			}
			if (recommendList.size() > 0) {
				headView.setTag("hasPage");
				holder.loadingLl.setVisibility(View.GONE);
				SetRecommendData(recommendList);
			} else {
				headView.setTag("noPage");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置推荐页列表
	 * 
	 * @param list
	 */
	public void SetRecommendData(List<RecommendNewsItem> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Collections.synchronizedList(list);
		imageList.clear();
		Iterator<RecommendNewsItem> iterator = list.iterator();
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		while (iterator.hasNext()) {
			RecommendNewsItem recommendNewsItem = iterator.next();
			String imageurl = TextUtils.isEmpty(recommendNewsItem.getFirstRecImg())
					? TextUtils.isEmpty(recommendNewsItem.getSmallLog()) ? null : recommendNewsItem.getSmallLog()
					: recommendNewsItem.getFirstRecImg();
			if (!TextUtils.isEmpty(imageurl)) {
				final AdvancedImageView image = new AdvancedImageView(context);
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				image.setLayoutParams(layoutParams);
				image.setErrorImage(R.drawable.default_loading_banner);
				image.setLoadingImage(R.drawable.default_loading_banner);
				image.setScaleType(ScaleType.FIT_XY);
				if (isShowPicture)
					ImageLoader.getInstance().displayImage(imageurl, image, imageOptions);
				image.setOnClickListener(listener);
				imageList.add(image);
			}
		}
		holder.advImage.getAdapter().notifyDataSetChanged();
		refreshDotImage();
	}

	private void refreshDotImage() {
		for (View dot : dots) {
			holder.imageNumDotContainer.removeView(dot);
		}
		dots.clear();
		for (int i = 0; i < imageList.size(); i++) {
			ImageView view = new ImageView(context);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(5, 0, 5, 0);

			view.setLayoutParams(layoutParams);
			dots.add(view);
			view.setBackgroundResource(R.drawable.dot_banner_normal);
			holder.imageNumDotContainer.addView(view);
		}
		dots.get(0).setBackgroundResource(R.drawable.dot_banner_focused);
		listener.onPageSelected(0);
	}

	public List<RecommendNewsItem> refreshRecommendList() {
		recommendDataArray = new JSONArray();
		JSONArray jsonArray = News.pushHomePageRec(null, context, null);
		if (jsonArray != null && jsonArray.length() > 0) {
			recommendList.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray item = jsonArray.optJSONObject(i).optJSONArray("recList");
				if (item != null & item.length() > 0) {
					JSONObject reommendItem;
					if (item.length() > 1) {
						for (int j = 0; j < item.length(); j++) {
							reommendItem = item.optJSONObject(j);
							recommendDataArray.put(reommendItem);
							setItemTypeAndID(reommendItem);
							recommendList.add(createNewsItem(item.optJSONObject(j)));
						}
						break;
					}
					reommendItem = item.optJSONObject(0);
					setItemTypeAndID(reommendItem);
					recommendDataArray.put(reommendItem);
					recommendList.add(createNewsItem(item.optJSONObject(0)));
				}
			}
		}
		if (recommendList.size() > 0) {
			SharedPreferences preferences = context.getSharedPreferences(ShareName, Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString(ShareKey, recommendDataArray.toString());
			editor.commit();
			// SetRecommendData(recommendList);
			headView.setTag("hasPage");
			holder.loadingLl.setVisibility(View.GONE);
		} else {
			headView.setTag("noPage");
		}

		if (mLoadImageListener != null && "noPage".equals(headView.getTag().toString())) {
			mLoadImageListener.loadComplete();
		}
		return recommendList;

	}

	protected RecommendNewsItem createNewsItem(JSONObject item) {
		RecommendNewsItem newsItem = new RecommendNewsItem();
		newsItem.setArticleTitle(item.optString("ArticleTitle"));
		newsItem.setArticleType(item.optString("ArticleType"));
		newsItem.setArticleURL(item.optString("ArticleURL"));
		newsItem.setArtilecID(item.optString("ArtilecID"));
		newsItem.setFirstRecImg(item.optString("FirstRecImg"));
		newsItem.setLead(item.optString("Lead"));
		newsItem.setSmallLog(item.optString("SmallLog"));
		return newsItem;
	}

	/**
	 * 有些字段没得那个ArticleType类型 没有的去请求录找一次
	 * 
	 * @param item
	 */
	private void setItemTypeAndID(JSONObject item) {
		try {
			item.put("id", item.optString("ArtilecID"));
			if (item.has("ArticleType") == false) {
				JSONArray jsonArray = News.getArticleById(item.optString("ArtilecID"), null, null, null, null, context,
						null);
				if (jsonArray != null && jsonArray.length() > 0)
					item.put("type", jsonArray.optJSONObject(0).optString("type"));
			} else {
				item.put("type", item.optString("ArticleType"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	class TopRecommendHolder {
		@GinInjectView(id = R.id.mAdImage)
		public ViewPager advImage;
		@GinInjectView(id = R.id.recommendTitle)
		public TextView recommendTitle;
		@GinInjectView(id = R.id.imageNumDotContainer)
		public LinearLayout imageNumDotContainer;

		@GinInjectView(id = R.id.loading_ll)
		public LinearLayout loadingLl;
	}

	class ImageAdaptor extends PagerAdapter {

		@Override
		public int getCount() {
			if (imageList.size() > 1) {
				pageTotalNum = imageList.size() * 1000;
			} else {
				pageTotalNum = imageList.size();
			}
			return pageTotalNum;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			position %= imageList.size();
			if (position < 0) {
				position = imageList.size() + position;
			}
			ImageView view = imageList.get(position);
			// 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
			ViewParent vp = view.getParent();
			if (vp != null) {
				ViewGroup parent = (ViewGroup) vp;
				parent.removeView(view);
			}
			container.addView(view);
			// add listeners here if necessary
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// ((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * 推荐列表项
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class RecommendNewsItem {
		public String getArticleURL() {
			return ArticleURL;
		}

		public void setArticleURL(String articleURL) {
			ArticleURL = articleURL;
		}

		public String getArtilecID() {
			return ArtilecID;
		}

		public void setArtilecID(String artilecID) {
			ArtilecID = artilecID;
		}

		public String getFirstRecImg() {
			return FirstRecImg;
		}

		public void setFirstRecImg(String firstRecImg) {
			FirstRecImg = firstRecImg;
		}

		public String getArticleTitle() {
			return ArticleTitle;
		}

		public void setArticleTitle(String articleTitle) {
			ArticleTitle = articleTitle;
		}

		public String getSmallLog() {
			return SmallLog;
		}

		public void setSmallLog(String smallLog) {
			SmallLog = smallLog;
		}

		public String getLead() {
			return Lead;
		}

		public void setLead(String lead) {
			Lead = lead;
		}

		private String ArticleURL;
		private String ArtilecID;
		private String FirstRecImg;
		private String ArticleTitle;
		private String SmallLog;
		private String Lead;
		private String ArticleType;

		public String getArticleType() {
			return ArticleType;
		}

		public void setArticleType(String articleType) {
			ArticleType = articleType;
		}
	}

	class ViewPageListener implements ViewPager.OnPageChangeListener, OnTouchListener, OnClickListener {
		class Timer implements Runnable {
			Message message = new Message();

			public Timer(final int index) {
				message.what = index;
			}

			public boolean flag = true;

			@Override
			public void run() {
				if (!flag)
					return;
				switchHandler.sendMessage(message);
			}

		}

		private Handler handler = new Handler();
		private Timer runnable;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.getParent().requestDisallowInterceptTouchEvent(true);
			return false;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			String version = android.os.Build.VERSION.RELEASE + "";
			if (!version.contains("2.3")) {
				holder.advImage.getParent().requestDisallowInterceptTouchEvent(true);
			}
		}

		@Override
		public void onPageSelected(int index) {
			currentIndex = index;
			// Log.d("zxd", "onPageSelected:"+index);
			if (++currentIndex >= pageTotalNum)
				currentIndex = 0;
			final int idx = currentIndex;
			if (runnable != null)
				runnable.flag = false;
			runnable = new Timer(idx);
			handler.postDelayed(runnable, INTERVAL);

			for (int i = 0; i < recommendList.size(); i++) {
				if (i >= dots.size()) {
					i = 0;
				}
				ImageView view = dots.get(i);
				LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) view
						.getLayoutParams();
				if (i == index % (imageList.size())) {
					view.setBackgroundResource(R.drawable.dot_banner_focused);
					holder.recommendTitle.setText(recommendList.get(i).getArticleTitle());
					// holder.advImage.setCurrentItem(index, true);
				} else {
					view.setBackgroundResource(R.drawable.dot_banner_normal);
				}
				view.setLayoutParams(layoutParams);
			}
		}

		@Override
		public void onClick(View arg0) {
			Log.d("zxd", "onClick:" + arg0);
			try {
				int index = holder.advImage.getCurrentItem() % 1000 % imageList.size();
				String type = recommendDataArray.optJSONObject(index).optString("type");
				openDetailActivity(Integer.valueOf(type), recommendDataArray.optJSONObject(index).toString());
			} catch (Exception e) {
				ToastUtil.showToast(context, "新闻不存在或已经删除");
			}
		}

		public void openDetailActivity(int type, String information) {
			switch (type) {
			case MConfig.TypePicture:
				Intent intent = new Intent(context, PhotoNewsDetailActivity.class);
				intent.putExtra("information", information);
				context.startActivity(intent);
				break;
			case MConfig.TypeVideo:
				HuiZhouSarft.disposeVideoComponent(context);
				Intent intent1 = new Intent(context, VideoNewsDetailActivity.class);
				intent1.putExtra("information", information);
				context.startActivity(intent1);
				break;
			case MConfig.TypeNews:
				Intent intent2 = new Intent(context, GeneralNewsDetailActivity.class);
				intent2.putExtra("information", information);
				context.startActivity(intent2);
				break;
			default:
				HuiZhouSarft.disposeVideoComponent(context);
				Intent intent3 = new Intent(context, VideoNewsDetailActivity.class);
				intent3.putExtra("information", information);
				context.startActivity(intent3);
				break;
			}
		}

	}

	public interface LoadImageListener {
		public void loadComplete();
	}
}
