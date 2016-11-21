package com.sobey.cloud.webtv.utils;

import com.dylan.common.utils.ScaleConversion;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.share.ShareControl;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class SharePopWindow {
	private Context mContext;
	private PopupWindow mSharePopupWindow = null;
	private View attachView;
	private TextView mPopWindowShareCancel;
	private ImageButton mPopWindowShareSinaWB;
	private ImageButton mPopWindowShareTencentWB;
	private ImageButton mPopWindowShareWeiXin;
	private ImageButton mPopWindowShareWeiXinCircle;
	private ImageButton mPopWindowShareQQ;
	private ImageButton mPopWindowShareMessage;
	private int mHeight;
	private int mScreenWidth;
	private String mShareUrl;
	private String mShareTitle;
	private String mShareContent;
	private String mShareMessageContent;
	private String mImageUrl;
	private ShareControl mShareControl;
	private SharePopWindowClickListener listener;

	public SharePopWindow(Context context, View attachView) {
		mContext = context;
		this.attachView = attachView;
		mShareControl = new ShareControl();
		View mShareView = LayoutInflater.from(mContext).inflate(R.layout.layout_popwindow_share, null);
		mSharePopupWindow = new PopupWindow(mShareView);
		mPopWindowShareCancel = (TextView) mShareView.findViewById(R.id.popwindow_share_cancel);
		mPopWindowShareSinaWB = (ImageButton) mShareView.findViewById(R.id.popwindow_share_sinawb);
		mPopWindowShareTencentWB = (ImageButton) mShareView.findViewById(R.id.popwindow_share_tencentwb);
		mPopWindowShareWeiXin = (ImageButton) mShareView.findViewById(R.id.popwindow_share_weixin);
		mPopWindowShareWeiXinCircle = (ImageButton) mShareView.findViewById(R.id.popwindow_share_weixin_circle);
		mPopWindowShareQQ = (ImageButton) mShareView.findViewById(R.id.popwindow_share_qq);
		mPopWindowShareMessage = (ImageButton) mShareView.findViewById(R.id.popwindow_share_message);
		
		mPopWindowShareCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideShareWindow();
			}
		});
		
		mPopWindowShareSinaWB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.SINA);
				}
				mShareControl.share(mContext, SHARE_MEDIA.SINA, mShareUrl, mShareTitle, mShareContent, mImageUrl);
				hideShareWindow();
			}
		});
		
		mPopWindowShareTencentWB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.TENCENT);
				}
				mShareControl.share(mContext, SHARE_MEDIA.TENCENT, mShareUrl, mShareTitle, mShareContent, mImageUrl);
				hideShareWindow();
			}
		});
		
		mPopWindowShareWeiXin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.WEIXIN);
				}
				mShareControl.share(mContext, SHARE_MEDIA.WEIXIN, mShareUrl, mShareTitle, mShareContent, mImageUrl);
				hideShareWindow();
			}
		});
		
		mPopWindowShareWeiXinCircle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.WEIXIN_CIRCLE);
				}
				mShareControl.share(mContext, SHARE_MEDIA.WEIXIN_CIRCLE, mShareUrl, mShareTitle, mShareContent, mImageUrl);
				hideShareWindow();
			}
		});
		
		mPopWindowShareQQ.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.QQ);
				}
				mShareControl.share(mContext, SHARE_MEDIA.QQ, mShareUrl, mShareTitle, mShareContent, mImageUrl);
				hideShareWindow();
			}
		});
		
		mPopWindowShareMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(SHARE_MEDIA.SMS);
				}
				mShareControl.shareMessage(mContext, mShareMessageContent);
				hideShareWindow();
			}
		});
		
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		mHeight = ScaleConversion.dip2px(mContext, 310);
		mScreenWidth = metrics.widthPixels;
	}
	
	public void showShareWindow(String shareUrl, String shareTitle, String shareContent, String imageUrl) {
		if(!mSharePopupWindow.isShowing()) {
			if(shareContent != null) {
				mShareUrl = shareUrl;
				mShareTitle = shareTitle;
				mShareMessageContent = "(" + shareTitle + ")";
				mImageUrl = imageUrl;
				if (shareContent.length() > MConfig.ShareContentLength) {
					mShareContent = shareContent.substring(0, MConfig.ShareContentLength) + "...";
				} else {
					mShareContent = shareContent;
				}
				if (shareContent.length() > MConfig.ShareMessageContentLength) {
					mShareMessageContent += shareContent.substring(0, MConfig.ShareMessageContentLength) + "...";
				} else {
					mShareMessageContent += shareContent;
				}
				mShareMessageContent += " " + shareUrl;
				mSharePopupWindow.showAtLocation(attachView, Gravity.BOTTOM, 0, 0);
				mSharePopupWindow.update(0, 0, mScreenWidth, mHeight);
			} else {
				Toast.makeText(mContext, "分享内容为空,请稍后重试", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void hideShareWindow() {
		if(mSharePopupWindow.isShowing()) {
			mSharePopupWindow.dismiss();
		}
	}
	
	public boolean isShowing() {
		return mSharePopupWindow.isShowing();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    UMSsoHandler ssoHandler = mShareControl.getController().getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	public void setOnSharePopWindowClickListener(SharePopWindowClickListener l) {
		listener = l;
	}
	
	public interface SharePopWindowClickListener {
		void onClick(SHARE_MEDIA media);
	}
}
