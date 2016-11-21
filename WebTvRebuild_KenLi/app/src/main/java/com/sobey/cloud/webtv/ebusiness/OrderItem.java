package com.sobey.cloud.webtv.ebusiness;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * 一个定单
 * @author zouxudong
 *
 */
public class OrderItem {

	/**
	 * 定单编号
	 */
	public final String orderNo;
	/**
	 * 成交时间
	 */
	public final String orderDate;
	/**
	 * 交易状态
	 */
	public final String tradeState;
	/**
	 * 定单下的商品列表数据
	 */
	public final List<OrderGoodsItem> goods;
	/***
	 * 运费
	 */
	public double carriage=0.0f;
	public OrderItem(JSONObject data)
	{
		goods=new ArrayList<OrderGoodsItem>();
		orderNo=data.optString("orderNo");
		orderDate=data.optString("orderDate");
		tradeState=data.optString("tradeState");
		carriage=data.optDouble("carriage");
	}
}
