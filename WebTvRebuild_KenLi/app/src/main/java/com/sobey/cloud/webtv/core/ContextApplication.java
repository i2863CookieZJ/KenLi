package com.sobey.cloud.webtv.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.higgses.griffin.imageloader.utils.GinStorage;
import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.utils.GinUSharedP;
import com.sobey.cloud.webtv.config.AppConfig;
import com.sobey.cloud.webtv.config.SharedConstant;

import android.os.Handler;

public class ContextApplication extends BaseApplication
{
    private static final String TAG = "ContextApplication";

    /**
     * 全局的Handler
     */
    private Map<String, Handler> globalHandlers;


    public static ContextApplication getApp()
    {
        return (ContextApplication) getApplication();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        init();
    }

    private void init()
    {
        globalHandlers = new HashMap<>();
    }


    /**
     * 添加一个全局的Handler
     *
     * @param handler
     */
    public void addGlobalHandler(String tag, Handler handler)
    {
        globalHandlers.put(tag, handler);
    }

    /**
     * 返回一个全局Handler
     *
     * @param tag
     * @return
     */
    public Handler getGlobalHandler(String tag)
    {
        if (globalHandlers.containsKey(tag))
        {
            return globalHandlers.get(tag);
        }
        return null;
    }


    /**
     * 图片保存的位置
     *
     * @return
     */
    public File getImageFile()
    {
        return new File(getImageFilePath());
    }

    public String getImageFilePath()
    {
        return GinStorage.getFileDirectory(this) + File.separator + AppConfig.IMAGE_DIR;
    }

    public String getDownloadFilePath()
    {
        return GinStorage.getFileDirectory(this) + File.separator + AppConfig.DOWNLOAD_DIR;
    }


    /**
     * 设置登录状态
     *
     * @param isLogin
     */
    private void setLoginState(boolean isLogin)
    {
        GinLog.i(TAG, "设置登录状态:" + isLogin);
        GinUSharedP.putBoolean(this, SharedConstant.User.NAME, SharedConstant.User.LOGIN_STATUS, isLogin);
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isUserLogin()
    {
        return GinUSharedP.getBoolean(this, SharedConstant.User.NAME, SharedConstant.User.LOGIN_STATUS);
    }


}