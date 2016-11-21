package com.higgses.griffin.http;

import com.higgses.griffin.http.listener.SimpleProgressListener;
import com.higgses.griffin.log.GinLog;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.higgses.griffin.cache.AsyncTask;
import com.higgses.griffin.http.listener.GinIProgressListener;

/**
 * 文件上传
 * Created by User on 2014/6/30.
 */
public class HttpUploader
{
    private static final String TAG  = "HttpUploader";
    private static final String FILE = "_!@#$file!@#$";

    /**
     * 在后台上传文件
     *
     * @param url
     *         服务器接受文件的url
     * @param param
     *         请求参数，除了文件还可以有其他的附带参数
     * @param loadListener
     *         上传监听，完成，失败或者正在上传
     */
    public void uploadFileInBack(String url, Map<String, Object> param, GinIProgressListener loadListener)
    {
        ArrayList<NameValuePair> list = new ArrayList<>();
        for (String key : param.keySet())
        {
            Object valueObj = param.get(key);
            String value = String.valueOf(valueObj);
            String realKey = key;
            if (valueObj instanceof File)
            {
                realKey = getFileKey(key);
                value = ((File) valueObj).getAbsolutePath();
            }
            list.add(new BasicNameValuePair(realKey, value));
        }
        new HttpPostAsyncTask(list, loadListener).execute(url);
    }

    /**
     * 处理参数是文件的时候的Key
     *
     * @param key
     *         需要被转换的key
     *
     * @return
     */
    private static String getFileKey(String key)
    {
        return key + FILE;
    }

    /**
     * 上传文件的任务
     */
    private class HttpPostAsyncTask extends AsyncTask<String, Long, String>
    {
        private GinIProgressListener mProgressListener;
        private long                     mTotalSize;
        private ArrayList<NameValuePair> mNameValuePairs;
        private boolean isComplete = false;

        public HttpPostAsyncTask(ArrayList<NameValuePair> valuePairs, GinIProgressListener listener)
        {
            mProgressListener = listener;
            mNameValuePairs = valuePairs;
            if (mProgressListener == null)
            {
                mProgressListener = new SimpleProgressListener();
            }
        }

        /**
         * 处理参数
         *
         * @param entity
         */
        private Map<String, String> handlerParams(UploadMultipartEntity entity)
        {
            Map<String, String> paramsPrint = new HashMap<>();
            for (NameValuePair nameValuePair : mNameValuePairs)
            {
                String name = nameValuePair.getName();
                String value = nameValuePair.getValue();
                if (name.endsWith(FILE))
                {// 上传的是文件
                    String replaceName = name.replace(FILE, "");
                    entity.addPart(replaceName, new FileBody(new File(value)));
                    paramsPrint.put(replaceName, value);
                }
                else
                {// 参数是字符
                    try
                    {
                        entity.addPart(name, new StringBody(value, Charset.forName("UTF-8")));
                        paramsPrint.put(name, value);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            GinLog.i(TAG, "上传参数:" + paramsPrint);
            return paramsPrint;
        }

        /**
         * 处理结果
         *
         * @param httpClient
         * @param httpPost
         */
        private String handlerResult(HttpClient httpClient, HttpPost httpPost)
        {
            HttpResponse response = null;
            //取出回应字串
            String strResult = null;
            try
            {
                response = httpClient.execute(httpPost);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                strResult = e.getMessage();
                return strResult;
            }
            //若状态码为200
            if (response.getStatusLine().getStatusCode() == 200)
            {
                try
                {
                    strResult = EntityUtils.toString(response.getEntity());
                    isComplete = true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    strResult = e.getMessage();
                }
            }
            else
            {
                strResult = "Error Response" + response.getStatusLine().toString();
            }
            return strResult;
        }

        @Override
        protected String doInBackground(String... params)
        {
            String url = params[0];
            isComplete = false;
            UploadMultipartEntity entity = new UploadMultipartEntity(new WriteListener()
            {
                @Override
                public void write(long length)
                {
                    publishProgress(length);
                }
            });
            handlerParams(entity);
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            mTotalSize = entity.getContentLength();

            return handlerResult(httpClient, httpPost);

        }

        @Override
        protected void onProgressUpdate(Long... values)
        {
            super.onProgressUpdate(values);
            mProgressListener.onProgress(mTotalSize, values[0]);
        }

        @Override
        protected void onPostExecute(String response)
        {
            super.onPostExecute(response);
            if (isComplete)
            {
                mProgressListener.onComplete(response);
            }
            else
            {
                mProgressListener.onFail(response);
            }
        }
    }

    /**
     * 扩展了参数类型，让其可以监听文件的上传状态
     */
    private class UploadMultipartEntity extends MultipartEntity
    {
        private final WriteListener mListener;

        public UploadMultipartEntity(final WriteListener listener)
        {
            super();
            mListener = listener;
        }

        public UploadMultipartEntity(final HttpMultipartMode mode, final WriteListener listener)
        {
            super(mode);
            mListener = listener;
        }

        public UploadMultipartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final WriteListener listener)
        {
            super(mode, boundary, charset);
            mListener = listener;
        }

        @Override
        public void writeTo(OutputStream outstream) throws IOException
        {
            super.writeTo(new ProgressOutputStream(outstream, mListener));
        }
    }

    /**
     * 文件输出的进程，可以监听文件的写入状态
     */
    private class ProgressOutputStream extends FilterOutputStream
    {
        private final WriteListener mListener;
        private       long          mLength;

        public ProgressOutputStream(final OutputStream out, final WriteListener listener)
        {
            super(out);
            mListener = listener;
            mLength = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException
        {
            out.write(b, off, len);
            mLength += len;
            mListener.write(mLength);
        }

        public void write(int b) throws IOException
        {
            out.write(b);
            mLength++;
            mListener.write(mLength);
        }
    }

    private interface WriteListener
    {
        public void write(long length);
    }
}
