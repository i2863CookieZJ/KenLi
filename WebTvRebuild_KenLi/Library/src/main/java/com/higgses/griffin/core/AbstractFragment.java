/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.core;

import java.util.ArrayList;
import java.util.List;

import com.higgses.griffin.core.app.AbstractApplication;
import com.higgses.griffin.core.app.GinFragment;
import com.higgses.griffin.core.inf.GinIControl;
import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.core.inf.GinIObservable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 抽象Fragment
 *
 * @author Higgses
 */
public abstract class AbstractFragment<Model extends GinIModel> extends GinFragment implements GinIControl<Model>, View.OnClickListener
{
    /**
     * 模型
     */
    private List<GinIModel>  mModels;
    /**
     * Activity
     */
    private AbstractActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        preOnCreate();
        super.onCreate(savedInstanceState);
        mActivity = (AbstractActivity) getActivity();
        mModels = new ArrayList<>();
        GinIModel iModel = initModel();
        if (iModel != null)
        {
            mModels.add(iModel);
        }
        List<GinIModel> iModels = initModels();
        if (iModels != null && iModels.size() > 0)
        {
            mModels.addAll(iModels);
        }
        registerObserves();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        onDataFinish(savedInstanceState);
    }

    @Override
    public void onDataFinish(Bundle savedInstanceState)
    {

    }

    /**
     * 注册观察者
     */
    private void registerObserves()
    {
        for (GinIModel model : mModels)
        {
            model.addObserves(this);
        }
    }

    @Override
    public List<GinIModel> initModels()
    {
        return null;
    }

    @Override
    public GinIModel initModel()
    {
        return null;
    }

    @Override
    public <M extends GinIModel> M getModel(Class<M> modelType)
    {
        for (GinIModel model : mModels)
        {
            if (modelType.getName().equals(model.getClass().getName()))
            {
                return (M) model;
            }
        }
        try
        {
            throw new ClassNotFoundException("class" + modelType.toString() + " not found in register models");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Model getModel()
    {
        if (mModels != null && mModels.size() > 0)
        {
            return (Model) mModels.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void preOnCreate()
    {

    }

    @Override
    public void update(GinIObservable observable, Object data)
    {

    }

    @Override
    public int getContentView()
    {
        return 0;
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onClick(View view)
    {

    }

    public void setOnClick(View view)
    {
        onClick(view);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null)
        {
            for (Fragment fragment : fragments)
            {
                AbstractFragment abstractFragment = (AbstractFragment) fragment;
                if (abstractFragment == null)
                {
                    continue;
                }
                if (abstractFragment.getChildFragmentManager().getFragments() != null)
                {
                    abstractFragment.setOnClick(view);
                }
                else
                {
                    abstractFragment.onClick(view);
                }
            }
        }
    }

    /**
     * 显示提示
     *
     * @param msg
     */
    public void showToast(String msg)
    {
        ((AbstractApplication) getApp()).showToast(msg);
    }

    public void showToast(int resId)
    {
        ((AbstractApplication) getApp()).showToast(resId);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard()
    {
        hideKeyboard(InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘，使用hideSoftInputFromWindow中的flags
     *
     * @param flags InputMethodManager.hideSoftInputFromWindow（IBinder windowToken, int flags）中的flags
     */
    public void hideKeyboard(int flags)
    {
        mActivity.hideKeyboard(flags);
    }
}
