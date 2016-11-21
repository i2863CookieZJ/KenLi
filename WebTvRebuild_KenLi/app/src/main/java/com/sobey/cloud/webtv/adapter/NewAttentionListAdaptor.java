package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.Date;

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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.RequestResultParser;
import com.sobey.cloud.webtv.fragment.NewAttentionFragment;
import com.sobey.cloud.webtv.utils.FaceUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo.State;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewAttentionListAdaptor extends BaseAdapter {

	protected ListView listView;
	private LayoutInflater inflater;
	public static final String NORMAL_NEWS = "1";
	public static final String IMAGE_NEWS = "2";
	public static final String VIDEO_NEWS = "5";
	private Context context;
	private DisplayImageOptions imageOptions;

	public NewAttentionListAdaptor(ListView listView, LayoutInflater inflater, Context context) {
		this.listView = listView;
		this.inflater = inflater;
		this.context = context;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_pic).showImageOnLoading(R.drawable.default_thumbnail_banner)
				.showImageOnFail(R.drawable.default_pic);
		imageOptions = builder.build();
	}

	private ArrayList<JSONObject> newsListData = new ArrayList<JSONObject>();

	public ArrayList<JSONObject> getNewsListData() {
		return newsListData;
	}

	public void addNewsList(ArrayList<JSONObject> list) {

		newsListData.addAll(list);
		notifyDataSetChanged();
	}

	@SuppressWarnings("deprecation")
	private String formatDate(String dateStr, String formatStr) {
		Date date = DateParse.parseDate(dateStr, formatStr);
		Date now = new Date(System.currentTimeMillis());
		long dif = now.getTime() - date.getTime();
		// 5分钟内
		if (dif <= (5 * 60 * 1000)) {
			return "刚刚";
		} else if (date.getYear() == now.getYear() && date.getMonth() == now.getMonth()) {
			if (date.getDate() == now.getDate())
				return "今天 "
						+ DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "HH:mm");
			else if (date.getDate() + 1 == now.getDate())
				return "昨天 "
						+ DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "HH:mm");
		}
		return DateParse.getDate(0, 0, 0, 0, dateStr, DateParse.mDateFormat.toLocalizedPattern(), "MM-dd");
	}

	@Override
	public int getCount() {
		return newsListData.size();
	}

	@Override
	public Object getItem(int index) {
		return newsListData.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) {
		JSONObject jsonObject = getNewsListData().get(index);
		String newsType;
		try {
			newsType = jsonObject.getString("type");
			// TODO
		} catch (JSONException e) {
			newsType = NORMAL_NEWS;
			e.printStackTrace();
		}
		RegularNewsHolder holder;
		if (view == null) {
			view = inflater.inflate(R.layout.item_newattention_listnews, viewGroup,false);
			holder = new RegularNewsHolder(NewsHolder.NEWS);
			holder.itemData = jsonObject;
			view.setTag(holder);
			GinInjector.manualInjectView(holder, view);
		} else {
			holder = (RegularNewsHolder) view.getTag();
		}
		try {
			setViewData(jsonObject, holder, newsType, view, index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return view;
	}

	private void setViewData(JSONObject jsonObject, final RegularNewsHolder holder, String newsType, View view,
			final int index) throws JSONException {
		holder.videoNewsIcon.clear();
		holder.normalNewsIcon.clear();
		GroupSubjectModel subject = null;
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(context);
		boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		if (String.valueOf(NewAttentionFragment.GroupType).equals(newsType)) {
			subject = RequestResultParser.parseSubjectModel(jsonObject);
		}
		holder.viewIndex = index;
		String date = formatDate(jsonObject.getString("publishdate"), "yyyy-MM-dd HH:mm:ss");
		holder.newsPublishDate.setText(date);
		if (jsonObject.has("catalogname")) {
			String catalogName = jsonObject.getString("catalogname");
			holder.newsBelongTitle.setText(catalogName);
		}
		// holder.newsCommentCount.setText(jsonObject.getString("commcount"));
		holder.newsContentTitle.setText(jsonObject.getString("title"));
		// holder.newsSummary.setText(jsonObject.getString("summary"));
		holder.newsSummary.setVisibility(View.INVISIBLE);
		String imageURL = jsonObject.optString("logo");

		// holder.belongSubject.setVisibility(View.GONE);
		// holder.groupLike.setVisibility(View.GONE);
		holder.normalNewsIcon.setVisibility(View.GONE);
		holder.videoNewsContainer.setVisibility(View.GONE);
		holder.videoNewsPlayIcon.setVisibility(View.INVISIBLE);

		// 一般的新闻
		if (NORMAL_NEWS.equals(newsType)) {
			holder.newsSummary.setVisibility(View.VISIBLE);
			holder.videoNewsContainer.setVisibility(View.GONE);
			holder.newsPublishDate.setVisibility(View.VISIBLE);
			String summaryString = jsonObject.getString("summary");
			summaryString = summaryString.trim();
			if (TextUtils.isEmpty(summaryString))
				holder.newsSummary.setVisibility(View.GONE);
			else {
				holder.newsSummary.setVisibility(View.VISIBLE);
				holder.newsSummary.setText(summaryString);
			}

			if (TextUtils.isEmpty(imageURL)) {
				holder.normalNewsIcon.setVisibility(View.GONE);
				return;
			}
			holder.normalNewsIcon.setVisibility(View.VISIBLE);

			holder.normalNewsIcon.setImageDrawable(
					inflater.getContext().getResources().getDrawable(R.drawable.default_thumbnail_video));
			// layoutParams = holder.normalNewsIcon.getLayoutParams();
			// layoutParams.width = (int) (screenWidth / 2 - 1.5 * margin);
			// layoutParams.height = (int) (layoutParams.width / 1.8125);
			// holder.normalNewsIcon.setLayoutParams(layoutParams);

			ImageLoadListener listener = new ImageLoadListener(holder.normalNewsIcon,
					inflater.getContext().getResources().getDrawable(R.drawable.default_thumbnail_video), index);
			listener.isVideoImage = false;
			listener.holder = holder;
			holder.loadingListener = listener;
			if (isShowPicture)
				ImageLoader.getInstance().loadImage(imageURL, imageOptions, listener);
		}
		// 视频新闻
		else if (VIDEO_NEWS.equals(newsType)) {

			if (TextUtils.isEmpty(imageURL)) {
				holder.videoNewsContainer.setVisibility(View.GONE);
				return;
			}
			holder.newsPublishDate.setVisibility(View.VISIBLE);
			holder.normalNewsIcon.setVisibility(View.GONE);
			holder.videoNewsContainer.setVisibility(View.VISIBLE);
			String summaryString = jsonObject.getString("summary");
			summaryString = summaryString.trim();
			if (TextUtils.isEmpty(summaryString))
				holder.newsSummary.setVisibility(View.GONE);
			else {
				holder.newsSummary.setVisibility(View.VISIBLE);
				holder.newsSummary.setText(summaryString);
			}
			// holder.newsContentSummary.setVisibility(View.GONE);
			holder.videoNewsTimeLength.setText(jsonObject.getString("duration"));
			holder.videoNewsPlayIcon.setVisibility(View.INVISIBLE);

			// layoutParams = holder.videoNewsIcon.getLayoutParams();
			// layoutParams.width = screenWidth - 2 * margin;
			// layoutParams.height = (int) (layoutParams.width / 1.8);
			// holder.videoNewsIcon.setLayoutParams(layoutParams);

			ImageLoadListener listener = new ImageLoadListener(holder.videoNewsIcon,
					inflater.getContext().getResources().getDrawable(R.drawable.default_thumbnail_banner), index);
			listener.isVideoImage = true;
			listener.holder = holder;
			holder.loadingListener = listener;
			if (isShowPicture)
				ImageLoader.getInstance().loadImage(imageURL, imageOptions, listener);

		}
		// 图片
		else if (IMAGE_NEWS.equals(newsType)) {
			holder.newsPublishDate.setVisibility(View.VISIBLE);
			holder.normalNewsIcon.setVisibility(View.GONE);
			holder.videoNewsContainer.setVisibility(View.GONE);
			// holder.newsContentSummary.setVisibility(View.GONE);

			JSONArray jsonArray = jsonObject.getJSONArray("content");
			String logoUrl = jsonObject.getString("logo");
			String url1, url2, url3;
			if (jsonArray.length() > 0) {
				url1 = jsonArray.getJSONObject(0).getString("filepath");
				if (isShowPicture)
					ImageLoader.getInstance().displayImage(logoUrl, holder.normalNewsIcon, imageOptions);
				holder.normalNewsIcon.setVisibility(View.VISIBLE);
			}
		}
		// 圈子的数据
		else if (String.valueOf(NewAttentionFragment.GroupType).equals(newsType)) {
			holder.newsPublishDate.setVisibility(View.VISIBLE);
			holder.newsBelongTitle.setVisibility(View.GONE);
			holder.newsSummary.setVisibility(View.VISIBLE);
			holder.newsSummary.setText(jsonObject.getString("catalogname"));
			// if (TextUtils.isEmpty(subject.subjectContent))
			// holder.newsContentSummary.setVisibility(View.GONE);
			// else {
			// holder.newsContentSummary.setVisibility(View.VISIBLE);
			// holder.newsContentSummary.setText(Html.fromHtml(
			// subject.subjectContent, new ImageGetter(), null));
			// }
			// holder.belongSubject.setVisibility(View.VISIBLE);
			// holder.belongSubject.setText(jsonObject.optString("groupName"));
			// holder.groupLike.setVisibility(View.VISIBLE);
			// holder.groupLike.setText(jsonObject.optString("subjectLikeCount"));

			// if (subject != null && subject.subjectPicUrls != null &&
			// subject.subjectPicUrls.length > 0) {
			if (subject != null && !TextUtils.isEmpty(subject.publishUserHeadUrl)) {
				holder.normalNewsIcon.setVisibility(View.VISIBLE);

				// holder.groupfirstImage.clear();
				// holder.groupsecondImage.clear();

				if (isShowPicture) {
					ImageLoader.getInstance().displayImage(subject.publishUserHeadUrl, holder.normalNewsIcon,
							imageOptions);
				} else {
					holder.normalNewsIcon.setImageResource(R.drawable.default_pic);
				}

				// holder.groupsecondImage.setNetImage(subject.subjectPicUrls[0]);
			} else {
				holder.normalNewsIcon.setVisibility(View.VISIBLE);
				holder.normalNewsIcon.setImageResource(R.drawable.default_pic);
			}
		}
	}

	public static class NewsHolder {
		public static int TOP = 1;
		public static int NOTE = 2;
		public static int NEWS = 3;
		public final int HOLDER_TYPE;
		public JSONObject itemData;

		public NewsHolder(int holderType) {
			HOLDER_TYPE = holderType;
		}
	}

	/**
	 * 置顶新闻
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class TopNewsHolder extends NewsHolder {
		public TopNewsHolder(int holderType) {
			super(holderType);
		}

		@GinInjectView(id = R.id.attention_topNormalnews_icon)
		public ImageView attention_topNormalnews_icon;

		@GinInjectView(id = R.id.attention_topGroupnews_icon)
		public TextView attention_topGroupnews_icon;

		@GinInjectView(id = R.id.attentionTopNewsTitle)
		public TextView attentionTopNewsTitle;
	}

	/**
	 * 通知服务类新闻
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class NoteServiceNewsHolder extends NewsHolder {
		public NoteServiceNewsHolder(int holderType) {
			super(holderType);
		}

		@GinInjectView(id = R.id.noteServiceNewsTitle)
		public TextView noteServiceNewsTitle;

		@GinInjectView(id = R.id.noteServiceNewsPublishDate)
		public TextView noteServiceNewsPublishDate;

		@GinInjectView(id = R.id.noteServiceNewsSummary)
		public TextView noteServiceNewsSummary;
	}

	/**
	 * 常规新闻
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class RegularNewsHolder extends NewsHolder {
		/**
		 * 当前渲染的索引
		 */
		public int viewIndex = 0;
		/**
		 * 事件监听
		 */
		public ImageLoadingListener loadingListener;

		public RegularNewsHolder(int holderType) {
			super(holderType);
		}

		/*
		 * @GinInjectView(id = R.id.newsHeaderIconContainer) public
		 * RelativeLayout newsHeaderIconContainer;
		 * 
		 * @GinInjectView(id = R.id.newsHeaderIcon) public AdvancedImageView
		 * newsHeaderIcon;
		 */

		@GinInjectView(id = R.id.newsBelongTitle)
		public TextView newsBelongTitle;

		@GinInjectView(id = R.id.newsPublishDate)
		public TextView newsPublishDate;

		// @GinInjectView(id = R.id.newsCommentCount)
		// public TextView newsCommentCount;

		@GinInjectView(id = R.id.newsContentTitle)
		public TextView newsContentTitle;

		@GinInjectView(id = R.id.newsSummary)
		public TextView newsSummary;

		@GinInjectView(id = R.id.normalNewsIcon)
		public AdvancedImageView normalNewsIcon;

		@GinInjectView(id = R.id.videoNewsContainer)
		public RelativeLayout videoNewsContainer;

		@GinInjectView(id = R.id.videoNewsIcon)
		public AdvancedImageView videoNewsIcon;

		@GinInjectView(id = R.id.videoNewsPlayIcon)
		public ImageView videoNewsPlayIcon;

		@GinInjectView(id = R.id.videoNewsTimeLength)
		public TextView videoNewsTimeLength;

		// @GinInjectView(id = R.id.imageNewsContainer)
		// public LinearLayout imageNewsContainer;
		//
		// @GinInjectView(id = R.id.firstImageNewsIcon)
		// public AdvancedImageView firstImageNewsIcon;
		//
		// @GinInjectView(id = R.id.secondImageNewsIcon)
		// public AdvancedImageView secondImageNewsIcon;
		//
		// @GinInjectView(id = R.id.thirdImageNewsIcon)
		// public AdvancedImageView thirdImageNewsIcon;

		@GinInjectView(id = R.id.newsImageContainer)
		public RelativeLayout newsImageContainer;

		// @GinInjectView(id = R.id.imageNewsLeftImageContainer)
		// public LinearLayout imageNewsLeftImageContainer;
		//
		// @GinInjectView(id = R.id.groupImageContainer)
		// public LinearLayout groupImageContainer;
		//
		// @GinInjectView(id = R.id.groupfirstImage)
		// public AdvancedImageView groupfirstImage;
		//
		// @GinInjectView(id = R.id.groupsecondImage)
		// public AdvancedImageView groupsecondImage;

		// @GinInjectView(id = R.id.belongSubject)
		// public TextView belongSubject;
		//
		// @GinInjectView(id = R.id.groupLike)
		// public TextView groupLike;
	}

	/**
	 * 图片加载监听
	 * 
	 * @author zouxudong
	 * 
	 */
	class ImageLoadListener implements ImageLoadingListener {

		public final ImageView imageView;
		public final Drawable failedDrawable;
		public final int viewIndex;
		public RegularNewsHolder holder;
		public boolean isVideoImage;

		public ImageLoadListener(ImageView imageView, Drawable failedDrawable, int viewIndex) {
			this.imageView = imageView;
			this.failedDrawable = failedDrawable;
			this.viewIndex = viewIndex;
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			if (viewIndex == holder.viewIndex) {
				imageView.setImageBitmap(arg2);
				if (isVideoImage)
					holder.videoNewsPlayIcon.setVisibility(View.VISIBLE);
			} else {
				imageView.setImageDrawable(failedDrawable);
			}
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			if (viewIndex == holder.viewIndex) {
				imageView.setImageDrawable(failedDrawable);
			}
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {

		}

	}

	private class ImageGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
			if (source.contains("default")) {
				drawable = context.getResources().getDrawable(FaceUtil.defaultFaces.get(face));
			} else if (source.contains("coolmonkey")) {
				drawable = context.getResources().getDrawable(FaceUtil.coolmonkeyFaces.get(face));
			} else if (source.contains("grapeman")) {
				drawable = context.getResources().getDrawable(FaceUtil.grapemanFaces.get(face));
			}
			if (null != drawable) {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
			return drawable;
		}

	}

}
