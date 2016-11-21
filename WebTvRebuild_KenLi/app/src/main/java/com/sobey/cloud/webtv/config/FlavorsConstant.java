package com.sobey.cloud.webtv.config;

public class FlavorsConstant {
	/**
	 * 版本类型
	 */
	public enum DevType {
		DEBUG, RELEASE, BETA
	}

	/**
	 * 当前版本
	 */
	public static final DevType DEV_VERSION = DevType.BETA;

	/**
	 * 域名
	 */
	private static final String DOMAIN_NAME = "shop.sobeycache.com/index.php";
	private static final String UC_DOMAIN_NAME = "uc.sobeycache.com/interface";

	/**
	 * api版本
	 */
	private static final String API_VERSION = "";
	private static final String UC_API_VERSION = "";

	/**
	 * 服务器地址
	 */
	public static final String VERSION_URL = "http://" + DOMAIN_NAME + API_VERSION;
	public static final String BASE_URL = "http://" + DOMAIN_NAME + API_VERSION;
	public static final String UC_VERSION_URL = "http://" + UC_DOMAIN_NAME + UC_API_VERSION;
	public static final String UC_BASE_URL = "http://" + UC_DOMAIN_NAME + UC_API_VERSION;

	/**
	 * 是否调试
	 */
	public static final boolean DEBUG = true;
}