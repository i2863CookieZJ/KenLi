package com.sobey.cloud.webtv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
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
public class AutoPlayUtil {

	private ArrayList<JSONObject> topNewsArray = new ArrayList<JSONObject>();
	private HashMap<String, GroupModel> attentionGroup = new HashMap<String, GroupModel>();

	protected static final long INTERVAL = 3000;
	private TopRecommendHolder holder;
	private final String ShareName = "RecommendNews";
	private final String ShareKey = "newsData";
	private final String LastTime = "lastRecommendPushTime";
	private Context context;
	public List<AdvancedImageView> imageList = new ArrayList<AdvancedImageView>();
	private List<ImageView> dots = new ArrayList<ImageView>();
	private int currentIndex = 0;
	private Handler switchHandler;
	private boolean runFlag = false;
	private List<RecommendNewsItem> recommendList = new ArrayList<RecommendNewsItem>();
	private ViewPageListener listener = new ViewPageListener();
	private DisplayImageOptions imageOptions;
	private View headView;

	private int pageTotlaNum;

	private LoadImageListener mLoadImageListener;

	public AutoPlayUtil(View view, Context context, LoadImageListener loadImageListener) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_loading_banner)
				.showImageOnLoading(R.drawable.default_loading_banner);
		mLoadImageListener = loadImageListener;
		headView = view;
		imageOptions = builder.build();
		holder = new TopRecommendHolder();
		GinInjector.manualInjectView(holder, view);
		this.context = context;
		LayoutParams layoutParams = holder.advImage.getLayoutParams();
		layoutParams.height = context.getResources().getDisplayMetrics().heightPixels * 2 / 7;
		holder.advImage.setLayoutParams(layoutParams);
		holder.advImage.setAdapter(new ImageAdaptor());

		holder.advImage.setOnTouchListener(listener);
		holder.advImage.setOnPageChangeListener(listener);

		switchHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (holder.advImage.getAdapter().getCount() == 0)
					return false;
				for (int index = 0; index < pageTotlaNum; index++) {
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

	}

	private void readBuffer() {
		String topData = BufferUtil.getTextData("att_top_news_bf");
		if (!TextUtils.isEmpty(topData)) {
			addTopNews2HeaderView(topData, false);
		} else {
			headView.setTag("noPage");// 没轮播的时候不显示
		}
	}

	/**
	 * 添加置顶新闻到List里面
	 * 
	 * @param news
	 */
	protected void addTopNews2HeaderView(String data, boolean continueLoadNormalNews) {
		BufferUtil.saveTextData("att_top_news_bf", data);
		try {
			JSONObject topNewData = new JSONObject(data);
			JSONArray topsNews = topNewData.getJSONArray("articles");
			// if (topsNews.length() == 0) {
			// clearTopNews();
			// topNewsArray.clear();
			// return;
			// }
			// clearTopNews();
			topNewsArray.clear();
			recommendList.clear();
			for (int i = 0; i < topsNews.length(); i++) {
				JSONObject jsonObject = topsNews.getJSONObject(i);
				recommendList.add(createNewsItem(jsonObject));
				topNewsArray.add(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (recommendList.size() > 0) {
				holder.loadingLl.setVisibility(View.GONE);
				headView.setTag("hasPage");
				SetRecommendData(recommendList);
			} else {
				headView.setTag("noPage");// 没轮播的时候不显示
			}
			if (mLoadImageListener != null) {
				mLoadImageListener.loadComplete();
			}
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
		holder.advImage.setCurrentItem(pageTotlaNum / 2, true);
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

	public void refreshRecommendList() {
		// recommendDataArray = new JSONArray();
		// JSONArray jsonArray = News.pushHomePageRec(null, context, null);
		// if (jsonArray != null && jsonArray.length() > 0) {
		// recommendList.clear();
		// for (int i = 0; i < jsonArray.length(); i++) {
		// JSONArray item = jsonArray.optJSONObject(i).optJSONArray("recList");
		// if (item != null & item.length() > 0) {
		// JSONObject reommendItem;
		// if (item.length() > 1) {
		// for (int j = 0; j < item.length(); j++) {
		// reommendItem = item.optJSONObject(j);
		// recommendDataArray.put(reommendItem);
		// setItemTypeAndID(reommendItem);
		// recommendList.add(createNewsItem(item.optJSONObject(j)));
		// }
		// break;
		// }
		// reommendItem = item.optJSONObject(0);
		// setItemTypeAndID(reommendItem);
		// recommendDataArray.put(reommendItem);
		// recommendList.add(createNewsItem(item.optJSONObject(0)));
		// }
		// }
		// }
		// if (recommendList.size() > 0) {
		// SharedPreferences preferences =
		// context.getSharedPreferences(ShareName, Activity.MODE_PRIVATE);
		// Editor editor = preferences.edit();
		// editor.putString(ShareKey, recommendDataArray.toString());
		// editor.commit();
		// // SetRecommendData(recommendList);
		// }
		// return recommendList;
		new GetTopicNewsDataTask().execute();
	}

	/**
	 * 取置顶新闻数据
	 * 
	 * @author zouxudong
	 * 
	 */
	class GetTopicNewsDataTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject maxtTopicCount = News.getAttentionTopNewsMaxCount();
			final int MAX_TOPNEWS_SIZE;
			if (maxtTopicCount != null) {
				String msg = maxtTopicCount.optString("Msg");
				if (msg != null && !msg.equals("0")) {
					Pattern pattern = Pattern.compile("[0-9]*");
					Matcher isNum = pattern.matcher(msg);
					if (!isNum.matches()) {
						MAX_TOPNEWS_SIZE = 5;
					} else {
						MAX_TOPNEWS_SIZE = Integer.valueOf(msg);
					}
				} else
					MAX_TOPNEWS_SIZE = 5;
			} else
				MAX_TOPNEWS_SIZE = 5;

			JSONObject cmsTopicNews = News.getTopNewsFromCMS(MConfig.SITE_ID, 1, MAX_TOPNEWS_SIZE, context, null);
			return cmsTopicNews;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null) {
				recommendList.clear();
				addTopNews2HeaderView(result.toString(), true);
			} else {
				headView.setTag("noPage");
				if (mLoadImageListener != null) {
					mLoadImageListener.loadComplete();
				}
			}
		}

	}

	protected RecommendNewsItem createNewsItem(JSONObject item) {
		RecommendNewsItem newsItem = new RecommendNewsItem();
		newsItem.setArticleTitle(item.optString("title"));
		newsItem.setArticleType(item.optString("type"));
		newsItem.setArticleURL("");
		newsItem.setArtilecID(item.optString("id"));
		newsItem.setFirstRecImg(item.optString("logo1"));// 头条轮播大图
		newsItem.setLead("");
		newsItem.setSmallLog(item.optString("logo"));
		return newsItem;
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
				pageTotlaNum = imageList.size() * 1000;
			} else {
				pageTotlaNum = imageList.size();
			}
			return pageTotlaNum;
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
			if (++currentIndex >= pageTotlaNum)
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
				if (i == index % imageList.size()) {
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
				String type = recommendList.get(index).getArticleType();
				String info = "{\"id\":\"" + recommendList.get(index).getArtilecID() + "\",\"parentid\":\"" + "\"}";
				openDetailActivity(Integer.valueOf(type), info);
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
