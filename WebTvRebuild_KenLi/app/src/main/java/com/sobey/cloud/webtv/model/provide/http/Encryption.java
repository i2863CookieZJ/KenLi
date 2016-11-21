package com.sobey.cloud.webtv.model.provide.http;

import java.security.MessageDigest;
import java.util.Date;

import com.higgses.griffin.utils.GinUCalendar;
import com.sobey.cloud.webtv.config.AppConfig;

/**
 * 加密工具
 * Created by higgses on 14-5-14.
 */
public class Encryption
{
    public final static String ACCESS_CODE = "b44e4e83ea";

    private static String initRandomCode()
    {
        return GinUCalendar.formatStringWithDate(new Date(), AppConfig.DateFormat.ENCRYPTION_RANDOM);
    }

    /**
     * 随机数：yyyyMMDDHHmmss格式的日期
     * @return
     */
    public static String getRandomCode()
    {
        return initRandomCode();
    }

    public static String getICityCode(String randomCode)
    {
        return encodeMD5ToString(randomCode + ACCESS_CODE);
    }

    public static byte[] encodeMD5(String strSrc)
    {
        byte[] returnByte = null;
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            returnByte = md5.digest(strSrc.getBytes("UTF-8"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnByte;
    }

    public static String encodeMD5ToString(String src)
    {
        return bytesToHexString(encodeMD5(src));
    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
            {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
