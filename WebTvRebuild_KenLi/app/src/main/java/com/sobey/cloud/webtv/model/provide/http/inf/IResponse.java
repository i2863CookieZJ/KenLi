package com.sobey.cloud.webtv.model.provide.http.inf;

/**
 * 数据响应接口 Created by higgses on 14-5-13.
 */
public interface IResponse<T> {
	/**
	 * 响应的数据回调
	 *
	 * @param data
	 */
	public void onResponse(T data);

	/**
	 * 错误返回回掉
	 *
	 * @param error
	 */
	public void onError(Object error);
}
