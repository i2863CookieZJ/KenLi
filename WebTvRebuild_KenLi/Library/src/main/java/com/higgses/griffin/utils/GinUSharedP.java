package com.higgses.griffin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreference工具
 *
 * @author Higgses
 */
public class GinUSharedP
{
    /**
     * 返回一个SharedPrefrence编辑对象
     *
     * @param context
     * @param name
     * @param mode
     *
     * @return
     */
    public static Editor getEditor(Context context, String name, int mode)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, mode);
        return sharedPreferences.edit();
    }

    /**
     * 返回一个私有的编辑对象
     *
     * @param context
     * @param name
     *
     * @return
     */
    public static Editor getEditor(Context context, String name)
    {
        return getEditor(context, name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences(Context context, String name)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    /**
     * 返回String对象
     *
     * @param context
     * @param name
     *
     * @return
     */
    public static String getString(Context context, String name, String key)
    {
        return getSharedPreferences(context, name).getString(key, "");
    }

    public static void putString(Context context, String name, String key, String value)
    {
        getEditor(context, name).putString(key, value).commit();
    }

    /**
     * 返回Boolean
     *
     * @param context
     * @param name
     *
     * @return
     */
    public static boolean getBoolean(Context context, String name, String key)
    {
        return getBoolean(context, name, key, false);
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defaultValue)
    {
        return getSharedPreferences(context, name).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String name, String key, boolean value)
    {
        getEditor(context, name).putBoolean(key, value).commit();
    }
}
