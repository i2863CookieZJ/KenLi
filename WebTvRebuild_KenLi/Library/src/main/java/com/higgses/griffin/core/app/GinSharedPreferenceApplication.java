package com.higgses.griffin.core.app;

import com.higgses.griffin.utils.GinUSharedP;

import android.content.SharedPreferences;

/**
 * 这个Application新增了可以使用SharedPreferences的功能，如果需要使用SharedPreferences就是用这个Application
 * Created by Carlton on 2014/7/4.
 */
public class GinSharedPreferenceApplication extends GinApplication
{
    /**
     * 返回SharedPreference编辑器
     *
     * @param name
     * @param mode
     *
     * @return
     */
    public SharedPreferences.Editor getEditor(String name, int mode)
    {
        return GinUSharedP.getEditor(this, name, mode);
    }

    /**
     * 返回SharedPreference的私有编辑器
     *
     * @param name
     *
     * @return
     */
    public SharedPreferences.Editor getEditor(String name)
    {
        return getEditor(name, MODE_PRIVATE);
    }

    public boolean saveBoolean(String name, String key, boolean value)
    {
        return saveBoolean(name, MODE_PRIVATE, key, value);
    }

    public boolean saveBoolean(String name, int mode, String key, boolean value)
    {
        return getEditor(name, mode).putBoolean(key, value).commit();
    }

    public boolean getBoolean(String name, int mode, String key, boolean defaultValue)
    {
        return getSharedPreferences(name, mode).getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String name, String key)
    {
        return getSharedPreferences(name, MODE_PRIVATE).getBoolean(key, false);
    }
    public boolean getBoolean(String name, String key, boolean defaultValue)
    {
        return getSharedPreferences(name, MODE_PRIVATE).getBoolean(key, defaultValue);
    }
    /**
     * 保存String数据到私有SharedPreference
     *
     * @param name
     * @param key
     * @param value
     *
     * @return
     */
    public boolean saveString(String name, String key, String value)
    {
        return saveString(name, MODE_PRIVATE, key, value);
    }

    /**
     * 保存String数据到SharedPreference
     *
     * @param name
     * @param mode
     * @param key
     * @param value
     *
     * @return
     */
    public boolean saveString(String name, int mode, String key, String value)
    {
        return getEditor(name, mode).putString(key, value).commit();
    }

    /**
     * 返回SharedPreference中String数据
     *
     * @param name
     * @param mode
     * @param key
     * @param defaultValue
     *
     * @return
     */
    public String getString(String name, int mode, String key, String defaultValue)
    {
        return getSharedPreferences(name, mode).getString(key, defaultValue);
    }

    /**
     * 返回SharedPreference中String数据，私有模型，默认值为：""
     *
     * @param name
     * @param key
     *
     * @return
     */
    public String getString(String name, String key)
    {
        return getString(name, MODE_PRIVATE, key, "");
    }

    /**
     * 返回SharedPreference中int数据
     *
     * @param name
     * @param mode
     * @param key
     * @param defaultValue
     *
     * @return
     */
    public int getInteger(String name, int mode, String key, int defaultValue)
    {
        return getSharedPreferences(name, mode).getInt(key, defaultValue);
    }

    public int getInteger(String name, String key, int defaultValue)
    {
        return getInteger(name, MODE_PRIVATE, key, defaultValue);
    }

    /**
     * 返回SharedPreference中String数据，私有模型，默认值为：0
     *
     * @param name
     * @param key
     *
     * @return
     */
    public int getInteger(String name, String key)
    {
        return getInteger(name, MODE_PRIVATE, key, 0);
    }

    /**
     * 保存int数据到SharedPreference
     *
     * @param name
     * @param mode
     * @param key
     * @param value
     *
     * @return
     */
    public boolean saveInteger(String name, int mode, String key, int value)
    {
        return getEditor(name, mode).putInt(key, value).commit();
    }

    /**
     * 保存int数据到私有SharedPreference
     *
     * @param name
     * @param key
     * @param value
     *
     * @return
     */
    public boolean saveInteger(String name, String key, int value)
    {
        return saveInteger(name, MODE_PRIVATE, key, value);
    }

    /**
     * 清空SharedPreference数据
     *
     * @param name
     *
     * @return
     */
    public boolean clearSharedPreference(String name)
    {
        return getEditor(name).clear().commit();
    }
}
