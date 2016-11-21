package com.higgses.griffin.core.inf;

/**
 * 观察者模式中的主题
 * 观察者通过注册，主题在发生变化时，则可以通知到他
 * 泛型T代表模型更新后返回给观察者的数据类型
 * Create by YangQiang on 13-10-8.
 */
public interface GinIObservable
{
    /**
     * 该方法的作用是通知所有观察者，主题已经发生了改变,携带数据给观察者
     */
    public void notifyObserves(Object data, String method);

    public void notifyObserves(Class<?> dataClassType, Object data, String method);

    /**
     * 添加观察者
     */
    public void addObserves(GinIObserver observer);

    /**
     * 删除观察者
     */
    public void deleteObserver(GinIObserver observer);

    /**
     * 删除所有观察者
     */
    public void deleteObservers();

    /**
     * 设置已经通知了改变
     *
     * @return
     */
    public void setChanged();

    /**
     * 清楚改变状态，可以继续通知观察者了
     *
     * @return
     */
    public void clearChanged();

    /**
     * 返回改变的状态
     *
     * @return
     */
    public boolean hasChanged();

    /**
     * 返回观察者数量
     *
     * @return
     */
    public int countObservers();
}
