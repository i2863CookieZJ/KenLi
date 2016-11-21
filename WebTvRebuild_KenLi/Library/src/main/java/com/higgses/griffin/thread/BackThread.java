package com.higgses.griffin.thread;

import com.higgses.griffin.thread.inf.GinIExecuteThread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 后台运行的线程拥有Thread的功能
 * Create by YangQiang on 13-8-22.
 */
public class BackThread extends Thread implements GinIExecuteThread<BackThread, Object>
{
	/**
	 * 开始执行后返回的对戏那个
	 */
	private Object mStartObj;
	/**
	 * 执行完成返回的对象
	 */
	private Object mExeObj;
	/**
	 * Context
	 */
	private Context mContext;
	/**
	 * Handler
	 */
	private Handler mHandler;
	/**
	 * 是否停止执行后面的回调
	 */
	private boolean mIsStop = false;

	public BackThread(Context context)
	{
		mContext = context;
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(!mIsStop)
				{
					BackThread.this.executeFinish(BackThread.this, mExeObj);
				}
			}
		};
	}
	public BackThread()
	{
		this(null);
	}
	@Override
	public void run()
	{
		if(!mIsStop)
		{
			mExeObj = this.executing(this, mStartObj);
			mHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public void stopExecute()
	{
		mIsStop = true;
		this.interrupt();
	}

	@Override
	public Context getContext()
	{
		return mContext;
	}

	@Override
	public Object preExecute(BackThread thread)
	{
		return null;
	}

	@Override
	public Object executing(BackThread backThread, Object startReturnObj)
	{
		return null;
	}

	@Override
	public void executeFinish(BackThread backThread, Object exeReturnObj)
	{
		this.stopExecute();
	}

	@Override
	public void execute()
	{
		mStartObj = preExecute(this);
		start();
	}
}
