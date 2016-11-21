package com.sobey.cloud.webtv.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LogUtil {

	/**
	 * 
	 * 
	 * @param str
	 * @return
	 */
	public static String HtmlToText(String str) {
		str = str.replace("<br />", "\n");
		str = str.replace("<br/>", "\n");
		str = str.replace("&nbsp;&nbsp;", "\t");
		str = str.replace("&nbsp;", " ");
		str = str.replace("&#39;", "\\");
		str = str.replace("&quot;", "\\");
		str = str.replace("&gt;", ">");
		str = str.replace("&lt;", "<");
		str = str.replace("&amp;", "&");

		return str;
	}

	static final Pattern patternHtml = Pattern.compile("<.+?>");

	/**
	 * �Ƴ�html���
	 * 
	 * @param html
	 * @return
	 */
	public static String RemoveHtmlTag(String html) {
		Matcher m = patternHtml.matcher(html);
		while (m.find()) {
			html = m.replaceAll("");
		}
		return html;
	}

	/**
	 * �ж��Ƿ���ͼƬ����
	 * 
	 * @param html
	 * @return
	 */
	static final Pattern patternImg = Pattern.compile("<img(.+?)src=\"(.+?)\"(.+?)(onload=\"(.+?)\")?([^\"]+?)>");

	public static boolean IsContainImg(String html) {
		Matcher m = patternImg.matcher(html);
		while (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * �Ƴ�ͼƬ���
	 * 
	 * @param html
	 * @return
	 */
	public static String RemoveImgTag(String html) {
		Matcher m = patternImg.matcher(html);
		while (m.find()) {
			html = m.replaceAll("");
		}
		return html;
	}

	/**
	 * �滻ͼƬ���
	 * 
	 * @param html
	 * @return
	 */
	static final Pattern patternImgSrc = Pattern.compile("<img(.+?)src=\"(.+?)\"(.+?)>");

	public static String ReplaceImgTag(String html) {
		Matcher m = patternImgSrc.matcher(html);
		while (m.find()) {
			html = m.replaceAll("��<a href=\"$2\">����鿴ͼƬ</a>��");
		}
		return html;
	}

	/**
	 * �Ƴ���Ƶ���
	 */
	static final Pattern patternVideo = Pattern
			.compile("<object(.+?)>(.*?)<param name=\"src\" value=\"(.+?)\"(.+?)>(.+?)</object>");

	public static String RemoveVideoTag(String html) {
		Matcher m = patternVideo.matcher(html);
		while (m.find()) {
			html = m.replaceAll("");
		}
		return html;
	}

	/**
	 * �滻��Ƶ���
	 */
	static final Pattern patternVideoSrc = Pattern
			.compile("<object(.+?)>(.*?)<param name=\"src\" value=\"(.+?)\"(.+?)>(.+?)</object>");

	public static String ReplaceVideoTag(String html) {
		Matcher m = patternVideoSrc.matcher(html);
		while (m.find()) {
			html = m.replaceAll("��<a href=\"$3\">����鿴��Ƶ</a>��");
		}
		return html;
	}

	public static String getErrorStack(Exception e) {
		String error = null;
		if (e != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				e.printStackTrace(ps);
				error = baos.toString();
				baos.close();
				ps.close();
			} catch (Exception e1) {
				error = e.toString();
			}
		}
		return error;
	}

	public static Map<String, Object> json2Map(JSONObject obj) throws JSONException {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = obj.keys();
		Map<String, Object> map = new HashMap<String, Object>();
		while (keys.hasNext()) {
			String key = keys.next();
			Object val = obj.get(key);
			if (val instanceof JSONArray) {
				JSONArray list = (JSONArray) val;
				List<Object> ms = new ArrayList<Object>();
				for (int i = 0, l = list.length(); i < l; i++) {
					JSONObject o = list.getJSONObject(i);
					@SuppressWarnings("unchecked")
					Iterator<String> os = o.keys();
					Map<String, Object> li = new HashMap<String, Object>();
					while (os.hasNext()) {
						String k = os.next();
						Object va = o.get(k);
						li.put(k, va);
					}
					ms.add(li);
				}
				map.put(key, ms);
			} else {
				map.put(key, val);
			}
		}
		return map;
	}

	public static List<Map<String, Object>> json2ListMap(JSONArray obj) throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (obj != null && obj.length() > 0) {
			for (int i = 0, l = obj.length(); i < l; i++) {
				JSONObject o = obj.getJSONObject(i);
				@SuppressWarnings("unchecked")
				Iterator<String> keys = o.keys();
				Map<String, Object> map = new HashMap<String, Object>();
				while (keys.hasNext()) {
					String key = keys.next();
					map.put(key, o.get(key));
				}
				list.add(map);
			}
		}
		return list;
	}

	public static byte[] toByteArray(Object obj) throws Exception {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			return bytes;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (oos != null) {
				oos.close();
				oos = null;
			}
			if (bos != null) {
				bos.close();
				bos = null;
			}

		}
	}

	public static Object toObject(byte[] bytes) throws Exception {
		Object obj = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			return obj;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (ois != null) {
				ois.close();
			}
		}
	}

	/**
	 * ��ȡ�����Ƿ����״̬
	 * 
	 * @return
	 */
	public static boolean networkIsAvailable(Context context) {
		try {
			ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info == null) {
				return false;
			}
			if (info.isConnected()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
