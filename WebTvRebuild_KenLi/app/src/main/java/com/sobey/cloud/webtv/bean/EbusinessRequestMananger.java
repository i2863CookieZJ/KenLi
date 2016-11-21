package com.sobey.cloud.webtv.bean;

import org.json.JSONObject;

import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.utils.PreferencesUtil;

import android.content.Context;

/**
 * 负责圈子相关接口的解析类
 * 
 * @author bin
 *
 */
public class EbusinessRequestMananger {

	public static EbusinessRequestMananger ebusinessRequestMananger = null;
	public SobeyBaseResult baseResult = null;
	private final String TAG = this.getClass().getName();

	private EbusinessRequestMananger() {

		baseResult = new SobeyBaseResult();
	}

	public static synchronized EbusinessRequestMananger getInstance() {
		if (null == ebusinessRequestMananger) {
			ebusinessRequestMananger = new EbusinessRequestMananger();
		}

		return ebusinessRequestMananger;
	}

	/**
	 * 获取我的收藏
	 * 
	 * @param uid
	 *            用户id
	 * @param ctx
	 * @param listener
	 */
	public void getMyCollect(int page,int pagesize,Context ctx, final RequestResultListner listener) {

		String uid = PreferencesUtil.getLoggedUserId();

		News.getEbusinessMyCollect(uid,page,pagesize, ctx, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				listener.onFinish(RequestResultParser.parseListEbusinessGoodsModel(result));
			}

			@Override
			public void onNG(String reason) {
				baseResult.resultInfo = reason;
				listener.onFinish(baseResult);
			}

			@Override
			public void onCancel() {
				baseResult.resultInfo = "cancel";
				listener.onFinish(baseResult);
			}
		});

	}
	/**
	 * 获取我的收获地址
	 * 
	 * @param uid
	 *            用户id
	 * @param ctx
	 * @param listener
	 */
	public void getMyAddress(int page,Context ctx, final RequestResultListner listener) {

		String uid = PreferencesUtil.getLoggedUserId();

		News.getEbusinessMyAddress(uid,page, ctx, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				listener.onFinish(RequestResultParser.parseListEbusinessReciverModel(result));
			}

			@Override
			public void onNG(String reason) {
				baseResult.resultInfo = reason;
				listener.onFinish(baseResult);
			}

			@Override
			public void onCancel() {
				baseResult.resultInfo = "cancel";
				listener.onFinish(baseResult);
			}

		});

	}

	/**
	 * 
	 * @author bin
	 *
	 */
	public interface RequestResultListner {

		void onFinish(SobeyType result);
	}
}
