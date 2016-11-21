/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.database.entity;

import com.higgses.griffin.database.inf.DBUpdateListener;

import android.content.Context;


/**
 * 数据库配置
 */
public class DaoConfig
{
    private Context mContext  = null; // android上下文
    private String  mDbName   = "data.db"; // 数据库名字
    private int     dbVersion = 1; // 数据库版本
    private boolean debug     = true; // 是否是调试模式（调试模式 增删改查的时候显示SQL语句）
    private DBUpdateListener dbUpdateListener;
    // private boolean saveOnSDCard = false;//是否保存到SD卡
    private String           targetDirectory;// 数据库文件在sd卡中的目录

    public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context context)
    {
        this.mContext = context;
    }

    public String getDbName()
    {
        return mDbName;
    }

    public void setDbName(String dbName)
    {
        this.mDbName = dbName;
    }

    public int getDbVersion()
    {
        return dbVersion;
    }

    public void setDbVersion(int dbVersion)
    {
        this.dbVersion = dbVersion;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public DBUpdateListener getDbUpdateListener()
    {
        return dbUpdateListener;
    }

    public void setDbUpdateListener(DBUpdateListener dbUpdateListener)
    {
        this.dbUpdateListener = dbUpdateListener;
    }

    // public boolean isSaveOnSDCard() {
    // return saveOnSDCard;
    // }
    //
    // public void setSaveOnSDCard(boolean saveOnSDCard) {
    // this.saveOnSDCard = saveOnSDCard;
    // }

    public String getTargetDirectory()
    {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory)
    {
        this.targetDirectory = targetDirectory;
    }
}