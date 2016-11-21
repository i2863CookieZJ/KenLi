package com.sobey.cloud.webtv.utils;

import java.util.ArrayList;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.CatalogObj;

public class MConfig {
	/**
	 * 圈子域名
	 */
//	public static final String QZ_DOMAIN = "http://qz.sobeycache.com/";
	 public static final String QZ_DOMAIN = "http://qz1.ccsobey.com/";

	/**
	 * 电商域名
	 */
//	public static final String SHOP_DOMAIN = "http://shop.sobeycache.com/";
	 public static final String SHOP_DOMAIN = "http://shop1.ccsobey.com/";

	/**
	 * CMS域名
	 */
//	public static final String CMS_DOMAIN = "http://m.sobeycloud.com/";
	 public static final String CMS_DOMAIN = "http://m1.ccsobey.com/";

	public static final String ECSHOPApiUrl = SHOP_DOMAIN + "index.php?controller=interfaceapi";
	public static final String ECSHOP = SHOP_DOMAIN + "index.php?controller=site&action=home&ftype=1&id="
			+ MConfig.SITE_ID + "&uid=";
	public static final String MY_ORDER = SHOP_DOMAIN + "index.php?controller=appucenter&action=order_list&id=";
	public static final String MY_COLLECT = "http://www.jetjiang.com/shop/index.php?controller=appucenter&action=favorite&user_id=";
	public static final String PAY_ORDER = SHOP_DOMAIN + "index.php?controller=block&action=doPay&order_id=";
	public static final String PAY_SHOP_CAR = SHOP_DOMAIN + "index.php?controller=simple&action=cart2&gids=";

	public static final String mServerUrl = CMS_DOMAIN + "APIServiceReceiver";
	public static final String SHOP_URL = SHOP_DOMAIN + "index.php?controller=appservice";

	/**
	 * 订单
	 */
	public static final String ECSHOP_ORDERAPI = SHOP_DOMAIN + "index.php?controller=interfaceapi";
	public static final String MYORDER_LIST = SHOP_DOMAIN + "index.php?controller=appservice&action=orderList";
	public static final String ORDER_LISTSIZE = SHOP_DOMAIN + "index.php?controller=appservice&action=orderListSize";
	public static final String ORDER_PIC_HEAD = SHOP_DOMAIN + "";
	public static final String MY_ORDER_DETAIL = SHOP_DOMAIN + "index.php?controller=appservice&action=orderDetail";

	/**
	 * 购物车
	 */
	public static final String ECSHOP_CARTAPI = ECSHOP_ORDERAPI;

	// test圈子接口地址
	public static final String mQuanZiUrl_test = "http://qz.ieator.com/plugin.php?id=sobeyapp:api";
	// test圈子资源接口地址
	public static final String mQuanZiResourceUrl_test = "http://qz.ieator.com/data/attachment/common/";
	// test圈子基本地址
	public static final String mQuanZiBaseUrl_test = "http://qz.ieator.com";
	// 圈子接口地址
	public static final String mQuanZiApiUrl = QZ_DOMAIN + "plugin.php?id=sobeyapp:api";
	// 个人中心之我的帖子2015/12/7
	public static final String myTieziApiUrl = SHOP_DOMAIN + "index.php?controller=appservice";
	// 圈子资源接口地址
	public static final String mQuanZiResourceUrl = QZ_DOMAIN + "data/attachment/common/";

	public static final int TIMEOUT_DEFAULT = 10000;
	public static final int SITE_ID = 43;
	public static final int SITE_ID_QUANZI = 43;
	/**
	 * cms里面配置的appkey 用于取惠州台惠州广电那一页的栏目 id
	 */
	public static final String HuiZhouGuangDian_AppKey = "46_huizhou_app_video_list";
	/**
	 * 惠州台首页资讯栏目id配置
	 */
	public static final String ZiXun_AppKey = SITE_ID + "_app_news_list";
	/**
	 * 首页关注置顶新闻最大条数配置key
	 */
	public static final String MAX_Attetion_Topic_KEY = SITE_ID + "_app_attention_top_max";
	public static final String mIncreaseHitCountUrl = "http://stat.sobeycloud.com/Services/Stat.jsp";
	// Video Ad
	public static final String mVideoAdUrl = "http://admin.sobeycloud.com/adServlet";
	public static final String mVideoAdSiteId = "485";
	// Video Ad

