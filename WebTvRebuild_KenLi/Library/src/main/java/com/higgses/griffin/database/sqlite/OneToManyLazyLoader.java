/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.database.sqlite;


import java.util.ArrayList;
import java.util.List;

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
public class OneToManyLazyLoader<O, M>
{
    O           ownerEntity;
    Class<O>    ownerClazz;
    Class<M>    listItemClazz;
    GinSqliteDB db;

    public OneToManyLazyLoader(O ownerEntity, Class<O> ownerClazz, Class<M> listItemclazz, GinSqliteDB db)
    {
        this.ownerEntity = ownerEntity;
        this.ownerClazz = ownerClazz;
        this.listItemClazz = listItemclazz;
        this.db = db;
    }

    List<M> entities;

    /**
     * 如果数据未加载，则调用loadOneToMany填充数据
     *
     * @return
     */
    public List<M> getList()
    {
        if (entities == null)
        {
            this.db.loadOneToMany((O) this.ownerEntity, this.ownerClazz, this.listItemClazz);
        }
        if (entities == null)
        {
            entities = new ArrayList<M>();
        }
        return entities;
    }

    public void setList(List<M> value)
    {
        entities = value;
    }

}
