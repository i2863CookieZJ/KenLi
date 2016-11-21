package com.higgses.griffin.core;

import java.util.ArrayList;
import java.util.List;

import com.higgses.griffin.core.app.AbstractApplication;
import com.higgses.griffin.core.app.Activity;
import com.higgses.griffin.core.inf.GinIControl;
import com.higgses.griffin.core.inf.GinIModel;
import com.higgses.griffin.core.inf.GinIObservable;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * 抽象的活动，为子类提供一些初始化的方法等 泛型T代表模型更新后返回给当前Activity视图的数据类型 Create by Higgses on
 * 13-10-9.
 */
public abstract class AbstractActivity<Model extends GinIModel> extends Activity implements GinIControl<Model>
{
    /**
     * 模型
     */
    private ArrayList<GinIModel> mModels;
    /**
     * 第一次点击返回的系统时间
     */
    private long mFirstClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        preOnCreate();
        super.onCreate(savedInstanceState);
        int contentView = getContentView();
        if (contentView != 0)
        {
            setContentView(contentView);
        }
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
        onDataFinish(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    public void onDataFinish(Bundle savedInstanceState)
    {

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


    @Override
    public void preOnCreate()
    {
    }

    @Override
    public void update(GinIObservable observable, Object data)
    {
    }

    /**
     * 双击退出
     */
    public boolean onDoubleClickExit(long timeSpace)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mFirstClickTime > timeSpace)
        {
            doubleExitCallBack();
            mFirstClickTime = currentTimeMillis;
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 双击退出，间隔时间为2000ms
     *
     * @return
     */
    public boolean onDoubleClickExit()
    {
        return onDoubleClickExit(2000);
    }

    /**
     * 双击退出不成功的回调。 第一次点击后回调，直到第二次点击的时间超过了给定时间，每一个回合回调一次
     */
    public void doubleExitCallBack()
    {
        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
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
     * @param flags
     *         InputMethodManager.hideSoftInputFromWindow（IBinder windowToken, int flags）中的flags
     */
    public void hideKeyboard(int flags)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View peekDecorView = getWindow().peekDecorView();
        inputMethodManager.hideSoftInputFromWindow(peekDecorView.getWindowToken(), flags);
    }

    /**
     * 点击事件,所有子类必须重写这个方法，在XML中配置onClick属性名称一定要是onClick
     *
     * @param view
     */
    public void onClick(View view)
    {
        hideKeyboard();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null)
        {
            for (int i = 0; i < fragments.size() && fragments.get(i) != null; ++i)
            {
                ((AbstractFragment) fragments.get(i)).setOnClick(view);
            }
        }
    }
}
