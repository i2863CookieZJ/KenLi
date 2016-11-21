package com.sobey.cloud.webtv.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sobey.cloud.webtv.senum.UserGender;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.content.Context;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * 登录注册用的
 * 
 * @author zouxudong
 * 
 */
public class SignUtil {
	// public static final String DESCRIPTOR = "com.umeng.share";
	public static final UMSocialService mUmSocialService = UMServiceFactory.getUMSocialService(MConfig.UMENT_LOGIN);
	public static boolean flag = true;

	{
		client.setTimeout(10000);
		mUmSocialService.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
	}

	private static AsyncHttpClient client = new AsyncHttpClient();
	// private static final String
	// ServerURL="http://interface.ieator.com/interface";
	/**
	 * 用新的域名访问
	 */
	public static final String ServerURL = "http://uc.sobeycache.com/interface";

	// public static Tencent mTencent;

	// public static void initTencent(Context context)
	// {
	// if(mTencent==null)
	// mTencent = Tencent.createInstance(MConfig.ShareQQAppId, context);
	// }

	/**
	 * 用邮箱注册
	 * 
	 * @param userName
	 * @param passWord
	 * @param listener
	 */
	public static void registAccordUCtenterWithEMail(String userName, String passWord, String nickName, String head,
			String sex, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "register");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("userName", userName);
		params.put("Password", passWord);
		params.put("nickName", nickName);
		params.put("head", head);
		params.put("sex", sex);
		client.get(ServerURL, params, listener);
	}

	/**
	 * 取验手机证码
	 * 
	 * @param phoneNumber
	 * @param listener
	 */
	public static void getMobileCaptcha(String phoneNumber, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "getMobileCaptcha");
		params.put("mobile", phoneNumber);
		params.put("siteId", MConfig.SITE_ID);
		client.get(ServerURL, params, listener);
	}

	public static void getMobileCaptchaTwo(String phoneNumber, String nickname, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "getMobileCaptchaTwo");
		params.put("mobile", phoneNumber);
		params.put("nickname", nickname);
		params.put("siteId", MConfig.SITE_ID);
		client.get(ServerURL, params, listener);
	}

	/**
	 * 取验手机证码
	 * 
	 * @param phoneNumber
	 * @param listener
	 */
	public static void getMobileCaptchaWithChangePsw(String phoneNumber, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "getMobileCaptcha");
		params.put("mobile", phoneNumber);
		params.put("type", 1);
		params.put("siteId", MConfig.SITE_ID);
		client.get(ServerURL, params, listener);
	}

	public static void login(String userName, String passWord, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "verify");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("userName", userName);
		params.put("Password", passWord);
		client.get(ServerURL, params, listener);
	}

	/**
	 * 通过验证码来注册
	 * 
	 * @param username
	 * @param password
	 * @param captcha
	 * @param listener
	 */
	public static void registerAccordUCenterWidthMobile(String userName, String passWord, String nickName,
			String captcha, AsyncHttpResponseHandler listener) {
		RequestParams params = new RequestParams();
		params.put("method", "mobileRegister");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("userName", userName);
		params.put("password", passWord);
		params.put("captcha", captcha);
		params.put("nickName", nickName);
		params.put("head", "");
		params.put("sex", "");
		client.get(ServerURL, params, listener);
	}

	public static void modifyUserInfoFromUCenter(String userName, String nickName, String sex, String email,
			String logo, AsyncHttpResponseHandler listener) {
		if (sex.equalsIgnoreCase(UserGender.Male.toString())) {
			sex = "男";
		} else if (sex.equalsIgnoreCase(UserGender.Female.toString())) {
			sex = "女";
		}
		RequestParams params = new RequestParams();
		params.put("method", "editMember");
		params.put("siteId", String.valueOf(MConfig.SITE_ID));
		params.put("userName", userName);
		params.put("Name", nickName);
		params.put("Sex", sex);
		params.put("Email", email);
		params.put("Logo", logo);
		client.post(MConfig.mServerUrl, params, listener);
	}

	public static ArrayList<String> getRegistScritpCodeValue(ArrayList<String> codeInfoList) {
		ArrayList<String> codeValueList = new ArrayList<String>();
		for (int i = 0; i < 1; i++)// 默认取第一个来解密
		{
			String item = codeInfoList.get(i);
			item = item.substring(item.indexOf("?") + 1);
			String[] param = item.split("&");
			innerLoop: for (int j = 0; j < param.length; j++) {
				String[] valuePair = param[j].split("=");
				String codeValue = null;
				try {
					codeValue = URLDecoder.decode(valuePair[1], "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if ("code".equals(valuePair[0])) {
					codeValueList.add(codeValue);
					break innerLoop;
				}
			}
		}
		return codeValueList;
	}

	/**
	 * 解析返回Code
	 * 
	 * @param scriptCode
	 * @return
	 */
	public static ArrayList<String> getRegistScriptSrcList(String scriptCode) {
		ArrayList<String> codeInfoList = new ArrayList<String>();
		String headRootTag = "<root>";
		String endRootTag = "</root>";
		String andReplaceMark = "&amp;";
		scriptCode = scriptCode.replaceAll("&", andReplaceMark);
		scriptCode = headRootTag.concat(scriptCode).concat(endRootTag);
		ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(scriptCode.getBytes());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(tInputStringStream);
			Element rootElement = doc.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName("script");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node item = nodeList.item(i);
				NamedNodeMap attribute = item.getAttributes();
				innerloop: for (int j = 0; j < attribute.getLength(); j++) {
					Node attributeItem = attribute.item(j);
					if ("src".equals(attributeItem.getNodeName())) {
						codeInfoList.add(attributeItem.getNodeValue());
						break innerloop;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeInfoList;
	}

	/**
	 * 解密注册脚本里面返回的Code值
	 * 
	 * @param codeData
	 * @return
	 */
	public static String decodeLoginScriptCodeData(String codeData) {
		return new Client().uc_authcode(codeData, "DECODE");
	}

	public static void deleteLoginAuth(Context context) {
		// try {
		// context.deleteDatabase("webview.db");
		// context.deleteDatabase("webviewCache.db");
		// context.deleteDatabase("webviewCookiesChromium.db");
		// context.deleteDatabase("webviewCookiesChromiumPrivate.db");
		// } catch (Exception e) {
		// }
		// if(context.getExternalCacheDir()!=null)
		// FileUtil.delDirectory(context.getExternalCacheDir());
		// if(context.getCacheDir()!=null)
		// FileUtil.delDirectory(context.getCacheDir());
		CookieSyncManager.createInstance(context);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().removeAllCookie();
		CookieManager.getInstance().removeExpiredCookie();
		CookieManager.getInstance().removeSessionCookie();
	}

	// public static void loginOutWithQQ(Context context)
	// {
	// mTencent.logout(context);
	// }
	// public static void LoginWithQQ(Activity activity,IUiListener listener)
	// {
	// mTencent.login(activity, "get_user_info,add_t", listener);
	// }
	// public static void LoginWithQQ(Fragment fragment,IUiListener listener)
	// {
	// mTencent.login(fragment, "get_user_info,add_t", listener);
	// }

	public static String[] shortUrl(String url) {
		// 可以自定义生成 MD5 加密字符传前的混合 KEY
		String key = "test";
		// 要使用生成 URL 的字符
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z"

		};
		// 对传入网址进行 MD5 加密
		String hex = md5ByHex(key + url);

		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {

			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);

			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 ,
			// 如果不用long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			for (int j = 0; j < 6; j++) {
				// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000003D & lHexLong;
				// 把取得的字符相加
				outChars += chars[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 5;
			}
			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}
		return resUrl;
	}

	/**
	 * MD5加密(32位大写)
	 * 
	 * @param src
	 * @return
	 */
	public static String md5ByHex(String src) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = src.getBytes();
			md.reset();
			md.update(b);
			byte[] hash = md.digest();
			String hs = "";
			String stmp = "";
			for (int i = 0; i < hash.length; i++) {
				stmp = Integer.toHexString(hash[i] & 0xFF);
				if (stmp.length() == 1)
					hs = hs + "0" + stmp;
				else {
					hs = hs + stmp;
				}
			}
			return hs.toUpperCase(Locale.getDefault());
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 清除第三方登录session
	 * 
	 * @param context
	 */
	public static void socialLoginOut(Context context) {
		mUmSocialService.deleteOauth(context, SHARE_MEDIA.SINA, null);
		mUmSocialService.deleteOauth(context, SHARE_MEDIA.QQ, null);
		mUmSocialService.deleteOauth(context, SHARE_MEDIA.WEIXIN, null);
	}

	/**
	 * 第三方登录的用户信息
	 * 
	 * @author zouxudong
	 * 
	 */
	public static class SocialUserInfo {
		public static final String MALE = "男";
		public static final String FEFAMLE = "女";
		private String sex = MALE;
		private String nickName = "";
		private String headURL = "";
		private String uid = "";

		public String getSex() {
			return sex;
		}

		public String getNickName() {
			return nickName;
		}

		public String getHeadURL() {
			return headURL;
		}

		public String getUID() {
			return uid;
		}

		public static SocialUserInfo getSocialUserInfo(Map<String, Object> info, final SHARE_MEDIA platform) {
			SocialUserInfo userInfo = new SocialUserInfo();
			switch (platform) {
			case QQ:
				userInfo.headURL = String.valueOf(info.get("profile_image_url"));
				userInfo.nickName = String.valueOf(info.get("screen_name"));
				userInfo.sex = MALE.equals(String.valueOf(info.get("gender"))) ? MALE : FEFAMLE;
				userInfo.uid = String.valueOf(info.get("uid"));
				break;
			case SINA:
				userInfo.headURL = String.valueOf(info.get("profile_image_url"));
				userInfo.nickName = String.valueOf(info.get("screen_name"));
				userInfo.sex = "1".equals(String.valueOf(info.get("gender"))) ? MALE : FEFAMLE;
				userInfo.uid = String.valueOf(info.get("id"));
				break;
			case WEIXIN:
				userInfo.headURL = String.valueOf(info.get("headimgurl"));
				userInfo.nickName = String.valueOf(info.get("nickname"));
				userInfo.sex = "1".equals(String.valueOf(info.get("sex"))) ? MALE : FEFAMLE;
				userInfo.uid = String.valueOf(info.get("openid"));
				// userInfo.uid = String.valueOf(info.get("uid"));
				break;
			case TENCENT:
				userInfo.headURL = String.valueOf(info.get("profile_image_url"));
				userInfo.nickName = String.valueOf(info.get("screen_name"));
				userInfo.sex = "1".equals(String.valueOf(info.get("gender"))) ? MALE : FEFAMLE;
				userInfo.uid = String.valueOf(info.get("uid"));
				break;
			default:
				return userInfo;
			}
			return userInfo;
		}

	}

}
