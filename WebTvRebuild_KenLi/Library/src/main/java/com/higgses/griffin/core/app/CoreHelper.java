package com.higgses.griffin.core.app;

import com.higgses.griffin.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.higgses.griffin.imageloader.core.ImageLoader;
import com.higgses.griffin.imageloader.core.ImageLoaderConfiguration;
import com.higgses.griffin.imageloader.core.assist.QueueProcessingType;

import android.content.Context;

/**
 * 核心辅助类
 * Created by Carlton on 2014/7/4.
 */
public class CoreHelper
{
    /**
     * CoreHelper单例
     */
    private static CoreHelper mInstance;

    /**
     * 获取单实例
     *
     * @return
     */
    public static CoreHelper getInstance()
    {
        if (mInstance == null)
        {
            synchronized (CoreHelper.class)
            {
                if (mInstance == null)
                {
                    mInstance = new CoreHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化图片加载类型
     *
     * @param context
     */
    public void initImageLoader(Context context)
    {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
