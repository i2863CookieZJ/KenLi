package com.higgses.griffin.volley.toolbox;

import org.codehaus.jackson.type.TypeReference;

public interface Jackson<T> {

	void destory();

	Object stringToObject(String jsonString, TypeReference<T> typeReference);

	Object stringToObject(int method, String jsonString, Class<?> clazz);

}
