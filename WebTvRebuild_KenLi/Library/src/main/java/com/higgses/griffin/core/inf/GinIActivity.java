package com.higgses.griffin.core.inf;

import com.higgses.griffin.netstate.utils.GinUNetWork;

/**
 * Activity接口
 */
public interface GinIActivity
{
    public void onConnect(GinUNetWork.NetType type);

    public void onDisConnect();
}
