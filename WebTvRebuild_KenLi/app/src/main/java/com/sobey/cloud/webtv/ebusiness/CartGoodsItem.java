package com.sobey.cloud.webtv.ebusiness;

import org.json.JSONObject;

/**
 * 一个购物车商品项
 * 
 * @author zouxudong
 *
 */
public class CartGoodsItem {

	/**
	 * 商家名
	 */
	public String sellerName;
	/**
	 * 商家id
	 */
	public String sellerID;
	/**
	 * 商品图片地址
	 */
	public String goodsImageURL;
	/**
	 * 实际成交价格
	 */
	public double goodsFactPrice;
	/**
	 * 商品页面地址
	 */
	public String goodsURL;
	/**
	 * 商品id
	 */
	public String goodsID;
	/**
	 * 商品名字
	 */
	public String goodsName;
	/**
	 * 商品收藏数量
	 */
	public int goodsCount;
	/**
	 * 商品原始价格或是折前价格
	 */
	public double goodsOrginalPrice;
	/***
	 * 运费
	 */
	public float carriage;
	/**
	 * 收藏状态 失效 还是正常
	 */
	public int status = GoodsStatus.EXCEPTION;

	/**
	 * 是否被选中
	 */
	public boolean isSelected;

	public CartGoodsItem(JSONObject jsonObject) {
		goodsID = jsonObject.optString("goodsID");
		goodsCount = jsonObject.optInt("goodsCount", 1);
		goodsURL = jsonObject.optString("goodsURL");
		goodsFactPrice = jsonObject.optDouble("goodsFactPrice");
		goodsOrginalPrice = jsonObject.optDouble("goodsOrginalPrice");
		goodsImageURL = jsonObject.optString("goodsImageURL");
		goodsName = jsonObject.optString("goodsName");
		status = jsonObject.optInt("goodsCount", GoodsStatus.EXCEPTION);
		sellerName = jsonObject.optString("sellerName");
		sellerID = jsonObject.optString("sellerID");
	}

	public CartGoodsItem() {

	}
}
