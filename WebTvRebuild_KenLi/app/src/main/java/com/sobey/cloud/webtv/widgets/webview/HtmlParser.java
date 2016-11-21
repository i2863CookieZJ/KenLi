package com.sobey.cloud.webtv.widgets.webview;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.web.Md5Builder;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;

public class HtmlParser extends AsyncTask<Void, Void, String> {

	public static final String Js2JavaClickDownload = "ClickDownload";
	public static final String Js2JavaShowImage = "ShowImage";
	public static final String DefaultImgSrc = "file:///android_asset/click_show.png";
	public static final String LoadingImgSrc = "file:///android_asset/loading.gif";
	public static final String NoneImgSrc = "file:///android_asset/download_fail.png";

	private String htmlData;
	private WebView webView;
	private boolean isShowImage;
	private ArrayList<ImageUrlInfo> imageUrlList = new ArrayList<ImageUrlInfo>();

	public HtmlParser(WebView wevView, String htmlData, boolean isShowImage) {
		this.webView = wevView;
		this.htmlData = htmlData;
		this.isShowImage = isShowImage;
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.d("HtmlParser", "beging parse" + new Date());
		Document doc = Jsoup.parse(htmlData);
		Elements es = doc.select("script");
		if (es != null) {
			es.remove();
		}
		Elements es1 = doc.select("style");
		if (es1 != null) {
			es1.remove();
		}
		handleImageClickEvent(doc);
		removeHyperlinks(doc);
		String htmlText = handleDocument(doc);
		return htmlText;
	}

	private String handleDocument(Document doc) {
		Log.d("HtmlParser", "end parse" + new Date());
		return doc.toString();
	}

	private void handleImageClickEvent(Document doc) {
		String isShowImageDo = "";
		Elements es = doc.getElementsByTag("img");
		for (Element e : es) {
			String imgUrl = e.attr("src");
			String imgName;
			File file = new File(imgUrl);
			imgName = file.getName();
			// 居然随机生成6个数字当文件名？卧槽哪个sb写的，怪不得同一张图片缓存了n张。
			// String randomName = getRandomFixLenthString(6) + "." +
			// getExtensionName(imgName);
			String randomName = getFileName(imgUrl);
			String filePath = "file://" + Environment.getExternalStorageDirectory() + MConfig.CachePath + "/"
					+ randomName;
			imageUrlList.add(new ImageUrlInfo(imgUrl, randomName, filePath));
			e.attr("id", randomName);
			e.attr("src", DefaultImgSrc);
			e.removeAttr("style");
			e.attr("style", "width:100%;height:auto;");
			if (isShowImage) {
				isShowImageDo += "window." + Js2JavaClickDownload + ".setImgSrc('" + imgUrl + "', '" + filePath + "', '"
						+ randomName + "');";
			} else {
				String str = "window." + Js2JavaClickDownload + ".setImgSrc('" + imgUrl + "', '" + filePath + "', '"
						+ randomName + "')";
				e.attr("onclick", str);
			}
		}
		if (isShowImage) {
			doc.getElementsByTag("body").get(0)
					.append("<script>window.onload=function(){" + isShowImageDo + "}</script>");
		}
		doc.getElementsByTag("body").get(0).append("<script>function mShowImage(imageId){" + "window."
				+ Js2JavaShowImage + ".showImage(imageId);" + "}</script>");
	}

	private void removeHyperlinks(Document doc) {
		Elements hrefs = doc.getElementsByTag("a");
		for (Element href : hrefs) {
			href.removeAttr("href");
		}
	}

	@Override
	protected void onPostExecute(String result) {
		webView.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
		super.onPostExecute(result);
	}

	/*
	 * 返回长度为strLength的随机数，在前面补0
	 */
	private static String getRandomFixLenthString(int strLength) {
		Random rm = new Random();
		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);
		// 返回固定的长度的随机数
		return fixLenthString.substring(1, strLength + 1);
	}

	/**
	 * 根据文件路径获得文件名,可以是网络路径或者本地路径
	 *
	 * @param filePath
	 *            传入的文件路径
	 * @return
	 */
	public static String getFileName(String imgUrl) {
//		if (filePath.contains("/")) {
//			return filePath.substring(filePath.lastIndexOf("/") + 1);
//		}
//		return filePath;
		

		String imageName = Md5Builder.getMD5(imgUrl, "MD5");
		imageName = imageName + imgUrl.substring(imgUrl.lastIndexOf("."), imgUrl.length());
		return imageName;
	}

	/*
	 * Java文件操作 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	protected ArrayList<ImageUrlInfo> getImageUrlInfo() {
		return imageUrlList;
	}

	protected void setDownloadState(String imageId) {
		if (imageUrlList != null) {
			for (int i = 0; i < imageUrlList.size(); i++) {
				if (imageId.equalsIgnoreCase(imageUrlList.get(i).getImageId())) {
					imageUrlList.get(i).setDownload(true);
				}
			}
		}
	}

}
