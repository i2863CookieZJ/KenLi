package com.sobey.cloud.webtv.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class WebUtil
{
    private static Object GETJSONFROMURL_LOCK = new Object();

    public static Map<String, Map<String, String>> DATA_UPLOAD_INFO   = new HashMap<String, Map<String, String>>();
    public static Map<String, Map<String, String>> DATA_DOWNLOAD_INFO = new HashMap<String, Map<String, String>>();

    /**
     * GET请求
     */
    public static JSONObject getJsonFromUrl(final String url) throws Exception
    {
        synchronized (WebUtil.GETJSONFROMURL_LOCK)
        {
            JSONObject o = new JSONObject();
            o.put("R", 0);
            HttpRequestUtil http = new HttpRequestUtil(o);
            http.doGet(url);
            return http.getO();
        }
    }

    /**
     * POST请求:无文件上传
     */
    public static JSONObject postJsonFromUrl(List<BasicNameValuePair> params, String url) throws Exception
    {
        return postJsonFromUrl(params, url, "");
    }

    /**
     * POST请求:无文件上传
     */
    public static JSONObject postJsonFromUrl(List<BasicNameValuePair> params, String url, String cookieStr) throws Exception
    {
        synchronized (WebUtil.GETJSONFROMURL_LOCK)
        {
            JSONObject o = new JSONObject();
            o.put("R", 0);
            HttpRequestUtil http = new HttpRequestUtil(o);
            http.doPost(url, params, cookieStr);
            return http.getO();
        }
    }

    /**
     * POST请求:有文件上传
     *
     * @throws Exception
     */
    public static JSONObject postFile(Map<String, String> textMap, Map<String, String> fileMap, String url) throws Exception
    {
        return postFile(textMap, fileMap, url, "");
    }

    /**
     * POST请求:有文件上传
     *
     * @throws Exception
     */
    public static JSONObject postFile(Map<String, String> textMap, Map<String, String> fileMap, String url, String cookie) throws Exception
    {
        JSONObject o = new JSONObject();
        o.put("R", 0);
        HttpRequestUtil http = new HttpRequestUtil(o);
        http.doUpload(url, textMap, fileMap, cookie);
        return http.getO();
    }
}
