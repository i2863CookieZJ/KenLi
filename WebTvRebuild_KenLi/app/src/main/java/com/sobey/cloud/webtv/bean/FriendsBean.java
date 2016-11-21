package com.sobey.cloud.webtv.bean;

/**
 * 关注的好友bean
 * 
 * @author Administrator
 *
 */
public class FriendsBean {
	private String friend_id;//
	private String nickname;
	private String head;
	private String sex;

	public String getFriend_id() {
		return friend_id;
	}

	public void setFriend_id(String friend_id) {
		this.friend_id = friend_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

}
