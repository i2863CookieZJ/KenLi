package com.dylan.common.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GSensor {

	private int mRotateDegree = 0;

	public GSensor(Context context) {
		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(new SensorEventListener() {
			public void onSensorChanged(SensorEvent event) {
				if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
					return;
				}
				float[] values = event.values;
				double x = values[0];
				double y = values[1];
				double i = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				if (x > 0) {
					mRotateDegree = (int) Math.round(Math.acos(y / i) * 180 / Math.PI);
				} else {
					mRotateDegree = (int) Math.round(2 * Math.PI - Math.acos(y / i) * 180 / Math.PI);
				}
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		}, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	public int getRotateDegree() {
		return mRotateDegree;
	}
}
