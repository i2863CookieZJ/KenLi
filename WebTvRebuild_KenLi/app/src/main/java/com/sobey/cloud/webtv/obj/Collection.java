package com.sobey.cloud.webtv.obj;

public class Collection {
	/**
	 * [{"id":"695","username":"1371150551@qq.com","aid":"26868061",
	 * "siteid":"46","title":"刘耀辉：掌舵市政协8年 曾经一战成名",
	 * "addtime":"2015-02-05 10:14:09",
	 * "logo":"http://www.sobeycloud.com/hzwldst/upload/Image/mrtp//2015/02/05/1_c7f429b7acbd4137ae788a3d4623565e.jpg",
	 * "summary":"工作会议在博罗县城召开。省委副书记、省长朱森林参加大会，刘耀辉在会上作了经验介绍。一个多月后，省人大常委会主任林若在惠州市委书记朱友植的陪同下到博罗视察，对县委第一把手亲自抓整治社会治安的做法给予了肯定和赞赏。 刘耀辉“一战成名”。后来在市政协主席任上，他仍多次对早年在博罗的社会综合治理实践引以为傲。 自称讲话材料基本自己动笔 刘耀辉干了4年多才离开博罗，出任市委宣传部长，这是他仕途中最稳定的时期。",
	 * "type":"1","count":"1"}]
	 * [{"returncode":"SUCCESS","returnmsg":"删除成功"}]
	 * {"subjectReplyCount":"2","publishTime":"2015-03-23 16:12:42","subjectTitle":"保养一次要花一千多，大家通常花

多少？","subjectLikeCount":null,"publishUserId":"14","subjectId":"140","publishUserName":"西山居上",
"subjectContent":"买车

两年多了，是福克斯，每次都要1000多，前天去保养又花了1200多，感觉是真贵，又不是啥豪车。朋友们说说您的车保养要多少钱哪，再换

车时好有个参考。谢谢啊\r\n\r\n\r\n\r\n","subjectPicUrls":"\/data\/attachment\/forum

\/201503\/23\/161230eo4q5ieeim4siek7.png","publishUserHeadUrl":"\/uc_server\/avatar.php?uid=14&size=small"}
	 * 
	 * 
	 */
	private String returnCode;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	private String subjectReplyCount;
	private String publishTime;
	private String subjectTitle;
	private String subjectContent;
	private String subjectPicUrls;
	private String publishUserHeadUrl;
	private String subjectId;
	private String publishUserId;
	private String subjectLikeCount;
	private String publishUserName;
	private String id;
	private String username;
	private String aid;
	private String siteid;
	private String title;
	private String addtime;
	private String logo;
	private String summary;
	private String type;
	private String count;
	private int delete=0;
	private String returncode;
	private String returnmsg;
	
	private String groupId;
	private String groupName;
	private String groupInfo;
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupInfo() {
		return groupInfo;
	}
	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}
	public String getSubjectReplyCount() {
		return subjectReplyCount;
	}
	public void setSubjectReplyCount(String subjectReplyCount) {
		this.subjectReplyCount = subjectReplyCount;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getSubjectTitle() {
		return subjectTitle;
	}
	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}
	public String getSubjectContent() {
		return subjectContent;
	}
	public void setSubjectContent(String subjectContent) {
		this.subjectContent = subjectContent;
	}
	public String getSubjectPicUrls() {
		return subjectPicUrls;
	}
	public void setSubjectPicUrls(String subjectPicUrls) {
		this.subjectPicUrls = subjectPicUrls;
	}
	public String getPublishUserHeadUrl() {
		return publishUserHeadUrl;
	}
	public void setPublishUserHeadUrl(String publishUserHeadUrl) {
		this.publishUserHeadUrl = publishUserHeadUrl;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getPublishUserId() {
		return publishUserId;
	}
	public void setPublishUserId(String publishUserId) {
		this.publishUserId = publishUserId;
	}
	public String getSubjectLikeCount() {
		return subjectLikeCount;
	}
	public void setSubjectLikeCount(String subjectLikeCount) {
		this.subjectLikeCount = subjectLikeCount;
	}
	public String getPublishUserName() {
		return publishUserName;
	}
	public void setPublishUserName(String publishUserName) {
		this.publishUserName = publishUserName;
	}
	public String getReturncode() {
		return returncode;
	}
	public void setReturncode(String returncode) {
		this.returncode = returncode;
	}
	public String getReturnmsg() {
		return returnmsg;
	}
	public void setReturnmsg(String returnmsg) {
		this.returnmsg = returnmsg;
	}
	public int getDelete() {
		return delete;
	}
	public void setDelete(int delete) {
		this.delete = delete;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getSiteid() {
		return siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
}
