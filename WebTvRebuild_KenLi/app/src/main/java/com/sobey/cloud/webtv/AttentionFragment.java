package com.sobey.cloud.webtv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.fragment.utils.IAsynTask;
import com.sobey.cloud.webtv.fragment.utils.JsonUtil;
import com.sobey.cloud.webtv.fragment.utils.MyGridView;
import com.sobey.cloud.webtv.fragment.utils.MyListView;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.obj.Topic;
import com.sobey.cloud.webtv.utils.AdBanner;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.ToastUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * 关注
 * 
 * @author lgx
 *
 */
public class AttentionFragment extends BaseFragment {
	// 活动
	@GinInjectView(id = R.id.hd_listView)
	private MyListView hd_listView;
	// 活动的信息
	private ArrayList<JSONObject> hd_list = new ArrayList<JSONObject>();
	private CatalogObj hdCatalogObj;
	private String hdmCatalogId = null;
	private ActivitiesAdpater hdAdpater;
	// 便民的信息
	private CatalogObj bmmCatalogObj;
	private ArrayList<JSONObject> bmCatalogs = new ArrayList<JSONObject>();
	private String bmCatalogId = null;
	private ArrayList<JSONObject> bm_list = new ArrayList<JSONObject>();
	@GinInjectView(id = R.id.bm_listView)
	private MyListView bm_listView;
	// 资讯列表
	@GinInjectView(id = R.id.zx_listView)
	private MyListView zx_listView;
	// 资讯CatalogObj
	private CatalogObj zxmCatalogObj;
	private ArrayList<JSONObject> zxmCatalogs = new ArrayList<JSONObject>();
	private String zxmCatalogId = null;
	private ArrayList<JSONObject> zx_list = new ArrayList<JSONObject>();
	// 图说
	@GinInjectView(id = R.id.ts_gridView)
	private MyGridView ts_gridView;
	private CatalogObj tsmCatalogObj;
	private ArrayList<JSONObject> tsmCatalogs = new ArrayList<JSONObject>();
	private String tsmCatalogId = null;
	private ArrayList<JSONObject> ts_list = new ArrayList<JSONObject>();
	private Handler handler;
	private int pagesize = 1;
	@GinInjectView(id = R.id.mAdImage)
	private AdvancedImageCarousel mAdImage;
	@GinInjectView(id = R.id.mAdLayout)
	private RelativeLayout mAdLayout;
	@GinInjectView(id = R.id.mAdCloseBtn)
	private ImageButton mAdCloseBtn;
	// 帖子列表
	private List<Topic> topics = new ArrayList<Topic>();
	@GinInjectView(id = R.id.qz_listView)
	private MyListView qz_listView;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mInflater = LayoutInflater.from(getActivity());
		View v = getCacheView(mInflater, R.layout.attention_frame);
		// View v = inflater.inflate(R.layout.attention_frame, container,
		// false);
		// ViewUtils.inject(this, v);

