package com.higgses.griffin.core.observer;


import java.util.ArrayList;
import java.util.List;

public class ObservableUtils
{

    List<Observer> observers = new ArrayList<Observer>();

    boolean changed = false;

    /**
     * Constructs a new {@code Observable} object.
     */
    public ObservableUtils()
    {
    }

    /**
     * Adds the specified observer to the list of observers. If it is already
     * registered, it is not added a second time.
     *
     * @param observer
     *         the Observer to add.
     */
    public void addObserver(Observer observer)
    {
        if (observer == null)
        {
            throw new NullPointerException("observer == null");
        }
        synchronized (this)
        {
            if (!observers.contains(observer))
            {
                observers.add(observer);
            }
        }
    }

    /**
     * Clears the changed flag for this {@code Observable}. After calling
     * {@code clearChanged()}, {@code hasChanged()} will return {@code false}.
     */
    public void clearChanged()
    {
        changed = false;
    }

    /**
     * Returns the number of observers registered to this {@code Observable}.
     *
     * @return the number of observers.
     */
    public int countObservers()
    {
        return observers.size();
    }

    /**
     * Removes the specified observer from the list of observers. Passing null
     * won't do anything.
     *
     * @param observer
     *         the observer to remove.
     */
    public synchronized void deleteObserver(Observer observer)
    {
        observers.remove(observer);
    }

    /**
     * Removes all observers from the list of observers.
     */
    public synchronized void deleteObservers()
    {
        observers.clear();
    }

    /**
     * Returns the changed flag for this {@code Observable}.
     *
     * @return {@code true} when the changed flag for this {@code Observable} is
     * set, {@code false} otherwise.
     */
    public boolean hasChanged()
    {
        return changed;
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every observer in the list of observers using null as the
     * argument. Afterwards, calls {@code clearChanged()}.
     * <p/>
     * Equivalent to calling {@code notifyObservers(null)}.
     */
    public void notifyObservers(Observable observable)
    {
        notifyObservers(observable, null);
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every Observer in the list of observers using the specified
     * argument. Afterwards calls {@code clearChanged()}.
     *
     * @param data
     *         the argument passed to {@code update()}.
     */
    @SuppressWarnings("unchecked")
    public void notifyObservers(Observable observable, Object data)
    {
        int size = 0;
        Observer[] arrays = null;
        synchronized (this)
        {
            if (hasChanged())
            {
                clearChanged();
                size = observers.size();
                arrays = new Observer[size];
                observers.toArray(arrays);
            }
        }
        if (arrays != null)
        {
            for (Observer observer : arrays)
            {
                observer.update(observable, data);
            }
        }
    }

    /**
     * Sets the changed flag for this {@code Observable}. After calling
     * {@code setChanged()}, {@code hasChanged()} will return {@code true}.
     */
    public void setChanged()
    {
        changed = true;
    }
}