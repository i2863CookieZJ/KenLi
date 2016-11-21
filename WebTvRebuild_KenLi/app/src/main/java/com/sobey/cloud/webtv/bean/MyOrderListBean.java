package com.sobey.cloud.webtv.bean;

import org.json.JSONArray;

public class MyOrderListBean {

	private String realFreight;
	private String id;
	private String sendTime;
	private String status;// 订单状态
	private String completionTime;
	private JSONArray goods;
	private String realAmount;// 总金额
	private String payableFreight;
	private String createTime;
	private String payableAmount;
	private String payTime;
	private String orderNo;// 订单号
	private String payType;// 0是货到付款
	private String order_comment_status;// 订单评论状态：0-评价；1-已评价（订单中所有商品都已经评价）

	public String getOrder_comment_status() {
		return order_comment_status;
	}

	public void setOrder_comment_status(String order_comment_status) {
		this.order_comment_status = order_comment_status;
	}

	public String getRealFreight() {
		return realFreight;
	}

	public void setRealFreight(String realFreight) {
		this.realFreight = realFreight;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public JSONArray getGoods() {
		return goods;
	}

	public void setGoods(JSONArray goods) {
		this.goods = goods;
	}

	public String getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(String realAmount) {
		this.realAmount = realAmount;
	}

	public String getPayableFreight() {
		return payableFreight;
	}

	public void setPayableFreight(String payableFreight) {
		this.payableFreight = payableFreight;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

}