	// Package Name
	public static final String mPackageNameHomeActivity = "com.sobey.cloud.webtv.HomeActivity";
	public static final String mPackageNameLiveNewsHomeActivity = "com.sobey.cloud.webtv.LiveNewsHomeActivity";
	public static final String mPackageNameBrokeNewsHomeActivity = "com.sobey.cloud.webtv.broke.BrokeNewsHomeActivity";
	public static final String mPackageNameRecommendNewsHomeActivity = "com.sobey.cloud.webtv.RecommendNewsHomeActivity";
	public static final String mPackageNameTopicNewsHomeActivity = "com.sobey.cloud.webtv.TopicNewsHomeActivity";

	// FTP Configuration
	public static final String mFtpHostName = "ftpdemo.sobeycloud.com";
	public static final String mFtpPort = "21";
	public static final String mFtpUserNameImage = "cmsapppic";
	public static final String mFtpPasswordImage = "cmsapppic";
	public static final String mFtpUserNameVideo = "cmsapp";
	public static final String mFtpPasswordVideo = "cmsapp";
	public static final String mFtpRemoteVideoPath = "/video/";
	public static final String mFtpRemoteImagePath = "/image/";
	public static final String mVmsSiteId = "485";
	public static final String mVmsCatalogId = "32";
	// 根据不同的电视台的定制true为需要吗，反之false
	// 栏目id
	public static ArrayList<CatalogObj> CatalogList = new ArrayList<CatalogObj>();
	public static final boolean HaveRecommendCatalog = true;// 首页
	public static final boolean HaveBrokeCatalog = true;// 爆料
	public static final boolean HaveCouponCatalog = true;// 优惠券
	public static final boolean HaveChargeMobileFeeCatalog = false; // 话费充值
	public static final boolean HaveSearchBusLineCatalog = false; // 公交线路查询
	public static final boolean HaveSearchIllegalCatalog = false; // 违章查询
	public static final boolean HaveLifeAroundCatalog = false; // 周边生活
	public static final boolean HaveTakeTaxiCatalog = false; // 滴滴打车
	public static final boolean HaveGuessCatalog = false; // 竞猜
	public static final boolean HaveCampaignCatalog = true; // 活动
	public static final String LiveVideoCatalogId = "134";
	public static final String LiveAudioCatalogId = "135";
	public static final int CatalogDefaultIconResId = R.drawable.broke_icon;

	// 爆料
	public static final String BrokeTypeId = "12";

	// 缓存过期时间
	public static final long CatalogDuration = 24 * 60 * 60 * 1000; // 24hour
	public static final long ListDuration = 5 * 60 * 1000; // 5min

	// 新闻类型
	public static final int TypeNews = 1;
	public static final int TypePicture = 2;
	public static final int TypeLive = 3;
	public static final int TypeVideo = 5;
	public static final int TypeBroke = 7;

	// 设备类型
	public static final String TerminalType = "ANDROID";

	// 字体尺寸
	public static final int FontSizeMax = 30;
	public static final int FontSizeMin = 14;
	public static final int FontSizeDefault = 16;

	// 保存图片前缀名
	public static final String ImageSavePrefix = "KenLiTV_";

	// 数据保存路径
	public static final String SavePath = "/KenLiTV";
	public static final String CachePath = "/KenLiTV/Cache";
	public static final String XmlFilePath = "/KenLiTV/Xml";

	// 友盟
	public static final String DESCRIPTOR = "com.umeng.share";

	public static final String UMENT_LOGIN = "com.umeng.login";

	// 分享内容
	public static final String ShareWeiXinAppId = "wxafcf850a8cf2d4b5";

	public static final String SHAREWEIXINAPPSECRET_STRING = "948a6d18bd352cc2d2155eaf7c4efaa6";

