package com.dylan.uiparts.views;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class FlingGesturer{

	private int FLING_MIN_DISTANCEX = 50;     
	private int FLING_MIN_DISTANCEY = 50;   
    private int FLING_MIN_VELOCITY = 100;
    private boolean FLIING_DISABLEX = false;
    private boolean FLIING_ENABLEY = false;
    private GestureDetector mGesturer = null;
	public FlingGesturer(Activity context, OnFilpListener listener) {
		DisplayMetrics dm = new DisplayMetrics(); 
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);  
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		FLING_MIN_DISTANCEX = width / 4;
		FLING_MIN_DISTANCEY = height / 4;
		mListener = listener;
		mGesturer = new GestureDetector(context, new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			@Override
			public void onShowPress(MotionEvent e) {
			}
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return false;
			}
			@Override
			public void onLongPress(MotionEvent e) {
			}
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (!FLIING_DISABLEX) {
					if (e1.getX()-e2.getX() > FLING_MIN_DISTANCEX && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
						if (mListener != null) {
							mListener.onFlipRight();
						}
					} else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCEX && Math.abs(velocityX) > FLING_MIN_VELOCITY) {       
						if (mListener != null) {
							mListener.onFlipLeft();
						}
				    } 
				}
				if (FLIING_ENABLEY) {
					if (e1.getY()-e2.getY() > FLING_MIN_DISTANCEY && Math.abs(velocityY) > FLING_MIN_VELOCITY) { 
						if (mListener != null) {
							mListener.onFlipUp();
						}
					} else if (e2.getY()-e1.getY() > FLING_MIN_DISTANCEY && Math.abs(velocityY) > FLING_MIN_VELOCITY) {       
						if (mListener != null) {
							mListener.onFlipDown();
						}
				    }
				}
		        return false;
			}
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		}); 
	}
	public void setSensitivity(View view) {
		if (view == null)return;
		int width = view.getLayoutParams().width;
		int height = view.getLayoutParams().height;
		if (width > 0)FLING_MIN_DISTANCEX = width / 4;
		if (height > 0)FLING_MIN_DISTANCEY = height / 4;
	}
	public void setSensitivity(int distanceX, int distanceY, int velocity) {
		if (distanceX > 0)FLING_MIN_DISTANCEX = distanceX;
		if (distanceY > 0)FLING_MIN_DISTANCEY = distanceY;
		if (velocity > 0)FLING_MIN_VELOCITY = velocity;
	}
	public void setDisableX(boolean disable) {
		FLIING_DISABLEX = disable;
	}
	public void setEnableY(boolean enable) {
		FLIING_ENABLEY = enable;
	}
	public boolean onTouchEvent(MotionEvent event) {
		if (mGesturer != null) {
			return mGesturer.onTouchEvent(event);
		} else {
			return false;
		}
	}
	
	private OnFilpListener mListener;
	public interface OnFilpListener {
		void onFlipLeft();
		void onFlipRight();
		void onFlipUp();
		void onFlipDown();
	}
}
