package com.sobey.cloud.webtv.ebusiness;

import org.json.JSONObject;

/**
 * 一个定单商品项
 * @author zouxudong
 *
 */
public class OrderGoodsItem {

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
	 * 卖家名字
	 */
	public String sellerName;
	/**
	 * 卖家id
	 */
	public String sellerID;
	/**
	 * 商品id
	 */
	public String goodsID;
	/**
	 * 商品名字
	 */
	public String goodsName;
	/**
	 * 商品成交数量
	 */
	public int goodsCount;
	/**
	 * 商品原始价格或是折前价格
	 */
	public double goodsOrginalPrice;
	/**
	 * 收藏状态  失效 还是正常
	 */
	public int status=GoodsStatus.EXCEPTION;
	
	public OrderGoodsItem()
	{
	}
	
	public OrderGoodsItem(JSONObject goodsItem)
	{
		goodsCount=goodsItem.optInt("goodsCount",1);
		goodsURL=goodsItem.optString("goodsURL");
		sellerID=goodsItem.optString("sellerID");
		sellerName=goodsItem.optString("sellerName");
		goodsName=goodsItem.optString("goodsName");//" 优越者（UNITEK）Y-2140 迷你型4口USB HUB集线器 长线版 苹果笔记本/平板电脑专用（白色） ";
		goodsImageURL=goodsItem.optString("goodsImageURL");//"http://img10.360buyimg.com/n7/g12/M00/05/19/rBEQYFGR6XMIAAAAAADOowELA9QAABIbAP_HQ4AAM67248.jpg";
		goodsOrginalPrice=goodsItem.optDouble("goodsOrginalPrice");
		goodsFactPrice=goodsItem.optDouble("goodsFactPrice");
		status=goodsItem.optInt("status", GoodsStatus.EXCEPTION);
		//"2015-06-22 21:58:42";
	}
}
