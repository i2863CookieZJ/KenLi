package com.sobey.cloud.webtv.core;

import com.higgses.griffin.core.AbstractModel;
import com.higgses.griffin.core.inf.GinIControl;

/**
 * BaseModel
 * Created by higgses on 14-5-12.
 */
public abstract class BaseModel extends AbstractModel
{
    public BaseModel(GinIControl control)
    {
        super(control);
        ContextApplication.getApp();
    }
}
