package com.sobey.cloud.webtv.config;

/**
 * SharedPreference常量
 * Created by higgses on 14-5-14.
 */
public class SharedConstant
{
    public static class App
    {


        public final static String NAME = "app_name";

        /**
         * SharedPreference名称
         */
        public final static String APP_SP_NAME = "wifi_app";

        /**
         * 上一个版本号
         */
        public final static String PREVIOUS_VERSION_CODE        = "previous_version_code";

        /**
         * 是否是第一次运行程序
         */
        public final static String IS_FIRST_RUNNING = "is_first_running";


        /**
         * 是否自动连接
         */
        public final static String IS_AUTO_CONNECT = "is_auto_connect";


        /**
         * 是否导入热点
         */
        public final static String IS_IMPORT_HOT = "is_import_hot";

        /**
         * 服务验证Token
         */
        public final static String TOKEN = "token";

        /**
         * Token过期时间
         */
        public final static String TOKEN_TIME = "token_time";
    }

    public static class User
    {
        public final static String NAME         = "user";
        public final static String LOGIN_STATUS = "login_status";

    }


    /**
     * 设置开关
     */
    public static class Toggle
    {
        public final static String NAME          = "set_toggle";
        public final static String IS_OPEN_PUSH  = "open_push";
        public final static String IS_LOAD_PIC   = "load_pic";
        public final static String IS_WIFI_CACHE = "wifi_cache";
    }

}
