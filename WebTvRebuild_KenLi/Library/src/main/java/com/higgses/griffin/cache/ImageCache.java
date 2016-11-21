/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.higgses.griffin.cache;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.higgses.griffin.core.utils.GriUAndroidVersion;
import com.higgses.griffin.log.GinLog;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

/**
 * This class handles disk and memory caching of bitmaps in conjunction with the
 * {@link ImageWorker} class and its subclasses. Use
 *
 * @link com.example.android.displayingbitmaps.util.ImageCache#getInstance(android.support.v4.app.FragmentManager,
 *com.example.android.displayingbitmaps.util.ImageCache.ImageCacheParams)} to get an instance of this
 * class, although usually a cache should be added directly to an {@link ImageWorker} by calling
 * @link ImageWorker#addImageCache(android.support.v4.app.FragmentManager, com.example.android.displayingbitmaps.util.ImageCache.ImageCacheParams)}.
 */
public class ImageCache extends AbstractCache<String, BitmapDrawable>
{
    private static final String TAG = "ImageCache";

    private Set<SoftReference<Bitmap>> mReusableBitmaps;

    /**
     * Create a new ImageCache object using the specified parameters. This should not be
     * called directly by other classes, instead use
     *
     * @param cacheParams
     *         The cache parameters to use to initialize the cache
     *
     * @link com.example.android.displayingbitmaps.util.ImageCache#getInstance(android.support.v4.app.FragmentManager,
     *com.example.android.displayingbitmaps.util.ImageCache.ImageCacheParams)} to fetch an ImageCache
     * instance.
     */
    private ImageCache(CacheParams cacheParams)
    {
        super(cacheParams);
        if (GriUAndroidVersion.hasHoneycomb())
        {
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }
    }

    /**
     * Return an @link com.example.android.displayingbitmaps.util.ImageCache} instance. A @link com.example.android.displayingbitmaps.util.ImageCache
     * .RetainFragment} is used to retain the
     * ImageCache object across configuration changes such as a change in device orientation.
     *
     * @param cacheParams
     *         The cache parameters to use if the ImageCache needs instantiation.
     *
     * @return An existing retained ImageCache object or a new one if one did not exist
     */
    public static ImageCache getInstance(CacheParams cacheParams)
    {
        return new ImageCache(cacheParams);
    }

