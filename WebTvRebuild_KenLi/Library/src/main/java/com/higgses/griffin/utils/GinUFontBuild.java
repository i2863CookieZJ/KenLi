package com.higgses.griffin.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * TextView的字体设置，包括让其中部分字体颜色改变，大小改变等
 * Create by YangQiang on 13-8-2.
 */
public class GinUFontBuild
{
    /**
     * 源字符
     */
    private static SpannableString mSrcFont;
    private static GinUFontBuild mInstance;

    private GinUFontBuild()
    {

    }

    public static GinUFontBuild build(SpannableString src)
    {
        mSrcFont = src;
        return newInstance();
    }

    private static GinUFontBuild newInstance()
    {
        return new GinUFontBuild();
    }

    public SpannableString create()
    {
        return mSrcFont;
    }

    public static GinUFontBuild build(String src)
    {
        return build(new SpannableString(src));
    }

    /**
     * 设置字体大小
     *
     * @param start
     *         要设置的字体开始的下标
     * @param end
     *         要设置的字体结束的小标
     */
    public GinUFontBuild setFontSize(int size, int start, int end)
    {
        //设置字体大小（绝对值,单位：像素）
        mSrcFont.setSpan(new AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param size
     * @param tagStr
     */
    public GinUFontBuild setFontSize(int size, SpannableString tagStr)
    {
        int start = mSrcFont.toString().indexOf(tagStr.toString());
        int end = start + tagStr.length();
        return setFontSize(size, start, end);
    }

    /**
     * 设置字体大小
     *
     * @param size
     * @param tagStr
     */
    public GinUFontBuild setFontSize(int size, String tagStr)
    {
        return setFontSize(size, new SpannableString(tagStr));
    }

    /**
     * 设置字体颜色
     *
     * @param color
     * @param start
     * @param end
     */
    public GinUFontBuild setFontColor(int color, int start, int end)
    {
        mSrcFont.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 设置字体颜色
     *
     * @param color
     * @param tagStr
     */
    public GinUFontBuild setFontColor(int color, SpannableString tagStr)
    {
        int start = mSrcFont.toString().indexOf(tagStr.toString());
        int end = start + tagStr.length();
        return setFontColor(color, start, end);
    }

    /**
     * 设置字体颜色
     *
     * @param color
     * @param tagStr
     */
    public GinUFontBuild setFontColor(int color, String tagStr)
    {
        return setFontColor(color, new SpannableString(tagStr));
    }

    /**
     * 设置颜色和字体
     *
     * @param color
     * @param size
     * @param start
     * @param end
     *
     * @return
     */
    public GinUFontBuild setFontColorAndSize(int color, int size, int start, int end)
    {
        return setFontSize(size, start, end).setFontColor(color, start, end);
    }

    /**
     * 设置颜色和字体
     *
     * @param color
     * @param size
     * @param tagStr
     *
     * @return
     */
    public GinUFontBuild setFontColorAndSize(int color, int size, SpannableString tagStr)
    {
        int start = mSrcFont.toString().indexOf(tagStr.toString());
        int end = start + tagStr.length();
        return setFontColorAndSize(color, size, start, end);
    }

    /**
     * 设置颜色和字体
     *
     * @param color
     * @param size
     * @param tagStr
     *
     * @return
     */
    public GinUFontBuild setFontColorAndSize(int color, int size, String tagStr)
    {
        return setFontColorAndSize(color, size, new SpannableString(tagStr));
    }
}
