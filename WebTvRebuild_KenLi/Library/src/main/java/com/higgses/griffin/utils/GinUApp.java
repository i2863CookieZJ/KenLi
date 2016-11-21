package com.higgses.griffin.utils;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * android 应用工具类
 * Create by YangQiang on 2014/6/2.
 */
public class GinUApp
{
    private GinUApp()
    {

    }

    /**
     * 处理返回的发送状态
     */
    public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";

    /**
     * 处理返回的接收状态
     */
    public static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

    /**
     * 发送短信
     *
     * @param context
     * @param tel
     * @param message
     */
    public static void sendSMS(Context context, String tel, String message)
    {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.putExtra("sms_body", message);
        context.startActivity(sendIntent);
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param tel
     */
    public static void call(Context context, String tel)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    public static void sendSMSInBack(Context context, String phoneNumber, String message)
    {
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents)
        {
            PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION), 0);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION), 0);
            smsManager.sendTextMessage(phoneNumber, null, text, sentIntent, deliveryIntent);
        }
    }
}
