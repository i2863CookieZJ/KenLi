package com.sobey.cloud.webtv.core;

import com.higgses.griffin.core.app.AbstractApplication;
import com.higgses.griffin.log.GinErrorTxtFileLogger;
import com.higgses.griffin.log.GinLog;
import com.higgses.griffin.log.GinTxtFileLogger;
import com.sobey.cloud.webtv.config.FlavorsConstant;

/**
 * 基础Application
 * Created by higgses on 14-4-1.
 */
public class BaseApplication extends AbstractApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        GinLog.setDebug(FlavorsConstant.DEBUG);
        GinLog.addLogger(new GinTxtFileLogger());
        GinLog.addLogger(new GinErrorTxtFileLogger());
    }

    /**
     * 是否是beta版本
     *
     * @return
     */
    public boolean isBeta()
    {
        return FlavorsConstant.DevType.BETA == FlavorsConstant.DEV_VERSION;
    }

    /**
     * 是否是开发版本
     *
     * @return
     */
    public boolean isDevelop()
    {
        return FlavorsConstant.DevType.DEBUG == FlavorsConstant.DEV_VERSION;
    }

    /**
     * 是否是发布版
     *
     * @return
     */
    public boolean isRelease()
    {
        return FlavorsConstant.DevType.RELEASE == FlavorsConstant.DEV_VERSION;
    }

    @Override
    protected boolean isUncaughtException()
    {
        return false;
    }
}
