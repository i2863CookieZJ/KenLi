/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.database.sqlite;

import java.util.LinkedList;

public class SqlInfo
{

    private String             sql;
    private LinkedList<Object> bindArgs;

    public String getSql()
    {
        return sql;
    }

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public LinkedList<Object> getBindArgs()
    {
        return bindArgs;
    }

    public void setBindArgs(LinkedList<Object> bindArgs)
    {
        this.bindArgs = bindArgs;
    }

    public Object[] getBindArgsAsArray()
    {
        if (bindArgs != null)
            return bindArgs.toArray();
        return null;
    }

    public String[] getBindArgsAsStringArray()
    {
        if (bindArgs != null)
        {
            String[] strings = new String[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++)
            {
                strings[i] = bindArgs.get(i).toString();
            }
            return strings;
        }
        return null;
    }

    public void addValue(Object obj)
    {
        if (bindArgs == null)
            bindArgs = new LinkedList<Object>();

        bindArgs.add(obj);
    }

}
