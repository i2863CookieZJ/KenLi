package com.sobey.cloud.webtv.bean;

import java.util.List;

/**
 * 私信列表bean
 * 
 * @author Administrator
 *
 */
public class LettersBean {
	private String talkCount;
	private List<TaskList> talkList;

	public String getTalkCount() {
		return talkCount;
	}

	public void setTalkCount(String talkCount) {
		this.talkCount = talkCount;
	}

	public List<TaskList> getTalkList() {
		return talkList;
	}

	public void setTalkList(List<TaskList> talkList) {
		this.talkList = talkList;
	}

	public class TaskList {
		private String talkid;
		private String newNum;
		private String uid;
		private String nickname;
		private String head;
		private String sex;
		private String friendid;
		private String friendnickname;
		private String friendhead;
		private String friendsex;
		private String lastmessage;
		private String lastmesagetime;

		public String getTalkid() {
			return talkid;
		}

		public void setTalkid(String talkid) {
			this.talkid = talkid;
		}

		public String getNewNum() {
			return newNum;
		}

		public void setNewNum(String newNum) {
			this.newNum = newNum;
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

		public String getFriendid() {
			return friendid;
		}

		public void setFriendid(String friendid) {
			this.friendid = friendid;
		}

		public String getFriendnickname() {
			return friendnickname;
		}

		public void setFriendnickname(String friendnickname) {
			this.friendnickname = friendnickname;
		}

		public String getFriendhead() {
			return friendhead;
		}

		public void setFriendhead(String friendhead) {
			this.friendhead = friendhead;
		}

		public String getFriendsex() {
			return friendsex;
		}

		public void setFriendsex(String friendsex) {
			this.friendsex = friendsex;
		}

		public String getLastmessage() {
			return lastmessage;
		}

		public void setLastmessage(String lastmessage) {
			this.lastmessage = lastmessage;
		}

		public String getLastmesagetime() {
			return lastmesagetime;
		}

		public void setLastmesagetime(String lastmesagetime) {
			this.lastmesagetime = lastmesagetime;
		}

	}
}
