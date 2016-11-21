package com.sobey.cloud.webtv.web;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.sobey.cloud.webtv.config.ServerConfig;

/**
 * 访问网络工具类
 *
 * @author lazy
 *         <p/>
 *         Create by LiuBin on 14-04-02
 */
public class Network {
	@SuppressWarnings("unused")
	private final static String TAG = "WiFiWebUtil";
	private static Network mInstance;

	private Network() {

	}

	/**
	 * @return WiFiWebUtil
	 */
	public static Network getInstance() {
		if (mInstance == null) {
			synchronized (Network.class) {
				if (mInstance == null) {
					mInstance = new Network();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 返回请求的真实地址
	 *
	 * @param url
	 *
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
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	private String parseUrl(String url, Map<String, String> params) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append(url + "?");

		for (Entry<String, String> param : params.entrySet()) {
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
}
