package com.higgses.griffin.volley.toolbox;

import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;

/**
 * 
 * @author dochys
 * @see Jackson tools
 */
public class JacksonUtils implements Jackson {

	private static final String TAG = "JacksonUtils";

	private static JsonGenerator mJsonGenerator;
	private static ObjectMapper mObjectMapper;

	public JacksonUtils() {
	}

	/**
	 * 
	 * @param createNew
	 *            :create ObjectMapper method that if createNew is false,will
	 *            use the existed ObjectMapper or (createNew is true) will new a
	 *            ObjectMapper
	 * @return
	 */
	public static JacksonUtils getInstance(boolean createNew) {
		if (createNew) {
			try {
				mObjectMapper = new ObjectMapper();
				mJsonGenerator = mObjectMapper.getJsonFactory()
						.createJsonGenerator(System.out, JsonEncoding.UTF8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mObjectMapper == null) {
			try {
				mObjectMapper = new ObjectMapper();
				mJsonGenerator = mObjectMapper.getJsonFactory()
						.createJsonGenerator(System.out, JsonEncoding.UTF8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new JacksonUtils();
	}

	@Override
	public void destory() {
		try {
			if (mJsonGenerator != null) {
				mJsonGenerator.flush();
				if (!mJsonGenerator.isClosed()) {
					mJsonGenerator.close();
				}
			}
			mJsonGenerator = null;
			mObjectMapper = null;
			System.gc();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 
	 * @param jsonString
	 *            :String that will be covert to Object
	 * @param clazz
	 *            : Class<?> clazz that target object's class object
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object stringToObject(int method, String jsonString, Class clazz) {
		Object object = null;
		try {
			object = mObjectMapper.readValue(jsonString, clazz);
		} catch (JsonParseException e) {
			Log.e(TAG, e.getMessage());
		} catch (JsonMappingException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return object;
	}

	@Override
	public Object stringToObject(String jsonString, TypeReference typeReference) {
		Object object = null;
		try {
			object = mObjectMapper.readValue(jsonString, typeReference);
		} catch (JsonParseException e) {
			Log.e(TAG, e.getMessage());
		} catch (JsonMappingException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return object;
	}
}
