package com.sobey.cloud.webtv.model.provide.common;

import com.higgses.griffin.core.inf.GinIControl;
import com.sobey.cloud.webtv.core.BaseModel;

import android.os.Handler.Callback;
import android.os.Message;

public class CommonModel extends BaseModel implements Callback {

	public CommonModel(GinIControl control) {
		super(control);
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

}
