package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.Whitelist;
import org.apache.cordova.engine.SystemWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.ViewHolderVideoNewsDetailComments;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.MorePopWindow;
import com.sobey.cloud.webtv.utils.MorePopWindow.CollectionClickListener;
import com.sobey.cloud.webtv.utils.MorePopWindow.FontSizeChangeListener;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.utils.SharePopWindow.SharePopWindowClickListener;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.widgets.webview.WebViewController;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class VideoNewsDetailActivity extends BaseActivity implements OnPreparedListener, OnCompletionListener,
        OnErrorListener, OnInfoListener, OnPlayingBufferCacheListener, OnCompletionWithParamListener {
    protected static final String TAG = "BaseActivity";

    private static int ColumnNum = 3;

    private String mShareImage = null;
    private String mShareUrl = null;
    private String mShareTitle = null;
    private String mShareContent = null;

    private int mFontSize = MConfig.FontSizeDefault;
    private boolean isShowPicture = true;
    @SuppressWarnings("unused")
    private boolean isNightMode = false;
    private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
    private MediaObj mediaObj;
    private JSONArray mCommentsArray = null;
    private JSONArray mRecommentArray = null;
    private boolean isShowVideoPlayer = true;
    private boolean isInitVideoPlayer = false;
    private JSONObject information;
    private String[] tabName = {"详情", "评论", "推荐"};
    private BaseAdapter mAdapter;
    private WebViewController webViewController;
    private SharePopWindow mSharePopWindow;
    private MorePopWindow mMorePopWindow;
    private TextView mCommentHeaderText;
    private String mUserName;

    @GinInjectView(id = R.id.mNewsdetailFooter)
    LinearLayout mNewsdetailFooter;
    @GinInjectView(id = R.id.mNewsdetailContentTab)
    TabHost mNewsdetailContentTab;
    // Detail
    @GinInjectView(id = R.id.mNewsdetailContentTitle)
    TextView mNewsdetailContentTitle;
    @GinInjectView(id = R.id.mNewsdetailContentDate)
    TextView mNewsdetailContentDate;
    @GinInjectView(id = R.id.mNewsdetailContentRefername)
    TextView mNewsdetailContentRefername;
    @GinInjectView(id = R.id.mNewsdetailWebview)
    WebView mNewsdetailWebview;
    // Comments
    @GinInjectView(id = R.id.mComments)
    ListView mComments;
    // Recomment
    @GinInjectView(id = R.id.mRecommendContent)
    TableLayout mRecommendContent;
    // Footer
    @GinInjectView(id = R.id.mNewsdetailBack)
    ImageButton mNewsdetailBack;
    @GinInjectView(id = R.id.mNewsdetailLeavemessage)
    TextView mNewsdetailLeavemessage;
    @GinInjectView(id = R.id.mNewsdetailDownload)
    ImageButton mNewsdetailDownload;
    @GinInjectView(id = R.id.mNewsdetailShare)
    ImageButton mNewsdetailShare;
    @GinInjectView(id = R.id.mNewsdetailMore)
    ImageButton mNewsdetailMore;
    /**
     * 百度播放器相关
     *
     * @time 2016-01-18
     */

    @GinInjectView(id = R.id.mplayerView)
    View playerView;

    private String mVideoSource = null;

    private BVideoView mVV = null;
    private RelativeLayout mViewHolder = null;
    private LinearLayout mControllerHolder = null;

    private ImageButton mPlaybtn = null;
    private ImageButton mFullScreenBtn = null;
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;

    private final Object SYNC_Playing = new Object();

    private final int EVENT_PLAY = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private WakeLock mWakeLock = null;
    private Toast toast;

    private boolean isFullScreen;
    private LinearLayout.LayoutParams oldLp;


    public PowerManager pm;
    private PowerManager.WakeLock wakeLock;

    /**
     * 注解加载器
     */
    private GinInjector mInjector;

    /**
     * 是否跳转
     */
    private boolean isJump;

    /**
     * 播放状态
     */
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    // Plugin to call when activity result is received
    protected int activityResultRequestCode;
    protected CordovaPlugin activityResultCallback;

    protected CordovaPreferences prefs = new CordovaPreferences();
    protected Whitelist internalWhitelist = new Whitelist();
    protected Whitelist externalWhitelist = new Whitelist();
    protected ArrayList<PluginEntry> pluginEntries;


    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    /**
                     * 如果已经播放了，等待上一次播放结束
                     */
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                                Log.v(TAG, "wait player status to idle");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 设置播放url
                     */

                    mVV.setVideoPath(mVideoSource);
//				mVV.setVideoPath("rtmp://pili-live-rtmp.live.vms.ccsobey.com/ccsobey5/new-vms");
                    // mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    /**
                     * 续播，如果需要如此
                     */
                    if (mLastPos > 0) {

                        mVV.seekTo(mLastPos);
                        mLastPos = 0;
                    }

                    /**
                     * 显示或者隐藏缓冲提示
                     */
                    mVV.showCacheInfo(true);

                    /**
                     * 开始播放
                     */
                    mVV.start();

                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                default:
                    break;
            }
        }
    }

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    if (mVV.isPlaying()) {
                        mProgress.setProgress(currPosition);
                    }
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                default:
                    break;
            }
        }
    };

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_videonews_detail;
    }

    @Override
    public void onDataFinish(Bundle savedInstanceState) {
        super.onDataFinish(savedInstanceState);
        HuiZhouSarft.disposeVideoComponent(this);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                "My Tag");
        wakeLock.acquire();
        wakeLock.setReferenceCounted(false);

        // Get preferrence
        SharedPreferences settings = getSharedPreferences("settings", 0);
        CheckNetwork network = new CheckNetwork(this);
        isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
                || network.getWifiState(false) == State.CONNECTED;
        isNightMode = settings.getInt("night_mode", 0) == 1 ? true : false;
        mFontSize = settings.getInt("fontsize", MConfig.FontSizeDefault);
        SharedPreferences userInfo = VideoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
        if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
            mUserName = "";
        } else {
            mUserName = userInfo.getString("id", "");
        }

        isShowVideoPlayer = true;
        initTab();
        initFooter();
        if (isShowVideoPlayer) {
            initPalyer();
        }
        String str = getIntent().getStringExtra("information");
        try {
            JSONObject obj = new JSONObject(str);
            News.getArticleById(String.valueOf(obj.getInt("id")), obj.optString("parentid"), mUserName,
                    MConfig.TerminalType, null, this, new OnJsonArrayResultListener() {
                        @Override
                        public void onOK(JSONArray result) {
                            try {
                                information = result.getJSONObject(0);
                                getCollection();
                                loadContent();
                                if (isShowVideoPlayer) {
                                    CheckNetwork checkNetwork = new CheckNetwork(VideoNewsDetailActivity.this);
                                    if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                VideoNewsDetailActivity.this);
                                        builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
                                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                getOnlineVideoPath();
                                            }
                                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    } else {
                                        getOnlineVideoPath();
                                    }
                                }
                                try {
                                    News.increaseHitCount(null, information.getString("catalogid"),
                                            information.getString("id"));
                                } catch (Exception e) {
                                }
                            } catch (Exception e) {
                                loadContent();
                            }
                            initComments();
                            initRecomment();
                        }

                        @Override
                        public void onNG(String reason) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && mAdapter != null && information != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getComments();
                }
            }, 1000);
        }
        if (hasFocus) {
            SharedPreferences userInfo = VideoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
            if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
                mUserName = "";
            } else {
                mUserName = userInfo.getString("id", "");
            }
        }
    }

    private void initFooter() {
        View mActivityLayoutView = (LinearLayout) findViewById(R.id.activity_videonews_detail_layout);
        mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);
        mSharePopWindow.setOnSharePopWindowClickListener(new SharePopWindowClickListener() {
            @Override
            public void onClick(SHARE_MEDIA media) {
                // if (isShowVideoPlayer && isInitVideoPlayer) {
                // mNewsdetailVideoView.pause();
                // }
                if (mVV.isPlaying()) {
                    mVV.pause();
                }
            }
        });
        mMorePopWindow = new MorePopWindow(this, mActivityLayoutView, new FontSizeChangeListener() {
            @Override
            public void onChange(int progress) {
                if (webViewController != null) {
                    webViewController.setFontSize(progress + MConfig.FontSizeMin);
                }
            }
        }, new CollectionClickListener() {
            @Override
            public void onClick(boolean addFlag) {
                try {
                    if (information == null || TextUtils.isEmpty(information.getString("id"))) {
                        Toast.makeText(VideoNewsDetailActivity.this, "暂时无法获取新闻详情,请稍后收藏", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String articleId = information.getString("id");
                    if (TextUtils.isEmpty(mUserName)) {
                        Toast.makeText(VideoNewsDetailActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VideoNewsDetailActivity.this, LoginActivity.class));
                        return;
                    }
                    if (!addFlag) {
                        Toast.makeText(VideoNewsDetailActivity.this, "正在收藏...", Toast.LENGTH_SHORT).show();
                        News.addCollect(mUserName, articleId, VideoNewsDetailActivity.this,
                                new OnJsonArrayResultListener() {
                                    @Override
                                    public void onOK(JSONArray result) {
                                        try {
                                            if (result.getJSONObject(0).getString("returncode")
                                                    .equalsIgnoreCase("SUCCESS")) {
                                                mMorePopWindow.setCollection(true);
                                                Toast.makeText(VideoNewsDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                Toast.makeText(VideoNewsDetailActivity.this,
                                                        result.getJSONObject(0).getString("returnmsg"),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(VideoNewsDetailActivity.this, "操作失败，请稍后重试",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNG(String reason) {
                                        Toast.makeText(VideoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(VideoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    } else {
                        Toast.makeText(VideoNewsDetailActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
                        News.deleteCollect(mUserName, articleId, VideoNewsDetailActivity.this,
                                new OnJsonArrayResultListener() {
                                    @Override
                                    public void onOK(JSONArray result) {
                                        try {
                                            if (result.getJSONObject(0).getString("returncode")
                                                    .equalsIgnoreCase("SUCCESS")) {
                                                mMorePopWindow.setCollection(false);
                                                Toast.makeText(VideoNewsDetailActivity.this, "取消成功", Toast.LENGTH_SHORT)
                                                        .show();
                                            } else {
                                                Toast.makeText(VideoNewsDetailActivity.this,
                                                        result.getJSONObject(0).getString("returnmsg"),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(VideoNewsDetailActivity.this, "操作失败，请稍后重试",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNG(String reason) {
                                        Toast.makeText(VideoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(VideoNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    }
                } catch (Exception e) {
                    Toast.makeText(VideoNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mNewsdetailBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        mNewsdetailLeavemessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (information != null) {
                    SharedPreferences userInfo = VideoNewsDetailActivity.this.getSharedPreferences("user_info", 0);
                    if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
                        VideoNewsDetailActivity.this
                                .startActivity(new Intent(VideoNewsDetailActivity.this, LoginActivity.class));
                        return;
                    }
                    Intent intent = new Intent(VideoNewsDetailActivity.this, CommentActivity.class);
                    intent.putExtra("information", information.toString());
                    VideoNewsDetailActivity.this.startActivity(intent);
                }
            }
        });
        mNewsdetailDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        mNewsdetailShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
            }
        });
        mNewsdetailMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMorePopWindow.showMoreWindow();
            }
        });
    }

    private void initTab() {
        mNewsdetailContentTab.setup();
        View view0 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_videonews, null);
        mNewsdetailContentTab
                .addTab(mNewsdetailContentTab.newTabSpec(tabName[0]).setIndicator(view0).setContent(R.id.mDetail));
        View view1 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_videonews, null);
        mNewsdetailContentTab
                .addTab(mNewsdetailContentTab.newTabSpec(tabName[1]).setIndicator(view1).setContent(R.id.mComments));
        View view2 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_videonews, null);
        mNewsdetailContentTab
                .addTab(mNewsdetailContentTab.newTabSpec(tabName[2]).setIndicator(view2).setContent(R.id.mRecommend));
        onTabChange(0);
        mNewsdetailContentTab.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                onTabChange(mNewsdetailContentTab.getCurrentTab());
            }
        });
    }

    private void onTabChange(int index) {
        for (int i = 0; i < tabName.length; i++) {
            View view = (View) mNewsdetailContentTab.getTabWidget().getChildAt(i);
            ((TextView) view.findViewById(R.id.text)).setText(tabName[i]);
            if (i == index) {
                ((TextView) view.findViewById(R.id.text))
                        .setTextColor(getResources().getColor(R.color.home_tab_text_focus));
                ((ImageView) view.findViewById(R.id.image)).setBackgroundResource(R.drawable.videonews_tab_select);
            } else {
                ((TextView) view.findViewById(R.id.text))
                        .setTextColor(getResources().getColor(R.color.home_tab_text_normal));
                ((ImageView) view.findViewById(R.id.image)).setBackgroundResource(R.drawable.videonews_tab_unselect);
            }
        }
    }

    // Detail
    private void initWebView() {
        mNewsdetailWebview.getSettings().setJavaScriptEnabled(true);
        mNewsdetailWebview.getSettings().setDefaultTextEncodingName("gbk");
        mNewsdetailWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mNewsdetailWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
    }

    private void loadContent() {
        initWebView();
        try {
            if (information != null) {
                mShareUrl = information.getString("url");
                mShareTitle = information.getString("title");
                mShareContent = information.getString("summary").trim();
                mShareImage = information.getString("logo");
                mNewsdetailContentTitle.setText(information.getString("title"));
                mNewsdetailContentDate.setText("发布时间："
                        + DateParse.getDate(0, 0, 0, 0, information.getString("publishdate"), null, "yyyy.MM.dd"));
                mNewsdetailContentRefername.setText("播放量：" + information.getString("hitcount"));
                String content = information.getString("summary");
                content = "<html><head></head><body style='font-size:" + mFontSize + "px; color:#6e6e6e;'>" + content;
                content = content + "</body></html>";
                webViewController = new WebViewController(this, mNewsdetailWebview, content, isShowPicture);
                webViewController.start();
            } else {
                mNewsdetailWebview.loadData("获取详情出错，请稍后重试", "text/html; charset=UTF-8", null);
            }
        } catch (Exception e) {
            mNewsdetailWebview.loadData("获取详情出错，请稍后重试", "text/html; charset=UTF-8", null);
        }
    }

    // Comments
    private void initComments() {
        mCommentHeaderText = new TextView(this);
        mComments.addHeaderView(mCommentHeaderText);
        setCommentsTitle("正在获取评论信息。。。");
        mAdapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolderVideoNewsDetailComments viewHolder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(VideoNewsDetailActivity.this)
                            .inflate(R.layout.listitem_videonews_detailcomments, null);
                    viewHolder = new ViewHolderVideoNewsDetailComments();
                    viewHolder.setUser((TextView) convertView.findViewById(R.id.user));
                    viewHolder.setComments((TextView) convertView.findViewById(R.id.comments));
                    viewHolder.setDate((TextView) convertView.findViewById(R.id.date));
                    viewHolder.setThumbnail((AdvancedImageView) convertView.findViewById(R.id.image));
                    convertView.setTag(viewHolder);
                    loadViewHolder(position, convertView, viewHolder);
                } else {
                    loadViewHolder(position, convertView, viewHolder);
                }
                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                if (mCommentsArray != null) {
                    return mCommentsArray.length();
                } else {
                    return 0;
                }
            }
        };
        mComments.setAdapter(mAdapter);
        getComments();
    }

    private void loadViewHolder(int position, View convertView, ViewHolderVideoNewsDetailComments viewHolder) {
        viewHolder = (ViewHolderVideoNewsDetailComments) convertView.getTag();
        if (mCommentsArray.length() > position) {
            try {
                int pos = mCommentsArray.length() - position - 1;
                viewHolder.getUser().setText(mCommentsArray.getJSONObject(pos).getString("commentuser"));
                viewHolder.getDate().setText(DateParse.getDate(0, 0, 0, 0,
                        mCommentsArray.getJSONObject(pos).getString("addtime"), null, "yyyy-MM-dd HH:mm"));
                viewHolder.getComments().setText(mCommentsArray.getJSONObject(pos).getString("content"));
                viewHolder.getThumbnail().setNetImage(mCommentsArray.getJSONObject(pos).getString("logo"));
            } catch (JSONException e) {
                viewHolder = null;
            }
        } else {
            viewHolder = null;
        }
    }

    private void getComments() {
        try {
            if (information != null) {
                String articleId = information.getString("id");
                String catalogId = information.getString("catalogid");
                News.getCommentByArticleId(articleId, catalogId, VideoNewsDetailActivity.this,
                        new OnJsonArrayResultListener() {
                            @Override
                            public void onOK(JSONArray result) {
                                JSONArray jsonArray = new JSONArray();
                                for (int i = (result.length() - 1); i >= 0; i--) {
                                    try {
                                        jsonArray.put(result.get(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mCommentsArray = jsonArray;
                                mAdapter.notifyDataSetChanged();
                                if (mCommentsArray.length() < 1) {
                                    setCommentsTitle("暂无评论");
                                } else {
                                    setCommentsTitle("共有" + mCommentsArray.length() + "条评论");
                                }
                            }

                            @Override
                            public void onNG(String reason) {
                                setCommentsTitle("网络不给力,请稍后再试吧");
                            }

                            @Override
                            public void onCancel() {
                                setCommentsTitle("网络不给力,请稍后再试吧");
                            }
                        });
            } else {
                setCommentsTitle("网络不给力,请稍后再试吧");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCommentsTitle(String title) {
        mCommentHeaderText.setText(title);
        mCommentHeaderText.setTextSize(18);
        mCommentHeaderText.setTextColor(Color.BLACK);
        mCommentHeaderText.setPadding(10, 10, 0, 10);
        mCommentHeaderText.setVisibility(View.VISIBLE);
    }

    // Recomment
    private void initRecomment() {
        if (information != null) {
            try {
                String articleId = information.getString("id");
                News.getRelativeArticle(articleId, VideoNewsDetailActivity.this, new OnJsonObjectResultListener() {
                    @Override
                    public void onOK(JSONObject result) {
                        int length;
                        try {
                            mRecommentArray = result.getJSONArray("articles");
                            length = mRecommentArray.length();
                            TableRow tableRowTop = new TableRow(VideoNewsDetailActivity.this);
                            tableRowTop.addView(new View(VideoNewsDetailActivity.this), 0);
                            tableRowTop.addView(new View(VideoNewsDetailActivity.this), 1);
                            tableRowTop.addView(new View(VideoNewsDetailActivity.this), 2);
                            mRecommendContent.addView(tableRowTop);
                            for (int i = 0; i < length; i++) {
                                if (i % ColumnNum == 0) {
                                    TableRow tableRow = new TableRow(VideoNewsDetailActivity.this);
                                    int column = 0;
                                    for (int j = i; j < ((i + ColumnNum) > length ? length : (i + ColumnNum)); j++) {
                                        View view = LayoutInflater.from(VideoNewsDetailActivity.this)
                                                .inflate(R.layout.listitem_videonews_detailrecomment, null);
                                        SharedPreferences settings = VideoNewsDetailActivity.this
                                                .getSharedPreferences("settings", 0);
                                        CheckNetwork network = new CheckNetwork(VideoNewsDetailActivity.this);
                                        boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
                                                || network.getWifiState(false) == State.CONNECTED;
                                        if (isShowPicture)
                                            ((AdvancedImageView) view.findViewById(R.id.image))
                                                    .setNetImage(mRecommentArray.getJSONObject(j).getString("logo"));
                                        ((TextView) view.findViewById(R.id.summary))
                                                .setText(mRecommentArray.getJSONObject(j).getString("title"));
                                        ((TextView) view.findViewById(R.id.summary))
                                                .setWidth(tableRow.getWidth() / (ColumnNum + 1));
                                        android.widget.TableRow.LayoutParams params = new android.widget.TableRow.LayoutParams(
                                                column);
                                        view.setLayoutParams(params);
                                        tableRow.addView(view);
                                        view.setOnClickListener(new onRecommentClickListener(j));
                                        column++;
                                    }
                                    mRecommendContent.addView(tableRow);
                                    i = i + ColumnNum - 1;
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNG(String reason) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class onRecommentClickListener implements OnClickListener {
        private int index;

        public onRecommentClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            try {
                openDetailActivity(Integer.valueOf(mRecommentArray.getJSONObject(index).getString("type")),
                        mRecommentArray.getJSONObject(index).toString());
                finishActivity();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDetailActivity(int type, String information) {
        switch (type) {
            case MConfig.TypePicture:
                Intent intent = new Intent(VideoNewsDetailActivity.this, PhotoNewsDetailActivity.class);
                intent.putExtra("information", information);
                VideoNewsDetailActivity.this.startActivity(intent);
                break;
            case MConfig.TypeVideo:
                HuiZhouSarft.disposeVideoComponent(VideoNewsDetailActivity.this);
                Intent intent1 = new Intent(VideoNewsDetailActivity.this, VideoNewsDetailActivity.class);
                intent1.putExtra("information", information);
                VideoNewsDetailActivity.this.startActivity(intent1);
                break;
            default:
                Intent intent2 = new Intent(VideoNewsDetailActivity.this, GeneralNewsDetailActivity.class);
                intent2.putExtra("information", information);
                VideoNewsDetailActivity.this.startActivity(intent2);
                break;
        }
    }

    // Video
    private void initPalyer() {
        mViewHolder = (RelativeLayout) playerView.findViewById(R.id.view_holder);
        mControllerHolder = (LinearLayout) playerView.findViewById(R.id.controller_holder);
        mProgress = (SeekBar) playerView.findViewById(R.id.media_progress);
        mDuration = (TextView) playerView.findViewById(R.id.time_total);
        mCurrPostion = (TextView) playerView.findViewById(R.id.time_current);
        mPlaybtn = (ImageButton) playerView.findViewById(R.id.play_btn);
        mFullScreenBtn = (ImageButton) playerView.findViewById(R.id.media_fullscreen);
        oldLp = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        registerCallbackForControl();
        /**
         * 设置ak
         */
        BVideoView.setAK(MConfig.AK);

        /**
         * 创建BVideoView和BMediaController
         */
        mVV = new BVideoView(this);
        mViewHolder.addView(mVV);

        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnCompletionWithParamListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        /**
         * 设置解码模式
         */
        // mVV.setDecodeMode(BVideoView.DECODE_HW);
        mVV.setDecodeMode(BVideoView.DECODE_SW);
        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
        isInitVideoPlayer = true;
    }

    /**
     * 为控件注册回调处理函数
     */
    private void registerCallbackForControl() {
        mPlaybtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (mVV.isPlaying()) {
                    mPlaybtn.setImageResource(R.drawable.play);
                    /**
                     * 暂停播放
                     */
                    mVV.pause();
                } else {
                    mPlaybtn.setImageResource(R.drawable.pause);
                    /**
                     * 继续播放
                     */
                    mVV.resume();
                }

            }
        });
        mFullScreenBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleScreen();
            }
        });
        OnSeekBarChangeListener osbc1 = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Log.v(TAG, "progress: " + progress);
                updateTextViewWithTimeFormat(mCurrPostion, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar开始seek时停止更新
                 */
                Log.v(TAG, "onStartTrackingTouch");
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int iseekPos = seekBar.getProgress();
                /**
                 * SeekBark完成seek时执行seekTo操作并更新界面
                 *
                 */
                mVV.seekTo(iseekPos);
                Log.v(TAG, "seek to " + iseekPos);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        };
        mProgress.setOnSeekBarChangeListener(osbc1);
    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }

    private long mTouchTime;
    private boolean barShow = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
            mTouchTime = System.currentTimeMillis();
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            long time = System.currentTimeMillis() - mTouchTime;
            if (time < 400) {
                updateControlBar(!barShow);
            }
        }

        return true;
    }

    public void updateControlBar(boolean show) {

        if (show) {
            mControllerHolder.setVisibility(View.VISIBLE);
        } else {
            mControllerHolder.setVisibility(View.INVISIBLE);
        }
        barShow = show;
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mLastPos = (int) mVV.getCurrentPosition();
            mPlaybtn.setImageResource(R.drawable.play);
            mVV.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        // 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
        if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mLastPos = (int) mVV.getCurrentPosition();
            mPlaybtn.setImageResource(R.drawable.play);
            mVV.pause();
        }
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != wakeLock) {
            wakeLock.release();
        }

        if ((mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mLastPos = (int) mVV.getCurrentPosition();
            mVV.stopPlayback();
        }
        if (toast != null) {
            toast.cancel();
        }
        /**
         * 结束后台事件处理线程
         */
        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // if (mNewsdetailVideoView != null) {
        // mNewsdetailVideoView.onConfigurationChanged();
        // }
        super.onConfigurationChanged(newConfig);
    }

    private void getOnlineVideoPath() {
        JSONArray videoArray;
        if (information != null) {
            try {
                videoArray = information.getJSONArray("staticfilepaths");
                mVideoSource = ((JSONObject) videoArray.get(0)).getString("playerpath");
                startPaly();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

//        try {
//            if (information != null) {
//                videoArray = information.getJSONArray("staticfilepaths");
//                for (int i = 0; i < videoArray.length(); i++) {
//                    News.getVideoPath(((JSONObject) videoArray.get(i)).getString("playerpath"), this,
//                            new OnJsonObjectResultListener() {
//                                @Override
//                                public void onOK(JSONObject result) {
//                                    try {
//                                        resolutionObjs.clear();
//                                        String str = result.getString("playerUrl");
//                                        str = str.substring(str.indexOf("{"));
//                                        JSONObject obj = new JSONObject(str);
//                                        JSONArray formatUriArray = ((JSONObject) obj.getJSONArray("clips").get(0))
//                                                .getJSONArray("urls");
//                                        JSONArray formatArray = obj.getJSONArray("formats");
//                                        String host = obj.getString("host");
//                                        String p2p = obj.getString("p2p");
//                                        for (int i = 0; i < formatUriArray.length(); i++) {
//                                            resolutionObjs.add(new ResolutionObj(formatArray.get(i).toString(),
//                                                    (host + ((String) formatUriArray.get(i)) + p2p)));
//                                        }
//                                        mediaObj = new MediaObj(obj.optString("title"),
//                                                new ResolutionList(resolutionObjs, 0), true);
//                                        if (mediaObj != null) {
//                                            if (mediaObj.isResolutionMode()) {
//                                                mVideoSource = mediaObj.getResolutionList().getResolutions()
//                                                        .get(mediaObj.getResolutionList().getResolutionIndex())
//                                                        .getMediaPath();
//                                            } else {
//                                                mVideoSource = mediaObj.getMediaPath();
//                                            }
//                                            startPaly();
//                                        }
//                                       //  mVideoAdManager.setVideoAd(VideoNewsDetailActivity.this,
//                                       //  mNewsdetailVideoView,
//                                       //  mediaObj, result.optInt("positionId",
//                                       //  0),
//                                         //result.optString("catalogId"));
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onNG(String reason) {
//                                }
//
//                                @Override
//                                public void onCancel() {
//                                }
//                            });
//                }
//            }
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
        // startPaly();
    }

    private void getCollection() {
        try {
            if (information != null && mMorePopWindow != null) {
                if (information.getString("iscollect").equalsIgnoreCase("1")) {
                    mMorePopWindow.setCollection(true);
                } else {
                    mMorePopWindow.setCollection(false);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMorePopWindow.isShowing()) {
                mMorePopWindow.hideMoreWindow();
                return true;
            }
            if (mSharePopWindow.isShowing()) {
                mSharePopWindow.hideShareWindow();
                return true;
            }
            // if (mNewsdetailVideoView != null &&
            // mNewsdetailVideoView.isFullScreen()
            // && keyCode == KeyEvent.KEYCODE_BACK) {
            // mNewsdetailVideoView.toggleFullScreen();
            // return true;
            // }
            if (isFullScreen) {
                toggleScreen();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSharePopWindow.onActivityResult(requestCode, resultCode, data);
    }

    private void toggleScreen() {
        if (isFullScreen) {
            mFullScreenBtn.setImageResource(R.drawable.full_screen);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            playerView.setLayoutParams(oldLp);
            isFullScreen = false;
        } else {
            mFullScreenBtn.setImageResource(R.drawable.full_screen_cancel);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            playerView.setLayoutParams(lp);
            isFullScreen = true;
        }
    }

    private void startPaly() {
        /**
         * 如果已经开始播放，先停止播放
         */
        // mVideoSource =
        // "http://182.132.33.53/vodtongh.sobeycache.com/vod/2016/01/17/8aa3890ec0b34331be508cbecc59e429?fmt=h264_256k_mp4&slice=001&wsiphost=local";
        if (mVV.isPlaying()) {
            mVV.stopPlayback();
        } else if (!mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mVV.resume();
        } else {
            /**
             * 发起一次新的播放任务
             */
            if (mEventHandler.hasMessages(EVENT_PLAY)) {
                mEventHandler.removeMessages(EVENT_PLAY);
            }
            mEventHandler.sendEmptyMessage(EVENT_PLAY);
        }
    }

    /**
     * 百度播放器监听
     */
    @Override
    public void OnCompletionWithParam(int arg0) {

    }

    @Override
    public void onPlayingBufferCache(int arg0) {

    }

    @Override
    public boolean onInfo(int what, int arg1) {
        switch (what) {
            /**
             * 开始缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                Log.i(TAG, "caching start,now playing url : " + mVV.getCurrentPlayingUrl());

                break;
            /**
             * 结束缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                Log.i(TAG, "caching start,now playing url : " + mVV.getCurrentPlayingUrl());

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onError(int arg0, int arg1) {
        Log.v(TAG, "onError");
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        return true;
    }

    @Override
    public void onCompletion() {
        Log.v(TAG, "onCompletion");

        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    @Override
    public void onPrepared() {
        Log.v(TAG, "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }

    /**
     * 显示分享
     */
    public void showSharePopWindow() {
        this.mSharePopWindow.showShareWindow(this.mShareUrl, this.mShareTitle, this.mShareContent, this.mShareImage);
    }

    /**
     * 返回注解加载器
     *
     * @return
     */
    public GinInjector getInjector() {
        if (mInjector == null) {
            mInjector = GinInjector.getInstance();
        }
        return mInjector;
    }
}
