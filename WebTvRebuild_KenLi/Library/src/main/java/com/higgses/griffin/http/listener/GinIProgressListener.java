package com.higgses.griffin.http.listener;

/**
 * 进度回调监听
 *
 * @author
 */
public interface GinIProgressListener
{
    /**
     * 正在执行
     *
     * @param totalSize
     * @param nowSize
     */
    public void onProgress(long totalSize, long nowSize);
    /**
     * 操作完成
     *
     * @param object
     */
    public void onComplete(Object object);

    /**
     * 失败
     *
     * @param object
     */
    public void onFail(Object object);
}

