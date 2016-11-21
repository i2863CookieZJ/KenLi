package com.sobey.cloud.webtv.bean;

import java.util.List;

/**
 * 私信列表
 * 
 * @author Administrator
 *
 */
public class TalkListBean {
	private String talkcount;
	private List<TalkingContentBean> tcbList;

	public String getTalkcount() {
		return talkcount;
	}

	public void setTalkcount(String talkcount) {
		this.talkcount = talkcount;
	}

	public List<TalkingContentBean> getTcbList() {
		return tcbList;
	}

	public void setTcbList(List<TalkingContentBean> tcbList) {
		this.tcbList = tcbList;
	}

}
