package com.higgses.griffin.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import com.higgses.griffin.core.inf.GinIControl;
import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.core.inf.GinIObserver;
import com.higgses.griffin.log.GinLog;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * 基本的MVC模式中的模型，所有模型继承该类实现模型功能 BaseModel实现是被观察者接口，是一个主题，数据发生变化可以通知到观察者 Create by
 * YangQiang on 13-10-8.
 */
public abstract class AbstractModel implements GinIModel
{
    private final String  TAG      = "AbstractModel";
    /**
     * 模型数据是否改变的状态
     */
    private       boolean mChanged = false;
    /**
     * 保存观察者，MVC模式中的视图
     */
    private Vector<Object> mObs;
    /**
     * 控制器
     */
    private GinIControl mControl;

    public AbstractModel(GinIControl control)
    {
        mControl = control;
        mObs = new Vector<>();
        setChanged();
    }

    @Override
    public GinIControl getControl()
    {
        return mControl;
    }

    public Context getContext()
    {
        if (mControl instanceof Fragment)
        {
            Fragment fragment = (Fragment) mControl;
            return fragment.getActivity();
        }
        else
        {// (mControl instanceof Activity)
            return (Context) mControl;
        }
    }

    @Override
    public void notifyObserves(Object data, String method)
    {
        notifyObserves(data.getClass(), data, method);
    }

    @Override
    public void notifyObserves(Class<?> dataClassType, Object data, String method)
    {
        Object[] arrLocal;
        synchronized (this)
        {
            if (!mChanged)
            {
                return;
            }
            arrLocal = mObs.toArray();
            clearChanged();
        }
        for (int i = arrLocal.length - 1; i >= 0; --i)
        {
            notifyObserve((GinIObserver) arrLocal[i], dataClassType, data, method);
        }
    }

    /**
     * 通知视图有数据更新
     *
     * @param iObserver
     * @param data
     * @param method
     */
    protected void notifyObserve(GinIObserver iObserver, Class<?> dataClassType, Object data, String method)
    {
        try
        {
            if (iObserver != null)
            {
                // 视图的类型
                Class<? extends GinIObserver> viewClassType = iObserver.getClass();
                if (method.equals("update"))
                {// 默认的更新数据的会掉
                    dataClassType = Object.class;
                }
                else
                {
                    if (data != null)
                    {
                        dataClassType = data.getClass();
                    }
                }
                // 模型的类型
                Method invokeMethod;
                if (data instanceof NullObject)
                {// 回调一个无参数的方法
                    invokeMethod = viewClassType.getMethod(method);
                    invokeMethod.invoke(iObserver);
                }
                else
                {
                    invokeMethod = viewClassType.getMethod(method, new Class[]{dataClassType});
                    invokeMethod.invoke(iObserver, data);
                }
            }
        }
        catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
        {
            GinLog.e(TAG, GinLog.getPrintException(e));
        }
    }

    protected void notifyObserves(Object data)
    {
        notifyObserves(data, "update");
    }

    @Override
    public synchronized void addObserves(GinIObserver observer)
    {
        if (observer == null)
        {
            throw new NullPointerException();
        }
        if (!mObs.contains(observer))
        {// 当前的观察者中没有传递进来的观察者，则添加到观察者列表
            mObs.addElement(observer);
        }
    }

    @Override
    public synchronized void deleteObserver(GinIObserver observer)
    {
        mObs.removeElement(observer);
    }

    @Override
    public synchronized void deleteObservers()
    {
        mObs.removeAllElements();
    }

    @Override
    public synchronized void setChanged()
    {
        mChanged = true;
    }

    @Override
    public synchronized void clearChanged()
    {
        mChanged = false;
    }

    @Override
    public synchronized boolean hasChanged()
    {
        return mChanged;
    }

    @Override
    public synchronized int countObservers()
    {
        return mObs.size();
    }

    /**
     * 默认数据改变的回调
     * 签名：public void update(Object data);
     *
     * @param data
     */
    public synchronized void dataCallback(Object data)
    {
        setChanged();
        notifyObserves(data);
    }

    @Override
    public synchronized void dataCallback()
    {
        dataCallback("update");
    }

    /**
     * 数据改变的回调
     *
     * @param data
     * @param method
     */
    public synchronized void dataCallback(Object data, String method)
    {
        if (data == null)
        {
            throw new NullPointerException("传递的数据不能为空，如果传递了空数据则需要使用：public synchronized void dataCallback(Class<?> dataClassType, Object data, String method)");
        }
        dataCallback(null, data, method);
    }

    @Override
    public synchronized void dataCallback(Class<?> dataClassType, Object data, String method)
    {
        setChanged();
        notifyObserves(dataClassType, data, method);
    }

    public synchronized void dataCallback(Class<?> dataClassType, String method)
    {
        dataCallback(dataClassType, null, method);
    }

    /**
     * 数据改变回调
     *
     * @param method
     */
    public synchronized void dataCallback(String method)
    {
        dataCallback(new NullObject(), method);
    }

    static class NullObject
    {

    }
}
