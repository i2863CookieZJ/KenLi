package com.sobey.cloud.webtv.ebusiness;


import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ItemGoodsListenerUtil {
   
	/**
	 * 设置某个商品的点击事件 点击后跳转到对应的url
	 * @param ulr
	 * @param context
	 * @param view
	 */
	public static void setViewListener(int Status,String url,final Context context,View view)
	{
		view.setOnClickListener(new Listener(context,url,Status));
	}
	
	public static class Listener implements View.OnClickListener
	{
		private Context context;
		private String url;
		private int status;
		public Listener(Context context, String url,int status) {
			this.context=context;
			this.url=url;
			this.status=status;
		}
		@Override
		public void onClick(View v) {
//			if(status==GoodsStatus.FINE)
//			{
				Intent intent=new Intent(context, GoodsDetailActivity.class);
				intent.putExtra("url", url);
				context.startActivity(intent);
//			}
//			else
//			{
//				ToastUtil.showToast(context, "亲,该商品已经失效或下架了哦^_^");
//			}
		}
	}
}
