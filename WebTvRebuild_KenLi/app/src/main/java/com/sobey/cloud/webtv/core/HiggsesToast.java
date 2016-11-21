package com.sobey.cloud.webtv.core;

import com.higgses.griffin.log.GinLog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
* 自定义Toast
*/
public class HiggsesToast
{
    private static final String TAG = "HiggsesToast";

    private static   String mOldMsg  = "";
    protected static Toast mToast   = null;
    private static   long   mOneTime = 0;
    private static   long   mTwoTime = 0;


    public static Toast makeText(Context context, String text, int duration)
    {
        if (TextUtils.isEmpty(text))
        {
            GinLog.d(TAG, "提示信息为空！");
            return null;
        }
        return Toast.makeText(context, text, duration);
    }

    public static Toast makeText(Context context, int resId, int duration)
    {
        if (resId == 0)
        {
            GinLog.d(TAG, "提示信息为空！");
            return null;
        }
        return Toast.makeText(context, resId, duration);
    }

    public static Toast makeText(Context context, String text)
    {
        return makeText(context, text, Toast.LENGTH_SHORT);
    }

    public static Toast makeText(Context context, int resId)
    {
        return makeText(context, resId, Toast.LENGTH_LONG);
    }


    public static void showToast(Context context, String message)
    {
        if (mToast == null)
        {
            mToast = makeText(context, message, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(message);
        }

        if (mToast == null)
        {
            return;
        }

        if (mOldMsg.equals(message))
        {
            mTwoTime = System.currentTimeMillis();
            if (mTwoTime - mOneTime > 3 * 1000)
            {

                mToast.show();
                mOneTime = mTwoTime;
            }
        }
        else
        {
            mToast.show();
            mOneTime = System.currentTimeMillis();
            mOldMsg = message;
        }
    }

    public static void showToast(Context context, int resId)
    {
        showToast(context, context.getString(resId));
    }

}
