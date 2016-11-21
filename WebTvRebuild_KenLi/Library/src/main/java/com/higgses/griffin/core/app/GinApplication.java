package com.higgses.griffin.core.app;

import java.lang.Thread.UncaughtExceptionHandler;

import com.higgses.griffin.annotation.app.GinInjector;
import com.higgses.griffin.core.GinAppManager;
import com.higgses.griffin.core.inf.GinIActivity;
import com.higgses.griffin.exception.GinAppException;
import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.netstate.GinNetChangeObserver;
import com.higgses.griffin.netstate.GinNetworkStateReceiver;

import android.app.Application;
import android.content.Context;

import com.higgses.griffin.netstate.utils.GinUNetWork;

/**
 * Application
 */
public class GinApplication extends Application
{
    private static final String TAG = "GinApplication";
    /**
     * 程序崩溃问题异常处理
     */
    private UncaughtExceptionHandler mUncaughtExceptionHandler;
    /**
     * 当前的Activity,在Activity栈最顶上的Activity
     */
    private GinIActivity mCurrentActivity;
    /**
     * Activity管理器
     */
    private GinAppManager mAppManager;
    /**
     * 网络是否有效
     */
    private Boolean mNetworkAvailable = false;
    /**
     * 网络改变的观察者，网络改变后会被通知
     */
    private GinNetChangeObserver mNetChangeObserver;
    /**
     * 注解加载器
     */
    private GinInjector mInjector;
    /**
     * Application的单实例
     */
    private static GinApplication mInstance;
    /**
     * CoreHelper
     */
    private        CoreHelper           mCoreHelper;

    @Override
    public void onCreate()
    {
        onPreCreate();
        super.onCreate();
        init();
        onCreating();
        onAfterCreate();
        getAppManager();
    }

    /**
     * 初始化数据,紧接着super.onCreate()后调用
     */
    private void init()
    {
        mInstance = this;
        mCoreHelper = CoreHelper.getInstance();
        initImageLoader(getApplicationContext());
    }

    /**
     * 初始化图片加载类型
     * 重写这个方法能提供缓存图片的方式
     *
     * @param context
     */
    protected void initImageLoader(Context context)
    {
        mCoreHelper.initImageLoader(context);
    }

    /**
     * 获取Application实例
     *
     * @return
     */
    public static GinApplication getApplication()
    {
        return mInstance;
    }

    /**
     * 正在创建Application，在Application中的onCreate（）中调用，
     */
    private void onCreating()
    {
        mInstance = this;
        // 注册App异常崩溃处理器
        if (isUncaughtException())
        {
            Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
        }
        // 网络改变的通知
        mNetChangeObserver = new GinNetChangeObserver()
        {
            @Override
            public void onConnect(GinUNetWork.NetType type)
            {
                super.onConnect(type);
                GinApplication.this.onConnect(type);
            }

            @Override
            public void onDisConnect()
            {
                super.onDisConnect();
                GinApplication.this.onDisConnect();
            }
        };
        // 注册网络改变观察者
        GinNetworkStateReceiver.registerObserver(mNetChangeObserver);
    }

    /**
     * 是否异常补获，如果返回true，那么程序崩溃就不会出现FC窗口
     *
     * @return
     */
    protected boolean isUncaughtException()
    {
        return false;
    }

    /**
     * 没有网络连接的时候回调
     */
    protected void onDisConnect()
    {
        mNetworkAvailable = false;
        if (mCurrentActivity != null)
        {
            mCurrentActivity.onDisConnect();
        }
    }

    /**
     * 网络连接连接时回调
     */
    protected void onConnect(GinUNetWork.NetType type)
    {
        mNetworkAvailable = true;
        if (mCurrentActivity != null)
        {
            mCurrentActivity.onConnect(type);
        }
    }

    /**
     * Application.onCreate()执行后执行
     */
    protected void onAfterCreate()
    {

    }

    /**
     * Application.onCreate()执行前执行
     */
    protected void onPreCreate()
    {

    }

    /**
     * 设置 App异常崩溃处理器
     *
     * @param uncaughtExceptionHandler
     */
    public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        mUncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    /**
     * 返回一个自定义的程序异常处理类，默认返回一个GinAppException处理
     *
     * @return
     *
     * @see GinAppException
     */
    protected UncaughtExceptionHandler getUncaughtExceptionHandler()
    {
        if (mUncaughtExceptionHandler == null)
        {
            mUncaughtExceptionHandler = GinAppException.getInstance(this);
        }
        return mUncaughtExceptionHandler;
    }

    /**
     * 返回应用管理类，批量退出Activity或者获取当前Activity等
     *
     * @return
     */
    public GinAppManager getAppManager()
    {
        if (mAppManager == null)
        {
            mAppManager = GinAppManager.getAppManager();
        }
        return mAppManager;
    }

    /**
     * 退出应用程序
     *
     * @param isBackground
     *         是否开开启后台运行,如果为true则为后台运行
     */
    public void exitApp(Boolean isBackground)
    {
        GinLog.d(TAG, "退出程序");
        mAppManager.AppExit(this, isBackground);
    }

    /**
     * 获取当前网络状态，true为网络连接成功，否则网络连接失败
     *
     * @return
     */
    public boolean isNetworkAvailable()
    {
        return mNetworkAvailable;
    }

    /**
     * 当一个Activity正在创建的时候回调，它被回调会在
     * Activity.onCreate()中的super.onCreate()之前调用。
     *
     * @param activity
     *         正在被创建的Activity
     */
    protected void onActivityCreating(GinIActivity activity)
    {
    }

    /**
     * 当一个Activity被创建的时候回调，它会被回调在Activity.onCreate()中super.onCreate()后调用
     *
     * @param activity
     */
    protected void onActivityCreated(GinIActivity activity)
    {
        if (activity == null)
        {
            throw new NullPointerException("Activity is don't allow null");
        }
        mCurrentActivity = activity;
    }

    /**
     * 返回Activity，在Activity的onBackPressed中调用，用于管理Activity
     */
    public void onBackPressed()
    {
        mAppManager.onBackPressed();
        setCurrentActivity();
    }

    /**
     * 停止一个Activity，同Activity.finish(),用于管理Activity
     */
    public void finishActivity()
    {
        mAppManager.finishActivity();
        setCurrentActivity();
    }

    /**
     * 从AppManger中移除最后一个Activity，和finishActivity()不同的是，这里移除的Activity不会调用Activity.finish();
     */
    public void removeLastActivity()
    {
        mAppManager.removeActivity(mCurrentActivity);
        setCurrentActivity();
    }

    /**
     * 设置当前显示的Activity
     */
    private void setCurrentActivity()
    {
        android.app.Activity currentActivity = mAppManager.currentActivity();
        if (currentActivity instanceof Activity)
        {
            mCurrentActivity = (Activity) currentActivity;
        }
    }

    /**
     * 返回注解加载器
     *
     * @return
     */
    public GinInjector getInjector()
    {
        if (mInjector == null)
        {
            mInjector = GinInjector.getInstance();
        }
        return mInjector;
    }
}
