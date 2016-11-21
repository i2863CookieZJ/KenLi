package com.sobey.cloud.webtv.share;

import java.util.HashMap;

import com.sobey.cloud.webtv.utils.MConfig;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;

public class ShareControl {
	public static boolean addSinaSSO = true;
	static final UMSocialService mUmSocialService = UMServiceFactory.getUMSocialService(MConfig.DESCRIPTOR,
			RequestType.SOCIAL);
	static SnsPostListener listener;

	{
		mUmSocialService.getConfig().closeToast();
		if (addSinaSSO) {
			// mUmSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
		}
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}

	private Context mContext;

	public void share(Context context, SHARE_MEDIA media, String shareUrl, String shareTitle, String shareContent,
			String imageUrl) {
		// 分享帖子的时候url后面加上参数siteId=21
		if (shareUrl.contains("qz.sobeycache.com") && !shareUrl.contains("siteId")) {
			shareUrl = shareUrl + "&siteId=" + MConfig.SITE_ID;
		}

		if (TextUtils.isEmpty(shareContent) && !TextUtils.isEmpty(shareTitle))
			shareContent = shareTitle;
		if (!TextUtils.isEmpty(shareContent) && TextUtils.isEmpty(shareTitle))
			shareTitle = shareContent;
		if (listener == null) {
			listener = new SnsPostListener() {

				@Override
				public void onStart() {
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int stCode, SocializeEntity arg2) {
					if (platform != SHARE_MEDIA.SMS && stCode == 200) {
						Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(mContext,
						// "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
						// .show();
					}
				}
			};

		}
		mUmSocialService.getConfig().registerListener(listener);
		mContext = context;
		addQQQPlatform();
		// 设置腾讯微博SSO handler
		// mUmSocialService.getConfig().setSsoHandler(new
		// TencentWBSsoHandler());
		if (shareContent != null) {
			// QQ
			if (media == SHARE_MEDIA.QQ) {
				if (TextUtils.isEmpty(shareUrl)) {
					Toast.makeText(mContext, "暂时无法分享,请稍后重试", Toast.LENGTH_SHORT).show();
					return;
				}
				QQShareContent qqShareContent = new QQShareContent();
				// 设置分享文字
				if (!TextUtils.isEmpty(shareContent))
					qqShareContent.setShareContent(shareContent);
				// 设置分享title
				if (!TextUtils.isEmpty(shareTitle))
					qqShareContent.setTitle(shareTitle);
				// 设置分享图片
				if (TextUtils.isEmpty(imageUrl) == false)
					qqShareContent.setShareImage(new UMImage(mContext, imageUrl));
				// 设置点击分享内容的跳转链接
				if (!TextUtils.isEmpty(shareUrl))
					qqShareContent.setTargetUrl(shareUrl);
				mUmSocialService.setShareMedia(qqShareContent);
			}

			// WeiXin
			else if (media == SHARE_MEDIA.WEIXIN) {
				if (TextUtils.isEmpty(shareUrl)) {
					Toast.makeText(mContext, "暂时无法分享,请稍后重试", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(shareContent)) {
					shareContent = shareTitle;
					mUmSocialService.setShareContent(shareContent);
				}
				// UMWXHandler wxHandler =
				// mUmSocialService.getConfig().supportWXPlatform((Activity)
				// mContext, MConfig.ShareWeiXinAppId, contentUrl_WeiXin);
				// mController.setShareMedia(weixinContent);
				// 设置分享标题
				// wxHandler.setWXTitle(shareTitle);

				// UMWXHandler wxHandler=new UMWXHandler(mContext,
				// MConfig.ShareWeiXinAppId,
				// MConfig.SHAREWEIXINAPPSECRET_STRING);
				UMWXHandler wxHandler = new UMWXHandler(context, MConfig.ShareWeiXinAppId);
				wxHandler.addToSocialSDK();
				wxHandler.showCompressToast(false);
				// 设置微信好友分享内容
				WeiXinShareContent weixinContent = new WeiXinShareContent();
				// 设置分享文字
				if (!TextUtils.isEmpty(shareContent))
					weixinContent.setShareContent(shareContent);
				// 设置title
				if (!TextUtils.isEmpty(shareTitle))
					weixinContent.setTitle(shareTitle);
				// 设置分享内容跳转URL
				if (!TextUtils.isEmpty(shareUrl))
					weixinContent.setTargetUrl(shareUrl);
				// 设置分享图片
				if (!TextUtils.isEmpty(imageUrl))
					weixinContent.setShareImage(new UMImage(mContext, imageUrl));
				mUmSocialService.setShareMedia(weixinContent);
			}

			// WeiXin Circle
			else if (media == SHARE_MEDIA.WEIXIN_CIRCLE) {
				if (TextUtils.isEmpty(shareUrl)) {
					Toast.makeText(mContext, "暂时无法分享,请稍后重试", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(shareContent)) {
					shareContent = shareTitle;
				}
				mUmSocialService.setShareContent(shareContent);
				// 支持微信朋友圈
				// UMWXHandler circleHandler =
				// mUmSocialService.getConfig().supportWXCirclePlatform((Activity)
				// mContext, MConfig.ShareWeiXinAppId, contentUrl_WinXinCircle);
				// circleHandler.setCircleTitle(shareTitle);

				// UMWXHandler wxCircleHandler =new UMWXHandler(mContext,
				// MConfig.ShareWeiXinAppId,
				// MConfig.SHAREWEIXINAPPSECRET_STRING);
				UMWXHandler wxCircleHandler = new UMWXHandler(context, MConfig.ShareWeiXinAppId);
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.addToSocialSDK();
				wxCircleHandler.showCompressToast(false);

				// 设置微信好友分享内容
				CircleShareContent weixinContent = new CircleShareContent();
				// 设置分享文字
				if (!TextUtils.isEmpty(shareContent))
					weixinContent.setShareContent(shareContent);
				// 设置title
				if (!TextUtils.isEmpty(shareTitle))
					weixinContent.setTitle(shareTitle);
				// 设置分享内容跳转URL
				if (!TextUtils.isEmpty(shareUrl))
					weixinContent.setTargetUrl(shareUrl);
				// 设置分享图片
				if (!TextUtils.isEmpty(imageUrl))
					weixinContent.setShareImage(new UMImage(mContext, imageUrl));
				mUmSocialService.setShareMedia(weixinContent);
			} else if (media == SHARE_MEDIA.SINA) {// 用ShareSDK来分享新浪
				OnekeyShare oks = new OnekeyShare();
				oks.setSilent(false);
				oks.setPlatform(SinaWeibo.NAME);
				// 新浪微博支持分享网络图片，但是这个需要高级微博写入权限，因此如果您需要分享网络图片，请申请权限以后，
				// 将图片Url设置给SinaWeibo.ShareParams.setImageUrl(imageUrl)即可
				// if (!TextUtils.isEmpty(imageUrl)) {
				// oks.setImageUrl(imageUrl);
				// }
				oks.setText("(" + shareTitle + ")" + shareContent + shareUrl);
				oks.show(mContext);
				return;
			} else {
				mUmSocialService.setShareImage(null);
				mUmSocialService.setShareMedia(null);
				mUmSocialService.setShareContent("(" + shareTitle + ")" + shareContent + shareUrl);
				if (!TextUtils.isEmpty(imageUrl)) {
					mUmSocialService.setShareMedia(new UMImage(mContext, imageUrl));
				}

			}

			mUmSocialService.postShare(mContext, media, listener);
			// new SnsPostListener() {
			// @Override
			// public void onStart() {
			// }
			//
			// @Override
			// public void onComplete(SHARE_MEDIA platform, int eCode,
			// SocializeEntity entity) {
			// ToastUtil.showToast(mContext, "eCode"+eCode+platform.toString());
			// if(platform == SHARE_MEDIA.SINA || platform ==
			// SHARE_MEDIA.TENCENT) {
			// if (eCode == StatusCode.ST_CODE_SUCCESSED) {
			// Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
			// } else {
			// Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
			// }
			// }
			// }
			// });
		} else {
			Toast.makeText(mContext, "分享内容为空", Toast.LENGTH_SHORT).show();
		}
	}

	private void addQQQPlatform() {
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) mContext, MConfig.ShareQQAppId,
				MConfig.ShareQQAppKey);
		qqSsoHandler.addToSocialSDK();
	}

	public void shareMessage(Context context, String shareContent) {
		SmsShareContent content = new SmsShareContent(shareContent);
		// mUmSocialService.setShareImage(null);
		// mUmSocialService.setShareMedia(null);
		// mUmSocialService.setShareContent(shareContent);
		mUmSocialService.setShareMedia(content);
		mUmSocialService.shareSms(context);
	}

	public UMSocialService getController() {
		return mUmSocialService;
	}
}
