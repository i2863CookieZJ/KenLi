package com.higgses.griffin.cache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.higgses.griffin.core.utils.GriUAndroidVersion;
import com.higgses.griffin.log.GinLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * 缓存数据
 * Created by higgses on 14-5-13.
 */
public abstract class AbstractCache<K, V>
{
    private static final   String TAG              = "AbstractCache";
    protected static final int    DISK_CACHE_INDEX = 0;
    /**
     * 缓存工具的参数
     */
    protected CacheParams    mCacheParams;
    /**
     * 内存缓存类的实例
     */
    protected LruCache<K, V> mMemoryCache;
    /**
     * 磁盘缓存类的实例
     */
    protected DiskLruCache   mDiskLruCache;
    /**
     * 是否正在使用磁盘缓存
     */
    protected boolean mDiskCacheStarting = true;

    /**
     * 磁盘缓的对象存锁
     */
    protected final Object mDiskCacheLock = new Object();

    /**
     * 使用指定的参数创建一个新的FileCache对象。
     *
     * @param cacheParams
     *         缓存参数用来初始化缓存
     */
    public AbstractCache(CacheParams cacheParams)
    {
        init(cacheParams);
    }

    /**
     * 创建一个缓存
     *
     * @param context
     *         上下文信息
     * @param uniqueName
     *         这个标示名字会被添加到生成緩存文件夹
     */
    public AbstractCache(Context context, String uniqueName)
    {
        init(new CacheParams(context, uniqueName));
    }

    private void init(CacheParams cacheParams)
    {
        mCacheParams = cacheParams;
        if (mCacheParams.MEMORY_CACHE_ENABLED)
        {
            mMemoryCache = getLurCache();
        }
        if (!mCacheParams.INIT_DISK_CACHE_ON_CREATE)
        {
            initDiskCache();
        }
    }

    protected abstract LruCache<K, V> getLurCache();

    /**
     * 初始化磁盘缓存
     */
    public void initDiskCache()
    {
        synchronized (mDiskCacheLock)
        {
            if (mDiskLruCache == null || mDiskLruCache.isClosed())
            {
                File diskCacheDir = mCacheParams.DISK_CACHE_DIR;
                if (mCacheParams.DISK_CACHE_ENABLED && diskCacheDir != null)
                {
                    if (!diskCacheDir.exists())
                    {
                        diskCacheDir.mkdirs();
                    }
                    if (getUsableSpace(diskCacheDir) > mCacheParams.DISK_CACHE_SIZE)
                    {
                        try
                        {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.DISK_CACHE_SIZE);
                        }
                        catch (final IOException e)
                        {
                            mCacheParams.DISK_CACHE_DIR = null;
                            GinLog.e(TAG, "initDiskCache - " + e);
                        }
                    }
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key)
    {
        String cacheKey;
        try
        {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes)
    {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
            {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context
     *         The context to use
     * @param uniqueName
     *         A unique directory name to append to the cache dir
     *
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        return new File(getDiskCacheDirName(context) + File.separator + uniqueName);
    }

    public static String getDiskCacheDirName(Context context)
    {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        File cacheDirPath = getExternalCacheDir(context);
        if (cacheDirPath == null)
        {
            cacheDirPath = context.getCacheDir();
        }
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable() ? cacheDirPath.getPath() : context.getCacheDir().getPath();
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable()
    {
        if (GriUAndroidVersion.hasGingerbread())
        {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context
     *         The context to use
     *
     * @return The external cache dir
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context)
    {
        if (GriUAndroidVersion.hasFroyo())
        {
            File cacheDir;
            if(!isExternalStorageRemovable())
            {
                cacheDir = context.getExternalCacheDir();
            }
            else
            {
                cacheDir = context.getCacheDir();
            }
            return cacheDir;
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path
     *         The path to check
     *
     * @return The space available in bytes
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path)
    {
        if (GriUAndroidVersion.hasGingerbread())
        {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }


    /**
     * 位图的惟一标识符清除内存和磁盘缓存和与FileCache相关的对象 。 注意,这包括对磁盘访问,所以这是不应当在主线程或UI线程上执行。
     */
    public void clearCache()
    {
        if (mMemoryCache != null)
        {
            mMemoryCache.evictAll();
        }

        synchronized (mDiskCacheLock)
        {
            mDiskCacheStarting = true;
            if (mDiskLruCache != null && !mDiskLruCache.isClosed())
            {
                try
                {
                    mDiskLruCache.delete();
                }
                catch (IOException e)
                {
                    GinLog.e(TAG, "clearCache - " + e);
                }
                mDiskLruCache = null;
                initDiskCache();
            }
        }
    }

    /**
     * 磁盘缓存刷新和与FileCache相关的对象。注意,这包括对磁盘访问,所以这是不应当在主线程或 UI线程上执行的。
     */
    public void flush()
    {
        synchronized (mDiskCacheLock)
        {
            if (mDiskLruCache != null)
            {
                try
                {
                    mDiskLruCache.flush();
                }
                catch (IOException e)
                {
                    GinLog.e(TAG, "flush - " + e);
                }
            }
        }
    }

    /**
     * 关闭磁盘缓存和与FileCache相关的对象。注意,这包括对磁盘访问,所以这是不应当在主线程或 UI线程上执行的。
     */
    public void close()
    {
        synchronized (mDiskCacheLock)
        {
            if (mDiskLruCache != null)
            {
                try
                {
                    if (!mDiskLruCache.isClosed())
                    {
                        mDiskLruCache.close();
                        mDiskLruCache = null;
                    }
                }
                catch (IOException e)
                {
                    GinLog.e(TAG, "close" + e);
                }
            }
        }
    }
}
