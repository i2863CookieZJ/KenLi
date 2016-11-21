package com.higgses.griffin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.TextView;

/**
 * 文本处理工具
 */
public class GinUText
{
    private GinUText()
    {

    }

    /**
     * 格式化字符
     *
     * @param scr
     * @param format
     *
     * @return
     */
    public static String decimalFormat(Number scr, String format)
    {
        String decimalFormat = new DecimalFormat(format).format(scr);
        return decimalFormat;
    }

    /**
     * 替换字符号(不带空格)
     *
     * @param str
     *
     * @return
     */
    public static String replaceSymbol(String str)
    {
        String dest = "";
        if (str != null)
        {
            // Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 首字母大写
     *
     * @param str
     *
     * @return
     */
    public static String firstUpperCase(String str)
    {
        if (android.text.TextUtils.isEmpty(str))
        {
            return null;
        }
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    /**
     * 从文本中读取数据，返回成List对象
     *
     * @param file
     *
     * @return
     */
    public static List<String> getTextToList(File file)
    {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<String> list = new ArrayList<String>();
        try
        {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String text;
            while ((text = bufferedReader.readLine()) != null)
            {
                list.add(text);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fileInputStream != null)
                {
                    fileInputStream.close();
                }
                if (inputStreamReader != null)
                {
                    inputStreamReader.close();
                }
                if (bufferedReader != null)
                {
                    bufferedReader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 从文本中读取数据，返回成List对象
     *
     * @param inputStream
     *
     * @return
     */
    public static List<String> getTextToList(InputStream inputStream)
    {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<String> list = new ArrayList<String>();
        try
        {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String text;
            while ((text = bufferedReader.readLine()) != null)
            {
                list.add(text);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputStreamReader != null)
                {
                    inputStreamReader.close();
                }
                if (bufferedReader != null)
                {
                    bufferedReader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 判断一个数是否是整数
     *
     * @param numStr
     *
     * @return
     */
    public static boolean isInteger(String numStr)
    {
        try
        {
            double parseDouble = Double.parseDouble(numStr);
            if (parseDouble % 1 == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * 判断一个数是否是大于0的数
     *
     * @return
     */
    public static boolean isPositiveInteger(String numStr)
    {
        if (isInteger(numStr))
        {
            double parseDouble = Double.parseDouble(numStr);
            if (parseDouble > 0)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化数据
     *
     * @param value
     *         需要转换的值
     * @param pattern
     *         小数位数
     *
     * @return
     */
    public static String decimalFormat(double value, String pattern)
    {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * 格式化数据
     *
     * @param value
     *         需要转换的值
     * @param pattern
     *         小数位数
     *
     * @return
     */
    public static String decimalFormat(String value, String pattern)
    {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(Double.parseDouble(value));
    }

    /**
     * 格式化数据
     *
     * @param value
     * @param scale
     *
     * @return
     */
    public static String decimalFormat(double value, int scale)
    {
        return decimalFormat(value, getScalePattern(scale));
    }

    /**
     * 格式化数据
     *
     * @param value
     * @param scale
     *
     * @return
     */
    public static String decimalFormat(String value, int scale)
    {
        return decimalFormat(value, getScalePattern(scale));
    }

    /**
     * 返回小数位数的匹配
     *
     * @param scale
     *
     * @return
     */
    private static String getScalePattern(int scale)
    {
        StringBuffer sb = new StringBuffer("#0.");
        if (scale <= 0)
        {
            sb = new StringBuffer("#");
        }
        for (int i = 0; i < scale; ++i)
        {
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * 返回TextView的值，没有或者null返回0
     *
     * @param view
     *
     * @return
     */
    public static String getViewText(TextView view)
    {
        if (view == null)
        {
            return "0";
        }
        boolean empty = android.text.TextUtils.isEmpty(view.getText().toString());
        return empty ? "0" : view.getText().toString();
    }

    /**
     * 替换字符串
     *
     * @param source
     * @param index
     * @param before
     * @param after
     *
     * @return
     */
    public static String replace(String source, int index, String before, String after)
    {
        String regex = before;
        Matcher matcher = Pattern.compile(regex).matcher(source);
        for (int counter = 0; matcher.find(); counter++)
        {
            if (counter == index)
            {
                return source.substring(0, matcher.start()) + after + source.substring(matcher.end(), source.length());

            }

        }
        return source;
    }

    public static String JsonToString(String src)
    {
        if ("{}".equals(src) || "[]".equals(src))
        {
            return "";
        }
        return src;
    }

    /**
     * 去掉空格和特殊字符
     *
     * @param str
     *
     * @return
     */
    public static String trimString(String str)
    {
        if (android.text.TextUtils.isEmpty(str))
        {
            return "";
        }
        else
        {
            return str.trim();
        }
    }
}
