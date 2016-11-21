/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.database.sqlite;


import com.higgses.griffin.database.GinSqliteDB;

/**
 * 
 * 一对多延迟加载类 Created by pwy on 13-7-25.
 * 
 * @param <O>
 *            宿主实体的class
 * @param <M>
 *            多放实体class
 */
public class ManyToOneLazyLoader<M, O>
{
    M           manyEntity;
    Class<M>    manyClazz;
    Class<O>    oneClazz;
    GinSqliteDB db;
    /**
     * 用于
     */
    private Object fieldValue;

    public ManyToOneLazyLoader(M manyEntity, Class<M> manyClazz, Class<O> oneClazz, GinSqliteDB db)
    {
        this.manyEntity = manyEntity;
        this.manyClazz = manyClazz;
        this.oneClazz = oneClazz;
        this.db = db;
    }

    O oneEntity;
    boolean hasLoaded = false;

    /**
     * 如果数据未加载，则调用loadManyToOne填充数据
     *
     * @return
     */
    public O get()
    {
        if (oneEntity == null && !hasLoaded)
        {
            this.db.loadManyToOne(null, this.manyEntity, this.manyClazz, this.oneClazz);
            hasLoaded = true;
        }
        return oneEntity;
    }

    public void set(O value)
    {
        oneEntity = value;
    }

    public Object getFieldValue()
    {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue)
	{
		this.fieldValue = fieldValue;
	}
}
