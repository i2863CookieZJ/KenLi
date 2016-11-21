package com.sobey.cloud.webtv.utils;

import java.util.Set;

import com.sobey.cloud.webtv.app.ExceptionApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 用户偏好设置工具
 * 
 * @author bin
 */
public class PreferencesUtil {
	/**
	 * 清楚所有信息
	 */
	public static void clear() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().clear().commit();
	}

	public static final String NAME = "SOBEY";
	public static final String KEY_FOLLOW_STATE_CHANGED = "key_follow_state_change";
	public static final String KEY_SEARCH_GROUP_RECORD = "key_search_group_record";
	public static final String KEY_SEARCH_SUBJECT_RECORD = "key_search_subject_record";
	public static final String KEY_COMMENT_LIKE_COUNT = "key_comment_like_count_";
	public static final String KEY_COMMENT_IS_LIKED = "key_comment_is_liked_";
	public static final String KEY_COMMENT_SEND_TIME = "key_comment_send_TIME_";

	public static void remove(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().remove(key).commit();
	}

	public static void putInt(String key, int value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putInt(key, value).commit();
	}

	public static void putLong(String key, long value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putLong(key, value).commit();
	}

	public static int getInt(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getInt(key, 0);
	}

	public static long getLong(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getLong(key, 0);
	}

	public static void putString(String key, String value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	public static String getString(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}

	@SuppressLint("NewApi")
	public static void putStringSet(String key, Set<String> values) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putStringSet(key, values);
	}

	@SuppressLint("NewApi")
	public static Set<String> getStringSet(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getStringSet(key, null);
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}

	/**
	 * 存储当前的versionCode到share中
	 * 
	 * @param value
	 */
	public static void putVersionCode(int value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putInt("versionCode", value).commit();
	}

	/**
	 * 获取share中存储的versionCode
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return preferences.getInt("versionCode", 0);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void putLoginStatus(String key, int value) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putInt(key, value).commit();
	}

	/**
	 * 获取登录状态
	 * 
	 * @param key
	 * @return
	 */
	public static int getLoginStatus(String key) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getInt(key, 0);
	}

	/**
	 * @param userId
	 *            用户登录成功后返回的userId
	 */
	public static void putLoggedUserId(int userId) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putInt("userId", userId).commit();
	}

	/**
	 * 获取用户登录成功后返回的userId
	 * 
	 * @return
	 */
	public static String getLoggedUserId() {
		SharedPreferences settings = ExceptionApplication.app.getSharedPreferences("user_info", 0);
		String uid = settings.getString("uid", "");
		return uid;

	}

	/**
	 * 
	 * @param token
	 *            用户登录成功后返回的token（服务器）
	 */
	public static void putLoggedToken(String token) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putString("token", token).commit();
	}

	/**
	 * 
	 * @param username
	 *            用户授权登录后返回的username
	 */
	public static void putLoggedUserName(String username) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putString("username", username).commit();
	}

	/**
	 * 
	 * @return 用户登录成功后的token（服务器）
	 */
	public static String getLoggedToken() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getString("token", "");
	}

	/**
	 * 
	 * @return username 用户授权登录后返回的username
	 */
	public static String getLoggedUserName() {
		SharedPreferences settings = ExceptionApplication.app.getSharedPreferences("user_info", 0);
		String userName = settings.getString("nickname", "");
		if (TextUtils.isEmpty(userName)) {
			userName = settings.getString("id", "");
		}
		return userName;
	}

	/**
	 * 
	 * @param bindFrom
	 *            登录方式（qqzone sina）
	 */
	public static void putLoggedBindFrom(String bindFrom) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putString("bindFrom", bindFrom).commit();
	}

	/**
	 * 
	 * @return 登录方式（qqzone sina）
	 */
	public static String getLoggedBindFrom() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getString("bindFrom", "");
	}

	/**
	 * 获取用户订单
	 * 
	 * @return
	 */
	public static String getOrderNumbers() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getString("orderNums", "");
	}

	/**
	 * 添加用户订单
	 * 
	 * @param orderNumber
	 */
	public static void putOrderNumbers(String orderNumber) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		if (TextUtils.isEmpty(getOrderNumbers())) {
			preferences.edit().putString("orderNums", orderNumber).commit();
		} else {
			String org = getOrderNumbers();
			StringBuilder builder = new StringBuilder();
			String orderNums = builder.append(org).append(",").append(orderNumber).toString();
			preferences.edit().putString("orderNums", orderNums).commit();
		}
	}

	/**
	 * 是否显示新手引导页
	 * 
	 * @return
	 */
	public static boolean isShowGuide() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getBoolean("showGuide", false);
	}

	/**
	 * 存储是否显示新手引导
	 * 
	 * @param showGuide
	 */
	public static void putShowGuide(boolean showGuide) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putBoolean("showGuide", showGuide).commit();
	}

	/**
	 * 是否需要推送
	 * 
	 * @return
	 */
	public static boolean isNeedPush() {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		return preferences.getBoolean("needPush", true);
	}

	/**
	 * 存储是否需要推送
	 * 
	 * @param needPush
	 */
	public static void putNeedPush(boolean needPush) {
		SharedPreferences preferences = ExceptionApplication.app.getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferences.edit().putBoolean("needPush", needPush).commit();
	}

	// public static void saveMyCollectionInfo(int gourmetId){
	// StringBuilder builder = new StringBuilder();
	// String city = PreferencesUtil.getString(PreferencesUtil.KEY_CACHE_CITY);
	// String date = Utils.formatCurrentTime("yyyy-MM-dd");
	// String time = Utils.formatCurrentTime("HH:mm");
	// long currentTimeMillis = System.currentTimeMillis();
	// builder.append(city).append(";").append(date).append(";")
	// .append(time).append(";").append(currentTimeMillis+"");
	// String value = builder.toString();
	// PreferencesUtil.putString(PreferencesUtil.KEY_COLLECTION_INFO+gourmetId,
	// value);
	// }
	// /**
	// * 获取我的收藏相关信息
	// * @param gourmetId
	// * @return
	// */
	// public static String[] getMyCollectionInfo(int gourmetId){
	// String info = getString(PreferencesUtil.KEY_COLLECTION_INFO+gourmetId);
	// String[] infos = info.split(";");
	// return infos;
	// }

}