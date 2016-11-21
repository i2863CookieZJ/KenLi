package com.higgses.griffin.thread.inf;

import android.content.Context;

/**
 * 后台线程执行的接口
 *
 * Create by YangQiang on 13-8-24.
 */
public interface GinIExecuteThread<Self, Obj>
{
	/**
	 * 运行前准备
	 *
	 * @param thread 后台执行对象的引用
	 */
	public Obj preExecute(Self thread);

	/**
	 * 正在运行
	 *
	 * @param backThread     后台执行对象的引用
	 * @param startReturnObj 返回的对象
	 */
	public Obj executing(Self backThread, Object startReturnObj);

	/**
	 * 执行完毕
	 *
	 * @param backThread   后台执行对象的引用
	 * @param exeReturnObj 返回的对象
	 */
	public void executeFinish(Self backThread, Object exeReturnObj);

	/**
	 * 执行
	 */
	public void execute();

	/**
	 * 停止流程
	 */
	public void stopExecute();

	/**
	 * 返回Context
	 * @return
	 */
	public Context getContext();
}
