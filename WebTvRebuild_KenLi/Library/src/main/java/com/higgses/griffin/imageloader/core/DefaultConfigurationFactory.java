/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.higgses.griffin.imageloader.core;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.higgses.griffin.imageloader.cache.disc.DiskCache;
import com.higgses.griffin.imageloader.cache.disc.impl.UnlimitedDiscCache;
import com.higgses.griffin.imageloader.cache.disc.impl.ext.LruDiscCache;
import com.higgses.griffin.imageloader.cache.disc.naming.FileNameGenerator;
import com.higgses.griffin.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.higgses.griffin.imageloader.cache.memory.MemoryCache;
import com.higgses.griffin.imageloader.cache.memory.impl.LruMemoryCache;
import com.higgses.griffin.imageloader.core.assist.QueueProcessingType;
import com.higgses.griffin.imageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.higgses.griffin.imageloader.core.decode.BaseImageDecoder;
import com.higgses.griffin.imageloader.core.decode.ImageDecoder;
import com.higgses.griffin.imageloader.core.display.BitmapDisplayer;
import com.higgses.griffin.imageloader.core.display.SimpleBitmapDisplayer;
import com.higgses.griffin.imageloader.core.download.BaseImageDownloader;
import com.higgses.griffin.imageloader.core.download.ImageDownloader;
import com.higgses.griffin.imageloader.utils.GinStorage;

import android.content.Context;

/**
 * Factory for providing of default options for {@linkplain ImageLoaderConfiguration configuration}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class DefaultConfigurationFactory {

	/** Creates default implementation of task executor */
	public static Executor createExecutor(int threadPoolSize, int threadPriority,
			QueueProcessingType tasksProcessingType) {
		boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
		BlockingQueue<Runnable> taskQueue =
				lifo ? new LIFOLinkedBlockingDeque<Runnable>() : new LinkedBlockingQueue<Runnable>();
		return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
				createThreadFactory(threadPriority, "uil-pool-"));
	}

	/** Creates default implementation of task distributor */
	public static Executor createTaskDistributor() {
		return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY, "uil-pool-d-"));
	}

	/** Creates {@linkplain HashCodeFileNameGenerator default implementation} of FileNameGenerator */
	public static FileNameGenerator createFileNameGenerator() {
		return new HashCodeFileNameGenerator();
	}

	/**
	 * Creates default implementation of {@link DiskCache} depends on incoming parameters
	 */
	public static DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator,
			long diskCacheSize, int diskCacheFileCount) {
		File reserveCacheDir = createReserveDiskCacheDir(context);
		if (diskCacheSize > 0 || diskCacheFileCount > 0) {
			File individualCacheDir = GinStorage.getIndividualCacheDirectory(context);
			LruDiscCache diskCache = new LruDiscCache(individualCacheDir, diskCacheFileNameGenerator, diskCacheSize,
					diskCacheFileCount);
			diskCache.setReserveCacheDir(reserveCacheDir);
			return diskCache;
		} else {
			File cacheDir = GinStorage.getCacheDirectory(context);
			return new UnlimitedDiscCache(cacheDir, reserveCacheDir, diskCacheFileNameGenerator);
		}
	}

	/** Creates reserve disk cache folder which will be used if primary disk cache folder becomes unavailable */
	private static File createReserveDiskCacheDir(Context context) {
		File cacheDir = GinStorage.getCacheDirectory(context, false);
		File individualDir = new File(cacheDir, "uil-images");
		if (individualDir.exists() || individualDir.mkdir()) {
			cacheDir = individualDir;
		}
		return cacheDir;
	}

	/**
	 * Creates default implementation of {@link MemoryCache} - {@link LruMemoryCache}<br />
	 * Default cache size = 1/8 of available app memory.
	 */
	public static MemoryCache createMemoryCache(int memoryCacheSize) {
		if (memoryCacheSize == 0) {
			memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		}
		return new LruMemoryCache(memoryCacheSize);
	}

	/** Creates default implementation of {@link ImageDownloader} - {@link BaseImageDownloader} */
	public static ImageDownloader createImageDownloader(Context context) {
		return new BaseImageDownloader(context);
	}

	/** Creates default implementation of {@link ImageDecoder} - {@link BaseImageDecoder} */
	public static ImageDecoder createImageDecoder(boolean loggingEnabled) {
		return new BaseImageDecoder(loggingEnabled);
	}

	/** Creates default implementation of {@link BitmapDisplayer} - {@link SimpleBitmapDisplayer} */
	public static BitmapDisplayer createBitmapDisplayer() {
		return new SimpleBitmapDisplayer();
	}

	/** Creates default implementation of {@linkplain java.util.concurrent.ThreadFactory thread factory} for task executor */
	private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
		return new DefaultThreadFactory(threadPriority, threadNamePrefix);
	}

	private static class DefaultThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final int threadPriority;

		DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
			this.threadPriority = threadPriority;
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) t.setDaemon(false);
			t.setPriority(threadPriority);
			return t;
		}
	}
}
