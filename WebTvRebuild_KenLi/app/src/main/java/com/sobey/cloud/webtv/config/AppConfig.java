package com.sobey.cloud.webtv.config;

import java.io.IOException;
import java.util.ArrayList;

import com.sobey.cloud.webtv.bean.UserBean;
import com.sobey.cloud.webtv.utils.BeanUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 应用配置 Created by higgses on 14-5-13.
 */
public class AppConfig {
	private static final String TAG = "AppConfig";

	private AppConfig() {
	}

	/**
	 * 图片缓存的目录： {@link Context#getCacheDir()}/IMAGE_CACHE_DIR or
	 * {@link Context#getExternalCacheDir()}/IMAGE_CACHE_DIR
	 */
	public static final String IMAGE_CACHE_DIR = "image_cache";

	/**
	 * 数据缓存目录 {@link Context#getCacheDir()}/DATA_CACHE_DIR or
	 * {@link Context#getExternalCacheDir()}/DATA_CACHE_DIR
	 */
	public static final String DATA_CACHE_DIR = "data_cache";

	/**
	 * 图片目录
	 */
	public static final String IMAGE_DIR = "image";

	/**
	 * 下载文件地址
	 */
	public static final String DOWNLOAD_DIR = "download";
	/**
	 * 分享图片的名称
	 */
	public static final String SHARE_IMAGE_NAME = "share_image.png";
	/**
	 * 加载数据的条数
	 */
	public static final int PAGE_DATA_COUNT = 10;
	public static final int PAGE_DATA_COUNT_MAP = 30;

	/**
	 * 检测更新延迟
	 */
	public static final int CHECK_VERSION_DELAYED = 5000;

	/**
	 * 滑动切换默认中间选中位置
	 */
	public static final int SLIDE_DEFAULT_MIDDLE_INDEX = 1;

	public static class Cache {

	}

	public static class DateFormat {
		public static final String ENCRYPTION_RANDOM = "yyyyMMDDHHmmss";
		public static final String Y_M_D_H_M_S_ = "yyyy-MM-DD HH-mm-ss";
	}

	/**
	 * 程序信息
	 */
	public static class App {
		/**
		 * Activity集合
		 */
		public static ArrayList<Activity> ACTIVITY_LIST = new ArrayList<Activity>();

		/**
		 * SD路径
		 */
		private static String SD_PATH = android.os.Environment.getExternalStorageDirectory() + "";

		/**
		 * 程序文件夹
		 */
		public static final String APP_PATH = SD_PATH + "/cn.com.klein.points/";

		/**
		 * 用户头像保存路径
		 */
		public static final String AVATAR_PATH = APP_PATH + "avatar/";

		/**
		 * 动态图片保存路径
		 */
		public static final String DYNAMIC_PATH = APP_PATH + "dynamic/";

		/**
		 * 文件缓存路径
		 */
		public static String CACHE_PATH = APP_PATH + "cache";

		/**
		 * 商家类型sql文件本地默认文件文件名
		 */
		public static final String MERCHANT_TYPE_LOCAL_NAME = "merchant_type.sql";

		/**
		 * 省市区sql文件本地默认文件文件名
		 */
		public static final String AREA_LOCAL_NAME = "area.sql";

		/**
		 * WiFi热点 sql文件本地默认文件文件名
		 */
		public static final String WIFI_HOT_LOCAL_NAME = "wifi_hot.sql";

		/**
		 * 选择地区RequestCode
		 */
		public static final int SELECT_AREA_CODE = 1234;

		/**
		 * 选择WiFi RequestCode
		 */
		public static final int SELECT_WIFI_CODE = 2345;

		/**
		 * ShareSdk
		 */
		// 分享
		public static String SHARE_SDK_APPKEY = "507f03b3a3c8";
		public static String SHARE_SDK_APPSECRET = "3461970a1e7a6c4269d40bd574d65380";

		// 短信
		public static String SHARE_SDK_SMS_KEY = "5ac2b1dac008";
		public static String SHARE_SDK_SMS_SECRET = "f2b881f2357dfb88f9e2bdafaf658a60";

		// 服务器分配的Code和api_key
		public static String SERVER_API_CODE = "HooHooPoints";
		public static String SERVER_API_KEY = "8608c007f1789d881036fd7cd5e42ca91119e7df";

		// public static String SERVER_API_CODE = "themepark";
		// public static String SERVER_API_KEY =
		// "8608d007f1789d881036fd7cd5e42ca91119e7df";

		/**
		 * 用户是否登录
		 */
		public static boolean IS_LOGIN = false;

		/**
		 * 下载客户端本地存储位置
		 */
		public static final String DOWNLOAD_APP_PATH = SD_PATH + "/com/higgses/wifi/update/";

		/**
		 * 下载客户端本地存储位置
		 */
		public static final String DOWNLOAD_APP_FILE_PATH = DOWNLOAD_APP_PATH + "/chudian.apk";

		/**
		 * 获取用户信息
		 *
		 * @param context
		 *
		 * @return
		 */
		public static UserBean getUser(Context context) {
			UserBean userEntity;
			try {
				SharedPreferences spf = context.getSharedPreferences(SharedConstant.App.APP_SP_NAME,
						Context.MODE_PRIVATE);
				String user = spf.getString("userEntity", "");
				userEntity = (UserBean) BeanUtil.unSerializable(user).get(0);
				IS_LOGIN = true;
			} catch (Exception e) {
				userEntity = null;
			}

			return userEntity;
		}

		/**
		 * 保存用户个人信息到本地
		 *
		 * @param userEntity
		 *            登录用户实体对象
		 */
		public static void saveUser(Context context, UserBean userEntity) {
			SharedPreferences spf = context.getSharedPreferences(SharedConstant.App.APP_SP_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = spf.edit();
			try {
				String user = BeanUtil.serializable(userEntity);
				editor.putString("userEntity", user);
			} catch (IOException e) {
			}
			editor.commit();
		}

		/**
		 * 清除用户登录信息
		 *
		 * @param context
		 *            Context
		 */
		public static void clearUser(Context context) {
			SharedPreferences spf = context.getSharedPreferences(SharedConstant.App.APP_SP_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = spf.edit();
			editor.putString("userEntity", "");
			IS_LOGIN = false;
			editor.commit();
		}

		/**
		 * 获取本地存储的Token
		 *
		 * @param context
		 *
		 * @return
		 */
		public static String getToken(Context context) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(SharedConstant.App.APP_SP_NAME,
					Context.MODE_PRIVATE);
			return sharedPreferences.getString(SharedConstant.App.TOKEN, "");
		}

		/**
		 * 是否需要重新获取Token
		 *
		 * @param context
		 *
		 * @return
		 */
		public static boolean isNeedToGetToken(Context context) {
			if (context == null) {
				return false;
			}
			SharedPreferences sharedPreferences = context.getSharedPreferences(SharedConstant.App.APP_SP_NAME,
					Context.MODE_PRIVATE);
			String token = sharedPreferences.getString(SharedConstant.App.TOKEN, "");
			long tokenTime = sharedPreferences.getLong(SharedConstant.App.TOKEN_TIME, 0);
			if ("".equals(token)) {
				return true;
			}

			long currentTime = System.currentTimeMillis();
			if (currentTime > tokenTime * 1000) {
				return true;
			}

			return false;
		}
	}

	/**
	 * 广播
	 */
	public static class Broadcast {
		
	}

	/**
	 * 事件
	 */
	public class Event {
		
	}
}
