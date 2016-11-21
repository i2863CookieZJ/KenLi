package com.higgses.griffin.netstate;

import java.util.ArrayList;

import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.netstate.utils.GinUNetWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @Title NetworkStateReceiver
 * @Package com.ta.util.netstate
 * @Description 是一个检测网络状态改变的，需要配置： <code>
 * <receiver android:name="com.higgses.gfiffin.netstate.GinNetworkStateReceiver" >
 * <intent-filter>
 * <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
 * <action android:name="uin.android.net.conn.CONNECTIVITY_CHANGE" />
 * </intent-filter>
 * </receiver>
 * </code> 需要开启权限 <uses-permission
 * android:name="android.permission.CHANGE_NETWORK_STATE" />
 * <uses-permission
 * android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission
 * android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission
 * android:name="android.permission.ACCESS_WIFI_STATE" />
 */
public class GinNetworkStateReceiver extends BroadcastReceiver
{
    private final  String  TAG               = "GinNetworkStateReceiver";
    /**
     * 网络是否有效
     */
    private static Boolean mNetworkAvailable = false;
    private static GinUNetWork.NetType mNetType;
    private static       ArrayList<GinNetChangeObserver> mNetChangeObserverArrayList  = new ArrayList<GinNetChangeObserver>();
    private final static String                          ANDROID_NET_CHANGE_ACTION    = "android.net.conn.CONNECTIVITY_CHANGE";
    public final static  String                          DK_ANDROID_NET_CHANGE_ACTION = "uin.android.net.conn.CONNECTIVITY_CHANGE";
    private static BroadcastReceiver mReceiver;

    private static BroadcastReceiver getReceiver()
    {
        if (mReceiver == null)
        {
            mReceiver = new GinNetworkStateReceiver();
        }
        return mReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        mReceiver = GinNetworkStateReceiver.this;
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION) || intent.getAction().equalsIgnoreCase(DK_ANDROID_NET_CHANGE_ACTION))
        {
            GinLog.i(TAG, "网络状态改变.");
            if (!GinUNetWork.isNetworkAvailable(context))
            {
                GinLog.i(TAG, "没有网络连接.");
                mNetworkAvailable = false;
            }
            else
            {
                GinLog.i(TAG, "网络连接成功.");
                mNetType = GinUNetWork.getAPNType(context);
                mNetworkAvailable = true;
            }
            notifyObserver();
        }
    }

    /**
     * 注册网络状态广播
     *
     * @param mContext
     */
    public static void registerNetworkStateReceiver(Context mContext)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DK_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    /**
     * 检查网络状态
     *
     * @param mContext
     */
    public static void checkNetworkState(Context mContext)
    {
        Intent intent = new Intent();
        intent.setAction(DK_ANDROID_NET_CHANGE_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * 注销网络状态广播
     *
     * @param mContext
     */
    public static void unRegisterNetworkStateReceiver(Context mContext)
    {
        if (mReceiver != null)
        {
            try
            {
                mContext.getApplicationContext().unregisterReceiver(mReceiver);
            }
            catch (Exception e)
            {
                GinLog.d("GinNetworkStateReceiver", e.getMessage());
            }
        }

    }

    /**
     * 获取当前网络状态，true为网络连接成功，否则网络连接失败
     *
     * @return
     */
    public static Boolean isNetworkAvailable()
    {
        return mNetworkAvailable;
    }

    public static GinUNetWork.NetType getAPNType()
    {
        return mNetType;
    }

    private void notifyObserver()
    {

        for (int i = 0; i < mNetChangeObserverArrayList.size(); i++)
        {
            GinNetChangeObserver observer = mNetChangeObserverArrayList.get(i);
            if (observer != null)
            {
                if (isNetworkAvailable())
                {
                    observer.onConnect(mNetType);
                }
                else
                {
                    observer.onDisConnect();
                }
            }
        }

    }

    /**
     * 注册网络连接观察者
     *
     * @param observer observer
     */
    public static void registerObserver(GinNetChangeObserver observer)
    {
        if (mNetChangeObserverArrayList == null)
        {
            mNetChangeObserverArrayList = new ArrayList<GinNetChangeObserver>();
        }
        mNetChangeObserverArrayList.add(observer);
    }

    /**
     * 注销网络连接观察者
     *
     * @param observer observer
     */
    public static void removeRegisterObserver(GinNetChangeObserver observer)
    {
        if (mNetChangeObserverArrayList != null)
        {
            mNetChangeObserverArrayList.remove(observer);
        }
    }
}
