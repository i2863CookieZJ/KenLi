/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.core.adapter;

import java.util.ArrayList;

import com.higgses.griffin.core.entity.GinViewHolder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 数据适配器的模板
 * Created by higgses on 14-4-21.
 */
public abstract class SampleAdapter<DataType, ViewEntity extends GinViewHolder> extends BaseAdapter
{
    private   ArrayList<DataType> mData;
    private   SparseArray<View>   mCacheView;
    protected Context             mContext;
    private   int                 mLayoutId;

    public SampleAdapter(Context context, ArrayList<DataType> data, int layoutId)
    {
        mContext = context;
        mCacheView = new SparseArray<View>();
        mData = data;
        if (mData == null)
        {
            mData = new ArrayList<DataType>();
        }
        mLayoutId = layoutId;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        preGetView(position, convertView, parent);
        convertView = mCacheView.get(position);
        ViewEntity viewEntity = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            viewEntity = createViewEntity(convertView);
            convertView.setTag(viewEntity);
            if(isCacheView())
            {
                mCacheView.put(position, convertView);
            }
        }
        viewEntity = (ViewEntity) convertView.getTag();
        DataType item = (DataType) getItem(position);
        preBindingData(position, convertView, parent);
        bindingData(position, viewEntity, item);
        bindingDataFinish(position, convertView, parent);
        return convertView;
    }
    protected boolean isCacheView()
    {
        return true;
    }
    /**
     * 绑定数据
     *
     * @param position
     * @param viewEntity
     * @param item
     */
    protected abstract void bindingData(int position, ViewEntity viewEntity, DataType item);

    /**
     * 创建ViewEntity
     *
     * @param convertView
     *
     * @return
     */
    protected abstract ViewEntity createViewEntity(View convertView);

    /**
     * getView方法体执行前
     *
     * @param position
     * @param convertView
     * @param parent
     */
    protected void preGetView(int position, View convertView, ViewGroup parent)
    {

    }

    /**
     * 绑定数据前
     *
     * @param position
     * @param convertView
     * @param parent
     */
    protected void preBindingData(int position, View convertView, ViewGroup parent)
    {

    }

    /**
     * 绑定数据完成
     *
     * @param position
     * @param convertView
     * @param parent
     */
    protected void bindingDataFinish(int position, View convertView, ViewGroup parent)
    {

    }
}
