package com.sobey.cloud.webtv.sdk.web.js;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.gson.GsonBuilder;
import com.higgses.griffin.log.GinLog;
import com.sobey.cloud.webtv.sdk.web.JSRequest;
import com.sobey.cloud.webtv.sdk.web.JSResponse;
import com.sobey.cloud.webtv.sdk.web.inf.ILoadUrlListener;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * JavaScript辅助类
 * Created by Carlton on 2014/7/11.
 */
@SuppressLint("NewApi")
public class JavaScriptHelper
{
    private static final String TAG = "JavaScriptHelper";

    /**
     * H5调用客户端功能的统一入口, 参数类型是String
     *
     * @param tagClass
     * @param method
     * @param data
     */
    public void jsClient(Object tagClass, String method, String data)
    {
        jsClient(tagClass, method, data, String.class);
    }

    /**
     * H5调用客户端功能的统一入口，功能参数类型是Request
     *
     * @param tagClass
     *         提供功能的类
     * @param method
     *         功能名称
     * @param data
     *         数据
     */
    public void jsClientWithRequest(Object tagClass, String method, String data)
    {
        jsClient(tagClass, method, data, new Class[]{JSRequest.class});
    }

    /**
     * H5调用客户端功能的统一入口
     *
     * @param tagClass
     * @param method
     * @param data
     * @param returnCls
     */
    private void jsClient(Object tagClass, String method, String data, Class<?>... returnCls)
    {
        GinLog.i(TAG, "Html5 <-<-<-：" + method + "_" + data);
        try
        {
            Method thisMethod = tagClass.getClass().getMethod(method, returnCls);
            try
            {
                if (returnCls != null && returnCls.length > 0 && returnCls[0].getName().equals(String.class.getName()))
                    {
                        thisMethod.invoke(tagClass, data);
                }
                else
                {
                    JSRequest request = formJsonToRequest(data);
                    thisMethod.invoke(tagClass, request);
                }
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 回调数据给JS的方法
     *
     * @param listener
     * @param name
     * @param data
     */
    public void jsCallback(ILoadUrlListener listener, String name, Object data)
    {
        data = (data == null || TextUtils.isEmpty(data.toString())) ? "''" : data;
        String url = "javascript:jsCallback('" + name + "'," + data + ")";
        GinLog.i(TAG, "Html5 ->->->【" + url + "】:" + name + "_" + data);
        if (listener != null)
        {
            listener.loadJavaScript(url);
        }
    }

    /**
     * 返回回调JavaScript的url内容
     *
     * @return
     */
    public static String getJsCallbackContent(JSResponse jsResponse)
    {
        if (jsResponse == null)
        {
            return "";
        }
        String data = jsResponse.getData();
        data = TextUtils.isEmpty(data) ? "''" : data;
        jsResponse.setData(data);
        String method = jsResponse.getResponseMethod();
        String resultData = jsResponse.toString();
        String url = "javascript:jsCallback('" + method + "'," + resultData + ")";
        GinLog.i(TAG, "Html5 Handler ->->->【" + url + "】");
        return url;
    }

    /**
     * 返回Json字符串
     *
     * @param object
     *
     * @return
     */
    public static String toJsonStr(Object object)
    {
        String result = "{}";
        if (object != null)
        {
            result = new GsonBuilder().create().toJson(object);
        }
        return result;
    }

    /**
     * 字符转对象
     *
     * @param data
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T formJson(String data, Class<T> cls)
    {
        if (TextUtils.isEmpty(data) && cls != null)
        {
            try
            {
                return cls.newInstance();
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return new GsonBuilder().create().fromJson(data, cls);
    }

    /**
     * 从字符串数据中提取JSRequest
     *
     * @param data
     *
     * @return
     */
    public static JSRequest formJsonToRequest(String data)
    {
        JSRequest entity;
        entity = new GsonBuilder().create().fromJson(data, JSRequest.class);
        return entity;
    }

    public void jsCallback(Handler handler, JSResponse jsResponse)
    {
        if (handler == null)
        {
            return;
        }
        Message msg = new Message();
        msg.obj = jsResponse;
        handler.sendMessage(msg);
    }
}
