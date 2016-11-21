/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.higgses.griffin.database.sqlite;

import java.util.HashMap;

public class DBModel
{
	private HashMap<String, Object> dataMap = new HashMap<String, Object>();
	
	public Object get(String column){
		return dataMap.get(column);
	}
	
	public String getString(String column){
		return String.valueOf(get(column));
	}
	
	public int getInt(String column){
		return Integer.valueOf(getString(column));
	}
	
	public boolean getBoolean(String column){
		return Boolean.valueOf(getString(column));
	}
	
	public double getDouble(String column){
		return Double.valueOf(getString(column));
	}
	
	public float getFloat(String column){
		return Float.valueOf(getString(column));
	}
	
	public long getLong(String column){
		return Long.valueOf(getString(column));
	}
	
	public void set(String key,Object value){
		dataMap.put(key, value);
	}
	
	public HashMap<String, Object> getDataMap(){
		return dataMap;
	}
}
