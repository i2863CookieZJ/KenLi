package com.sobey.cloud.webtv.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Display {
	public static int	ScreenWidth;
	public static int	ScreenHeight;

	/**
	 * 宽高用640的做标准，进行等宽缩放
	 * 
	 * @param dim
	 * @return
	 */
	public static int scaleW(float dimension) {
		return (int) ( dimension * Display.ScreenWidth / 640 );
	}

	public static int scaleH(float dimension) {
		return (int) ( dimension * Display.ScreenHeight / 960 );
	}
	public static int scaleWxxhdpi(float dimension) {
		return (int) ( dimension * Display.ScreenWidth / 1080 );
	}

	public static int scaleHxxhdpi(float dimension) {
		return (int) ( dimension * Display.ScreenHeight / 1920 );
	}

	/** 缩放后,更新按键的宽和高 */
	public static void setScaleLayoutParams(View view, int width, int height) {
		switch (width) {
		case ViewGroup.LayoutParams.MATCH_PARENT:
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			break;
		default:
			width = Display.scaleW( width );
			break;
		}
		switch (height) {
		case ViewGroup.LayoutParams.MATCH_PARENT:
		case ViewGroup.LayoutParams.WRAP_CONTENT:
			break;
		default:
			height = Display.scaleH( height );
			break;
		}
		setLayoutParams( view, width, height );
	}

	/** 更新按键的宽和高 */
	public static void setLayoutParams(View view, int width, int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if(params == null){
			params = new ViewGroup.LayoutParams(width,height);
		}
		params.width = width;
		params.height = height;
		view.setLayoutParams( params );
	}
	/** 更新按键的宽和高带重心*/
	public static void setLayoutParamsWithGravite(View view, int width, int height,int gravity) {
		LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		params.gravity = gravity;
		view.setLayoutParams( params );
	}

	/** 缩放后,设置相对位置距离 */
	public static void setScaleLayoutMargin(View view, float left, float top, float right, float bottom) {
		setLayoutMargin( view, Display.scaleW( left ), Display.scaleH( top ), Display.scaleW( right ), Display.scaleH( bottom ) );
	}

	/** 设置相对位置距离 */
	public static void setLayoutMargin(View view, int left, int top, int right, int bottom) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		params.leftMargin = left;
		params.topMargin = top;
		params.rightMargin = right;
		params.bottomMargin = bottom;
		view.setLayoutParams( params );
	}
	/** 设置相对位置距离 */
	public static void setLayoutMarginWithNew(View view, int left, int top, int right, int bottom) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.leftMargin = left;
		params.topMargin = top;
		params.rightMargin = right;
		params.bottomMargin = bottom;
		view.setLayoutParams( params );
	}

	/** 缩放后,设置边距 */
	public static void setScalePadding(View view, float left, float top, float right, float bottom) {
		setPadding( view, Display.scaleW( left ), Display.scaleH( top ), Display.scaleW( right ), Display.scaleH( bottom ) );
	}

	/** 设置边距 */
	public static void setPadding(View view, int left, int top, int right, int bottom) {
		view.setPadding( left, top, right, bottom );
	}

	/** 缩放后,设置文本组建的字体大小 */
	public static void setScaleTextSize(TextView textView, float textSize) {
		setTextSize( textView, Display.scaleH( textSize ) );
	}

	/** 设置文本组建的字体大小 */
	public static void setTextSize(TextView textView, float textSize) {
		textView.setTextSize( TypedValue.COMPLEX_UNIT_PX, textSize );
	}
	
	
	/** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

}
