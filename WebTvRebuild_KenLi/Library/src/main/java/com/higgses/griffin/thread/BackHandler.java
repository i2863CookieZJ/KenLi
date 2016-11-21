package com.higgses.griffin.thread;

import com.higgses.griffin.thread.inf.GinIExecuteThread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 后台运行的线程拥有Handler的全部功能,允许在executing中修改UI界面
 * Create by YangQiang on 13-8-24.
 */
public class BackHandler extends Handler implements GinIExecuteThread<BackHandler, Object>
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
	 * 是否停止执行后面的回调
	 */
	private boolean mIsStop = false;
	private Runnable mRunnable;

	public BackHandler(Context context)
	{
		mContext = context;
	}

	@Override
	public Object preExecute(BackHandler thread)
	{
		return null;
	}

	@Override
	public Object executing(BackHandler backThread, Object startReturnObj)
	{
		return null;
	}

	@Override
	public void executeFinish(BackHandler backThread, Object exeReturnObj)
	{
		stopExecute();
	}

	@Override
	public void handleMessage(Message msg)
	{
		if(msg.what == 0 && !mIsStop)
		{
			executeFinish(BackHandler.this, mExeObj);
		}
	}

	@Override
	public final void execute()
	{
		mStartObj = preExecute(this);
		mRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				if(!mIsStop)
				{
					mExeObj = executing(BackHandler.this, mStartObj);
					sendEmptyMessage(0);
				}
			}
		};
		this.post(mRunnable);
	}

	@Override
	public void stopExecute()
	{
		mIsStop = true;
		if(mRunnable != null)
		{
			this.removeCallbacks(mRunnable);
		}
	}

	@Override
	public Context getContext()
	{
		return mContext;
	}
}
