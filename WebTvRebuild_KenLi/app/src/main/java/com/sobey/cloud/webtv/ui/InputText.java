package com.sobey.cloud.webtv.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class InputText extends EditText {

	public InputText(Context context) {
		super(context);
	}
	public InputText(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 
    public InputText(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    } 
	public boolean onTouchEvent(MotionEvent event) {
		if(MotionEvent.ACTION_DOWN == event.getAction()) {
			clearFocus();  //在滑动设备列表的时候，editview无法弹出软键盘
		}
		return super.onTouchEvent(event);
	}
}
