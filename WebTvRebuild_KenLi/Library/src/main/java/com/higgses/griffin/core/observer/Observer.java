package com.higgses.griffin.core.observer;

/**
 * {@code Observer} is the interface to be implemented by objects that
 * receive notification of updates on an {@code Observable} object.
 *
 * @see ObservableUtils
 */
public interface Observer
{

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable
     *         the {@link ObservableUtils} object.
     * @param data
     *         the data passed to {@link ObservableUtils#notifyObservers(Observable, Object)}.
     */
    void update(Observable observable, Object data);
}