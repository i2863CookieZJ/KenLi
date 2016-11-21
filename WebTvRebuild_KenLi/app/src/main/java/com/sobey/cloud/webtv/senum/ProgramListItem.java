package com.sobey.cloud.webtv.senum;

import java.util.Date;

import com.dylan.common.utils.DateParse;

public class ProgramListItem {

	private String Name;
	private int isPlaying;
	private String Description;
	private String Length;
	private int Id;
	private String PlayTime;
	private String starttime;
	private String endtime;
	private String time;
	private String url;
	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public int getIsPlaying() {
		return isPlaying;
	}


	public void setIsPlaying(int isPlaying) {
		this.isPlaying = isPlaying;
	}


	public String getDescription() {
		return Description;
	}


	public void setDescription(String description) {
		Description = description;
	}


	public String getLength() {
		return Length;
	}


	public void setLength(String length) {
		Length = length;
	}


	public int getId() {
		return Id;
	}


	public void setId(int id) {
		Id = id;
	}


	public String getPlayTime() {
		return PlayTime;
	}


	public void setPlayTime(String playTime) {
		PlayTime = playTime;
	}


	public String getStarttime() {
		return starttime;
	}


	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}


	public String getEndtime() {
		return endtime;
	}


	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getChannelid() {
		return channelid;
	}


	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}


	private String channelid;
	
	
	public ProgramListItem() {
	}
	
	public static enum GuideItemState 
	{
		REPLAY, LIVE, COMING_SOON
	}
	/***
	 * 得到当前项是什么个状态
	 * @param object
	 * @return
	 */
	public static GuideItemState getItemState(ProgramListItem item) {
		Date startTime;
		try {
			startTime = DateParse.parseDate(item.getStarttime(), null);
			Date endTime = DateParse.parseDate(item.getEndtime(), null);
			Date nowTime = new Date(System.currentTimeMillis());
			if (nowTime.getTime() > endTime.getTime()) 
			{
				return GuideItemState.REPLAY;
			} 
			else if (nowTime.getTime() < startTime.getTime()) 
			{
				return GuideItemState.COMING_SOON;
			} 
			else 
			{
				return GuideItemState.LIVE;
			}
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	public static class Msg
	{
		public int index;
		public ProgramListItem item;
	}

}
