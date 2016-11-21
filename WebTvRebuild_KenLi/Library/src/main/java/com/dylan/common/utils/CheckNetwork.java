package com.dylan.common.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class CheckNetwork {

	public static final int NONE_CONNECT = 0;
	public static final int MOBILE_ONLY = 1;
	public static final int WIFI_ONLY = 2;
	public static final int BOTH_CONNECT = 3;
	public static final int CONNECT_STATE_UNKNOWN = 4;

	private Context context;
	private ConnectivityManager manager;
	private AlertDialogClickListener mAlertDialogClickListener;

	/**
	 * the network is 3G only, are you sure to go on?
	 * 
	 */
	public interface AlertDialogClickListener {
		/**
		 * agree to go on
		 */
		void onPositiveButtonClick();

		/**
		 * disagree to go on
		 */
		void onNegativeButtonClick();
	}

	public CheckNetwork(Context context) {
		this.context = context;
		manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * Get network state
	 * 
	 * @param enableAlertDialog
	 *            if true, when network is disconnected, it will show
	 *            AlertDialog
	 * @return true: network is connected</br>false: network is disconnected
	 */
	public boolean getNetworkState(boolean enableAlertDialog) {
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if(networkinfo == null || !networkinfo.isAvailable()) {
			if(enableAlertDialog) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("无可用网络").setMessage("现在去设置网络?");
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						try {
							int sdkVersion = android.os.Build.VERSION.SDK_INT;
							if(sdkVersion > 10) {
								intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
							} else {
								intent = new Intent();
								ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
								intent.setComponent(comp);
								intent.setAction("android.intent.action.VIEW");
							}
							context.startActivity(intent);
						} catch (Exception e) {
							return;
						}
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
			return false;
		}
		return true;
	}

	/**
	 * @param enableAlertDialog
	 *            if true, when wifi is disconnected, it will show AlertDialog
	 * @return Wifi state
	 */
	public State getWifiState(boolean enableAlertDialog) {
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(enableAlertDialog && wifi != State.CONNECTED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Wifi网络处于关闭状态").setMessage("现在去设置网络?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = null;
					try {
						int sdkVersion = android.os.Build.VERSION.SDK_INT;
						if(sdkVersion > 10) {
							intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
							intent.setComponent(comp);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					} catch (Exception e) {
						return;
					}
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
		return wifi;
	}

	/**
	 * @param enableAlertDialog
	 *            if true, when 3G is disconnected, it will show AlertDialog
	 * @return 3G state
	 */
	public State get3GState(boolean enableAlertDialog) {
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if(enableAlertDialog && mobile != State.CONNECTED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("3G网络处于关闭状态").setMessage("现在去设置网络?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = null;
					try {
						int sdkVersion = android.os.Build.VERSION.SDK_INT;
						if(sdkVersion > 10) {
							intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
							intent.setComponent(comp);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					} catch (Exception e) {
						return;
					}
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
		return mobile;
	}

	/**
	 * @param enableAlertDialog
	 *            if true, when network is disconnected or only 3G is connected,
	 *            it will show AlertDialog
	 * @param listener
	 *            CheckNetwork.AlertDialogClickListener
	 * @return 
	 *         CheckNetwork.NONE_CONNECT</br>CheckNetwork.MOBILE_ONLY</br>CheckNetwork
	 *         .WIFI_ONLY</br>CheckNetwork.BOTH_CONNECT</br>CheckNetwork.
	 *         CONNECT_STATE_UNKNOWN</br>
	 */
	public int check3GOnly(boolean enableAlertDialog, AlertDialogClickListener listener) {
		mAlertDialogClickListener = listener;
		if(!getNetworkState(enableAlertDialog)) {
			return NONE_CONNECT;
		}
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(mobile == State.CONNECTED && wifi == State.CONNECTED) {
			return BOTH_CONNECT;
		} else if(mobile != State.CONNECTED && wifi == State.CONNECTED) {
			return WIFI_ONLY;
		} else if(mobile == State.CONNECTED && wifi != State.CONNECTED) {
			if(enableAlertDialog) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续?");
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(mAlertDialogClickListener != null) {
							mAlertDialogClickListener.onPositiveButtonClick();
						}
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(mAlertDialogClickListener != null) {
							mAlertDialogClickListener.onNegativeButtonClick();
						}
					}
				}).show();
			}
			return MOBILE_ONLY;
		}
		return CONNECT_STATE_UNKNOWN;
	}

}
