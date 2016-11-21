package com.higgses.griffin.core.process;

/**
 * 流程回调接口
 * @author LiuBin
 *
 */
public interface GinIProcess<S,T>
{
	/**
	 * 开始前的准备
	 */
	public void preProcess();
	/**
	 * 正在执行
	 */
	public void processing();
	/**
	 * 执行完毕
	 * @throws Exception 
	 */
	public void processFinish(S self, T data);
}
