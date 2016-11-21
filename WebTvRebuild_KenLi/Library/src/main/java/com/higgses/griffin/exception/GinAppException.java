package com.higgses.griffin.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import com.higgses.griffin.core.app.GinApplication;
import com.higgses.griffin.log.GinLog;

import android.content.Context;

public class GinAppException implements UncaughtExceptionHandler
{
    public static final String TAG = "CrashHandler";
    private static GinAppException instance;
    private        Context                  mContext;
    private        UncaughtExceptionHandler mDefaultHandler;

    private GinAppException(Context context)
    {
        init(context);
    }

    public static GinAppException getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new GinAppException(context);
        }
        return instance;
    }

    private void init(Context context)
    {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        GinLog.un("程序Crash异常捕获:", GinLog.getPrintException(ex));
        if (!handleException(ex) && mDefaultHandler != null)
        {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     *
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex)
    {
        if (ex == null)
        {
            return true;
        }
        //        new Thread()
        //        {
        //            @Override
        //            public void run()
        //            {
        //                Looper.prepare();
        //                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false).setMessage("程序崩溃了...").setNeutralButton("我知道了", new OnClickListener()
        //                {
        //                    @Override
        //                    public void onClick(DialogInterface dialog, int which)
        //                    {
        ((GinApplication) mContext).exitApp(false);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
        //                    }
        //                }).create().show();
        //                Looper.loop();
        //            }
        //        }.start();
        return true;
    }
}
