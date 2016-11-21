package com.appsdk.video.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {

	private static final String PAUSE_TIME = "advancedvideoview_pausetime";
	private static final String VOLUME = "advancedvideoview_volume";
	private static final String SOUNDABLE = "advancedvideoview_soundable";

	public static long getPauseTime(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getLong(PAUSE_TIME, 0);
	}

	public static void putPauseTime(Context context, long time) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putLong(PAUSE_TIME, time);
		editor.commit();
	}

	public static int getVolume(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt(VOLUME, 1);
	}

	public static void putVolume(Context context, int volume) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(VOLUME, volume);
		editor.commit();
	}

	public static boolean getSoundable(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SOUNDABLE, true);
	}

	public static void putSoundable(Context context, boolean soundable) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(SOUNDABLE, soundable);
		editor.commit();
	}
}
