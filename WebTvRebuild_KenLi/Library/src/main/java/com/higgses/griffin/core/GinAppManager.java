package com.higgses.griffin.core;

import java.util.Stack;

import com.higgses.griffin.core.inf.GinIActivity;
import com.higgses.griffin.log.GinLog;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 应用管理类
 */
public class GinAppManager
{
    private static final String TAG = "GinAppManager";
    private static Stack<Activity> mActivityStack;
    private static GinAppManager mInstance;

    private GinAppManager()
    {
    }

    /**
     * 单一实例
     */
    public static GinAppManager getAppManager()
    {
        if (mInstance == null)
        {
            synchronized (GinAppManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new GinAppManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity)
    {
        if (mActivityStack == null)
        {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
        GinLog.i(TAG, "Activity栈:" + mActivityStack);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity()
    {
        Activity activity = null;
        if (mActivityStack.size() > 0)
        {
            activity = mActivityStack.lastElement();
            GinLog.i(TAG, "当前Activtiy:" + activity);
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity()
    {
        if (mActivityStack.empty())
        {
            return;
        }
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity)
    {
        if (activity != null)
        {
            mActivityStack.remove(activity);
            activity.finish();
            GinLog.i(TAG, "结束Activity:" + activity);
            activity = null;
        }
    }

    /**
     * 返回
     */
    public void onBackPressed()
    {
        finishActivity();
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(GinIActivity activity)
    {
        if (activity != null)
        {
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls)
    {
        for (Activity activity : mActivityStack)
        {
            if (activity.getClass().equals(cls))
            {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity()
    {
        for (int i = 0, size = mActivityStack.size(); i < size; i++)
        {
            if (null != mActivityStack.get(i))
            {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     *
     * @param context
     *         上下文
     * @param isBackground
     *         是否开开启后台运行
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context, Boolean isBackground)
    {
        try
        {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        }
        catch (Exception e)
        {

        }
        finally
        {
            // 注意，如果您有后台程序运行，请不要支持此句子
            if (!isBackground)
            {
                System.exit(0);
            }
        }
    }
}
