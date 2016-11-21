package com.sobey.cloud.webtv.model.provide;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sobey.cloud.webtv.config.AppConfig;
import com.sobey.cloud.webtv.config.HttpCode;
import com.sobey.cloud.webtv.config.ServerConfig;
import com.sobey.cloud.webtv.model.provide.http.HttpUtils;
import com.sobey.cloud.webtv.model.provide.http.inf.IResponse;

import android.content.Context;

/**
 * 请求网络数据
 */
public class ServerData {
	private static ServerData mServerData;
	private static Context mContext;
	public static final String CACHE_KEY = "cache_key";

	private ServerData(Context context) {
		mContext = context;
	}

	public static void init(Context context) {
		if (mServerData == null) {
			mServerData = new ServerData(context);
		}
	}

	private static boolean init() {
		if (mServerData == null || mContext == null) {
			try {
				throw new Exception("ServerData没有初始化，请先在Application中调用:ServerData.init(this)初始化！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 请求是否成功
	 *
	 * @param jsonObject
	 * @return
	 */
	public static boolean isRequestSuccess(JSONObject jsonObject) {
		String status = null;
		try {
			status = jsonObject.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return HttpCode.REQUEST_SUCCESS.equals(status);
	}

	/**
	 * 组合通用参数
	 *
	 * @param params
	 * @return
	 */
	public static Map<String, String> getParams(Map<String, String> params) {
		handlerParams(params);
		return params;
	}

	public static Map<String, Object> getParams() {
		Map<String, String> tmpParams = handlerParams(new HashMap<String, String>());
		return new HashMap<String, Object>(tmpParams);
	}

	private static Map<String, String> handlerParams(Map<String, String> params) {
		return params;
	}

	/**
	 * Header
	 *
	 * @return
	 */
	public static Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", AppConfig.App.getToken(mContext));
		return headers;
	}

	/**
	 * 返回请求的真实地址
	 *
	 * @param url
	 * @return
	 */
	private static String getUrl(String url) {
		return ServerConfig.url(url);
	}

	/**
	 * Get 方法组装Url访问请求
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private static String parseUrl(String url, LinkedHashMap<String, String> params) throws Exception {
		StringBuilder sb = new StringBuilder();

		if (params.size() == 0) {
			return url;
		}

		sb.append(url);

		if (!url.contains("?")) {
			sb.append(url + "?");
		} else {
			sb.append("&");
		}
		for (Map.Entry<String, String> param : params.entrySet()) {
			String key = param.getKey();
			String value = param.getValue();
			sb.append(key);
			sb.append("=");
			sb.append(URLEncoder.encode(value == null ? "" : value, "UTF-8"));
			sb.append("&");
		}
		if (params.size() > 0) {
			return sb.substring(0, sb.length() - 1);
		}

		return sb.toString();
	}

	/**
	 * 缓存数据
	 *
	 * @param url
	 * @param data
	 */
	public static void cacheData(String url, String data) {
		CacheUtils.addFileCache(mContext, url, data);
	}

	/**
	 * 获取缓存数据
	 *
	 * @param url
	 * @param type
	 * @return
	 */
	public static <T> T loadCacheData(String url, Type type) {
		return CacheUtils.loadDataEntity(mContext, url, type);
	}

	public static String loadCacheData(String url) {
		return CacheUtils.loadStringData(mContext, url);
	}

	/**
	 * 请求服务器数据
	 *
	 * @param params
	 * @param response
	 * @param url
	 */
	private static void requestServerData(String key, Map<String, String> params, IResponse<?> response, String url) {
		if (init()) {
			HttpUtils.postFromCache(mContext, key, getUrl(url), getParams(params), response);
		}
	}

	private static void requestServerData(Map<String, String> params, IResponse<?> response, String url) {
		String key = "";
		if (params.containsKey(CACHE_KEY)) {// 获取缓存数据的额外key标记
			key = params.get(CACHE_KEY);
			params.remove(CACHE_KEY);
		} else {
			key = params.toString();
		}
		requestServerData(key, params, response, url);
	}

	/**
	 * 请求服务器数据，不带缓存
	 *
	 * @param params
	 * @param response
	 * @param url
	 */
	private static void requestServerDataNoCache(Map<String, String> params, IResponse<?> response, String url) {
		if (init()) {
			HttpUtils.post(mContext, getUrl(url), getParams(params), response);
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Post,Json)
	 *
	 * @param jsonString
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByPost(String jsonString, IResponse<?> response, String url,
			boolean isVerify) {
		if (init()) {
			if (isVerify) {
				HttpUtils.post(mContext, getUrl(url), jsonString, null, getHeaders(), response);
			} else {
				HttpUtils.post(mContext, getUrl(url), jsonString, response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Post,Json)
	 *
	 * @param jsonString
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByPostAndTag(String jsonString, IResponse<?> response, String url,
			boolean isVerify, String tag) {
		if (init()) {
			if (isVerify) {
				HttpUtils.post(mContext, tag, getUrl(url), jsonString, null, getHeaders(), response);
			} else {
				HttpUtils.post(mContext, tag, getUrl(url), jsonString, response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json),默认不验证
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGet(LinkedHashMap<String, String> params, IResponse<?> response,
			String url) throws Exception {
		requestServerDataNoCacheByGet(params, response, url, false);
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json)
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGet(LinkedHashMap<String, String> params, IResponse<?> response,
			String url, boolean isVerify) throws Exception {
		if (init()) {
			if (isVerify) {
				HttpUtils.get(mContext, parseUrl(getUrl(url), params), getHeaders(), response);
			} else {
				HttpUtils.get(mContext, parseUrl(getUrl(url), params), response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json)
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGetAndNotGetUrl(LinkedHashMap<String, String> params,
			IResponse<?> response, String url, boolean isVerify) throws Exception {
		if (init()) {
			if (isVerify) {
				HttpUtils.get(mContext, parseUrl(url, params), getHeaders(), response);
			} else {
				HttpUtils.get(mContext, parseUrl(url, params), response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json)
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGetAndNotGetUrl(LinkedHashMap<String, String> params,
			IResponse<?> response, String url) throws Exception {
		requestServerDataNoCacheByGetAndNotGetUrl(params, response, url, false);
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json)
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGet4Authentication(LinkedHashMap<String, String> params,
			IResponse<?> response, String url, boolean isVerify) throws Exception {
		if (init()) {
			if (isVerify) {
				HttpUtils.get(mContext, parseUrl(url, params), getHeaders(), response);
			} else {
				HttpUtils.get(mContext, parseUrl(url, params), response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Get,Json)
	 *
	 * @param params
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCacheByGetAndTag(LinkedHashMap<String, String> params, IResponse<?> response,
			String url, boolean isVerify, String tag) throws Exception {
		if (init()) {
			if (isVerify) {
				HttpUtils.get(mContext, tag, parseUrl(getUrl(url), params), getHeaders(), response);
			} else {
				HttpUtils.get(mContext, tag, parseUrl(getUrl(url), params), response);
			}
		}
	}

	/**
	 * 请求服务器数据,不带缓存(Json)
	 *
	 * @param jsonString
	 * @param clazz
	 * @param response
	 * @param url
	 * @param isVerify
	 */
	private static void requestServerDataNoCache(String jsonString, Class<?> clazz, IResponse<?> response, String url,
			boolean isVerify) {
		if (init()) {
			if (isVerify) {
				HttpUtils.post(mContext, getUrl(url), jsonString, clazz, getHeaders(), response);
			} else {
				HttpUtils.post(mContext, getUrl(url), jsonString, clazz, response);
			}
		}
	}

	/**
	 * 
	 *
	 * @param jsonObject
	 * @param response
	 */
	public static void testPost(JSONObject jsonObject, IResponse<?> response) {
		requestServerDataNoCacheByPost(jsonObject.toString(), response, ServerConfig.LOGOUT, true);
	}

	/**
	 * 登录
	 *
	 * @param params
	 * @param response
	 */
	public static void login(LinkedHashMap<String, String> params, IResponse<?> response) throws Exception {
		requestServerDataNoCacheByGetAndNotGetUrl(params, response, ServerConfig.LOGIN);
	}

	/**
	 * 退出登录
	 *
	 * @param params
	 * @param response
	 */
	public static void logout(LinkedHashMap<String, String> params, IResponse<?> response) throws Exception {
		requestServerDataNoCacheByGet(params, response, ServerConfig.LOGOUT);
	}

	/**
	 * 支付
	 *
	 * @param params
	 * @param response
	 */
	public static void pay(LinkedHashMap<String, String> params, IResponse<?> response) throws Exception {
		requestServerDataNoCacheByGet(params, response, ServerConfig.PAY);
	}

}