	public static final String ShareQQAppId = "1105412707";
	public static final String ShareQQAppKey = "WzyKEE1E5drrmYHB";
	public static final String ShareFriendSMSContent = "能获悉本地新闻，还能看视频和电视直播，索贝WebTV客户端真好用，我强烈推荐。下载地址：http://www.sobeycloud.com/";
	public static final int ShareContentLength = 100;
	public static final int ShareMessageContentLength = 40;

	// 百度统计
	public static final String BaiduTongji = "afadf0da0b";
	public static final String ChannalName = "wait";
	/**
	 * 百度云视频APPKEY 您的AK 请到http://console.bce.baidu.com/iam/#/iam/accesslist获取
	 */
	public static final String AK = "7cd62775763e4f439bb29710243fecdb";
	// 二手交易地址
	public static final String ERSHOU = "http://www.baixing.com/appsdk/listing?appId=" + SITE_ID + "&moduleId=ershou";
	// 房屋中介
	public static final String FANGWU = "http://www.baixing.com/appsdk/listing?appId=" + SITE_ID + "&moduleId=fangchan";
	// 招聘求职
	public static final String ZHAOPIN = "http://www.baixing.com/appsdk/listing?appId=" + SITE_ID + "&moduleId=zhaopin";
	// 违章查询
	public static final String WEIZHANG = "http://m.weizhang8.cn/";
	// 公交查询
	public static final String GONGJIAO = "http://m.8684.cn/bus_switch";
	// 长途车查询
	public static final String CHANGTU = "http://touch.trip8080.com/";
	// 列车时刻查询
	public static final String SHIKE = "http://m.8684.cn/hc";
	// 发帖
	public static final String FATIE = "http://qz.ieator.com/forum.php?mod=post&action=newthread&fid=2";
	// 电商
	public static final String DIANSHANG = "http://iwebshop.ieator.com/site/home/id/16  ";
	// 滴滴打车
	public static final String DACHE = "http://pay.xiaojukeji.com/api/v2/webapp?maptype=baidu&channel=1272";
	// 圈子
	public static final String QUANZI = "http://qz.ieator.com/forum.php?forumlist=1&mobile=2";
	/** 百度地图 */
	public static final String DITU = "http://map.baidu.com/";
	/***
	 * 热度id
	 */
	public static final String REDUID = "2444";
	/***
	 * 政务公告id
	 */
	public static final String ZHENGWUID = "3683";
	/**
	 * 图片欣赏id
	 */
	public static final String TUPIANID = "3684";
	/**
	 * 专题id
	 */
	public static final String ZHUANTIID = "2447";
	/**
	 * 点播id
	 */
	public static final String DIABOID = "2446";
	/***
	 * 直播id
	 */
	public static final String ZHIBOID = "2445";
	/**
	 * 节目id 这个是webtvc产品 版本里面才有 目前惠州台没有
	 */
	public static final String JIEMUID = "136";
	// getMessageList("2448", 2);
	// getMessageList("2451", 3);
	// getMessageList("2449", 4);
	// getMessageList("2450", 5);
	// getMessageList("2460", 6);
	/**
	 * 热度下的滚动
	 */
	public static final String Hot_GunDong = "2448";
	/**
	 * 热度下的今日 搜索
	 */
	public static final String Hot_TodaySeach = "2450";
	/**
	 * 热度下的图集
	 */
	public static final String Hot_Photograph = "2451";
	/**
	 * 热度下的惠州新闻
	 */
	public static final String Hot_HuiZhouNews = "2449";
	/**
	 * 点播下的纪录片
	 */
	public static final String Vod_Recording = "2460";

	public static final String VIDEO_LIVE_ID = "2475";

	public static final String AUDIO_LIVE_ID = "2482";

	public static final String ZhengWu_ItemID0 = "8760";
	public static final String ZhengWu_ItemID1 = "8761";
	public static final String ZhengWu_ItemID2 = "8762";
	public static final String ZhengWu_ItemID3 = "8775";
	public static final String ZhengWu_ItemID4 = "8776";

	/**
	 * 直播模块栏目ID
	 */
	public static final String LIVE_GUANGDIAN = "9254:广电直播:2";
	public static final String LIVE_360 = "9255:现场Live:2";
	public static final String LIVE_ACTIVITY = "9256:活动直播:2";
}