		return v;
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
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws Exception {
		try {
			for (CatalogObj c : MConfig.CatalogList) {
				if (c.name.equals("活动")) {
					hdCatalogObj = c;
					hdmCatalogId = hdCatalogObj.id;
				}
				if (c.id.equals(MConfig.ZHENGWUID)) {
					bmmCatalogObj = c;
				}
				if (c.id.equals(MConfig.REDUID)) {
					zxmCatalogObj = c;
				}
				if (c.id.equals(MConfig.TUPIANID)) {
					tsmCatalogObj = c;
				}
			}
			if (TextUtils.isEmpty(hdmCatalogId)) {

			}
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
		}
		hdAdpater = new ActivitiesAdpater(hd_list, getActivity(), 1);
		hd_listView.setAdapter(hdAdpater);
		hd_list.clear();
		hdAdpater.notifyDataSetChanged();
		loadCatalog(bmmCatalogObj, 1);
		loadCatalog(zxmCatalogObj, 2);
		loadCatalog(tsmCatalogObj, 3);
		loadMore();
		getQuanZi();
		SharedPreferences adManager = getActivity().getSharedPreferences("ad_manager", 0);
		if (adManager.getBoolean("banner_enable", false)) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					new AdBanner(getActivity(), zxmCatalogObj.id, mAdLayout, mAdImage, mAdCloseBtn, false);
				}
			}, 5000);
		}
	}

	/**
	 * 加载活动信息
	 */
	private void loadMore() {
		News.getActivityList(getActivity(), new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0 && !TextUtils.isEmpty(result.optJSONObject(0).optString("ID"))) {
						hd_list.clear();
						for (int i = 0; i < 1; i++) {
							hd_list.add(result.optJSONObject(i));
						}
						hdAdpater.notifyDataSetChanged();
					}
				} catch (Exception e) {
				} finally {
				}
			}

			@Override
			public void onNG(String reason) {
			}

			@Override
			public void onCancel() {
			}
		});
	}

	@GinOnClick(id = { R.id.more_ac, R.id.zixun, R.id.more_price, R.id.lin_bianmin, R.id.lin_quanzi })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.more_ac:// 更多活动
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
			break;
		case R.id.zixun:// 更多资讯
			for (int i = 0; i < MConfig.CatalogList.size(); i++) {
				if (MConfig.CatalogList.get(i).id.equals(MConfig.REDUID)) {
					Intent intent0 = new Intent(getActivity(), HomeActivity.class);
					intent0.putExtra("index", i);
					startActivity(intent0);
				}
			}
			break;
		case R.id.more_price:
			for (int i = 0; i < MConfig.CatalogList.size(); i++) {
				if (MConfig.CatalogList.get(i).id.equals(MConfig.TUPIANID)) {
					Intent intent0 = new Intent(getActivity(), HomeActivity.class);
					intent0.putExtra("index", i);
					startActivity(intent0);
				}
			}
			break;
		case R.id.lin_bianmin:
			for (int i = 0; i < MConfig.CatalogList.size(); i++) {
				if (MConfig.CatalogList.get(i).id.equals(MConfig.ZHENGWUID)) {
					Intent intent0 = new Intent(getActivity(), HomeActivity.class);
					intent0.putExtra("index", i);
					startActivity(intent0);
				}
			}
			break;
		case R.id.lin_quanzi:
			Message msg = new Message();
			msg.what = 2;
			handler.sendMessage(msg);
			break;
		}
	}

	/**
	 * 
	 * @param catalogObj
	 * @param state
	 */
	private void loadCatalogCache(CatalogObj catalogObj, int state) {
		JsonCacheObj obj = JsonCache.getInstance().get(catalogObj.id);
		if (obj != null) {
			try {
				switch (state) {
				case 1:// 便民
					JSONArray result = (JSONArray) obj.getContent();
					for (int i = 0; i < result.length(); i++) {
						bmCatalogs.add(result.getJSONObject(i));
					}
					bmCatalogId = bmCatalogs.get(0).getString("id");
					Log.v("便民", bmCatalogs.get(0).getString("id") + "");
					if (bmCatalogs != null && bmCatalogs.size() > 0) {
						getMessageList(bmCatalogId, state);
					}
					break;

				case 2:// 资讯
					JSONArray result1 = (JSONArray) obj.getContent();
					for (int i = 0; i < result1.length(); i++) {
						zxmCatalogs.add(result1.getJSONObject(i));
					}
					zxmCatalogId = zxmCatalogs.get(0).getString("id");
					Log.v("便民", bmCatalogs.get(0).getString("id") + "");
					if (zxmCatalogs != null && zxmCatalogs.size() > 0) {
						getMessageList(zxmCatalogId, state);
					}
					break;
				case 3:// 组图
					JSONArray result2 = (JSONArray) obj.getContent();
					for (int i = 0; i < result2.length(); i++) {
						tsmCatalogs.add(result2.getJSONObject(i));
					}
					tsmCatalogId = tsmCatalogs.get(0).getString("id");
					Log.v("便民", bmCatalogs.get(0).getString("id") + "");
					if (tsmCatalogs != null && tsmCatalogs.size() > 0) {
						getMessageList(tsmCatalogId, state);
					}
					break;
				}

			} catch (JSONException e) {
			}
		} else {
			loadCatalog(catalogObj, state);
		}
	}

	/**
	 * 获得政务公告、资讯、图说的类型 通过类型获得类型下的列表信息
	 */
	private void loadCatalog(final CatalogObj catalogObj, final int state) {
		News.getCatalogList(catalogObj.id, 0, getActivity(), new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					switch (state) {
					case 1:
						for (int i = 0; i < result.length(); i++) {
							bmCatalogs.add(result.getJSONObject(i));
						}
						JsonCache.getInstance().set(bmmCatalogObj.id, "catalog", result);
						loadCatalogCache(catalogObj, state);
						break;

					case 2:
						for (int i = 0; i < result.length(); i++) {
							zxmCatalogs.add(result.getJSONObject(i));
						}
						JsonCache.getInstance().set(zxmCatalogObj.id, "catalog", result);
						loadCatalogCache(catalogObj, state);
						break;
					case 3:
						for (int i = 0; i < result.length(); i++) {
							tsmCatalogs.add(result.getJSONObject(i));
						}
						JsonCache.getInstance().set(tsmCatalogObj.id, "catalog", result);
						loadCatalogCache(catalogObj, state);
						break;
					}
				} catch (JSONException e) {

				}
			}

			@Override
			public void onNG(String reason) {

			}

			@Override
			public void onCancel() {

			}
		});
	}

	/**
	 * 获得便民信息
	 * 
	 * @param catalogId
	 *            便民id
	 */
	public void getMessageList(final String catalogId, final int state) {

		if (state != 1) {
			pagesize = 3;
		}
		News.getArticleList(0, catalogId, pagesize, 0, getActivity(), new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					switch (state) {
					case 1:
						bm_list.clear();
						JSONArray array = result.getJSONArray("articles");
						for (int i = 0; i < 1; i++) {
							bm_list.add(array.getJSONObject(i));
						}
						JsonCache.getInstance().set(catalogId, "list", result);
						bm_listView.setAdapter(new ConvenienceAdapter(bm_list, getActivity()));
						break;

					case 2:
						zx_list.clear();
						JSONArray array1 = result.getJSONArray("articles");
						for (int i = 0; i < pagesize; i++) {
							zx_list.add(array1.getJSONObject(i));
						}
						JsonCache.getInstance().set(catalogId, "list", result);
						zx_listView.setAdapter(new InformationAdapter(zx_list, getActivity()));
						break;
					case 3:
						ts_list.clear();
						JSONArray array2 = result.getJSONArray("articles");
						for (int i = 0; i < pagesize; i++) {
							ts_list.add(array2.getJSONObject(i));
						}
						JsonCache.getInstance().set(catalogId, "list", result);
						ts_gridView.setAdapter(new HomePriceAdapter(ts_list, getActivity()));
						break;
					}
				} catch (Exception e) {

				}

			}

			@Override
			public void onNG(String reason) {

			}

			@Override
			public void onCancel() {

			}
		});
	}

	/**
	 * 获得热门圈子信息
	 */
	public void getQuanZi() {
		JsonUtil.asynTask(getActivity(), new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				HashMap<String, String> map = (HashMap<String, String>) runData;
				if (map != null) {
					try {
						topics = com.alibaba.fastjson.JSONArray.parseArray(map.get("list"), Topic.class);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (topics != null && topics.size() > 0) {
						if (topics.size() > 3) {
							List<Topic> list = new ArrayList<Topic>();
							for (int i = 0; i < 3; i++) {
								list.add(topics.get(i));
							}
							topics = list;
						}
						qz_listView.setAdapter(new TopicAdapter(topics, getActivity()));
					} else {
						ToastUtil.showToast(getActivity(), "暂无帖子信息");
					}
				} else {
					ToastUtil.showToast(getActivity(), "网络连接错误");
				}
			}

			@Override
			public Serializable run() {
				Map<String, String> map = new HashMap<String, String>();
				try {
					SharedPreferences settings = getActivity().getSharedPreferences("user_info", 0);
					String userid = settings.getString("id", null);
					if (TextUtils.isEmpty(userid)) {
						map = HttpInvoke.sendGETRequest("http://qz.ieator.com/plugin.php",
								"?id=sobeyapp:api&action=gethotfollow&uid=2");
					} else {
						map = HttpInvoke.sendGETRequest("http://qz.ieator.com/plugin.php",
								"?id=sobeyapp:api&action=gethotfollow&uid=" + userid);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub
				return (Serializable) map;
			}
		});
	}
}
