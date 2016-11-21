package com.dylan.uiparts.views;

import com.dylan.common.utils.AsyncImageLoader;
import com.dylan.common.utils.AsyncImageLoader.ImageCallback;
import com.dylan.common.utils.AsyncLocalImageLoader;
import com.dylan.common.utils.AsyncLocalImageLoader.LocalImageCallback;
import com.third.library.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NetImageView extends ImageView {
	private static Context mContext;

	public NetImageView(Context context) {
		super(context);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
		mFitWidth = a.getBoolean(R.styleable.ImageView_fitWidth, false);
		mFitHeight = a.getBoolean(R.styleable.ImageView_fitHeight, false);
		mCenterCrop = a.getBoolean(R.styleable.ImageView_centerCrop, false);
		mAspectRatio = a.getFloat(R.styleable.ImageView_aspectRatio, 0.0f);
		if (a.hasValue(R.styleable.ImageView_imageDefault))
			mDefaultDrawable = a.getDrawable(R.styleable.ImageView_imageDefault);
		if (a.hasValue(R.styleable.ImageView_imageLoading))
			mLoadingDrawable = a.getDrawable(R.styleable.ImageView_imageLoading);
		mRoundRadius = a.getDimensionPixelOffset(R.styleable.ImageView_roundRadius, 0);
		if (a != null) {
			a.recycle();
		}
		initRoundRadius();
	}

	private Drawable mDefaultDrawable = null;
	private int mDefaultResId = 0;
	private Drawable mLoadingDrawable = null;
	private int mLoadingResId = 0;
	private OnLoadListener mListener = null;
	private boolean mFitHeight = false;
	private boolean mFitWidth = false;
	private boolean mCenterCrop = false;
	private float mAspectRatio = 0.0f;
	private int mRoundRadius = 0;
	private long mImageFlag;

	public void setDefault(Drawable drawable) {
		mDefaultDrawable = drawable;
	}

	public void setDefault(int resId) {
		mDefaultResId = resId;
	}

	public void setLoading(Drawable drawable) {
		mLoadingDrawable = drawable;
	}

	public void setLoading(int resId) {
		mLoadingResId = resId;
	}

	public void setListener(OnLoadListener listener) {
		mListener = listener;
	}

	public void setFitHeight(boolean fit) {
		mFitHeight = fit;
	}

	public void setFitWidth(boolean fit) {
		mFitWidth = fit;
	}

	public void setCenterCrop(boolean fit) {
		mCenterCrop = fit;
	}

	public void setAspectRatio(int ratio) {
		mAspectRatio = ratio;
	}

	public void setRondRadius(int radius) {
		mRoundRadius = radius;
		initRoundRadius();
	}

	private static AsyncLocalImageLoader mLocalImageLoader;

	public void setLocalImage(String filePath) {
		mImageFlag = System.currentTimeMillis();
		if(mLocalImageLoader == null) {
			mLocalImageLoader = new AsyncLocalImageLoader(mContext);
		}
		if (filePath == null || filePath.length() < 1) {
			if (mDefaultDrawable != null)
				setImageDrawable(mDefaultDrawable);
			else
				setImageResource(mDefaultResId);
		} else {
			if (mLoadingDrawable != null)
				setImageDrawable(mLoadingDrawable);
			else
				setImageResource(mLoadingResId);
			mLocalImageLoader.loadDrawable(filePath, mImageFlag, new LocalImageCallback() {
				@Override
				public void onLoaded(Bitmap bitmap, long imageFlag) {
					if(imageFlag == mImageFlag) {
						setImageBitmap(bitmap);
					}
					if (mListener != null)
						mListener.onFinish(NetImageView.this, true, bitmap);
				}

				@Override
				public void onError(Exception e, long imageFlag) {
					if (mDefaultDrawable != null) {
						if(imageFlag == mImageFlag) {
							setImageDrawable(mDefaultDrawable);
						}
						if (mListener != null)
							mListener.onFinish(NetImageView.this, true, ((BitmapDrawable) mDefaultDrawable).getBitmap());
					} else {
						if(imageFlag == mImageFlag) {
							setImageResource(mDefaultResId);
						}
						Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(mDefaultResId)).getBitmap();
						if (mListener != null)
							mListener.onFinish(NetImageView.this, true, bitmap);
					}
				}
			});
		}
	}

	private static AsyncImageLoader mLoader = new AsyncImageLoader();

	public void setImage(String url, AsyncImageLoader loader) {
		setImage(url, false, loader);
	}

	public void setImage(String url, boolean useCache, AsyncImageLoader loader) {
		mImageFlag = System.currentTimeMillis();
		if (loader == null) {
			loader = mLoader;
		}
		if (url == null || url.length() < 1 || loader == null) {
			if (mDefaultDrawable != null)
				setImageDrawable(mDefaultDrawable);
			else
				setImageResource(mDefaultResId);
		} else {
			if (mLoadingDrawable != null)
				setImageDrawable(mLoadingDrawable);
			else
				setImageResource(mLoadingResId);
			loader.loadDrawable(getContext(), url, mImageFlag, useCache, new ImageCallback() {
				@Override
				public void onError(Exception e, long imageFlag) {
					if (mDefaultDrawable != null) {
						if(imageFlag == mImageFlag) {
							setImageDrawable(mDefaultDrawable);
						}
						if (mListener != null)
							mListener.onFinish(NetImageView.this, true, ((BitmapDrawable) mDefaultDrawable).getBitmap());
					} else {
						if(imageFlag == mImageFlag) {
							setImageResource(mDefaultResId);
						}
						Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(mDefaultResId)).getBitmap();
						if (mListener != null)
							mListener.onFinish(NetImageView.this, true, bitmap);
					}
				}

				@Override
				public void onLoaded(Bitmap bitmap, long imageFlag) {
					if(imageFlag == mImageFlag) {
						setImageBitmap(bitmap);
					}
					if (mListener != null)
						mListener.onFinish(NetImageView.this, true, bitmap);
				}
			});
		}
	}

	private final RectF roundRect = new RectF();
	private float rectadius = 0;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();

	private void initRoundRadius() {
		if (mRoundRadius < 1)
			return;
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(Color.WHITE);
		float density = getResources().getDisplayMetrics().density;
		rectadius = mRoundRadius * density;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
		if (mRoundRadius < 1) {
			super.draw(canvas);
		} else {
			canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
			canvas.drawRoundRect(roundRect, rectadius, rectadius, zonePaint);
			canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
			super.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mFitHeight) {
			mFitWidth = false;
			mCenterCrop = false;
			mAspectRatio = 0.0f;
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			if (getDrawable() != null) {
				Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
				width = height * bitmap.getWidth() / bitmap.getHeight();
			}
			this.setMeasuredDimension(width, height);
		} else if (mFitWidth) {
			mFitHeight = false;
			mCenterCrop = false;
			mAspectRatio = 0.0f;
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			if (getDrawable() != null) {
				Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
				height = width * bitmap.getHeight() / bitmap.getWidth();
			}
			this.setMeasuredDimension(width, height);
		} else if (mCenterCrop) {
			mFitWidth = false;
			mFitHeight = false;
			mAspectRatio = 0.0f;
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			if (getDrawable() != null) {
				Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
				int bitmapWidth = bitmap.getWidth();
				int bitmapHeight = bitmap.getHeight();
				if ((bitmapWidth * height) > (bitmapHeight * width)) {
					width = height * bitmap.getWidth() / bitmap.getHeight();
				} else {
					height = width * bitmap.getHeight() / bitmap.getWidth();
				}
			}
			this.setMeasuredDimension(width, height);
		} else if (mAspectRatio != 0) {
			mFitWidth = false;
			mFitHeight = false;
			mCenterCrop = false;
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			if (getDrawable() != null) {
				height = (int) (width / mAspectRatio);
			}
			this.setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public interface OnLoadListener {
		void onFinish(NetImageView view, boolean result, Bitmap bitmap);
	}
}
