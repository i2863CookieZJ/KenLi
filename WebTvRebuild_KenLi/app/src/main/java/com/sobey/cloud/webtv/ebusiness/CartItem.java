package com.sobey.cloud.webtv.ebusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * 来自同一个商家下的购物车收藏项
 * @author zouxudong
 *
 */
public class CartItem {

	/**
	 * 商家名
	 */
	public String sellerName;
	/**
	 * 商家id
	 */
	public String sellerID;
	/**
	 * 定单下的商品列表数据
	 */
	public List<CartGoodsItem> goods;
	
	public CartItem()
	{
		goods=new ArrayList<CartGoodsItem>();
	}
}
