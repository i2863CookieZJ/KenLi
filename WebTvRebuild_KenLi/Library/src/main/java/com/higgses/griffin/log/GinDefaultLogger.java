package com.higgses.griffin.log;

import com.higgses.griffin.log.inf.GinILogger;

import android.util.Log;

/**
 * 默认的日志，主要用于控制台输出
 */
public class GinDefaultLogger implements GinILogger
{
    private boolean isOpen = true;


    @Override
    public void println(int priority, String tag, String message)
    {
        Log.println(priority, tag, message);
    }

    @Override
    public int v(String tag, String msg)
    {
        return Log.v(tag, msg);
    }

    @Override
    public int v(String tag, String msg, Throwable tr)
    {
        return Log.v(tag, msg, tr);
    }

    @Override
    public int d(String tag, String msg)
    {
        return Log.d(tag, msg);
    }

    @Override
    public int d(String tag, String msg, Throwable tr)
    {
        return Log.d(tag, msg, tr);
    }

    @Override
    public int i(String tag, String msg)
    {
        return Log.i(tag, msg);
    }

    @Override
    public int i(String tag, String msg, Throwable tr)
    {
        return Log.i(tag, msg, tr);
    }

    @Override
    public int w(String tag, String msg)
    {
        return Log.w(tag, msg);
    }

    @Override
    public int w(String tag, String msg, Throwable tr)
    {
        return Log.w(tag, msg, tr);
    }

    @Override
    public int e(String tag, String msg)
    {
        return Log.e(tag, msg);
    }

    @Override
    public int e(String tag, String msg, Throwable tr)
    {
        return Log.e(tag, msg, tr);
    }

    @Override
    public void open()
    {
        isOpen = true;
    }

    @Override
    public void close()
    {
        isOpen = false;
    }

    @Override
    public boolean isOpen()
    {
        return isOpen;
    }
}
