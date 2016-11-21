package com.sobey.cloud.webtv.bean;

public class MyOrderDetailGoodsBean {

	private String realPrice;//商品真实价格
	private String goodsPrice;
	private String shopId;
	private String goodsId;
	private String goodsName;//商品名称
	private String siteId;
	private String goodsSno;
	private String goodsNums;//商品数量
	private String goodsImg;//商品图片
	private String shopName;//来源

	public String getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(String realPrice) {
		this.realPrice = realPrice;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getGoodsSno() {
		return goodsSno;
	}

	public void setGoodsSno(String goodsSno) {
		this.goodsSno = goodsSno;
	}

	public String getGoodsNums() {
		return goodsNums;
	}

	public void setGoodsNums(String goodsNums) {
		this.goodsNums = goodsNums;
	}

	public String getGoodsImg() {
		return goodsImg;
	}

	public void setGoodsImg(String goodsImg) {
		this.goodsImg = goodsImg;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
}
