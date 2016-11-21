package com.sobey.cloud.webtv.bean;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSONArray;

/**
 * 我的关注Bean
 * 
 * @author Administrator
 *
 */
public class MyGuanZhuBean {
	private String friend_count;
	private List<FriendsBean> fdList;

	public String getFriend_count() {
		return friend_count;
	}

	public void setFriend_count(String friend_count) {
		this.friend_count = friend_count;
	}

	public List<FriendsBean> getFdList() {
		return fdList;
	}

	public void setFdList(List<FriendsBean> fdList) {
		this.fdList = fdList;
	}

}
