package com.sobey.cloud.webtv.fragment.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.higgses.griffin.log.GinLog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class JsonUtil {
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);

	/**
	 * 将map数据解析出来，并拼接成json字符串
	 * 
	 * @param map
	 * @return
	 */
	public static JSONObject setJosn(Map<String, String> map) throws Exception {
		JSONObject json = null;
		StringBuffer temp = new StringBuffer();
		if (!map.isEmpty()) {
			temp.append("{");

			// 遍历map
			Set set = map.entrySet();
			Iterator i = set.iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				temp.append("'" + key + "':");
				temp.append("'" + value + "',");
			}
			if (temp.length() > 1) {
				temp = new StringBuffer(temp.substring(0, temp.length() - 1));
			}
			temp.append("}");
			json = new JSONObject(temp.toString());
		}
		return json;
	}

	/**
	 * 将json字符串解析，并放置到map中
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, String> getJosn(String jsonStr) throws Exception {
		Map<String, String> map = null;
		if (!TextUtils.isEmpty(jsonStr)) {
			map = new HashMap<String, String>();
			JSONObject json = new JSONObject(jsonStr);
			Iterator i = json.keys();
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = json.getString(key);
				map.put(key, value);
			}
		}
		return map;
	}

	public static void asynTask(final Context frame, final IAsynTask task) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String dataType = data.getString("IAsynTaskResult");
				Serializable ser = data.getSerializable("IAsynTaskRunData");
				if ("success".equals(dataType))
					task.updateUI(ser);
				else if ("error".equals(dataType)) {
					Toast.makeText(frame, "网络异常，请稍候再试！", Toast.LENGTH_SHORT).show();
					System.err.println("--------------异步任务错误！-------------");
					if (null == ser)
						Log.e("Util异步任务错误！", ((Throwable) ser) + "");
					else
						((Throwable) ser).printStackTrace();
				} else {
					task.updateUI(null);
				}
			}
		};

		executorService.execute(new Runnable() {
			// new Thread(new Runnable(){
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				try {
					data.putSerializable("IAsynTaskRunData", task.run());
					data.putString("IAsynTaskResult", "success");
				} catch (Throwable e) {
					GinLog.e("JsonUtil", "------------------异步任务错误！-----------------", e);
					data.putSerializable("IAsynTaskRunData", e);
					data.putString("IAsynTaskResult", "error");
				}
				msg.setData(data);
				handler.sendMessage(msg);
			}
		});
	}
}
