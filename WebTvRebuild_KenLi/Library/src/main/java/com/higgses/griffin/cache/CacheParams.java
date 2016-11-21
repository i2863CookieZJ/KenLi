/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.cache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

/**
 * 缓存的参数类
 */
public class CacheParams
{
    /**
     * 默认内存储存储大小
     */
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
    /**
     * 默认磁盘存储大小
     */
    public static final  int DEFAULT_DISK_CACHE_SIZE   = 1024 * 1024 * 20; // 20MB
    /**
     * 当压缩图片到磁盘的是默认格式
     */
    private static final int DEFAULT_COMPRESS_QUALITY  = 80;

    private static final CompressFormat DEFAULT_COMPRESS_FORMAT           = CompressFormat.PNG;
    /**
     * 默认内存缓存启用
     */
    private static final boolean        DEFAULT_MEMORY_CACHE_ENABLED      = true;
    /**
     * 默认磁盘缓存启用
     */
    private static final boolean        DEFAULT_DISK_CACHE_ENABLED        = true;
    /**
     * 默认的初始化的磁盘高速缓存开始
     */
    private static final boolean        DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;
    /**
     * 内存缓存大小
     */
    public               int            MEMORY_CACHE_SIZE                 = DEFAULT_MEMORY_CACHE_SIZE;
    /**
     * 磁盘缓存大小
     */
    public               int            DISK_CACHE_SIZE                   = DEFAULT_DISK_CACHE_SIZE;
    /**
     * 磁盘缓存的文件目录
     */
    public File DISK_CACHE_DIR;
    /**
     * 缓存的图片清晰度(默认80)
     */
    public int            COMPRESS_QUALITY          = DEFAULT_COMPRESS_QUALITY;
    /**
     * 缓存的图片格式
     */
    public CompressFormat COMPRESS_FORMAT           = DEFAULT_COMPRESS_FORMAT;
    /**
     * 内存缓存开关
     */
    public boolean        MEMORY_CACHE_ENABLED      = DEFAULT_MEMORY_CACHE_ENABLED;
    /**
     * 磁盘缓存开关
     */
    public boolean        DISK_CACHE_ENABLED        = DEFAULT_DISK_CACHE_ENABLED;
    /**
     * 是否在启动的时候清理磁盘缓存
     */
    /**
     * 磁盘缓存是否初始化
     */
    public boolean        INIT_DISK_CACHE_ON_CREATE = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

    /**
     * 初始化磁盘参数
     *
     * @param context
     *         上下文
     * @param uniqueName
     *         缓存文件包名
     */
    public CacheParams(Context context, String uniqueName)
    {
        DISK_CACHE_DIR = AbstractCache.getDiskCacheDir(context, uniqueName);
    }

    /**
     * 初始化磁盘参数
     *
     * @param diskCacheDir
     *         缓存文件包
     */
    public CacheParams(File diskCacheDir)
    {
        this.DISK_CACHE_DIR = diskCacheDir;
    }

    /**
     * 设置缓存的大小
     *
     * @param context
     *         上下文
     * @param percent
     *         设置分配缓存为本设备的百度比，以0.01f计算
     */
    public void setMemCacheSizePercent(Context context, float percent)
    {
        if (percent < 0.01f || percent > 0.8f)
        {
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be between 0.01 and 0.8 (inclusive)");
        }
        MEMORY_CACHE_SIZE = Math.round(percent * Runtime.getRuntime().maxMemory() * 1024 * 1024);
    }
}