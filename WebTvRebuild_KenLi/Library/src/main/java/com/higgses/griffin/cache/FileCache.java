package com.higgses.griffin.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.higgses.griffin.log.GinLog;

import android.content.Context;

/**
 * 文件的缓存操作类,包括内存缓存，与磁盘缓存
 */
public class FileCache extends AbstractCache<String, byte[]>
{
    /**
     * TAG
     */
    private final String TAG = "FileCache";

    /**
     * 使用指定的参数创建一个新的FileCache对象。
     *
     * @param cacheParams
     *         缓存参数用来初始化缓存
     */
    public FileCache(CacheParams cacheParams)
    {
        super(cacheParams);
    }

    /**
     * 创建一个缓存
     *
     * @param context
     *         上下文信息
     * @param uniqueName
     *         这个标示名字会被添加到生成緩存文件夹
     */
    public FileCache(Context context, String uniqueName)
    {
        super(context, uniqueName);
    }

    @Override
    protected LruCache<String, byte[]> getLurCache()
    {
        LruCache<String, byte[]> lruCache = new LruCache<String, byte[]>(mCacheParams.MEMORY_CACHE_SIZE)
        {
            @Override
            protected int sizeOf(String key, byte[] value)
            {
                return value.length;
            }
        };
        return lruCache;
    }

    /**
     * 添加 byte[]类型数据到内存缓存和磁盘缓存
     *
     * @param data
     *         byte[]的惟一标识符来存储,一般是URL
     * @param buffer
     *         需要添加到缓存的数据
     */
    public void addBufferToCache(String data, byte[] buffer)
    {
        if (data == null || buffer == null)
        {
            return;
        }

        // 添加到内存缓存
        if (mMemoryCache != null && mMemoryCache.get(data) == null)
        {
            mMemoryCache.put(data, buffer);
        }

        synchronized (mDiskCacheLock)
        {
            // 添加到磁盘缓存
            if (mDiskLruCache != null)
            {
                String key = hashKeyForDisk(data);
                GinLog.i(TAG, "缓存数据到磁盘[key:" + data + ",hashKey:" + key + "]");
                OutputStream out = null;
                try
                {
                    //                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    //                    if (snapshot == null)
                    //                    {
                    final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null)
                    {
                        out = editor.newOutputStream(DISK_CACHE_INDEX);
                        out.write(buffer, 0, buffer.length);
                        editor.commit();
                        out.flush();
                        out.close();
                    }
                    //                    }
                    //                    else
                    //                    {
                    //                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    //                    }
                } catch (Exception e)
                {
                    GinLog.e(TAG, "addBufferToCache - " + e);
                } finally
                {
                    try
                    {
                        if (out != null)
                        {
                            out.close();
                        }
                    } catch (IOException ignored)
                    {
                    }
                }
            }
        }
    }

    /**
     * 从内存缓存获取数据
     *
     * @param data
     *         byte[]的惟一标识符来存储,一般是URL
     *
     * @return 返回byte[]类型的一个数据
     */
    public byte[] getBufferFromMemoryCache(String data)
    {
        byte[] memValue = null;
        try
        {
            if (mMemoryCache != null)
            {
                memValue = mMemoryCache.get(data);
            }
        } catch (Exception e)
        {
            GinLog.e(TAG, "获取缓存数据失败！" + GinLog.getPrintException(e));
        }
        return memValue;
    }

    /**
     * 从磁盘缓存中获取数据,如果内存缓存没有该数据，则添加到内存缓存
     *
     * @param data
     *         独特的标识符项目得到
     *
     * @return 如果在缓存中找到相应的数据，则返回数据,否则为null
     */
    public byte[] getBufferFromDiskCache(String data)
    {
        String key = hashKeyForDisk(data);

        synchronized (mDiskCacheLock)
        {
            while (mDiskCacheStarting)
            {
                try
                {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e)
                {
                }
            }
            if (mDiskLruCache != null)
            {
                byte[] buffer = null;
                try
                {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null)
                    {
                        InputStream fileInputStream = snapshot.getInputStream(DISK_CACHE_INDEX);

                        buffer = readStream(fileInputStream);
                        // 添加数据到内存缓存
                        if (buffer != null && mMemoryCache != null && mMemoryCache.get(data) == null)
                        {
                            mMemoryCache.put(data, buffer);
                        }
                        return buffer;
                    }
                } catch (final IOException e)
                {
                    GinLog.e(TAG, "getBufferFromDiskCache - " + e);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    GinLog.e(TAG, "getBufferFromDiskCache - " + e);
                } finally
                {

                }
            }
            return null;
        }
    }

    /**
     * 得到图片字节流 数组大小
     */
    public static byte[] readStream(InputStream inStream) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        return outStream.toByteArray();
    }
}
