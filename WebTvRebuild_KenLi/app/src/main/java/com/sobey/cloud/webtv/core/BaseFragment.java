package com.sobey.cloud.webtv.core;

import com.higgses.griffin.core.AbstractFragment;
import com.higgses.griffin.core.inf.GinIModel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * BaseFragment
 * Created by Carlton on 14-4-1.
 */
public abstract class BaseFragment<Model extends GinIModel> extends AbstractFragment<Model>
{
    protected View mCacheView;
    private boolean isUseCache = false;

    @Override
    public void onDataFinish(Bundle savedInstanceState)
    {
        super.onDataFinish(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * 显示提示
     *
     * @param msg
     */
    public void showToast(String msg)
    {
        HiggsesToast.makeText(getActivity(), msg).show();
    }

    public void showToast(int resId)
    {
        HiggsesToast.makeText(getActivity(), resId).show();
    }

    public View getCacheView(LayoutInflater inflater, int layoutId)
    {
        if (mCacheView == null)
        {
            mCacheView = inflater.inflate(layoutId, null);
            isUseCache = false;
        }
        else
        {
            isUseCache = true;
        }
        ViewGroup parent = (ViewGroup) (mCacheView != null ? mCacheView.getParent() : null);
        if (parent != null)
        {
            parent.removeView(mCacheView);
        }
        return mCacheView;
    }

    public ContextApplication getApp()
    {
        return ContextApplication.getApp();
    }

    public boolean isUseCache()
    {
        return isUseCache;
    }
}
