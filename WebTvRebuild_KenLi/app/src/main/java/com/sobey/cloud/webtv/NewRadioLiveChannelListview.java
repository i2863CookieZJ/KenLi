package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.NewRadioProgramlistAdaptor;
import com.sobey.cloud.webtv.broadcast.DestoryRadioInstanceReciver;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft.HuiZhouSarftBroadcast;
import com.sobey.cloud.webtv.senum.ProgramListItem;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.Utility;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * 新的音频直播列表而
 * 
 * @author zouxudong
 *
 */
public class NewRadioLiveChannelListview extends BaseActivity implements DestoryRadioInstanceReciver.ReciveHandle {
	/**
	 * 改变首页广电 那的标签
	 */
	protected static final int CHANGE_HOME_LABEL = 0;
	/**
	 * 选择一个列表项进行回看
	 */
	public static final int CHOOSE_ITEM_REPLAY = 1;
	/**
	 * 选择的直播
	 */
	public static final int CHOOSE_ITEM_LIVE = 2;
	/**
	 * 回看的时候改变标签
	 */
	private static final int CHANGE_REPLAY_LABEL = 3;
	/**
	 * 回看的时候改变直播的
	 */
	private static final int CHANGE_LIVE_LABEL = 4;

	@GinInjectView(id = R.id.headerDateGroup)
	private LinearLayout headerDateGroup;
	@GinInjectView(id = R.id.titlebar_name)
	protected TextView titlebar_name;
	@GinInjectView(id = R.id.top_back)
	protected ImageButton top_back;
	@GinInjectView(id = R.id.radio_status_label)
	protected TextView radio_status_label;
	@GinInjectView(id = R.id.radio_play_status_button)
	protected ImageView radio_play_status_button;
	@GinInjectView(id = R.id.programListLoadTips)
	protected RelativeLayout programListLoadTips;
	@GinInjectView(id = R.id.audio_programlist)
	protected ListView audio_programlist;
	@GinInjectView(id = R.id.noProgramListTipsImage)
	protected ImageView noProgramListTipsImage;
	@GinInjectView(id = R.id.loading_chinese)
	protected TextView loading_chinese;

	/**
	 * 是不是在播放
	 */
	protected static boolean isPlay = true;

	protected static boolean hadAddRes2Play;

	protected boolean isDisposed;
	/**
	 * 播放器
	 */
	public static AdvancedVideoView radioViewPlayer;
	private String[] days = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
	private TextView[] textViews = new TextView[days.length];

	private String mArticalId;
	private String mCatalogId;
	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private MediaObj replayObj;
	private boolean timeShift;
	private Map<String, List<ProgramListItem>> programListBuffer = new HashMap<String, List<ProgramListItem>>();
	protected List<ProgramListItem> currentProgramList;
	private static boolean initialzed;
	/**
	 * 今天的日期
	 */
	private final int today = DateParse.getWeekToday() > 0 ? (DateParse.getWeekToday() - 1) : 0;

	/**
	 * 选择的日期索引
	 */
	protected int chooseDateIndex = 0;
	/**
	 * 滑动没得
	 */
	protected boolean touched;

	/**
	 * 消息处理
	 */
	private Handler handler;
	/**
	 * 刷新节目单 的线程
	 */
	private Thread radiolistRefreshThread;

	private static AsyncHttpClient client = new AsyncHttpClient();

	/**
	 * 选择的哪个索引播放的
	 */
	private int index;
	/**
	 * 上次播放的电台索引
	 */
	private static int lastIndex;

	/***
	 * 节目单中哪一个节目在播放 用来更新最下面那个标签用的
	 */
	protected static int itemPlayIndex;
	/**
	 * 请求播放地址文件成功没有
	 */
	protected boolean invokePlayPathSuccess;
	/**
	 * 是不是回看模式
	 */
	private static boolean isSeek;

	protected static ProgramListItem seekItem;

	private static DestoryRadioInstanceReciver broadcastReciver;

	{
		client.setTimeout(5000);
	}

	@Override
	public int getContentView() {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		return R.layout.activity_newaudio_playlist;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		initView();
		initHandle();
	}

