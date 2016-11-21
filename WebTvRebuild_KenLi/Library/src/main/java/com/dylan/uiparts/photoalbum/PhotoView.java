/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.dylan.uiparts.photoalbum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.advancedimageview.listener.AdvancedImageViewLoadListener;
import com.dylan.uiparts.photoalbum.PhotoViewAttacher.OnMatrixChangedListener;
import com.dylan.uiparts.photoalbum.PhotoViewAttacher.OnPhotoTapListener;
import com.dylan.uiparts.photoalbum.PhotoViewAttacher.OnViewTapListener;
import com.third.library.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class PhotoView extends AdvancedImageView implements IPhotoView {
	private Context context;
	private final PhotoViewAttacher mAttacher;
	private static PopupWindow mControler = null;
	private static PopupWindow mControlerTop = null;
	private boolean mControlerShowFlag = true;
	private static int mScreenHeight = 0;
	private static int mControlerViewHeight = 0;
	private ScaleType mPendingScaleType;
	private static ArrayList<PhotoViewCache> mPhotoViewCaches = null;
	private boolean mToggleControlerFlag = true;

	public PhotoView(Context context) {
		this(context, null, null);
	}

	public PhotoView(Context context, View mControlerView, View mControlerViewTop) {
		this(context, null, mControlerView, mControlerViewTop);
	}

	public PhotoView(Context context, AttributeSet attr, View mControlerView, View mControlerViewTop) {
		this(context, attr, 0, mControlerView, mControlerViewTop);
	}

	public PhotoView(Context context, AttributeSet attr, int defStyle, View mControlerView, View mControlerViewTop) {
		super(context, attr, defStyle);
		super.setScaleType(ScaleType.MATRIX);
		this.context = context;
		mAttacher = new PhotoViewAttacher(this);
		mAttacher.setOnViewTapListener(new OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				toggleControler(mControlerShowFlag, false);
			}
		});
		if (mPhotoViewCaches == null) {
			mPhotoViewCaches = new ArrayList<PhotoViewCache>();
		}
		initControler(mControlerView, mControlerViewTop);

		if (null != mPendingScaleType) {
			setScaleType(mPendingScaleType);
			mPendingScaleType = null;
		}

		setDefaultImage(R.drawable.default_thumbnail_photoalbum);
		setLoadingImage(R.drawable.default_thumbnail_photoalbum);
		setErrorImage(R.drawable.default_thumbnail_photoalbum);
		setImageDrawable(context.getResources().getDrawable(R.drawable.default_thumbnail_photoalbum));
	}

	private void initControler(View mControlerView, View mControlerViewTop) {
		if (mControlerView != null && mControler == null) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			mScreenHeight = metrics.heightPixels;
			mControlerViewHeight = 0;
			mControler = new PopupWindow(mControlerView);
			mControler.setOutsideTouchable(false);
			mControler.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			mControler.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
			if (mControlerViewTop != null && mControlerTop == null) {
				mControlerTop = new PopupWindow(mControlerViewTop);
				mControlerTop.setOutsideTouchable(false);
				mControlerTop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
				mControlerTop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
			}
			toggleControler(!mControlerShowFlag, true);
		}
	}

	public void toggleControler(boolean controlerShowFlag, boolean forceFlag) {
		if (mToggleControlerFlag || forceFlag) {
			if (controlerShowFlag) {
				mControler.dismiss();
				if (mControlerTop != null) {
					mControlerTop.dismiss();
				}
				mControlerShowFlag = false;
			} else {
				mControler.showAtLocation(this, Gravity.BOTTOM, 0, 0);
				if (mControlerViewHeight < (mScreenHeight / 3.0) && mControlerViewHeight > 0) {
					mControler.update(0, 0, -1, mControlerViewHeight);
				} else {
					mControler.update(0, 0, -1, (int) (mScreenHeight / 3.0));
				}
				if (mControlerTop != null) {
					mControlerTop.showAtLocation(this, Gravity.TOP, 0, 80);
					if (mControlerTop != null) {
						mControlerTop.update();
					}
				}
				mControlerShowFlag = true;
			}
		}
	}

	public void canToggleControler(boolean toggleControlerFlag) {
		mToggleControlerFlag = toggleControlerFlag;
	}

	public void setControlerViewHeight(int height) {
		mControlerViewHeight = height;
		toggleControler(!mControlerShowFlag, true);
	}

	@Override
	public boolean canZoom() {
		return mAttacher.canZoom();
	}

	@Override
	public RectF getDisplayRect() {
		return mAttacher.getDisplayRect();
	}

	@Override
	public float getMinScale() {
		return mAttacher.getMinScale();
	}

	@Override
	public float getMidScale() {
		return mAttacher.getMidScale();
	}

	@Override
	public float getMaxScale() {
		return mAttacher.getMaxScale();
	}

	@Override
	public float getScale() {
		return mAttacher.getScale();
	}

	@Override
	public ScaleType getScaleType() {
		return mAttacher.getScaleType();
	}

	@Override
	public void setAllowParentInterceptOnEdge(boolean allow) {
		mAttacher.setAllowParentInterceptOnEdge(allow);
	}

	@Override
	public void setMinScale(float minScale) {
		mAttacher.setMinScale(minScale);
	}

	@Override
	public void setMidScale(float midScale) {
		mAttacher.setMidScale(midScale);
	}

	@Override
	public void setMaxScale(float maxScale) {
		mAttacher.setMaxScale(maxScale);
	}

	@Override
	// setImageBitmap calls through to this method
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	public void setImageURL(final String urlStr) {
		setNetImage(urlStr);
		setOnloadListener(new AdvancedImageViewLoadListener() {
			@Override
			public void onFinish(AdvancedImageView arg0, boolean arg1, Bitmap arg2) {
				PhotoViewCache cache = new PhotoViewCache();
				cache.setUrl(urlStr);
				cache.setBitmap(arg2);
				if(mPhotoViewCaches!=null)
					mPhotoViewCaches.add(cache);
			}
		});
	}

	@Override
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		mAttacher.setOnMatrixChangeListener(listener);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mAttacher.setOnLongClickListener(l);
	}

	@Override
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mAttacher.setOnPhotoTapListener(listener);
	}

	@Override
	public void setOnViewTapListener(OnViewTapListener listener) {
		mAttacher.setOnViewTapListener(listener);
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (null != mAttacher) {
			mAttacher.setScaleType(scaleType);
		} else {
			mPendingScaleType = scaleType;
		}
	}

	@Override
	public void setZoomable(boolean zoomable) {
		mAttacher.setZoomable(zoomable);
	}

	@Override
	public void zoomTo(float scale, float focalX, float focalY) {
		mAttacher.zoomTo(scale, focalX, focalY);
	}

	@Override
	protected void onDetachedFromWindow() {
		mAttacher.cleanup();
		super.onDetachedFromWindow();
	}

	public void destroy() {
		mControler = null;
		mControlerTop = null;
		mControlerShowFlag = false;
		mScreenHeight = 0;
		mPhotoViewCaches = null;
	}

	public boolean downloadImage(String urlStr, String path, String saveName) {
		for (int i = 0; i < mPhotoViewCaches.size(); i++) {
			if (mPhotoViewCaches.get(i).getUrl() == urlStr) {
				// photoView.setImageBitmap(mPhotoViewCaches.get(i).getBitmap());
				Bitmap bitmap = mPhotoViewCaches.get(i).getBitmap();
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(path, saveName);
				if (file.exists()) {
					file.delete();
				}
				try {
					FileOutputStream out = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.flush();
					out.close();
					return true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
		return false;
	}
}