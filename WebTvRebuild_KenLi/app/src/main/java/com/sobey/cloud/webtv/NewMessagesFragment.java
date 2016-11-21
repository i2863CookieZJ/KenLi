package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.handmark.pulltorefresh.library.Pull2RefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.higgses.griffin.annotation.app.BitmapUtils;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.higgses.griffin.annotation.app.bitmap.BitmapDisplayConfig;
import com.higgses.griffin.annotation.app.bitmap.callback.BitmapLoadFrom;
import com.higgses.griffin.annotation.app.bitmap.callback.DefaultBitmapLoadCallBack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.ImageShowAdaptor;
import com.sobey.cloud.webtv.adapter.NewMessageAdapter;
import com.sobey.cloud.webtv.adapter.Video_Image_Text_NewsAdaptor;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.fragment.utils.MyGridView;
import com.sobey.cloud.webtv.fragment.utils.MyListView;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.CatalogType;
import com.sobey.cloud.webtv.obj.Information;
import com.sobey.cloud.webtv.utils.BufferUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.Utility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 最新资讯
 * 
 * @author lgx
 * 
 */
public class NewMessagesFragment extends BaseFragment {
	// 顶部轮播图
	@GinInjectView(id = R.id.viewpager)
	protected ViewPager viewpager;
	protected List<ImageView> imageViews = new ArrayList<ImageView>();
	protected List<View> dots;
	// 获取数据的条数
	@GinInjectView(id = R.id.ts_gridView)
	protected MyGridView ts_gridView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	protected Handler handler;
	protected BitmapUtils bitmapUtils;
	@GinInjectView(id = R.id.vp_title)
	protected TextView vp_title;
	protected ViewPager viewPager;
	protected int width;
	@GinInjectView(id = R.id.banner_re)
	protected RelativeLayout banner_re;
	protected int loadEndFlag = 0;
	protected CatalogObj mCatalogObj;
	protected ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	protected static AsyncHttpClient client = new AsyncHttpClient();
	// 资讯内容
	protected Map<String, String> messageMap = new HashMap<String, String>();
	protected List<Information> informations = new ArrayList<Information>();
	@GinInjectView(id = R.id.new_message_Lin)
	protected LinearLayout new_message_Lin;
	/***
	 * 后台返回 来的栏目id 这个是根据cms后台配置里面的
	 */
	protected String[] catalogIDs;

	protected HashMap<String, String> cataLogNameMap = new HashMap<String, String>();
	/**
	 * 栏目id缓存
	 */
	protected final String catalogIDBufferFile = "catalogid_buffer";
	/**
	 * 首页资讯缓存
	 */
	protected final String newsBufferFile = "home_news_buffer";
	/**
	 * 首页推荐缓存
	 */
	protected final String recommendBufferFile = "recommend_buffer";

	/**
	 * 装缓存的数据
	 */
	protected List<View> dynamicViews = new ArrayList<View>();
	@GinInjectView(id = R.id.pull_refresh_scrollview)
	protected Pull2RefreshScrollView pull_refresh_scrollview;

	protected HashMap<String, JSONObject> catatlogDetailData = new HashMap<String, JSONObject>();

	protected int needInvokeTime = 0;

