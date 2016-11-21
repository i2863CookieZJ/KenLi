package com.sobey.cloud.webtv.web;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.log.GinLog;

import android.util.Log;

public class HttpRequestUtil
{
    private JSONObject o;

    private int WIFI_REQUEST_TIMEOUT = 3 * 1000;// 设置请求超时
    private int WIFI_SO_TIMEOUT      = 10 * 1000; // 设置等待数据超时时间

    private HttpResponse httpResponse;

    private HttpContext localContext = null;

    public HttpRequestUtil(JSONObject o)
    {
        this.o = o;
    }

    /**
     * 访问服务器：GET 方式进行访问
     *
     * @param url
     */
    public void doGet(String url)
    {
        final HttpGet httpGet = new HttpGet(url);

        try
        {
            final HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), WIFI_REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), WIFI_SO_TIMEOUT);
            httpResponse = null;

            Thread hth = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        httpResponse = httpClient.execute(httpGet);
                    }
                    catch (Exception ec)
                    {
                        httpResponse = null;
                        interrupted();
                    }
                }
            };

            hth.start();

            hth.join(WIFI_REQUEST_TIMEOUT + WIFI_SO_TIMEOUT);
            if (httpResponse == null)
            {
                hth.interrupt();
                String error = "无法访问服务器！";
                o.put("error", error);
            }
            else
            {
                if (httpResponse.getStatusLine().getStatusCode() == 200)
                {
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    o = new JSONObject(result);
                }
                else
                {
                    String error = "服务器返回错误的状态:" + httpResponse.getStatusLine().getStatusCode();
                    o.put("error", error);
                }
            }

            o.put("R", 1);
        }
        catch (Exception e)
        {
            try
            {
                o.put("R", 1);
                o.put("error", "获得服务器Json信息失败:" + LogUtil.getErrorStack(e));
            }
            catch (JSONException e1)
            {
            }
        }
    }

    /**
     * 访问服务器：Post方式请求，普通请求无文件上传
     *
     * @param params
     * @param baseUrl
     *
     * @return
     */
    public void doPost(String baseUrl, List<BasicNameValuePair> params)
    {
        doPost(baseUrl, params, "");
    }

    /**
     * 访问服务器：Post方式请求，普通请求无文件上传
     *
     * @param params
     * @param baseUrl
     *
     * @return
     */
    public void doPost(String baseUrl, List<BasicNameValuePair> params, String cookieStr)
    {
        try
        {
            Log.d("WebUtils", baseUrl);

            final HttpPost httpPost = new HttpPost(baseUrl);
            if (params.size() == 0)
            {
                httpPost.setEntity(new StringEntity(""));
            }
            else
            {
                httpPost.setEntity(new StringEntity(params.get(0).getValue(), "utf-8"));
                GinLog.i("WebUtils", params.get(0).getValue());
            }
            //            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //
            final HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, WIFI_REQUEST_TIMEOUT);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, WIFI_SO_TIMEOUT);

            if (!"".equals(cookieStr))
            {
                // // parse name/value from mCookies[0]. If you have more than
                // one
                // // cookie, a for cycle is needed.
                // CookieStore cookieStore = new BasicCookieStore();
                // Cookie cookie = new BasicClientCookie("Cookie", cookieStr);
                // cookieStore.addCookie(cookie);
                // localContext = new BasicHttpContext();
                // localContext.setAttribute(ClientContext.COOKIE_STORE,
                // cookieStore);
                //
                // httpClient.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER,
                // cookieStr);

                httpPost.addHeader("session_id", cookieStr);
                httpPost.addHeader("cookie", cookieStr);
            }

            Thread hth = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (localContext == null)
                        {
                            httpResponse = httpClient.execute(httpPost);
                        }
                        else
                        {
                            httpResponse = httpClient.execute(httpPost, localContext);
                        }
                    }
                    catch (Exception ec)
                    {
                        httpResponse = null;
                        interrupted();
                    }
                }
            };

            hth.start();

            hth.join(WIFI_REQUEST_TIMEOUT + WIFI_SO_TIMEOUT);

            if (httpResponse == null)
            {
                hth.interrupt();

                String error = "无法访问服务器！";
                o.put("error", error);
            }
            else
            {
                if (httpResponse.getStatusLine().getStatusCode() == 200)
                {
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    o = new JSONObject(result);

                    Log.d("WebUtils", "获得服务器Json信息:" + result);
                }
                else
                {
                    String error = "服务器返回错误的状态:" + httpResponse.getStatusLine().getStatusCode();
                    o.put("error", error);
                }
            }

            o.put("R", 1);
        }
        catch (Exception e)
        {
            String msg = "获得服务器Json信息失败:" + LogUtil.getErrorStack(e);
            try
            {
                o.put("R", 1);
                o.put("error", msg);
            }
            catch (Exception e2)
            {
            }
        }
    }

    /**
     * 上传图片
     *
     * @param baseUrl
     * @param textMap
     * @param fileMap
     *
     * @return
     */
    public void doUpload(String baseUrl, Map<String, String> textMap, Map<String, String> fileMap)
    {
        doUpload(baseUrl, textMap, fileMap, "");
    }

    /**
     * 上传图片
     *
     * @param baseUrl
     * @param textMap
     * @param fileMap
     *
     * @return
     */
    public void doUpload(String baseUrl, Map<String, String> textMap, Map<String, String> fileMap, String authorization)
    {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
        try
        {
            URL url = new URL(baseUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            if (!"".equals(authorization))
            {
                conn.setRequestProperty("Authorization", authorization);
            }

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null)
            {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    GinLog.i(inputName, inputValue);
                    if (inputValue == null)
                    {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            if (fileMap != null)
            {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null)
                    {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();
                    String contentType = new MimetypesFileTypeMap().getContentType(file);
                    if (filename.endsWith(".png"))
                    {
                        contentType = "image/png";
                    }
                    if (contentType == null || contentType.equals(""))
                    {
                        contentType = "application/octet-stream";
                    }

                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

                    out.write(strBuf.toString().getBytes());

                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1)
                    {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            o = new JSONObject(res);
            o.put("R", 1);
            reader.close();
            reader = null;
        }
        catch (Exception e)
        {
            System.out.println("发送POST请求出错:" + baseUrl);
            String msg = "获得服务器Json信息失败:" + LogUtil.getErrorStack(e);
            try
            {
                o.put("R", 1);
                o.put("error", msg);
            }
            catch (Exception e2)
            {
            }
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
                conn = null;
            }
        }
    }

    public JSONObject getO()
    {
        return o;
    }

    public void setO(JSONObject o)
    {
        this.o = o;
    }

}
