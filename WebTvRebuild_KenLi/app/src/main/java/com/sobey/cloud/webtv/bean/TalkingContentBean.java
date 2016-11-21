package com.sobey.cloud.webtv.bean;

/**
 * 私信每条消息的具体内容
 * 
 * @author Administrator
 *
 */
public class TalkingContentBean {
	private String talk_id;
	private String new_num;
	private String uid;
	private String nickname;
	private String head;
	private String sex;
	private String friend_id;
	private String friend_nickname;
	private String friend_head;
	private String friend_sex;
	private String last_message;
	private String last_mesage_time;

	public String getTalk_id() {
		return talk_id;
	}

	public void setTalk_id(String talk_id) {
		this.talk_id = talk_id;
	}

	public String getNew_num() {
		return new_num;
	}

	public void setNew_num(String new_num) {
		this.new_num = new_num;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getFriend_id() {
		return friend_id;
	}

	public void setFriend_id(String friend_id) {
		this.friend_id = friend_id;
	}

	public String getFriend_nickname() {
		return friend_nickname;
	}

	public void setFriend_nickname(String friend_nickname) {
		this.friend_nickname = friend_nickname;
	}

	public String getFriend_head() {
		return friend_head;
	}

	public void setFriend_head(String friend_head) {
		this.friend_head = friend_head;
	}

	public String getFriend_sex() {
		return friend_sex;
	}

	public void setFriend_sex(String friend_sex) {
		this.friend_sex = friend_sex;
	}

	public String getLast_message() {
		return last_message;
	}

	public void setLast_message(String last_message) {
		this.last_message = last_message;
	}

	public String getLast_mesage_time() {
		return last_mesage_time;
	}

	public void setLast_mesage_time(String last_mesage_time) {
		this.last_mesage_time = last_mesage_time;
	}

}
