package com.higgses.griffin.core.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.higgses.griffin.utils.GinUDensity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * ListView的数据适配器
 *
 * @author Higgses
 */
public abstract class AbstractListAdapter extends BaseAdapter
{

    /**
     * 传入数据集合
     */
    private List<Object> mData = new ArrayList<Object>();

    /**
     * 当前Activity
     */
    protected Context        mContext;
    protected LayoutInflater inflater;
    protected int            layoutId;

    public AbstractListAdapter(Context context, Object data, int layoutId)
    {
        this.mContext = context;
        this.layoutId = layoutId;
        if (data == null)
        {
            data = new ArrayList<Object>();
        }
        else
        {
            if (data instanceof List)
            {
                try
                {
                    this.mData = (List<Object>) data;
                }
                catch (Exception e)
                {
                }
            }
        }

        this.inflater = LayoutInflater.from(this.mContext);
    }

    public int getCount()
    {
        return mData.size();
    }

    public Object getItem(int position)
    {
        return mData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            AbstractViewCache item;
            if (convertView == null)
            {
                convertView = this.inflater.inflate(this.layoutId, null);
                item = getViewCache(convertView);
                convertView.setTag(item);
            }
            else
            {
                item = (AbstractViewCache) convertView.getTag();
            }
            parseViewCache(item, position);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertView;
    }

    public AbstractViewCache parseViewCache(AbstractViewCache abstractViewCache, int position) throws ParseException
    {
        return abstractViewCache;
    }

    public abstract class AbstractViewCache
    {
        public abstract void initializationComponent(View convertView) throws Exception;
    }

    public abstract AbstractViewCache getViewCache(View view) throws Exception;

    /**
     * 追加数据
     *
     * @param data 追加的数据
     */
    public void additionalData(ArrayList<?> data)
    {
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 普通无事件控件
     */
    public View addElement(View convertView, int id)
    {
        View view = convertView.findViewById(id);
        return view;
    }

    public void setTopMargin(int position, LinearLayout topLayout, int dip)
    {
        if (position == 0)
        {
            LayoutParams layoutParams = (LayoutParams) topLayout.getLayoutParams();
            if (layoutParams == null)
            {
                return;
            }
            int topMargins = dipToPx(dip);
            layoutParams.setMargins(layoutParams.leftMargin, topMargins, layoutParams.rightMargin, layoutParams.bottomMargin);
            topLayout.setLayoutParams(layoutParams);
        }
    }

    public void setBottomMargin(int position, LinearLayout bottomLayout, int dip)
    {
        if (getCount() < 1)
        {
            return;
        }
        if (position == getCount() - 1)
        {
            LayoutParams layoutParams = (LayoutParams) bottomLayout.getLayoutParams();
            if (layoutParams == null)
            {
                return;
            }
            int bottomMargins = dipToPx(dip);
            assert layoutParams != null;
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, bottomMargins);
            bottomLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * dip转换成PX
     *
     * @param dip
     * @return
     */
    private int dipToPx(int dip)
    {
        int px = GinUDensity.dipToPx(mContext, dip);
        return px;
    }

    /**
     * ListView中组件被点击的事件监听
     *
     * @author Higgses
     */
    public static class WeightOnClickListener implements OnClickListener
    {
        private int mPosition;

        public WeightOnClickListener()
        {

        }

        public WeightOnClickListener(int position)
        {
            mPosition = position;
        }

        public void setPosition(int position)
        {
            mPosition = position;
        }

        @Override
        public void onClick(View v)
        {
            onClick(mPosition, v);
        }

        public void onClick(int position, View v)
        {

        }
    }

    public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }

}