	protected boolean flag = false;

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View view = getCacheView(mInflater, R.layout.new_message_frame);
		return view;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}
		client.setTimeout(11000);
		bitmapUtils = new BitmapUtils(getActivity());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				mArticles = (ArrayList<JSONObject>) msg.obj;
				BufferUtil.saveTextData(recommendBufferFile, mArticles.toString());
				showTopImg(mArticles);
				// loadEndSaveFlagJudge();
			}
		};
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		mLoadingIconLayout.setVisibility(View.VISIBLE);
		callBuffer();
		init();
	}

	protected void callBuffer() {
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				readBufferData();
				getCatalogID();
			}
		});
	}

	protected void init() {
		try {
			for (CatalogObj c : MConfig.CatalogList) {
				if (c.type == CatalogType.Recommend) {
					mCatalogObj = c;
				}
			}
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
		}
		RecommendNewsHome recommendNewsHome = new RecommendNewsHome(getActivity(), mCatalogObj, handler);
		recommendNewsHome.init();

		// pull_refresh_scrollview.setPullToRefreshEnabled(false);
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				flag = true;
				new GetDataTask().execute();
			}
		});
	}

	protected void showTopImg(final List<JSONObject> list) {
		Log.d("zxd", list.toString());
		imageViews.clear();
		try {
			vp_title.setText(list.get(0).getString("ArticleTitle"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) banner_re.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.width = width;
		linearParams.height = displayMetrics.heightPixels * 2 / 7;
		banner_re.getLayoutParams();
		// FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams)
		// viewPager
		// .getLayoutParams(); // 取控件textView当前的布局参数
		// viewPager.getLayoutParams();
		// 头部滚动广告 start
		dots = new ArrayList<View>();
		dots.add((View) getActivity().findViewById(R.id.v_dot0));
		dots.add((View) getActivity().findViewById(R.id.v_dot1));
		dots.add((View) getActivity().findViewById(R.id.v_dot2));
		dots.add((View) getActivity().findViewById(R.id.v_dot3));
		dots.add((View) getActivity().findViewById(R.id.v_dot4));
		dots.add((View) getActivity().findViewById(R.id.v_dot5));
		dots.add((View) getActivity().findViewById(R.id.v_dot6));
		dots.add((View) getActivity().findViewById(R.id.v_dot7));
		dots.add((View) getActivity().findViewById(R.id.v_dot8));
		dots.add((View) getActivity().findViewById(R.id.v_dot9));
		for (int i = 0; i < dots.size(); i++) {
			dots.get(i).setVisibility(View.GONE);
		}
		imageViews.clear();
		int size = list.size();
		if (size > 5) {
			size = 5;
		}
		for (int i = 0; i < size; i++) {
			final int j = i;
			final AdvancedImageView advancedImageView = new AdvancedImageView(getActivity());
			Log.v("Newmessage", list.get(i).optString("FirstRecImg"));
			String imageurl = null;
			if (!TextUtils.isEmpty(list.get(i).optString("FirstRecImg"))) {
				imageurl = list.get(i).optString("FirstRecImg");
			} else if (!TextUtils.isEmpty(list.get(i).optString("SmallLog"))) {
				imageurl = list.get(i).optString("SmallLog");
			} else if (!TextUtils.isEmpty(list.get(i).optString("BigLog"))) {
				imageurl = list.get(i).optString("BigLog");
			}
			bitmapUtils.display(advancedImageView, imageurl, new DefaultBitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					arg2 = Utility.zoomBitmap(arg2, width, displayMetrics.heightPixels * 2 / 7);
					super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {

				}
			});

			advancedImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						openDetailActivity(Integer.valueOf(list.get(j).getString("ArticleType")),
								list.get(j).toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			imageViews.add(advancedImageView);
		}
		for (int i = 0; i < dots.size(); i++) {
			if (i < imageViews.size())
				dots.get(i).setVisibility(View.VISIBLE);
			else
				dots.get(i).setVisibility(View.GONE);
		}
		viewPager.setAdapter(new MyAdapter());
		viewpager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (true) {
					int count = viewPager.getAdapter().getCount();
					int index = viewPager.getCurrentItem();
					for (int i = 0; i < count; i++) {
						try {
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Message msg = new Message();
						index = viewPager.getCurrentItem() + 1;
						if (index > count)
							index = 0;
						msg.obj = i;
						msg.what = -1;
						layout1BannerHandle.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	protected boolean isScroll = true;
	protected Handler layout1BannerHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isScroll && -1 == msg.what) {
				if (null != msg.obj) {
					viewPager.setCurrentItem(Integer.parseInt(msg.obj + ""));
					try {
						vp_title.setText(mArticles.get(Integer.parseInt(msg.obj + "")).getString("ArticleTitle"));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	};

	protected class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	protected class MyPageChangeListener implements OnPageChangeListener {
		protected int oldPosition = 0;

		public void onPageSelected(int position) {
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			String version = android.os.Build.VERSION.RELEASE + "";
			if (version.contains("2.3")) {

			} else {
				viewpager.getParent().requestDisallowInterceptTouchEvent(true);
			}
		}
	}

	protected void loadEndSaveFlagJudge() {
		if (++loadEndFlag >= needInvokeTime) {
			if (mLoadingIconLayout.getVisibility() != View.GONE)
				mLoadingIconLayout.setVisibility(View.GONE);
			pull_refresh_scrollview.onRefreshComplete();
		}
	}

	/**
	 * 根据新闻的类型打开不同的详细页面
	 */
	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(getActivity(), PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			getActivity().startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(getActivity());
			Intent intent1 = new Intent(getActivity(), VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			getActivity().startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(getActivity(), GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			getActivity().startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(getActivity());
			Intent intent4 = new Intent(getActivity(), VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			getActivity().startActivity(intent4);
			break;
		}
	}

	/***
	 * 取栏目id
	 */
	public void getCatalogID() {
		RequestParams params = new RequestParams();
		params.put("method", "appConfig");
		params.put("appkey", MConfig.ZiXun_AppKey);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				loadCatalogData(new String(arg2));
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

			}
		});
	}

	/***
	 * 这根据 返回 来的栏目id 去请求相应 的数据
	 * 
	 * @param args
	 */
	protected void loadCatalogData(String args) {
		Log.d("zxd", "返回aap的栏目id数据" + args);
		if (TextUtils.isEmpty(args)) {
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(args);
			String ids = jsonObject.getString("Msg");
			String[] pairsValue = ids.split(",");
			catalogIDs = new String[pairsValue.length];
			needInvokeTime = catalogIDs.length;
			for (int i = 0; i < pairsValue.length; i++) {
				String[] item = pairsValue[i].split(":");
				if (item.length == 1) {
					catalogIDs = new String[0];
					cataLogNameMap.clear();
					// Toast.makeText(getActivity(),
					// "暂无最新数据!",Toast.LENGTH_SHORT).show();
					return;
				}
				cataLogNameMap.put(item[0], item[1]);
				catalogIDs[i] = item[0];
			}
			BufferUtil.saveTextData(catalogIDBufferFile, ids);
			invokeCatalogData();
		} catch (Exception e) {
			Log.e("zxd", e.getMessage());
		}

	}

	/**
	 * 请求数据
	 * 
	 * @param id
	 */
	protected void invokeCatalogData() {
		needInvokeTime = catalogIDs.length;
		// for(int i=0;i<catalogIDs.length;i++)
		// {
		// getCatalogData(catalogIDs[i]);
		// }
		new GetDataTask().execute();
	}
	// protected void getCatalogData(final String catalogId)
	// {
	// RequestParams params = new RequestParams();
	// params.put("method", "getArticleList");
	// params.put("siteId", String.valueOf(MConfig.SITE_ID));
	// params.put("TerminalType", MConfig.TerminalType);
	// params.put("catalogId", catalogId);
	// params.put("pageNum", 1);//第一页
	// params.put("pageSize", 2);//取5条置顶新闻
	// params.put("isTop", 0);
	// params.put("showcatalog", 1);//新加的接口参数 用于返回栏目名称和栏目的图标用的
	// params.put("sortField", "PUBLISHDATE");
	// params.put("sort", "DESC");
	// client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {
	//
	// @Override
	// public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	// String data = new String(arg2);
	// // BufferUtil.saveTextData(newsBufferFile, data);
	// try {
	// catatlogDetailData.put(catalogId, new JSONObject(data));
	// } catch (JSONException e) {
	// needInvokeTime--;
	// e.printStackTrace();
	// }
	// finally
	// {
	// loadEndSaveFlagJudge();
	// if(catatlogDetailData.entrySet().size()>=needInvokeTime)
	// pushCatalogoData();
	// }
	// Log.v("newMessageFragment", data);
	// // setNewsData(data);
	// }
	//
	// @Override
	// public void onFailure(int arg0, Header[] arg1, byte[] arg2,
	// Throwable arg3) {
	// needInvokeTime--;
	// loadEndSaveFlagJudge();
	// }
	//
	// @Override
	// public void onFinish() {
	// super.onFinish();
	// }
	// });
	// }

	protected void pushCatalogoData() {
		Log.d("zxd", "begin pushCatalogoData");
		destoryBufferView();
		for (int index = 0; index < catalogIDs.length; index++) {
			if (catatlogDetailData.containsKey(catalogIDs[index])) {
				try {
					setViewData2(catatlogDetailData.get(catalogIDs[index]), catalogIDs[index],
							cataLogNameMap.get(catalogIDs[index]));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("zxd", "after pushCatalogoData");
	}

	/**
	 * 设置具体某个类型的视图
	 * 
	 * @param information
	 * @throws JSONException
	 * @throws Exception
	 */
	protected void setViewData2(final JSONObject information, final String parentId, final String catalogName)
			throws JSONException {
		List<Information> programs = com.alibaba.fastjson.JSONArray.parseArray(information.getString("articles"),
				Information.class);
		for (Information item : programs) {
			item.setParentid(parentId);
		}
		JSONArray jsonArray = information.getJSONArray("articles");
		if (jsonArray == null | jsonArray.length() == 0)// &&information.getId().equals(MConfig.ZHUANTIID)==false)
			return;
		View view = crateView();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, Utility.dpToPx(getActivity(), 8.0f));
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setBackgroundColor(getResources().getColor(R.color.new_backgroud));
		layout.setLayoutParams(lp);
		new_message_Lin.addView(layout);
		new_message_Lin.addView(view);
		dynamicViews.add(layout);
		dynamicViews.add(view);
		Holder holder = (Holder) view.getTag();
		gone(holder.lin_zhuanti, holder.ts_gridView, holder.jm_gridView, holder.zhuanti, holder.zt_lin, holder.zt_timu,
				holder.lin_shipin);
		visibile(holder.tw_title, holder.tw_listView);
		holder.tw_title.setText(catalogName);
		holder.tw_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent0 = new Intent(getActivity(), VideoAndNormalNewsListActivity.class);
				intent0.putExtra("type", MConfig.TypeNews);
				intent0.putExtra("ids", parentId);
				intent0.putExtra("title", catalogName);
				getActivity().startActivity(intent0);
			}
		});
		// holder.tw_listView.setAdapter(new RegularNewsAdapter(programs,
		// getActivity(), width, parentId));

		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObjects.add(jsonArray.getJSONObject(i));
		}
		holder.tw_listView.setAdapter(new Video_Image_Text_NewsAdaptor(jsonObjects, getActivity(), parentId, width));
	}

	protected void readBufferData() {
		String catalogBuffer = BufferUtil.getTextData(catalogIDBufferFile);
		String newsBuffer = BufferUtil.getTextData(newsBufferFile);
		String recommendBuffer = BufferUtil.getTextData(recommendBufferFile);
		if (TextUtils.isEmpty(catalogBuffer) || TextUtils.isEmpty(newsBuffer) || TextUtils.isEmpty(recommendBuffer)) {
			mLoadingIconLayout.setVisibility(View.VISIBLE);
		} else {
			try {
				JSONArray jsonArray = new JSONArray(recommendBuffer);
				List<JSONObject> list = new ArrayList<JSONObject>();
				for (int i = 0; i < jsonArray.length(); i++) {
					list.add(jsonArray.getJSONObject(i));
				}
				showTopImg(list);

				String ids = catalogBuffer;
				String[] pairsValue = ids.split(",");
				catalogIDs = new String[pairsValue.length];
				needInvokeTime = catalogIDs.length;
				for (int i = 0; i < pairsValue.length; i++) {
					String[] item = pairsValue[i].split(":");
					if (item.length > 1) {
						cataLogNameMap.put(item[0], item[1]);
						catalogIDs[i] = item[0];
					} else {
						cataLogNameMap.clear();
						catalogIDs = new String[0];
						break;
					}
				}
				JSONObject buffer = new JSONObject(newsBuffer);
				Iterator<String> iterator = buffer.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					catatlogDetailData.put(key, buffer.getJSONObject(key));
				}
				pushCatalogoData();

			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				mLoadingIconLayout.setVisibility(View.GONE);
			}
		}
	}

	protected void destoryBufferView() {
		for (View view : dynamicViews) {
			new_message_Lin.removeView(view);
		}
		dynamicViews.clear();
	}

	/**
	 * 设置具体某个类型的视图
	 * 
	 * @param information
	 * @throws Exception
	 */
	protected void setViewData(final Information information) {
		String parentId = information.getId();
		List<Information> programs = com.alibaba.fastjson.JSONArray.parseArray(information.getContent(),
				Information.class);
		for (Information item : programs) {
			item.setParentid(parentId);
		}
		if (programs.size() == 0)// &&information.getId().equals(MConfig.ZHUANTIID)==false)
			return;
		View view = crateView();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, Utility.dpToPx(getActivity(), 8.0f));
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setBackgroundColor(getResources().getColor(R.color.new_backgroud));
		layout.setLayoutParams(lp);
		new_message_Lin.addView(layout);
		new_message_Lin.addView(view);
		dynamicViews.add(layout);
		dynamicViews.add(view);
		Holder holder = (Holder) view.getTag();
		if (information.getId().equals(MConfig.TUPIANID)) {
			gone(holder.lin_zhuanti, holder.jm_gridView, holder.zhuanti, holder.tw_listView, holder.tw_listView,
					holder.lin_shipin);
			visibile(holder.tw_title, holder.zt_timu, holder.ts_gridView);
			holder.tw_title.setText(information.getName());
			Information item = programs.get(0);
			holder.zt_timu.setText(item.getTitle());
			holder.ts_gridView.setAdapter(new ImageShowAdaptor(programs, getActivity()));
			holder.s_container.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					for (int i = 0; i < MConfig.CatalogList.size(); i++) {
						if (MConfig.CatalogList.get(i).name.equals("图片欣赏")) {
							Intent intent0 = new Intent(getActivity(), HomeActivity.class);
							intent0.putExtra("index", i);
							startActivity(intent0);
						}
					}
				}
			});
		} else if (information.getId().equals(MConfig.ZHUANTIID)) {
			// gone(holder.tw_title,holder.zt_lin,holder.ts_gridView,holder.s_container,
			// // holder.zt_gap,
			// holder.jm_gridView,holder.tw_listView,holder.lin_shipin);
			gone(holder.zixun_item_container, holder.lin_shipin);
			visibile(holder.lin_zhuanti, holder.zhuanti);
			holder.tw_title.setText(programs.get(0).getTitle());
			holder.zhuanti.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					for (int j = 0; j < MConfig.CatalogList.size(); j++) {
						if (MConfig.CatalogList.get(j).id.equals(MConfig.ZHUANTIID)) {
							Intent intent6 = new Intent(getActivity(), TopicNewsHomeActivity.class);
							intent6.putExtra("index", j);
							startActivity(intent6);
							break;
						}
					}
				}
			});
			// holder.zhuanti.setText(programs.get(0).getTitle());
		} else if (MConfig.DIABOID.equals(information.getId())) {
			// gone(holder.jm_gridView,holder.lin_zhuanti,holder.ts_gridView,holder.tw_listView,
			// holder.s_container,
			// holder.zt_lin,
			// holder.tw_title,holder.zhuanti,holder.zhuanti,holder.zt_timu);
			gone(holder.zixun_item_container, holder.lin_zhuanti);
			visibile(holder.lin_shipin);
			holder.sp_title.setText(information.getName());
			holder.lin_shipin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					for (int i = 0; i < MConfig.CatalogList.size(); i++) {
						if (MConfig.CatalogList.get(i).name.equals("点播")) {
							Intent intent0 = new Intent(getActivity(), HomeActivity.class);
							intent0.putExtra("index", i);
							// intent0.putExtra("ids",
							// MConfig.CatalogList.get(i).id);
							// intent0.putExtra("state", "2460");
							// intent0.putExtra("title", "纪录片");
							startActivity(intent0);
						}
					}
				}
			});
			try {
				JSONArray jsonArray = new JSONArray(information.getContent());
				List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObjects.add(jsonArray.getJSONObject(i));
				}
				holder.sp_listView.setAdapter(new VideoAdapter(jsonObjects, getActivity()));
			} catch (Exception e) {
				new_message_Lin.removeView(layout);
				new_message_Lin.removeView(view);
				e.printStackTrace();
				Log.e("zxd", e.toString());
			}

		} else {
			gone(holder.lin_zhuanti, holder.ts_gridView, holder.jm_gridView, holder.zhuanti, holder.zt_lin,
					holder.zt_timu, holder.lin_shipin);
			visibile(holder.tw_title, holder.tw_listView);
			holder.tw_title.setText(information.getName());
			holder.tw_title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					for (int i = 0; i < MConfig.CatalogList.size(); i++) {
						if (MConfig.CatalogList.get(i).id.equals(information.getId())) {
							Intent intent0 = new Intent(getActivity(), HomeActivity.class);
							intent0.putExtra("index", i);
							getActivity().startActivity(intent0);
						}
					}
				}
			});
			holder.tw_listView.setAdapter(new NewMessageAdapter(programs, getActivity(), width, information.getId()));
		}
	}

	/***
	 * 新增一个新的item
	 * 
	 * @author lgx
	 */
	public View crateView() {
		Holder holder = new Holder();
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.new_message_item, null);
		GinInjector.manualInjectView(holder, view);
		view.setTag(holder);
		return view;
	}

	class Holder {
		// 专题lin
		@GinInjectView(id = R.id.lin_zhuanti)
		protected LinearLayout lin_zhuanti;
		// 专题名称
		@GinInjectView(id = R.id.zhuanti)
		protected TextView zhuanti;
		// 图文视频标题
		@GinInjectView(id = R.id.tw_title)
		protected TextView tw_title;
		// 图文、视频listview
		@GinInjectView(id = R.id.tw_listView)
		protected MyListView tw_listView;
		// 节目grid
		@GinInjectView(id = R.id.jm_gridView)
		protected MyGridView jm_gridView;
		// 组图标题
		@GinInjectView(id = R.id.zt_timu)
		protected TextView zt_timu;
		// 组图grid
		@GinInjectView(id = R.id.ts_gridView)
		protected MyGridView ts_gridView;
		// 组图lin
		@GinInjectView(id = R.id.zt_lin)
		protected LinearLayout zt_lin;
		/**
		 * 视频列表布局
		 */
		@GinInjectView(id = R.id.lin_shipin)
		protected LinearLayout lin_shipin;
		/**
		 * 视频栏目标题
		 */
		@GinInjectView(id = R.id.sp_title)
		protected TextView sp_title;
		/**
		 * 视频列表
		 */
		@GinInjectView(id = R.id.sp_listView)
		protected MyListView sp_listView;
		// @GinInjectView(id = R.id.zt_gap)
		// protected MyListView zt_gap;
		// @GinInjectView(id = R.id.tw_container)
		// protected MyListView tw_container;
		@GinInjectView(id = R.id.zixun_item_container)
		protected LinearLayout zixun_item_container;

		@GinInjectView(id = R.id.s_container)
		protected LinearLayout s_container;
	}

	public void gone(View... views) {
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null)
				views[i].setVisibility(View.GONE);
		}
	}

	public void visibile(View... views) {
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null)
				views[i].setVisibility(View.VISIBLE);
		}
	}

	protected class GetDataTask extends AsyncTask<Void, Void, HashMap<String, JSONObject>> {

		@Override
		protected HashMap<String, JSONObject> doInBackground(Void... args) {
			catatlogDetailData.clear();
			JSONObject jsonObject = News.getNewsCatalogIDs();
			String ids = jsonObject.optString("Msg");
			String[] pairsValue = ids.split(",");
			catalogIDs = new String[pairsValue.length];
			needInvokeTime = catalogIDs.length;
			for (int i = 0; i < pairsValue.length; i++) {
				String[] item = pairsValue[i].split(":");
				if (item.length == 1) {
					catalogIDs = new String[0];
					cataLogNameMap.clear();
					Toast.makeText(getActivity(), "暂无最新数据!", Toast.LENGTH_SHORT).show();
					return null;
				}
				cataLogNameMap.put(item[0], item[1]);
				catalogIDs[i] = item[0];
			}
			BufferUtil.saveTextData(catalogIDBufferFile, ids);
			JSONObject buffer = new JSONObject();
			int index = -1;
			for (int i = 0; i < catalogIDs.length; i++) {
				final String catalogId = catalogIDs[i];
				jsonObject = News.getArticleList(0, catalogId, 2, 1, NewMessagesFragment.this.getActivity(), null);
				if (jsonObject != null) {
					catatlogDetailData.put(catalogId, jsonObject);
					try {
						buffer.put(catalogId, jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			BufferUtil.saveTextData(newsBufferFile, buffer.toString());
			return catatlogDetailData;
		}

		@Override
		protected void onPostExecute(HashMap<String, JSONObject> result) {
			pull_refresh_scrollview.onRefreshComplete();
			super.onPostExecute(result);
			mLoadingIconLayout.setVisibility(View.GONE);
			if (catatlogDetailData.size() > 0)
				pushCatalogoData();
			else
				Toast.makeText(getActivity(), "暂无最新数据!", Toast.LENGTH_SHORT).show();
		}
	}

}