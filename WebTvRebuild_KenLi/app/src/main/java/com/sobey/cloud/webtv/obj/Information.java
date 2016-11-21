package com.sobey.cloud.webtv.obj;
/**
 * 资讯
 * @author lgx
 *
 */
public class Information {
	/**
	 * "id":"2923320","title":"w112w12",
	 * "logo":"http://demo.sobeycloud.com/WebTVyssjz/upload/Image/nopicture.jpg"
	 */
	private String id;
	private String title;
	private String logo;
	private String state;
	private String type;
	private String hitcount;
	private String duration;
	private String commcount;
	private String content;
	private String name;
	private String channels;
	private String vod;
	private String comment;
	private String parentid;
	private String summary;
	private String refername;
	public String getChannels() {
		return channels;
	}
	public void setChannels(String channels) {
		this.channels = channels;
	}
	public String getVod() {
		return vod;
	}
	public void setVod(String vod) {
		this.vod = vod;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHitcount() {
		return hitcount;
	}
	public void setHitcount(String hitcount) {
		this.hitcount = hitcount;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCommcount() {
		return commcount;
	}
	public void setCommcount(String commcount) {
		this.commcount = commcount;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSummary(String summary)
	{
		this.summary=summary;
	}
	public String getSummary()
	{
		return this.summary;
	}
	public String getRefername() {
		return refername;
	}
	public void setRefername(String refername) {
		this.refername = refername;
	}
	
}
