package com.sobey.cloud.webtv.views.web;

import android.content.Intent;

/**
 * 和JS交互的时候，页面返回的结果监听
 * Created by Carlton on 2014/8/12.
 */
public interface OnActivityResult
{
    /**
     * Activity带结果的返回回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data);
}
