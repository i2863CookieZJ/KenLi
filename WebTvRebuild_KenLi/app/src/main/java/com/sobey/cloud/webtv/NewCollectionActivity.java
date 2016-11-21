package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.fragment.utils.MyListView;
import com.sobey.cloud.webtv.obj.Collection;
import com.sobey.cloud.webtv.ui.RoundAngleImageView;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewCollectionActivity extends BaseActivity {
	private static AsyncHttpClient client = new AsyncHttpClient();
	@GinInjectView(id = R.id.mLoadingIconLayout)
	private RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.mListView)
	private MyListView mListView;
	@GinInjectView(id = R.id.quznzimListView)
	private MyListView quznzimListView;
	@GinInjectView(id = R.id.titlebar_name)
	private TextView titlebar_name;
	@GinInjectView(id = R.id.bianji)
	private TextView bianji;
	private String mUserName;
	private int count = 0;
	private List<Collection> allCollections = new ArrayList<Collection>();
	private List<Collection> deleteCollections = new ArrayList<Collection>();
	private List<Collection> quanziCollections = new ArrayList<Collection>();
	private List<Collection> deleteQuanZiCollections = new ArrayList<Collection>();
	private int quanziCount = 0;
	private int width;
	@GinInjectView(id = R.id.top_back)
	private ImageButton titlebar_module;
	@GinInjectView(id = R.id.quxiao)
	private TextView quxiao;
	@GinInjectView(id = R.id.delete_select)
	private TextView delete_select;
	private int deleteCount = 0;
	private int deleteQuanZiCount = 0;
	private String uid = "";

	private boolean isHaveCollect;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.new_activity_collection;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		client.setTimeout(20000);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width = displayMetrics.widthPixels;
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(NewCollectionActivity.this, LoginActivity.class));
			finishActivity();
		}
		// uid="1";
		uid = PreferencesUtil.getLoggedUserId();
		mUserName = userInfo.getString("id", "");
		getShoucang("1");
		if (TextUtils.isEmpty(uid)) {

		} else {
			getcollectedSubjectList();
		}
	}

	@GinOnClick(id = { R.id.bianji, R.id.top_back, R.id.quxiao, R.id.delete_select })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.bianji:
			titlebar_module.setVisibility(View.INVISIBLE);
			quxiao.setVisibility(View.VISIBLE);
			bianji.setVisibility(View.INVISIBLE);
			titlebar_name.setText("编辑");
			delete_select.setVisibility(View.VISIBLE);
			mListView.setAdapter(new DeleteCollectionAdapter());
			quznzimListView.setAdapter(new QuanZiDeleteCollectionAdapter());
			break;

		case R.id.top_back:
			finishActivity();
			break;
		case R.id.quxiao:
			titlebar_module.setVisibility(View.VISIBLE);
			quxiao.setVisibility(View.INVISIBLE);
			bianji.setVisibility(View.VISIBLE);
			titlebar_name.setText("收藏");
			delete_select.setVisibility(View.GONE);
			for (int i = 0; i < allCollections.size(); i++) {
				allCollections.get(i).setDelete(0);
			}
			for (int i = 0; i < quanziCollections.size(); i++) {
				quanziCollections.get(i).setDelete(0);
			}
			mListView.setAdapter(new CollectionAdapter());
			quznzimListView.setAdapter(new QuanZiCollectionAdapter());
			break;
		case R.id.delete_select:
			deleteCollections.clear();
			deleteQuanZiCollections.clear();
			for (int i = 0; i < allCollections.size(); i++) {
				if (allCollections.get(i).getDelete() == 1) {
					deleteCollections.add(allCollections.get(i));
				}
			}
			for (int i = 0; i < quanziCollections.size(); i++) {
				if (quanziCollections.get(i).getDelete() == 1) {
					deleteQuanZiCollections.add(quanziCollections.get(i));
				}
			}
			if (deleteCollections.size() > 0) {
				deleteMoreCollection(deleteCount);
			}
			if (deleteQuanZiCollections.size() > 0) {
				quanZiDeleteMoreCollection(deleteQuanZiCount);
			}

			break;
		}
	}

	/**
	 * 获得收藏的信息
	 */
	public void getShoucang(String type) {

		RequestParams params = new RequestParams();
		params.put("username", mUserName);
		params.put("method", "getCollect");
		params.put("type", type);
		params.put("siteId", MConfig.SITE_ID);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					allCollections.addAll(JSONArray.parseArray(new String(arg2), Collection.class));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				count++;
				if (count == 2) {
					for (int i = 0; i < allCollections.size(); i++) {
						allCollections.get(i).setDelete(0);
					}
					mListView.setAdapter(new CollectionAdapter());
					mLoadingIconLayout.setVisibility(View.GONE);
				} else {
					getShoucang("2");
				}
			}
		});
	}

	class CollectionAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		CollectionAdapter() {
			inflater = LayoutInflater.from(NewCollectionActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allCollections.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allCollections.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_collection_normal, null);
				holder = new Holder();
				GinInjector.manualInjectView(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final Collection collection = allCollections.get(position);
			holder.title.setText(collection.getTitle());
			holder.summary.setText(collection.getSummary());
			holder.addtime.setText(collection.getAddtime());
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 5;
			linearParams.height = width / 5;
			holder.logo.getLayoutParams();
			LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder.linn1.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams1.width = width;
			holder.linn1.getLayoutParams();
			SharedPreferences settings = NewCollectionActivity.this.getSharedPreferences("settings", 0);
			CheckNetwork network = new CheckNetwork(NewCollectionActivity.this);
			boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
					|| network.getWifiState(false) == State.CONNECTED;
			if (isShowPicture)
				ImageLoader.getInstance().displayImage(collection.getLogo(), holder.logo);
			holder.action_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					deleteCollection(position);
				}
			});
			holder.linn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JSONObject obj = new JSONObject();
					obj.put("id", collection.getAid());
					// TODO Auto-generated method stub
					openDetailActivity(Integer.parseInt(collection.getType()), obj.toString());
				}
			});
			return convertView;
		}

		class Holder {
			@GinInjectView(id = R.id.campaign_logo_imageview)
			private RoundAngleImageView logo;
			@GinInjectView(id = R.id.title)
			private TextView title;
			@GinInjectView(id = R.id.addtime)
			private TextView addtime;
			@GinInjectView(id = R.id.summary)
			private TextView summary;
			@GinInjectView(id = R.id.action_btn)
			private Button action_btn;
			@GinInjectView(id = R.id.linn1)
			private LinearLayout linn1;
		}
	}

	class DeleteCollectionAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		DeleteCollectionAdapter() {
			inflater = LayoutInflater.from(NewCollectionActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allCollections.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allCollections.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_collection_normal, null);
				// holder=new Holder();
				// ViewUtils.inject(holder, convertView);
				// convertView.setTag(holder);
			} /*
				 * else { holder=(Holder) convertView.getTag(); }
				 */
			RoundAngleImageView logo = (RoundAngleImageView) convertView.findViewById(R.id.campaign_logo_imageview);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView addtime = (TextView) convertView.findViewById(R.id.addtime);
			TextView summary = (TextView) convertView.findViewById(R.id.summary);
			Button action_btn = (Button) convertView.findViewById(R.id.action_btn);
			LinearLayout linn1 = (LinearLayout) convertView.findViewById(R.id.linn1);
			final ImageView select = (ImageView) convertView.findViewById(R.id.select);
			final Collection collection = allCollections.get(position);
			select.setVisibility(View.VISIBLE);
			action_btn.setVisibility(View.GONE);
			if (collection.getDelete() == 0) {
				select.setImageResource(R.drawable.bj_selscted);
			} else {
				select.setImageResource(R.drawable.bj_selscted1);
			}
			title.setText(collection.getTitle());
			summary.setText(collection.getSummary());
			addtime.setText(collection.getAddtime());
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 5;
			linearParams.height = width / 5;
			logo.getLayoutParams();
			LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) linn1.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams1.width = width;
			linn1.getLayoutParams();
			ImageLoader.getInstance().displayImage(collection.getLogo(), logo);
			linn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (allCollections.get(position).getDelete() == 0) {
						allCollections.get(position).setDelete(1);
						mListView.setAdapter(new DeleteCollectionAdapter());

					} else {
						allCollections.get(position).setDelete(0);
						mListView.setAdapter(new DeleteCollectionAdapter());
					}
				}
			});
			return convertView;
		}
	}

	private void deleteCollection(int position) {
		String articleId = "";
		try {
			articleId = allCollections.get(position).getAid();
		} catch (Exception e) {
			Toast.makeText(NewCollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(articleId)) {
			Toast.makeText(NewCollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("method", "deleteCollect");
		params.put("siteId", MConfig.SITE_ID);
		params.put("articleId", articleId);
		params.put("username", mUserName);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				List<Collection> list = JSONArray.parseArray(new String(arg2), Collection.class);
				if (list != null && list.size() > 0) {
					if (list.get(0).getReturncode().equalsIgnoreCase("SUCCESS")) {

					} else {
						Toast.makeText(NewCollectionActivity.this, list.get(0).getReturnmsg(), Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				allCollections.clear();
				count = 0;
				getShoucang("1");
			}
		});
	}

	private void deleteMoreCollection(int position) {
		String articleId = "";
		try {
			articleId = deleteCollections.get(position).getAid();
		} catch (Exception e) {
			Toast.makeText(NewCollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(articleId)) {
			Toast.makeText(NewCollectionActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("method", "deleteCollect");
		params.put("siteId", MConfig.SITE_ID);
		params.put("articleId", articleId);
		params.put("username", mUserName);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				List<Collection> list = JSONArray.parseArray(new String(arg2), Collection.class);
				if (list != null && list.size() > 0) {
					if (list.get(0).getReturncode().equalsIgnoreCase("SUCCESS")) {

					} else {
						Toast.makeText(NewCollectionActivity.this, list.get(0).getReturnmsg(), Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				deleteCount++;
				if (deleteCount == deleteCollections.size()) {
					Toast.makeText(NewCollectionActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
					titlebar_module.setVisibility(View.VISIBLE);
					quxiao.setVisibility(View.INVISIBLE);
					bianji.setVisibility(View.VISIBLE);
					titlebar_name.setText("收藏");
					delete_select.setVisibility(View.GONE);
					count = 0;
					List<Collection> list = new ArrayList<Collection>();
					for (int i = 0; i < allCollections.size(); i++) {
						if (allCollections.get(i).getDelete() == 0) {
							list.add(allCollections.get(i));
						}
					}
					allCollections = list;
					mListView.setAdapter(new CollectionAdapter());

				} else {
					deleteMoreCollection(deleteCount);
				}
			}
		});
	}

	protected void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(this);
			Intent intent1 = new Intent(this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			this.startActivity(intent1);
			break;
		case MConfig.TypeBroke:
			Intent intent2 = new Intent(this, com.sobey.cloud.webtv.broke.BrokeNewsDetailActivity.class);
			intent2.putExtra("information", information);
			this.startActivity(intent2);
			break;
		case MConfig.TypeNews:
			Intent intent3 = new Intent(this, GeneralNewsDetailActivity.class);
			intent3.putExtra("information", information);
			this.startActivity(intent3);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(this);
			Intent intent4 = new Intent(this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			this.startActivity(intent4);
			break;
		}
	}

	/**
	 * 获得圈子的收藏信息
	 */
	public void getcollectedSubjectList() {
		News.getcollectedSubjectList(uid, this, new OnJsonObjectResultListener() {

			@Override
			public void onOK(org.json.JSONObject result) {
				// TODO Auto-generated method stub
				// if (result == null) {
				String jieguo = result.optString("subjectList");
				if (jieguo.equals("null")) {
					// Toast.makeText(NewCollectionActivity.this, "暂无内容",
					// Toast.LENGTH_SHORT).show();
				} else {
					quanziCollections = JSONArray.parseArray(jieguo, Collection.class);
					for (int i = 0; i < quanziCollections.size(); i++) {
						quanziCollections.get(i).setDelete(0);
					}
					quznzimListView.setAdapter(new QuanZiCollectionAdapter());
				}
				// } else {
				// Toast.makeText(NewCollectionActivity.this,
				// "网络连接错误 ", Toast.LENGTH_SHORT).show();
				// }
			}

			@Override
			public void onNG(String reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}

		});
	}

	class QuanZiCollectionAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		QuanZiCollectionAdapter() {
			inflater = LayoutInflater.from(NewCollectionActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return quanziCollections.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return quanziCollections.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_collection_normal, null);
				holder = new Holder();
				GinInjector.manualInjectView(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final Collection collection = quanziCollections.get(position);
			holder.title.setText(collection.getSubjectTitle());
			String comment = collection.getSubjectContent();
			if (!TextUtils.isEmpty(comment) && comment.contains("\n")) {
				comment = comment.replace("\n", "<br/>");
			}
			holder.summary.setText(Html.fromHtml(comment, new MyImgGetter(), null));
			holder.addtime.setText(collection.getPublishTime());
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 5;
			linearParams.height = width / 5;
			holder.logo.getLayoutParams();
			LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder.linn1.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams1.width = width;
			holder.linn1.getLayoutParams();
			StringBuilder sb = new StringBuilder();
			// sb.append(MConfig.mQuanZiBaseUrl).append(collection.getPublishUserHeadUrl());
			sb.append(MConfig.QZ_DOMAIN).append(collection.getPublishUserHeadUrl());
			ImageLoader.getInstance().displayImage(collection.getPublishUserHeadUrl(), holder.logo);
			holder.action_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					quanZiDeleteCollection(collection.getSubjectId());
				}
			});
			holder.linn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// JSONObject obj = new JSONObject();
					// obj.put("id", collection.getAid());
					// openDetailActivity(Integer.parseInt(collection.getType()),
					// obj.toString());

					// 根据com.sobey.cloud.webtv.adapter.GroupDetailAdapter
					// 280行的跳转方式修改
					Intent intent = new Intent(NewCollectionActivity.this, GroupSubjectActivity.class);
					Bundle bundle = new Bundle();
					GroupSubjectModel subjectModel = new GroupSubjectModel();
					subjectModel.groupId = collection.getGroupId();
					subjectModel.groupInfo = collection.getGroupInfo();
					subjectModel.groupName = collection.getGroupName();
					subjectModel.publishTime = collection.getPublishTime();
					subjectModel.publishUserHeadUrl = collection.getPublishUserHeadUrl();
					subjectModel.publishUserId = collection.getPublishUserId();
					subjectModel.publishUserName = collection.getPublishUserName();
					subjectModel.subjectContent = collection.getSubjectContent();
					subjectModel.subjectId = collection.getSubjectId();
					subjectModel.subjectLikeCount = collection.getSubjectLikeCount();
					subjectModel.subjectReplyCount = collection.getSubjectReplyCount();
					subjectModel.subjectTitle = collection.getSubjectTitle();
					String subjectPicUrls = collection.getSubjectPicUrls();
					if (!TextUtils.isEmpty(subjectPicUrls)) {
						subjectModel.subjectPicUrls = subjectPicUrls.split(",");
						for (int i = 0; i < subjectModel.subjectPicUrls.length; i++) {
							subjectModel.subjectPicUrls[i] = MConfig.QZ_DOMAIN + subjectModel.subjectPicUrls[i];
						}
					}
					bundle.putString("title", collection.getGroupName());
					bundle.putParcelable("mSubjectModel", subjectModel);
					intent.putExtras(bundle);
					NewCollectionActivity.this.startActivity(intent);
				}
			});
			return convertView;
		}

		class Holder {
			@GinInjectView(id = R.id.campaign_logo_imageview)
			private RoundAngleImageView logo;
			@GinInjectView(id = R.id.title)
			private TextView title;
			@GinInjectView(id = R.id.addtime)
			private TextView addtime;
			@GinInjectView(id = R.id.summary)
			private TextView summary;
			@GinInjectView(id = R.id.action_btn)
			private Button action_btn;
			@GinInjectView(id = R.id.linn1)
			private LinearLayout linn1;
		}
	}

	private class MyImgGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
			if (source.contains("default")) {
				drawable = getResources().getDrawable(FaceUtil.defaultFaces.get(face));
			} else if (source.contains("coolmonkey")) {
				drawable = getResources().getDrawable(FaceUtil.coolmonkeyFaces.get(face));
			} else if (source.contains("grapeman")) {
				drawable = getResources().getDrawable(FaceUtil.grapemanFaces.get(face));
			}
			if (null != drawable) {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
			return drawable;
		}

	}

	class QuanZiDeleteCollectionAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		QuanZiDeleteCollectionAdapter() {
			inflater = LayoutInflater.from(NewCollectionActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return quanziCollections.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return quanziCollections.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_collection_normal, null);
				// holder=new Holder();
				// ViewUtils.inject(holder, convertView);
				// convertView.setTag(holder);
			} /*
				 * else { holder=(Holder) convertView.getTag(); }
				 */
			RoundAngleImageView logo = (RoundAngleImageView) convertView.findViewById(R.id.campaign_logo_imageview);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView addtime = (TextView) convertView.findViewById(R.id.addtime);
			TextView summary = (TextView) convertView.findViewById(R.id.summary);
			Button action_btn = (Button) convertView.findViewById(R.id.action_btn);
			LinearLayout linn1 = (LinearLayout) convertView.findViewById(R.id.linn1);
			final ImageView select = (ImageView) convertView.findViewById(R.id.select);
			final Collection collection = quanziCollections.get(position);
			select.setVisibility(View.VISIBLE);
			action_btn.setVisibility(View.GONE);
			if (collection.getDelete() == 0) {
				select.setImageResource(R.drawable.bj_selscted);
			} else {
				select.setImageResource(R.drawable.bj_selscted1);
			}
			title.setText(collection.getSubjectTitle());
			summary.setText(Html.fromHtml(collection.getSubjectContent(), new MyImgGetter(), null));
			addtime.setText(collection.getPublishTime());
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) logo.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width / 5;
			linearParams.height = width / 5;
			logo.getLayoutParams();
			LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) linn1.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams1.width = width;
			linn1.getLayoutParams();
			ImageLoader.getInstance().displayImage(MConfig.QZ_DOMAIN + collection.getPublishUserHeadUrl(), logo);
			linn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (quanziCollections.get(position).getDelete() == 0) {
						quanziCollections.get(position).setDelete(1);
						quznzimListView.setAdapter(new QuanZiDeleteCollectionAdapter());

					} else {
						quanziCollections.get(position).setDelete(0);
						quznzimListView.setAdapter(new QuanZiDeleteCollectionAdapter());
					}
				}
			});
			return convertView;
		}
	}

	private void quanZiDeleteCollection(String id) {
		RequestParams params = new RequestParams();
		params.put("action", "collectSubject");
		params.put("isCollect", "0");
		params.put("uid", uid);
		params.put("id", "sobeyapp:api");
		params.put("subjectId", id);
		client.get(MConfig.QZ_DOMAIN + "/plugin.php", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Collection collection = JSONObject.parseObject(new String(arg2), Collection.class);
				if (collection != null) {
					if (collection.getReturnCode().equalsIgnoreCase("200")) {

					} else {

					}
				} else {
					Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				quanziCollections.clear();
				getcollectedSubjectList();
				// deleteQuanZiCount++;
				// if (deleteQuanZiCount == deleteQuanZiCollections.size()) {
				// Toast.makeText(NewCollectionActivity.this, "删除成功",
				// Toast.LENGTH_SHORT).show();
				// titlebar_module.setVisibility(View.VISIBLE);
				// quxiao.setVisibility(View.INVISIBLE);
				// bianji.setVisibility(View.VISIBLE);
				// titlebar_name.setText("收藏");
				// delete_select.setVisibility(View.GONE);
				// quanziCount = 0;
				// quanziCollections.clear();
				// getcollectedSubjectList();
				//
				// } else {
				// deleteMoreCollection(deleteCount);
				// }
			}
		});
	}

	private void quanZiDeleteMoreCollection(int postion) {
		String id = deleteQuanZiCollections.get(postion).getSubjectId();
		RequestParams params = new RequestParams();
		params.put("action", "collectSubject");
		params.put("isCollect", "0");
		params.put("uid", uid);
		params.put("id", "sobeyapp:api");
		params.put("subjectId", id);
		client.get(MConfig.QZ_DOMAIN + "/plugin.php", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Collection collection = JSONObject.parseObject(new String(arg2), Collection.class);
				if (collection != null) {
					if (collection.getReturnCode().equalsIgnoreCase("200")) {

					} else {

					}
				} else {
					Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(NewCollectionActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				deleteQuanZiCount++;
				if (deleteQuanZiCount == deleteQuanZiCollections.size()) {
					titlebar_module.setVisibility(View.VISIBLE);
					quxiao.setVisibility(View.INVISIBLE);
					bianji.setVisibility(View.VISIBLE);
					titlebar_name.setText("收藏");
					delete_select.setVisibility(View.GONE);
					// quanziCollections.clear();
					// getcollectedSubjectList();
					List<Collection> list = new ArrayList<Collection>();
					for (int i = 0; i < quanziCollections.size(); i++) {
						if (quanziCollections.get(i).getDelete() == 0) {
							list.add(quanziCollections.get(i));
						}
					}
					quanziCollections = list;
					quznzimListView.setAdapter(new QuanZiCollectionAdapter());
				} else {
					quanZiDeleteMoreCollection(deleteQuanZiCount);
				}
			}
		});
	}

}
