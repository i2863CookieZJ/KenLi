package com.sobey.cloud.webtv.sdk.web.inf;

import com.sobey.cloud.webtv.sdk.web.JSRequest;

/**
 * 导航交互接口
 * Created by Carlton on 2014/7/11.
 */
public interface INavJavaScript
{
    /**
     * 更新导航样式
     *
     * @param jsRequest
     */
    public void updateNav(JSRequest jsRequest);

    /**
     * 更新文字
     *
     * @param jsRequest
     */
    public void updateNavText(JSRequest jsRequest);

    /**
     * 更新左边的文字
     *
     * @param jsRequest
     */
    public void updateNavLeftText(JSRequest jsRequest);

    /**
     * 更新右边的文字
     *
     * @param jsRequest
     */
    public void updateNavRightText(JSRequest jsRequest);
}
