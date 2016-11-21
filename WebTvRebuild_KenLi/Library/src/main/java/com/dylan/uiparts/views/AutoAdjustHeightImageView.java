package com.dylan.uiparts.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AutoAdjustHeightImageView extends ImageView {
	
	public AutoAdjustHeightImageView(Context context) {
		super(context);
		recalcWithBackground();
	}
    public AutoAdjustHeightImageView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        recalcWithBackground();
    } 
	public AutoAdjustHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
		recalcWithBackground(); 
	}

	private int imageWidth;  
    private int imageHeight;  
    public void recalcWithBackground() {
    	Drawable background = this.getBackground();  
        if (background == null) return;  
        Bitmap bitmap = ((BitmapDrawable)background).getBitmap();  
        imageWidth = bitmap.getWidth();  
        imageHeight = bitmap.getHeight();  
    }
    public void recalWithImage() {
    	Drawable image = this.getDrawable();
    	if (image == null)return;
        Bitmap bitmap = ((BitmapDrawable)image).getBitmap();  
        imageWidth = bitmap.getWidth();  
        imageHeight = bitmap.getHeight(); 
    }
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
    	if (imageWidth == 0 || imageHeight == 0) {
    		recalWithImage();
    	}
    	if (imageWidth == 0 || imageHeight == 0) {
    		recalcWithBackground();
    	}
    	if (imageWidth == 0 || imageHeight == 0) {
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	} else {
	        int width = MeasureSpec.getSize(widthMeasureSpec);  
	        int height = width  * imageHeight / imageWidth;  
	        this.setMeasuredDimension(width, height);  
    	}
    }  
}
