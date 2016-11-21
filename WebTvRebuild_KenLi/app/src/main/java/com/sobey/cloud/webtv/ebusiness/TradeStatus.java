package com.sobey.cloud.webtv.ebusiness;

public class TradeStatus {

	private TradeStatus() {
	}
	public static final int WAIT_2PAY=0;//为等待付款 
	public static final int WAIT_SEND=1;//已经下单等待发货 
	public static final int SEND_COMPLETE=2;//为已经发货  
	public static final int TRADE_COMPLETE=3;//为交易完成 
	public static final int CANCEL=4;//为交易取消
}
