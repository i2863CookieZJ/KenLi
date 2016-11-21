/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.higgses.griffin.log.inf.GinILogger;
import com.higgses.griffin.utils.GinUCalendar;

/**
 * 保存到文件的日志记录
 * 需要添加权限：
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 */
public class GinTxtFileLogger implements GinILogger
{
    private boolean isOpen = true;

    @Override
    public int d(String tag, String message)
    {
        return writeLogToFile(tag, message);
    }

    @Override
    public int d(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int e(String tag, String message)
    {
        return writeLogToFile(tag, message);
    }

    @Override
    public int e(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int i(String tag, String message)
    {
        return writeLogToFile(tag, message);
    }

    @Override
    public int i(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int v(String tag, String message)
    {
        return writeLogToFile(tag, message);
    }

    @Override
    public int v(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public int w(String tag, String message)
    {
        return writeLogToFile(tag, message);
    }

    @Override
    public int w(String tag, String msg, Throwable tr)
    {
        return 0;
    }

    @Override
    public void println(int priority, String tag, String message)
    {
        GinLog.println(priority, tag, message);
        writeLogToFile(tag, message);
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

    /**
     * 返回日志路径
     *
     * @return
     */
    public String getLogPath()
    {
        return android.os.Environment.getExternalStorageDirectory().getPath() + File.separator + "GinLog" + ".log";
    }

    @Override
    public boolean isOpen()
    {
        return isOpen;
    }

    /**
     * 把日志记录到文件
     *
     * @param tag
     * @param message
     */
    private int writeLogToFile(String tag, String message)
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(GinUCalendar.getNowDataTime());
        stringBuffer.append("    ");
        stringBuffer.append(tag);
        stringBuffer.append("    ");
        stringBuffer.append(message);
        stringBuffer.append("\n");
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(new FileWriter(getLogPath(), true));
            writer.append(stringBuffer);
        }
        catch (Exception e)
        {
            //            e.printStackTrace();
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
        return 0;
    }
}
