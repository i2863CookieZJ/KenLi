package com.higgses.griffin.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类的操作工具
 * Created by Carlton on 2014/7/26.
 */
public class GinUClassUtil
{
    /**
     * 获取类所有的成员变量，包括父类的
     *
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> cls)
    {
        List<Field> list = new ArrayList<>();
        list.addAll(Arrays.asList(cls.getDeclaredFields()));
        if (!cls.getSuperclass().getName().equals(Object.class.getName()))
        {
            list.addAll(getDeclaredFields(cls.getSuperclass()));
        }
        return list;
    }

    /**
     * 返回targetClass中名字叫methodName的方法的Method对象,包括了父类
     *
     * @param methodName
     *         即将要返回的的Method的方法名称
     * @param targetClass
     *         Method所在的类
     * @param field
     *         Method的参数类型
     *
     * @return
     */
    public static Method getDeclaredMethod(String methodName, Class<?> targetClass, Field field) throws NoSuchMethodException
    {
        return targetClass.getMethod(methodName, field.getType());
    }

    /**
     * 返回targetClass中名字叫methodName的方法的Method对象,包括了父类
     *
     * @param methodName
     * @param targetClass
     *
     * @return
     *
     * @throws NoSuchMethodException
     */
    public static Method getDeclaredMethod(String methodName, Class<?> targetClass) throws NoSuchMethodException
    {
        return targetClass.getMethod(methodName);
    }
}
