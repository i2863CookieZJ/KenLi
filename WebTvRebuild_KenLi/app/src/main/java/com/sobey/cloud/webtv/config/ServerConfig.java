package com.sobey.cloud.webtv.config;

/**
 * 服务器配置信息
 */
public class ServerConfig {
	public String TAG = "ServerConfig";

	/**
	 * 请求服务器正确码
	 */
	public static final int SUCCESS_CODE = 0x888;

	/**
	 * 请求失败错误码
	 */
	public static final int ERROR_CODE = 0x999;

	/**
	 * 已是最新版本
	 */
	public static final int LAST_VERSION = 4023;

	/**
	 * 返回URL
	 *
	 * @param url
	 * @return
	 */
	public static String url(String url) {
		return VERSION_URL + url;
	}

	/**
	 * 服务器地址
	 */
	public static final String VERSION_URL = FlavorsConstant.VERSION_URL;
	public static final String BASE_URL = FlavorsConstant.BASE_URL;
	public static final String UC_VERSION_URL = FlavorsConstant.UC_VERSION_URL;
	public static final String UC_BASE_URL = FlavorsConstant.UC_BASE_URL;

	/**
	 * 登录
	 */
	public final static String LOGIN = UC_VERSION_URL + "?method=verify";

	/**
	 * 退出登录
	 */
	public final static String LOGOUT = "controller=simple&action=logout";

	/**
	 * 支付
	 */
	public final static String PAY = "controller=appservice";
	public final static String ORDERCANCEL = "orderCancel";
	public final static String DODELIVERY = "doDelivery";
}
