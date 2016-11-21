package com.sobey.cloud.webtv.cordova.plugin;

import org.apache.cordova.CordovaPlugin;

public class MyCordovaPlugin extends CordovaPlugin {
	@Override
	public Boolean shouldAllowNavigation(String url) {
		return true;
	}

	@Override
	public Boolean shouldAllowRequest(String url) {
		return true;
	}
}