	// TODO
	private void stopPlay() {
		// sendBroadcast2HuiZhouSarftPage();
		isDisposed = true;
		programListBuffer.clear();
		programListBuffer = null;
		if (currentProgramList != null) {
			currentProgramList.clear();
		}
		radiolistRefreshThread = null;
		lastIndex = index;
		invokePlayPathSuccess = false;
		boolean isPlay = false;
		try {
			isPlay = radioViewPlayer.isPlaying();
		} catch (Exception e) {
		}
		if (isPlay) {
			togglePlayRadio();
		} else {
			isPlay = false;
			radiolistRefreshThread = null;
			lastIndex = -1;
			isSeek = false;
			itemPlayIndex = -1;
			NewRadioProgramlistAdaptor.currentDateIndex = -1;
			NewRadioProgramlistAdaptor.lastChooseIndex = -1;
			NewRadioProgramlistAdaptor.seekFlag = false;
		}
		initialzed = false;
//		radioViewPlayer.onDestory();
		// super.onDestroy();
	}

	protected void initView() {
		noProgramListTipsImage.setVisibility(View.GONE);
		isPlay = true;
		String string = getIntent().getStringExtra("information");
		index = getIntent().getIntExtra("index", -1);
		try {
			JSONObject jsonObject = new JSONObject(string);
			mCatalogId = jsonObject.getString("parentid");
			mArticalId = jsonObject.getString("id");
			if (index != lastIndex) {
				if (radioViewPlayer != null) {
					radioViewPlayer.pause();
				}
				hadAddRes2Play = false;
				NewRadioProgramlistAdaptor.lastChooseIndex = -1;
				seekItem = null;
			}
			if (!initialzed) {
				if (broadcastReciver != null) {
					try {
						unregisterReceiver(broadcastReciver);
					} catch (Exception e) {
					}

				}
				radioViewPlayer = new AdvancedVideoView(this);
				ViewGroup.LayoutParams layoutParams = new LayoutParams(0, 0);
				radioViewPlayer.setLayoutParams(layoutParams);
				radioViewPlayer.init();
				initialzed = true;
				broadcastReciver = new DestoryRadioInstanceReciver();
				DestoryRadioInstanceReciver.handle = this;
				try {
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(DestoryRadioInstanceReciver.DestoryRadioInstance);
					registerReceiver(broadcastReciver, intentFilter);
				} catch (Exception e) {
				}

			} else {
				if (!isPlay || !hadAddRes2Play) {
					NewRadioProgramlistAdaptor.lastChooseIndex = -1;
					seekItem = null;
					isSeek = false;
					NewRadioProgramlistAdaptor.seekFlag = false;
				} else {
					radio_status_label.setText("正在播放");
				}
			}

			titlebar_name.setText(jsonObject.getString("title"));
			if (seekItem != null) {
				radio_status_label.setText("正在播放:" + seekItem.getName());
			}
			// getRadioPlaySource();
			getRadioJOSNPath(mArticalId, mCatalogId, DateParse.getDate(0, 0, 0, 0, null, null, "yyyyMMdd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		days[today] = "今天";
		for (int i = 0; i < days.length; i++) {

			TextView textView = new TextView(this);
			textView.setTag(i);
			LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.BOTTOM;
			layoutParams.weight = 1;
			layoutParams.width = 0;
			textView.setCompoundDrawablePadding(Utility.dpToPx(this, 5F));
			textView.setLayoutParams(layoutParams);
			textView.setGravity(Gravity.CENTER);
			int size = (int) textView.getTextSize() + 1;
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 2);
			textView.setText(days[i]);
			textView.setTextColor(getResources().getColor(R.color.black));
			Drawable bottom = getResources().getDrawable(R.drawable.selector_newradiolive_daybutton_unchecked);
			bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());
			textView.setCompoundDrawables(null, null, null, bottom);
			headerDateGroup.addView(textView);
			textViews[i] = textView;

			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					int index = (Integer) view.getTag();
					changeHeaderDate(index);
				}
			});
		}
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stopPlay();
				finishActivity();
			}
		});

		radio_play_status_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				togglePlayRadio();
			}
		});

		audio_programlist.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				touched = true;
				return false;
			}
		});

	}

	protected void initHandle() {
		handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				ProgramListItem.Msg data = (ProgramListItem.Msg) msg.obj;
				if (msg.what == CHANGE_HOME_LABEL) {
					if (!touched) {
						if (!isSeek)
							audio_programlist.setSelection(data.index);
						else
							audio_programlist.setSelection(itemPlayIndex);
					}
					if (isPlay) {
						if (!isSeek)
							radio_status_label.setText("正在播放:" + data.item.getName());
						else {
							ProgramListItem item = currentProgramList.get(itemPlayIndex);
							radio_status_label.setText("正在播放:" + item.getName());
						}
					}
				} else if (msg.what == CHOOSE_ITEM_REPLAY) {
					isSeek = true;
					itemPlayIndex = data.index;
					seek2RePlay(data.item);
					getOnlineVideoPath();
				} else if (msg.what == CHOOSE_ITEM_LIVE) {
					isSeek = false;
					itemPlayIndex = data.index;
					getOnlineVideoPath();
				} else if (msg.what == CHANGE_LIVE_LABEL) {
					if (!touched) {
						audio_programlist.setSelection(data.index);
					}
				} else if (msg.what == CHANGE_REPLAY_LABEL) {
					if (!touched) {
						audio_programlist.setSelection(data.index);
					}
					radio_status_label.setText("正在播放:" + data.item.getName());
				}
				return false;
			}
		});
	}

	// TODO
	protected void togglePlayRadio() {
		if (!isPlay) {
			this.radio_play_status_button
					.setImageDrawable(getResources().getDrawable(R.drawable.bottom_radio_pause_btn));
			setProgramListPosition();
			if (!hadAddRes2Play)
				getOnlineVideoPath();
			else
				radioViewPlayer.play();
			isPlay = true;
			if (currentProgramList == null || currentProgramList.size() == 0)
				this.radio_status_label.setText("正在播放");
		} else {
			lastIndex = -1;
			this.radio_play_status_button
					.setImageDrawable(getResources().getDrawable(R.drawable.bottom_radio_play_btn));
			this.radio_status_label.setText("播放完成");
			if (radioViewPlayer.isPlaying()) {
				radioViewPlayer.pause();
			}
			isPlay = false;
			// sendBroadcast2HuiZhouSarftPage();
		}
	}

	/***
	 * 选择不同日期
	 * 
	 * @param index
	 */
	protected void changeHeaderDate(int index) {
		touched = false;
		chooseDateIndex = index;
		for (int i = 0; i < textViews.length; i++) {
			TextView textView = textViews[i];
			if (index == i) {
				Drawable bottom = getResources().getDrawable(R.drawable.selector_newradiolive_daybutton_checked);
				bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());
				textView.setCompoundDrawables(null, null, null, bottom);

				loadRadioProgramList(mArticalId, mCatalogId, index - today, new ProgramListLoaderListener() {

					@Override
					public void onPreLoadPrgramList() {
						audio_programlist.setVisibility(View.GONE);
						programListLoadTips.setVisibility(View.VISIBLE);
						noProgramListTipsImage.setVisibility(View.GONE);
						loading_chinese.setText("正在加载节目单...");
					}

					@Override
					public void loadProgramListError(String reason) {
						audio_programlist.setVisibility(View.GONE);
						programListLoadTips.setVisibility(View.GONE);
						noProgramListTipsImage.setVisibility(View.VISIBLE);
						String currentLoadDay = DateParse.getDate(0, 0, 0, 0, null, null, "yyyyMMdd");
						if (currentLoadDay.equals(reason)) {
							// if(!isPlay)
							if (!hadAddRes2Play)
								radio_status_label.setText("播放完成");
							else
								radio_status_label.setText("正在播放");
						}
						// programListLoadTips.setText("暂无节目单数据");
					}

					@Override
					public void loadProgramListComplete(String currentDay) {
						if (!programListBuffer.containsKey(currentDay)) {
							loadProgramListError(currentDay);
							return;
						}
						currentProgramList = programListBuffer.get(currentDay);
						audio_programlist.setVisibility(View.VISIBLE);
						programListLoadTips.setVisibility(View.GONE);
						noProgramListTipsImage.setVisibility(View.GONE);
						NewRadioProgramlistAdaptor adaptor = new NewRadioProgramlistAdaptor(currentProgramList,
								LayoutInflater.from(getApplicationContext()), chooseDateIndex, timeShift, today);
						adaptor.handler = handler;
						if (timeShift) {
							audio_programlist.setOnItemClickListener(adaptor);
						}
						audio_programlist.setAdapter(adaptor);
						// adaptor.notifyDataSetChanged();
						setProgramListPosition();
					}
				});
			} else {
				Drawable bottom = getResources().getDrawable(R.drawable.selector_newradiolive_daybutton_unchecked);
				bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());
				textView.setCompoundDrawables(null, null, null, bottom);
			}
		}
	}

	/**
	 * 取播放路径
	 * 
	 * @param articleId
	 * @param catalogId
	 * @param actDate
	 */
	protected void getRadioJOSNPath(String articleId, String catalogId, String actDate) {
		RequestParams params = new RequestParams();
		params.put("method", "getArticleById");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("articleId", articleId);
		params.put("catalogId", catalogId);
		params.put("username", "");
		params.put("TerminalType", MConfig.TerminalType);
		params.put("actDate", actDate);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String data = new String(arg2);
				if (isDisposed)
					return;
				try {
					JSONArray jsonArray = new JSONArray(data);
					JSONArray guideArray = jsonArray.getJSONObject(0).getJSONArray("staticfilepaths");
					invokeRadioJSONData(guideArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				invokePlayPathSuccess = false;
			}
		});
	}

	/**
	 * 请求JSON数据
	 * 
	 * @param jsonArray
	 * @throws JSONException
	 */
	private void invokeRadioJSONData(final JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			String jsonPath = jsonArray.getJSONObject(i).getString("playerpath");
			if (radioViewPlayer == null) {
				return;
			}
			client.get(jsonPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if (isDisposed)
						return;
					try {
						invokePlayPathSuccess = true;
						resolutionObjs.clear();
						String data = new String(arg2);
						JSONArray result = new JSONArray(data);
						String channelName = null;
						for (int i = 0; i < result.length(); i++) {
							// m3u8的
							// JSONArray jsonArray =
							// result.getJSONObject(i).getJSONArray("C_Address");
							// flv的
							JSONArray jsonArray = result.getJSONObject(i).getJSONArray("C_ScreenShot");
							JSONObject item = result.getJSONObject(i);
							if (item.has("TimeShift")) // 判断有无支持时移标签
							{
								if ("0".equals(item.getString("TimeShift"))) {
									timeShift = false;
								} else {
									timeShift = true;
								}
							} else {
								timeShift = false;
							}
							// timeShift=true;
							for (int j = 0; j < jsonArray.length(); j++) {
								resolutionObjs.add(
										new ResolutionObj(((JSONObject) jsonArray.optJSONObject(j)).optString("title"),
												((JSONObject) jsonArray.optJSONObject(j)).optString("url")));
								// resolutionObjs.add(new
								// ResolutionObj(((JSONObject)
								// jsonArray.optJSONObject(j)).optString("title"),
								// "http://liveh2.sobeycache.com/live/5207befb73644f90b0b90a1591d96baa.m3u8?fmt=h264_450k_ts&m3u8"));
							}
							channelName = result.optJSONObject(i).optString("C_Name");
							// mediaObj = new MediaObj(channelName, new
							// ResolutionList(resolutionObjs, 0), false);
						}
						if (resolutionObjs.size() > 0) {
							mediaObj = new MediaObj(channelName, new ResolutionList(resolutionObjs, 0), false);
							if (isPlay) {
								radio_play_status_button.setImageDrawable(
										getResources().getDrawable(R.drawable.bottom_radio_pause_btn));
								if (lastIndex != index || !hadAddRes2Play)
									getOnlineVideoPath();
							} else {
								isPlay = false;
								radio_play_status_button
										.setImageDrawable(getResources().getDrawable(R.drawable.bottom_radio_play_btn));
								sendBroadcast2HuiZhouSarftPage();
							}
						} else {
							isPlay = false;
							sendBroadcast2HuiZhouSarftPage();
							radio_play_status_button
									.setImageDrawable(getResources().getDrawable(R.drawable.bottom_radio_play_btn));
						}
					} catch (Exception e) {
					} finally {
						changeHeaderDate(today);
					}

				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					invokePlayPathSuccess = false;
				}
			});
		}
	}

	private void loadRadioProgramList(String articleId, String catalogId, final int day,
			final ProgramListLoaderListener listener) {
		listener.onPreLoadPrgramList();
		final String currentLoadDay = DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd");
		if (programListBuffer.containsKey(currentLoadDay)) {
			listener.loadProgramListComplete(currentLoadDay);
			return;
		}

		RequestParams params = new RequestParams();
		params.put("method", "getArticleById");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("articleId", articleId);
		params.put("catalogId", catalogId);
		params.put("username", "");
		params.put("TerminalType", MConfig.TerminalType);
		params.put("actDate", currentLoadDay);
		client.get(MConfig.mServerUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (isDisposed)
					return;
				try {
					JSONArray result = new JSONArray(new String(arg2));
					JSONArray guideArray = result.getJSONObject(0).getJSONArray("staticfilepaths");
					for (int i = 0; i < guideArray.length(); i++) {
						String programListPath = guideArray.getJSONObject(i).getString("pointpath");
						programListPath += currentLoadDay + ".json";
						// programListPath="http://static.vms.sobeycache.com/hzwldst/liveChannel/5207befb73644f90b0b90a1591d96baa/"+currentLoadDay+".json";

						client.get(programListPath, new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
								if (isDisposed)
									return;
								Log.d("zxd", "取节目单完成 :" + arg2);
								try {
									List<ProgramListItem> list = com.alibaba.fastjson.JSONArray
											.parseArray(new String(arg2), ProgramListItem.class);
									programListBuffer.put(currentLoadDay, list);
									listener.loadProgramListComplete(currentLoadDay);
								} catch (Exception e) {
									listener.loadProgramListError(currentLoadDay);
								}

							}

							@Override
							public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
								listener.loadProgramListError(currentLoadDay);
							}
						});
					}
				} catch (Exception e) {
					listener.loadProgramListError(currentLoadDay);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				listener.loadProgramListError(currentLoadDay);
			}
		});
	}

	/**
	 * 定时刷新状态
	 */
	private void setProgramListPosition() {
		if (radiolistRefreshThread == null) {
			Runnable runnable = new Runnable() {
				@SuppressWarnings("static-access")
				@Override
				public void run() {

					while (!isDisposed) {
						try {
							if (currentProgramList != null) {
								int i = 0;
								for (i = 0; i < currentProgramList.size(); i++) {
									ProgramListItem object = currentProgramList.get(i);
									ProgramListItem.GuideItemState guideItemState = ProgramListItem
											.getItemState(object);
									// 如果是回看模式下的话 先判断是不是在当前选择的日期下回放 如果是在今天
									// 或是往天的 就直接滚到回的列表下 否则就滚动到正在直播的列表下
									if (isSeek) {
										Message msg = new Message();
										// 如果不是选择的今天的回看 直接滚动到今天的正在播的节目
										if (NewRadioProgramlistAdaptor.currentDateIndex == chooseDateIndex) {
											if (itemPlayIndex != i)
												continue;
											ProgramListItem.Msg data = new ProgramListItem.Msg();
											if (NewRadioProgramlistAdaptor.currentDateIndex != today) {
												msg.what = CHANGE_REPLAY_LABEL;
												data.item = object;
												data.index = i;
												msg.obj = data;
											}
											// 否则就直接滚动到正在回看的节目前
											else {
												msg.what = CHANGE_LIVE_LABEL;
												data.item = object;
												data.index = i;
												msg.obj = data;
											}
											handler.sendMessage(msg);
											break;
										} else if (chooseDateIndex == today
												&& guideItemState == ProgramListItem.GuideItemState.LIVE) {
											ProgramListItem.Msg data = new ProgramListItem.Msg();
											msg.what = CHANGE_LIVE_LABEL;
											data.item = object;
											data.index = i;
											msg.obj = data;
											handler.sendMessage(msg);
											break;
										}

									} else// 非回放模式下直接把当前播放的直播列表索引项拿过去
											// 滚动到那条索引就可以了
									{
										if (guideItemState.equals(ProgramListItem.GuideItemState.LIVE)) {
											Message msg = new Message();
											ProgramListItem.Msg data = new ProgramListItem.Msg();
											msg.what = CHANGE_HOME_LABEL;
											data.item = object;
											data.index = i;
											msg.obj = data;
											handler.sendMessage(msg);
											break;
										}
									}
								}
							}
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							Log.e("zxd", e.toString());
							e.printStackTrace();
						}
					}
				}
			};
			radiolistRefreshThread = new Thread(runnable);
			radiolistRefreshThread.setName("音频节目单刷新状态线程");
			radiolistRefreshThread.start();
		}
	}

	private void getOnlineVideoPath() {
		CheckNetwork checkNetwork = new CheckNetwork(NewRadioLiveChannelListview.this);
		if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
			AlertDialog.Builder builder = new AlertDialog.Builder(NewRadioLiveChannelListview.this);
			builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续收听?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					playOnlineVideo();
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					isPlay = false;
					sendBroadcast2HuiZhouSarftPage();
				}
			}).show();
		} else {
			playOnlineVideo();
		}
	}

	// TODO
	private void playOnlineVideo() {
		if (isSeek) {
			if (replayObj != null && radioViewPlayer != null) {
				if (radioViewPlayer.isPlaying())
					radioViewPlayer.stop();
				hadAddRes2Play = true;
				radioViewPlayer.addMedia(replayObj, true, true);
				isPlay = true;
				ProgramListItem item = currentProgramList.get(itemPlayIndex);
				this.radio_status_label.setText("正在播放:" + item.getName());
				sendBroadcast2HuiZhouSarftPage();
				lastIndex = index;
			} else {
				lastIndex = -1;
				isPlay = false;
				sendBroadcast2HuiZhouSarftPage();
			}
		} else {
			if (mediaObj != null && radioViewPlayer != null) {
				if (radioViewPlayer.isPlaying())
					radioViewPlayer.stop();
				hadAddRes2Play = true;
				radioViewPlayer.addMedia(mediaObj, true, true);
				isPlay = true;
				ProgramListItem item = null;
				if (currentProgramList != null && currentProgramList.size() > itemPlayIndex) {
					item = currentProgramList.get(itemPlayIndex);
					this.radio_status_label.setText("正在播放:" + item.getName());
				} else
					this.radio_status_label.setText("正在播放");
				sendBroadcast2HuiZhouSarftPage();
				lastIndex = index;
			} else {
				lastIndex = -1;
				isPlay = false;
				sendBroadcast2HuiZhouSarftPage();
			}
		}

	}

	/**
	 * 回看
	 */
	private void seek2RePlay(ProgramListItem object) {
		hadAddRes2Play = false;
		seekItem = object;
		Date startTime;
		try {
			startTime = DateParse.parseDate(object.getStarttime(), null);
			long seekTime = startTime.getTime();
			String urls = object.getUrl().toString();
			urls = urls.substring(urls.indexOf("["));
			JSONArray jsonArray = new JSONArray(urls);
			List<JSONObject> arr = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.length(); i++) {
				arr.add(jsonArray.getJSONObject(i));
			}
			Collections.reverse(arr);
			ArrayList<ResolutionObj> replayResolutions = new ArrayList<ResolutionObj>();
			for (int j = 0; j < resolutionObjs.size(); j++) {
				// flv流的回看
				ResolutionObj obj = new ResolutionObj(resolutionObjs.get(j).getTitle(),
						arr.get(j).getString("url").concat("&shifttime=") + seekTime);
				// hls流的回看
				// ResolutionObj obj=new
				// ResolutionObj(resolutionObjs.get(j).getTitle(),resolutionObjs.get(j).getMediaPath().concat("&shifttime=")+seekTime);
				replayResolutions.add(obj);
				Log.d("zxd", "seekmediapath:" + obj.getMediaPath());
			}
			replayObj = new MediaObj(mediaObj.getTitle(), new ResolutionList(replayResolutions, 0), true);

		} catch (Exception e) {
			e.printStackTrace();
			replayObj = null;
		}
	}

	protected void sendBroadcast2HuiZhouSarftPage() {
		Intent intent = new Intent(HuiZhouSarftBroadcast.HuiZhouSarftPageReciver);
		if (!invokePlayPathSuccess || !isPlay) {
			intent.putExtra("msg", HuiZhouSarftBroadcast.STOP_PLAY);
			lastIndex = -1;
			itemPlayIndex = -1;
		} else {
			intent.putExtra("msg", HuiZhouSarftBroadcast.RESUME_PLAY);
			lastIndex = index;
		}
		intent.putExtra("index", index);
		sendBroadcast(intent);
	}

	public void handle(Intent intent) {
		// String msg=intent.getStringExtra("msg");
		// if(Destory.equals(msg))
		// {
		isPlay = false;
		sendBroadcast2HuiZhouSarftPage();
		radiolistRefreshThread = null;
		lastIndex = -1;
		if (radioViewPlayer != null) {
			radioViewPlayer.onDestory();
		}
		hadAddRes2Play = false;
		isSeek = false;
		itemPlayIndex = -1;
		hadAddRes2Play = false;
		radioViewPlayer = null;
		initialzed = false;
		NewRadioProgramlistAdaptor.currentDateIndex = -1;
		NewRadioProgramlistAdaptor.lastChooseIndex = -1;
		NewRadioProgramlistAdaptor.seekFlag = false;

		// if(broadcastReciver!=null)
		// unregisterReceiver(broadcastReciver);
		// broadcastReciver=null;
		// }
	}

	/**
	 * 加载节目单的接口
	 * 
	 * @author zouxudong
	 *
	 */
	private interface ProgramListLoaderListener {
		void onPreLoadPrgramList();

		void loadProgramListComplete(String currentDay);

		void loadProgramListError(String reason);
	}
}