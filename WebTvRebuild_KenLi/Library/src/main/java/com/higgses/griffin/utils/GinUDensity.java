package com.higgses.griffin.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 设备像素、分辨率相关的工具类
 */
public class GinUDensity
{
    private GinUDensity()
    {

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     *         dp值
     *
     * @return 返回像素值
     */
    public static int dipToPx(Context context, float dpValue)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