    @Override
    protected LruCache<String, BitmapDrawable> getLurCache()
    {
        return new LruCache<String, BitmapDrawable>(mCacheParams.MEMORY_CACHE_SIZE)
        {
            /**
             * Notify the removed entry that is no longer being cached
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue)
            {
                if (RecyclingBitmapDrawable.class.isInstance(oldValue))
                {
                    // The removed entry is a recycling drawable, so notify it
                    // that it has been removed from the memory cache
                    ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
                }
                else
                {
                    // The removed entry is a standard BitmapDrawable

                    if (GriUAndroidVersion.hasHoneycomb())
                    {
                        // We're running on Honeycomb or later, so add the bitmap
                        // to a SoftReference set for possible use with inBitmap later
                        mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                    }
                }
            }

            /**
             * Measure item size in kilobytes rather than units which is more practical
             * for a bitmap cache
             */
            @Override
            protected int sizeOf(String key, BitmapDrawable value)
            {
                final int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     *
     * @param data
     *         Unique identifier for the bitmap to store
     * @param value
     *         The bitmap drawable to store
     */
    public void addBitmapToCache(String data, BitmapDrawable value)
    {
        //BEGIN_INCLUDE(add_bitmap_to_cache)
        if (data == null || value == null)
        {
            return;
        }
        // Add to memory cache
        if (mMemoryCache != null)
        {
            if (RecyclingBitmapDrawable.class.isInstance(value))
            {
                // The removed entry is a recycling drawable, so notify it
                // that it has been added into the memory cache
                ((RecyclingBitmapDrawable) value).setIsCached(true);
            }
            mMemoryCache.put(data, value);
        }

        synchronized (mDiskCacheLock)
        {
            // Add to disk cache
            if (mDiskLruCache != null)
            {
                final String key = hashKeyForDisk(data);
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
                        value.getBitmap().compress(mCacheParams.COMPRESS_FORMAT, mCacheParams.COMPRESS_QUALITY, out);
                        editor.commit();
                        out.close();
                    }
                    //                    }
                    //                    else
                    //                    {
                    //                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    //                    }
                }
                catch (Exception e)
                {
                    GinLog.e(TAG, GinLog.getPrintException(e));
                }
                finally
                {
                    try
                    {
                        if (out != null)
                        {
                            out.close();
                        }
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
        }
        //END_INCLUDE(add_bitmap_to_cache)
    }

    /**
     * Get from memory cache.
     *
     * @param data
     *         Unique identifier for which item to get
     *
     * @return The bitmap drawable if found in cache, null otherwise
     */
    public BitmapDrawable getBitmapFromMemCache(String data)
    {
        //BEGIN_INCLUDE(get_bitmap_from_mem_cache)
        BitmapDrawable memValue = null;

        if (mMemoryCache != null)
        {
            memValue = mMemoryCache.get(data);
        }
        return memValue;
        //END_INCLUDE(get_bitmap_from_mem_cache)
    }

    /**
     * Get from disk cache.
     *
     * @param data
     *         Unique identifier for which item to get
     *
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(String data)
    {
        //BEGIN_INCLUDE(get_bitmap_from_disk_cache)
        final String key = hashKeyForDisk(data);
        Bitmap bitmap = null;

        synchronized (mDiskCacheLock)
        {
            while (mDiskCacheStarting)
            {
                try
                {
                    mDiskCacheLock.wait();
                }
                catch (InterruptedException ignored)
                {
                }
            }
            if (mDiskLruCache != null)
            {
                InputStream inputStream = null;
                try
                {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null)
                    {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null)
                        {
                            FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                            // Decode bitmap, but we don't want to sample so give
                            // MAX_VALUE as the target dimensions
                            bitmap = ImageResizer.decodeSampledBitmapFromDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this);
                        }
                    }
                }
                catch (final IOException ignored)
                {
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                        {
                            inputStream.close();
                        }
                    }
                    catch (IOException ignored)
                    {
                    }
                }
            }
            return bitmap;
        }
        //END_INCLUDE(get_bitmap_from_disk_cache)
    }

    /**
     * @param options
     *         - BitmapFactory.Options with out* options populated
     *
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options)
    {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty())
        {
            synchronized (mReusableBitmaps)
            {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext())
                {
                    item = iterator.next().get();

                    if (null != item && item.isMutable())
                    {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options))
                        {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    }
                    else
                    {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        return bitmap;
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    /**
     * @param candidate
     *         - Bitmap to check
     * @param targetOptions
     *         - Options that have the out* value populated
     *
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     * <code>targetOptions</code>
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions)
    {
        //BEGIN_INCLUDE(can_use_for_inbitmap)
        if (!GriUAndroidVersion.hasKitKat())
        {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth && candidate.getHeight() == targetOptions.outHeight && targetOptions.inSampleSize == 1;
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return byteCount <= candidate.getAllocationByteCount();
        //END_INCLUDE(can_use_for_inbitmap)
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     *
     * @param config
     *         The bitmap configuration.
     *
     * @return The byte usage per pixel.
     */
    private static int getBytesPerPixel(Config config)
    {
        if (config == Config.ARGB_8888)
        {
            return 4;
        }
        else if (config == Config.RGB_565)
        {
            return 2;
        }
        else if (config == Config.ARGB_4444)
        {
            return 2;
        }
        else if (config == Config.ALPHA_8)
        {
            return 1;
        }
        return 1;
    }


    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).
     *
     * @param value
     *
     * @return size in bytes
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value)
    {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (GriUAndroidVersion.hasKitKat())
        {
            return bitmap.getAllocationByteCount();
        }

        if (GriUAndroidVersion.hasHoneycombMR1())
        {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
