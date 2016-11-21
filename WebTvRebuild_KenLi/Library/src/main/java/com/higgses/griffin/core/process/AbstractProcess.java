package com.higgses.griffin.core.process;

/**
 * 一个抽象的进程处理类
 *
 * @param <S>
 * @param <T>
 */
public abstract class AbstractProcess<S, T> implements GinIProcess<S, T>
{
    @Override
    public void preProcess()
    {
    }

    @Override
    public void processing()
    {
    }

    @Override
    public void processFinish(S self, T data)
    {
    }
}
