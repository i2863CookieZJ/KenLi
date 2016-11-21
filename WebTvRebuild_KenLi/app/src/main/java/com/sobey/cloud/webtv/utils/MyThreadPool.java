package com.sobey.cloud.webtv.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 线程池的实现方式
public class MyThreadPool {
    private final static int POOL_SIZE = 4;// 线程池的大小最好设置成为CUP核数的2N
    private final static int MAX_POOL_SIZE = 6;// 设置线程池的最大线程数
    private final static int KEEP_ALIVE_TIME = 1;// 设置线程的存活时间
    private final ThreadPoolExecutor mExecutor;
	BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(3);
//	private final String TAG = this.getClass().getName();
    public MyThreadPool() {
        // 创建线程池工厂
//        ThreadFactory factory = Executors.defaultThreadFactory() ;
        // 创建工作队列
//        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
//        mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
    	mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.HOURS, queue, 
    			new ThreadPoolExecutor.CallerRunsPolicy()); 
    }
    // 在线程池中执行线程
    public void submit(Runnable command){
    	mExecutor.execute(command);
    }
    public void shutDown(){
    	mExecutor.shutdown();
    }
    public boolean isTerminated(){
//    	Log.i(TAG, "taskCount:"+mExecutor.getTaskCount());
    	return mExecutor.isTerminated();
    }
}
