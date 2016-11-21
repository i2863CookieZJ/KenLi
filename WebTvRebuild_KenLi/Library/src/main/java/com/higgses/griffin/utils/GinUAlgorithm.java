package com.higgses.griffin.utils;

import java.io.IOException;

import android.media.MediaPlayer;

/**
 * 算法工具
 * Created by Carlton on 2014/6/26.
 */
public class GinUAlgorithm
{
    /**
     * 计算两点的直线距离
     *
     * @param latA
     * @param lngA
     * @param latB
     * @param lngB
     *
     * @return
     */
    public static double getDistance(double latA, double lngA, double latB, double lngB)
    {
        final double EARTH_RADIUS = 6378137.0;
        double radLat1 = (latA * Math.PI / 180.0);
        double radLat2 = (latB * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lngA - lngB) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 获取视频时长
     *
     * @param path
     *
     * @return
     */
    public static long getVideoDurationFromPath(String path)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 0;
        }
        return mediaPlayer.getDuration();
    }
}
