package com.sobey.cloud.webtv.sdk.web.inf;

import com.sobey.cloud.webtv.sdk.web.JSRequest;

/**
 * Html交互接口
 * Created by higgses on 14-5-30.
 */
public interface IcityJavaScript
{
    /**
     * 获取用户信息
     *
     * @param jsRequest
     */
    public void getUserInfo(JSRequest jsRequest);

    /**
     * 发送短信息
     *
     * @param jsRequest
     */
    public void sendMessage(JSRequest jsRequest);

    /**
     * 拨打电话的功能
     *
     * @param jsRequest
     */
    public void call(JSRequest jsRequest);

    /**
     * 支付接口
     *
     * @param jsRequest
     */
    public void pay(JSRequest jsRequest);

    /**
     * 分享接口
     *
     * @param jsRequest
     */
    public void jsShare(JSRequest jsRequest);

    /**
     * 三方登陆
     *
     * @param jsRequest
     */
    public void login(JSRequest jsRequest);

    /**
     * 下载
     *
     * @param jsRequest
     */
    public void download(JSRequest jsRequest);

    /**
     * 播放视频
     *
     * @param jsRequest
     */
    public void playVideo(JSRequest jsRequest);

    /**
     * 重新加载页面
     */
    public void reload();

    /**
     * 启动一个新页面
     *
     * @param jsRequest
     */
    public void startOtherPage(JSRequest jsRequest);

    /**
     * 启动页面
     */
    public void startPage(JSRequest jsRequest);

    /**
     * 启动页面带返回结果
     */
    public void startPageForResult(JSRequest jsRequest);

    /**
     * 获取应用信息
     *
     * @param jsRequest
     */
    public void getAppInfo(JSRequest jsRequest);

    /**
     * 关闭这个页面
     */
    public void finishPage(JSRequest jsRequest);

    /**
     * 设置返回的结果
     */
    public void setResult(JSRequest jsRequest);

    /**
     * 返回
     */
    public void onBackPress(JSRequest jsRequest);

    /**
     * 支付宝支付
     *
     * @param jsRequest
     */
    public void alipay(JSRequest jsRequest);

    /**
     * 图片浏览器
     *
     * @param jsRequest
     */
    public void imageBrowser(JSRequest jsRequest);

    /**
     * 普通事件统计
     *
     * @param jsRequest
     */
    public void event(JSRequest jsRequest);

    /**
     * 延迟事件开始
     *
     * @param jsRequest
     */
    public void delayEventStart(JSRequest jsRequest);

    /**
     * 延迟事件结束
     *
     * @param jsRequest
     */
    public void delayEventEnd(JSRequest jsRequest);

    /**
     * 加载相关新闻
     *
     * @param jsRequest
     */
    public void loadRelatedNews(JSRequest jsRequest);

    /**
     * 加载相关服务
     *
     * @param jsRequest
     */
    public void loadRelatedServer(JSRequest jsRequest);

    /**
     * 加载相关活动
     *
     * @param jsRequest
     */
    public void loadRelatedActivity(JSRequest jsRequest);

    /**
     * 地图定位
     *
     * @param jsRequest
     */
    public void mapLocation(JSRequest jsRequest);

    /**
     * 路径规划
     *
     * @param jsRequest
     */
    public void routePlan(JSRequest jsRequest);
}
