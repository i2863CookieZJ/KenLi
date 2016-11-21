/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.higgses.griffin.database.utils;


import com.higgses.griffin.annotation.sqlite.Table;
import com.higgses.griffin.database.sqlite.ManyToOneLazyLoader;
import com.higgses.griffin.database.table.ManyToOne;
import com.higgses.griffin.database.table.OneToMany;
import com.higgses.griffin.database.table.Property;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.higgses.griffin.annotation.sqlite.Id;
import com.higgses.griffin.exception.database.DBException;

/**
 * 数据库反射操作工具类
 */
public class ClassUtils
{

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz)
    {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null || table.name().trim().length() == 0)
        {
            // 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
            return clazz.getName().replace('.', '_');
        }
        return table.name();
    }

    public static Object getPrimaryKeyValue(Object entity)
    {
        return FieldUtils.getFieldValue(entity, ClassUtils.getPrimaryKeyField(entity.getClass()));
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getPrimaryKeyColumn(Class<?> clazz)
    {
        String primaryKey = null;
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null)
        {
            Id idAnnotation = null;
            Field idField = null;

            for (Field field : fields)
            { // 获取ID注解
                idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null)
                {
                    idField = field;
                    break;
                }
            }

            if (idAnnotation != null)
            { // 有ID注解
                primaryKey = idAnnotation.column();
                if (primaryKey == null || primaryKey.trim().length() == 0)
                {
                    primaryKey = idField.getName();
                }
            }
            else
            { // 没有ID注解,默认去找 _id 和 id 为主键，优先寻找 _id
                for (Field field : fields)
                {
                    if ("_id".equals(field.getName()))
                    {
                        return "_id";
                    }
                }

                for (Field field : fields)
                {
                    if ("id".equals(field.getName()))
                    {
                        return "id";
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("this model[" + clazz + "] has no field");
        }
        return primaryKey;
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static Field getPrimaryKeyField(Class<?> clazz)
    {
        Field primaryKeyField = null;
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null)
        {

            for (Field field : fields)
            { // 获取ID注解
                if (field.getAnnotation(Id.class) != null)
                {
                    primaryKeyField = field;
                    break;
                }
            }

            if (primaryKeyField == null)
            { // 没有ID注解
                for (Field field : fields)
                {
                    if ("_id".equals(field.getName()))
                    {
                        primaryKeyField = field;
                        break;
                    }
                }
            }

            if (primaryKeyField == null)
            { // 如果没有_id的字段
                for (Field field : fields)
                {
                    if ("id".equals(field.getName()))
                    {
                        primaryKeyField = field;
                        break;
                    }
                }
            }

        }
        else
        {
            throw new RuntimeException("this model[" + clazz + "] has no field");
        }
        return primaryKeyField;
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getPrimaryKeyFieldName(Class<?> clazz)
    {
        Field f = getPrimaryKeyField(clazz);
        return f == null ? null : f.getName();
    }

    /**
     * 将对象转换为ContentValues
     *
     * @param clazz
     * @return
     */
    public static List<Property> getPropertyList(Class<?> clazz)
    {

        List<Property> plist = new ArrayList<Property>();
        try
        {
            Field[] fs = clazz.getDeclaredFields();
            String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
            for (Field f : fs)
            {
                // 必须是基本数据类型和没有标瞬时态的字段
                if (!FieldUtils.isTransient(f))
                {
                    if (FieldUtils.isBaseDateType(f))
                    {
                        // 过滤主键,并且属性名是以_开头的
                        if (f.getName().equals(primaryKeyFieldName) || f.getName().startsWith("_"))
                        {
                            continue;
                        }

                        Property property = new Property();

                        property.setColumn(FieldUtils.getColumnByField(f));
                        property.setFieldName(f.getName());
                        property.setDataType(f.getType());
                        property.setDefaultValue(FieldUtils.getPropertyDefaultValue(f));
                        property.setSet(FieldUtils.getFieldSetMethod(clazz, f));
                        property.setGet(FieldUtils.getFieldGetMethod(clazz, f));
                        property.setField(f);

                        plist.add(property);
                    }
                }
            }
            return plist;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为ContentValues
     *
     * @param clazz
     * @return
     */
    public static List<ManyToOne> getManyToOneList(Class<?> clazz)
    {

        List<ManyToOne> mList = new ArrayList<ManyToOne>();
        try
        {
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs)
            {
                if (!FieldUtils.isTransient(f) && FieldUtils.isManyToOne(f))
                {

                    ManyToOne mto = new ManyToOne();
                    // 如果类型为ManyToOneLazyLoader则取第二个参数作为manyClass（一方实体）
                    // 2013-7-26
                    if (f.getType() == ManyToOneLazyLoader.class)
                    {
                        Class<?> pClazz = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[1];
                        if (pClazz != null)
                        {
                            mto.setManyClass(pClazz);
                        }
                    }
                    else
                    {
                        mto.setManyClass(f.getType());
                    }
                    mto.setColumn(FieldUtils.getColumnByField(f));
                    mto.setFieldName(f.getName());
                    mto.setDataType(f.getType());
                    mto.setSet(FieldUtils.getFieldSetMethod(clazz, f));
                    mto.setGet(FieldUtils.getFieldGetMethod(clazz, f));

                    mList.add(mto);
                }
            }
            return mList;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为ContentValues
     *
     * @param clazz
     * @return
     */
    public static List<OneToMany> getOneToManyList(Class<?> clazz)
    {

        List<OneToMany> oList = new ArrayList<OneToMany>();
        try
        {
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs)
            {
                if (!FieldUtils.isTransient(f) && FieldUtils.isOneToMany(f))
                {

                    OneToMany otm = new OneToMany();

                    otm.setColumn(FieldUtils.getColumnByField(f));
                    otm.setFieldName(f.getName());

                    Type type = f.getGenericType();

                    if (type instanceof ParameterizedType)
                    {
                        ParameterizedType pType = (ParameterizedType) f.getGenericType();
                        // 如果类型参数为2则认为是LazyLoader 2013-7-25
                        if (pType.getActualTypeArguments().length == 1)
                        {
                            Class<?> pClazz = (Class<?>) pType.getActualTypeArguments()[0];
                            if (pClazz != null)
                            {
                                otm.setOneClass(pClazz);
                            }
                        }
                        else
                        {
                            Class<?> pClazz = (Class<?>) pType.getActualTypeArguments()[1];
                            if (pClazz != null)
                            {
                                otm.setOneClass(pClazz);
                            }
                        }
                    }
                    else
                    {
                        throw new DBException("getOneToManyList Exception:" + f.getName() + "'s type is null");
                    }
                    /* 修正类型赋值错误的bug，f.getClass返回的是Filed */
                    otm.setDataType(f.getType());
                    otm.setSet(FieldUtils.getFieldSetMethod(clazz, f));
                    otm.setGet(FieldUtils.getFieldGetMethod(clazz, f));

                    oList.add(otm);
                }
            }
            return oList;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
