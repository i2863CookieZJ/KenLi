package com.higgses.griffin.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.higgses.griffin.log.inf.GinILogger;

/**
 * 日志处理
 */
public class GinLog
{
    public static final int VERBOSE = 2;

    public static final int DEBUG = 3;

    public static final int INFO = 4;

    public static final int WARN = 5;

    public static final int ERROR = 6;

    public static final int ASSERT = 7;

    public static final int UN = 8;

    private static HashMap<String, GinILogger> loggerHashMap = new HashMap<String, GinILogger>();

    private static final GinILogger defaultLogger = new GinDefaultLogger();
    private static       boolean isDebug       = false;

    public static void addLogger(GinILogger logger)
    {
        String loggerName = logger.getClass().getName();
        String defaultLoggerName = defaultLogger.getClass().getName();
        if (!loggerHashMap.containsKey(loggerName) && !defaultLoggerName.equalsIgnoreCase(loggerName))
        {
            loggerHashMap.put(loggerName, logger);
        }
    }

    /**
     * 关闭默认的日志
     */
    public static void closeDefaultLogger()
    {
        defaultLogger.close();
    }

    /**
     * 打开默认的日志
     */
    public static void openDefaultLogger()
    {
        defaultLogger.open();
    }

    public static void removeLogger(GinILogger logger)
    {
        String loggerName = logger.getClass().getName();
        if (loggerHashMap.containsKey(loggerName))
        {
            logger.close();
            loggerHashMap.remove(loggerName);
        }
    }

    public static void d(Object object, String message)
    {
        printLogger(DEBUG, object, message);
    }

    public static void e(Object object, String message)
    {
        printLogger(ERROR, object, message);
    }

    public static void i(Object object, String message)
    {
        printLogger(INFO, object, message);
    }

    public static void v(Object object, String message)
    {
        printLogger(VERBOSE, object, message);
    }

    public static void w(Object object, String message)
    {
        printLogger(WARN, object, message);
    }

    public static void d(String tag, String message)
    {
        printLogger(DEBUG, tag, message);
    }

    public static void e(String tag, String message)
    {
        printLogger(ERROR, tag, message);
    }

    public static void e(String tag, String message, Throwable throwable)
    {
        e(tag, message + throwable.getMessage());
    }

    public static void i(String tag, String message)
    {
        printLogger(INFO, tag, message);
    }

    public static void v(String tag, String message)
    {
        printLogger(VERBOSE, tag, message);
    }

    public static void w(String tag, String message)
    {
        printLogger(WARN, tag, message);
    }

    public static void un(String tag, String message)
    {
        printLogger(UN, tag, message);
    }

    public static void println(int priority, String tag, String message)
    {
        printLogger(priority, tag, message);
    }

    public static void setDebug(boolean isDebug)
    {
        GinLog.isDebug = isDebug;
    }

    private static void printLogger(int priority, Object object, String message)
    {
        Class<?> cls = object.getClass();
        String tag = cls.getName();
        String arrays[] = tag.split("\\.");
        tag = arrays[arrays.length - 1];
        printLogger(priority, tag, message);
    }

    private static void printLogger(int priority, String tag, String message)
    {
        if (isDebug)
        {
            printLogger(defaultLogger, priority, tag, message);
            Iterator<Entry<String, GinILogger>> iter = loggerHashMap.entrySet().iterator();
            while (iter.hasNext())
            {
                Entry<String, GinILogger> entry = iter.next();
                GinILogger logger = entry.getValue();
                if (logger != null)
                {
                    printLogger(logger, priority, tag, message);
                }
            }
        }
    }

    private static void printLogger(GinILogger logger, int priority, String tag, String message)
    {
        if (!logger.isOpen())
        {
            return;
        }
        switch (priority)
        {
            case VERBOSE:
                logger.v(tag, message);
                break;
            case DEBUG:
                logger.d(tag, message);
                break;
            case INFO:
                logger.i(tag, message);
                break;
            case WARN:
                logger.w(tag, message);
                break;
            case ERROR:
                logger.e(tag, message);
                break;
            case UN:
                logger.e(tag, message);
                break;
            default:
                break;
        }
    }

    /**
     * 返回打印的异常格式
     *
     * @param ex
     *
     * @return
     */
    public static String getPrintException(Throwable ex)
    {
        StringBuffer err = new StringBuffer();
        err.append("ExceptionDetailed:\n");
        err.append("====================Exception Info====================\n");
        err.append(ex.toString());
        err.append("\n");
        StackTraceElement[] stack = ex.getStackTrace();
        for (int i = 0; i < stack.length; ++i)
        {
            StackTraceElement stackTraceElement = stack[i];
            err.append(stackTraceElement.toString() + "\n");
        }
        Throwable cause = ex.getCause();
        if (cause != null)
        {
            err.append("【Caused by】: ");
            err.append(cause.toString());
            err.append("\n");
            StackTraceElement[] stackTrace = cause.getStackTrace();
            for (int i = 0; i < stackTrace.length; ++i)
            {
                StackTraceElement stackTraceElement = stackTrace[i];
                err.append(stackTraceElement.toString() + "\n");
            }
        }
        err.append("==============================================");
        return err.toString();
    }

    public static String getMapString(Map map)
    {
        Set set = map.keySet();
        if (set.size() < 1)
        {
            return "[]";
        }
        StringBuffer stringBuffer = new StringBuffer();
        Object[] array = set.toArray();
        stringBuffer.append("[" + array[0] + "=" + map.get(array[0]));
        for (int i = 1; i < array.length; ++i)
        {
            stringBuffer.append(", ");
            stringBuffer.append(array[i] + "=");
            stringBuffer.append(map.get(array[i]));
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
