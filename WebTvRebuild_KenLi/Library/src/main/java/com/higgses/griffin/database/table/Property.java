/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.higgses.griffin.database.table;


import com.higgses.griffin.database.utils.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @version 1.0
 * @title 属性
 * @description 【非主键】的【基本数据类型】 都是属性
 * @created 2012-10-10
 */
public class Property
{

    private String   fieldName;
    private String   column;
    private String   defaultValue;
    private Class<?> dataType;
    private Field    field;

    private Method get;
    private Method set;

    public void setValue(Object receiver, Object value)
    {
        if (set != null && value != null)
        {
            try
            {
                if (dataType == String.class)
                {
                    set.invoke(receiver, value.toString());
                }
                else if (dataType == int.class || dataType == Integer.class)
                {
                    set.invoke(receiver, value == null ? (Integer) null : Integer.parseInt(value.toString()));
                }
                else if (dataType == float.class || dataType == Float.class)
                {
                    set.invoke(receiver, value == null ? (Float) null : Float.parseFloat(value.toString()));
                }
                else if (dataType == double.class || dataType == Double.class)
                {
                    set.invoke(receiver, value == null ? (Double) null : Double.parseDouble(value.toString()));
                }
                else if (dataType == long.class || dataType == Long.class)
                {
                    set.invoke(receiver, value == null ? (Long) null : Long.parseLong(value.toString()));
                }
                else if (dataType == Date.class || dataType == java.sql.Date.class)
                {
                    set.invoke(receiver, value == null ? (Date) null : FieldUtils.stringToDateTime(value.toString()));
                }
                else if (dataType == boolean.class || dataType == Boolean.class)
                {
                    set.invoke(receiver, value == null ? (Boolean) null : "1".equals(value.toString()));
                }
                else
                {
                    set.invoke(receiver, value);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                if (field != null)
                {
                    field.setAccessible(true);
                    field.set(receiver, value);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取某个实体执行某个方法的结果
     *
     * @param obj
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Object obj)
    {
        if (obj != null && get != null)
        {
            try
            {
                return (T) get.invoke(obj);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Class<?> getDataType()
    {
        return dataType;
    }

    public void setDataType(Class<?> dataType)
    {
        this.dataType = dataType;
    }

    public Method getGet()
    {
        return get;
    }

    public void setGet(Method get)
    {
        this.get = get;
    }

    public Method getSet()
    {
        return set;
    }

    public void setSet(Method set)
    {
        this.set = set;
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

}
