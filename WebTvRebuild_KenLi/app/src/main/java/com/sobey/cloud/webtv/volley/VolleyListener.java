package com.sobey.cloud.webtv.volley;

import com.android.volley.VolleyError;

public abstract class VolleyListener {

	public abstract void onSuccess(String arg0);

	public abstract void onFail(VolleyError arg0);

	public abstract void onFinish();
}
