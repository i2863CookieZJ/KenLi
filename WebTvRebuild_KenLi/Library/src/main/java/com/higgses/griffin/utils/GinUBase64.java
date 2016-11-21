package com.higgses.griffin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Base64工具
 * Create by YangQiang on 13-6-30.
 */
public class GinUBase64
{
    private GinUBase64()
    {
    }

    /**
     * 将对象转换成Base64字符
     *
     * @param object
     *         将要被转换的对象
     *
     * @return 转换后的字符串
     */
    public static String convertObjectToString(Object object)
    {
        //将对象转换为byte[]
        ByteArrayOutputStream toByte = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(toByte);
            oos.writeObject(object);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //对byte[]进行Base64编码
        String base64String = new String(Base64.encode(toByte.toByteArray(), Base64.DEFAULT));
        return base64String;
    }

    /**
     * 把Base64字符串转换成对象
     *
     * @param string
     *         将要转换的Base64字符串
     *
     * @return 转换完成后的对象
     */
    public static Object convertStringToObject(String string)
    {
        byte[] base64Bytes = Base64.decode(string, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois = null;
        Object object = null;
        try
        {
            ois = new ObjectInputStream(bais);
            object = ois.readObject();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 把Base64字符转换成Bitmap
     *
     * @param string
     *         将要转换的Base64字符串
     *
     * @return 转换完成的图片
     */
    public static Bitmap convertStringToBitmap(String string)
    {
        Bitmap bitmap = null;
        try
        {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 图片转换成Base64字符串
     *
     * @param bitmap
     *         将要转换的图片对象
     *
     * @return 转换完成的字符
     */
    public static String convertBitmapToString(Bitmap bitmap)
    {
        String string = null;
        if (null != bitmap)
        {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return string;
    }
}
