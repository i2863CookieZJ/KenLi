package com.higgses.griffin.thread.inf;

/**
 * 回调
 * Created by Higgses on 2014/6/11.
 */
public interface GinICallback
{
    public void onFinish(Object object);

    public class SimpleCallbackGin implements GinICallback
    {
        @Override
        public void onFinish(Object object)
        {

        }
    }
}
