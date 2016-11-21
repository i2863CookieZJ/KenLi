package com.higgses.griffin.core.app;

import com.higgses.griffin.core.GinToast;

/**
 * 抽象Application
 *
 * @author YangQiang
 */
public abstract class AbstractApplication extends GinSharedPreferenceApplication
{
    /**
     * 显示提示
     *
     * @param msg
     */
    public void showToast(String msg)
    {
        GinToast.show(this, msg);
    }

    public void showToast(int resId)
    {
        GinToast.show(this, resId);
    }
}
