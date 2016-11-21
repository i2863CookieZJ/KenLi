package com.higgses.griffin.core;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast显示的具体类
 * Created by Carlton on 2014/7/4.
 */
public class GinToast
{
    public static void show(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int id)
    {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
    }
}
