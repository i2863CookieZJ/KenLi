package com.higgses.griffin.utils;

import com.higgses.griffin.log.GinLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件操作工具 可以创建和删除文件等 Create by YangQiang on 13-7-2.
 */
public class GinUFile
{
    private static final String KB = "KB";
    private static final String MB = "MB";
    private static final String GB = "GB";

    private GinUFile()
    {
    }

    private static final String TAG = "GinUFile";

    /**
     * 创建文件 当文件不存在的时候就创建一个文件，否则直接返回文件
     *
     * @param path
     *
     * @return
     */
    public static File createFile(String path)
    {
        File file = new File(path);
        if (!file.getParentFile().exists())
        {
            GinLog.d(TAG, "目标文件所在路径不存在，准备创建……");
            if (!createDir(file.getParent()))
            {
                GinLog.d(TAG, "创建目录文件所在的目录失败！");
            }
        }
        // 创建目标文件
        try
        {
            if (!file.exists())
            {
                if (file.createNewFile())
                {
                    GinLog.d(TAG, "创建文件成功:" + file.getAbsolutePath());
                }
                return file;
            }
            else
            {
                return file;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建目录 当目录不存在的时候创建文件，否则返回false
     *
     * @param path
     *
     * @return
     */
    public static boolean createDir(String path)
    {
        File file = new File(path);
        if (!file.exists())
        {
            if (!file.mkdirs())
            {
                GinLog.d(TAG, "创建失败，请检查路径和是否配置文件权限！");
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 拷贝文件
     *
     * @param fromPath
     * @param toPath
     *
     * @return
     */
    public static boolean copy(String fromPath, String toPath)
    {
        File file = new File(fromPath);
        if (!file.exists())
        {
            return false;
        }
        createFile(toPath);
        return copyFile(fromPath, toPath);
    }

    /**
     * 拷贝文件
     *
     * @param fromFile
     * @param toFile
     *
     * @return
     */
    private static boolean copyFile(String fromFile, String toFile)
    {
        InputStream fosfrom = null;
        OutputStream fosto = null;
        try
        {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            return true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                if (fosfrom != null)
                {
                    fosfrom.close();
                }
                if (fosto != null)
                {
                    fosto.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除文件 如果文件存在删除文件，否则返回false
     *
     * @param path
     *
     * @return
     */
    public static boolean deleteFile(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir
     *         将要删除的文件目录
     *
     * @return 删除成功返回true，否则返回false,如果文件是空，那么永远返回true
     */
    public static boolean deleteDir(File dir)
    {
        if (dir == null)
        {
            return true;
        }
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 返回目录中文件的大小（单位:KB） 指定一个路径，然后返回该目录或者文件的大小，如果是路径，包括子目录文件的大小总和
     *
     * @param path
     *
     * @return
     */
    public static float getFileSize(String path)
    {
        return getSize(path, new Float(0));
    }

    /**
     * 返回目录中文件的大小（单位:KB） 指定一个文件，然后返回该目录或文件的大小，如果是路径，包括子目录文件的大小总和
     *
     * @param file
     *
     * @return
     */
    public static float getFileSize(File file)
    {
        return getSize(file.getPath(), new Float(0));
    }

    /**
     * 递归返回文件或者目录的大小（单位:KB）
     *
     * @param path
     * @param size
     *
     * @return
     */
    private static float getSize(String path, Float size)
    {
        File file = new File(path);
        if (file.exists())
        {
            if (file.isDirectory())
            {
                String[] children = file.list();
                for (int fileIndex = 0; fileIndex < children.length; ++fileIndex)
                {
                    float tmpSize = getSize(file.getPath() + File.separator + children[fileIndex], size) / 1000;
                    size += tmpSize;
                }
            }
            else if (file.isFile())
            {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 组合成有单位的文件大小（单位： KB）
     *
     * @param size
     *         大小
     * @param isCase
     *         是否大写
     */
    public static String unitWithKb(float size, boolean isCase)
    {
        return setUnitStr(size, isCase, KB);
    }

    /**
     * 组合成有单位的文件大小（单位： MB）
     *
     * @param size
     *         大小
     * @param isCase
     *         是否大写
     */
    public static String unitWithMb(float size, boolean isCase)
    {
        return setUnitStr(size, isCase, MB);
    }

    /**
     * 组合成有单位的文件大小（单位： GB）
     *
     * @param size
     *         大小
     * @param isCase
     *         是否大写
     */
    public static String unitWithGb(float size, boolean isCase)
    {
        return setUnitStr(size, isCase, GB);
    }

    /**
     * 根据大小自动适配单位，例如超过1024KB 则单位为MB，超过1024MB 单位为GB
     *
     * @param size
     * @param conversionPoint
     *         转换点。size大于转换点就转换
     * @param conversionRate
     *         转换率（例如1024KB = 1M 或者 1000KB = 1M）
     *
     * @return
     */
    public static String autoUnit(float size, float conversionPoint, int conversionRate)
    {
        String tmpStr = String.valueOf(unitWithMb(size / conversionRate, true));
        if ((size / conversionRate) > conversionPoint)
        {// mb 大于1024 自动转换成GB
            float tmpSize = size / conversionRate;
            tmpStr = unitWithGb(tmpSize, true);
        }
        return tmpStr;
    }

    /**
     * 根据大小自动适配单位，例如超过1024KB 则单位为MB，超过1024MB 单位为GB 默认转换率为1024
     *
     * @param size
     *
     * @return
     */
    public static String autoUnit(float size)
    {
        return autoUnit(size, 1024, 1024);
    }

    /**
     * 设置单位字符串
     *
     * @param size
     * @param isCase
     * @param unit
     *
     * @return
     */
    private static String setUnitStr(float size, boolean isCase, String unit)
    {
        return GinUText.decimalFormat(size, "#0.00") + (isCase ? unit.toUpperCase() : unit.toLowerCase());
    }

}
