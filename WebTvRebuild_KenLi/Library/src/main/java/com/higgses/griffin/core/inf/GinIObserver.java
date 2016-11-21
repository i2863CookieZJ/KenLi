package com.higgses.griffin.core.inf;

/**
 * 观察者模式中的观察者
 * 泛型D代表模型更新后返回给观察者的数据类型
 * Create by YangQiang on 13-10-8.
 */
public interface GinIObserver
{
	/**
	 * 当被观察者的数据发生变化，并且被观察者的状态是被改变的状态，则回调此方法
	 */
	public void update(GinIObservable observable, Object data);
}
