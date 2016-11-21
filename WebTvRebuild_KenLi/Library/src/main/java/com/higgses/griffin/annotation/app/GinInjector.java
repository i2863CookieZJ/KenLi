/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.higgses.griffin.annotation.app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.higgses.griffin.annotation.app.view.EventListenerManager;
import com.higgses.griffin.annotation.app.view.PreferenceInject;
import com.higgses.griffin.annotation.app.view.ResInject;
import com.higgses.griffin.annotation.app.view.ResLoader;
import com.higgses.griffin.annotation.app.view.ViewFinder;
import com.higgses.griffin.annotation.app.view.ViewInjectInfo;
import com.higgses.griffin.core.utils.LogUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.support.v4.app.Fragment;
import android.view.View;

public class GinInjector {

	private static GinInjector instance;

	private GinInjector() {

	}

	public static GinInjector getInstance() {
		if (instance == null) {
			instance = new GinInjector();
		}
		return instance;
	}

	public static void inject(View view) {
		injectObject(view, new ViewFinder(view));
	}

	public void inject(Activity activity) {
		injectObject(activity, new ViewFinder(activity));
	}

	public static void inject(PreferenceActivity preferenceActivity) {
		injectObject(preferenceActivity, new ViewFinder(preferenceActivity));
	}

	public void inject(Object handler, View view) {
		injectObject(handler, new ViewFinder(view));
	}

	public static void inject(Object handler, Activity activity) {
		injectObject(handler, new ViewFinder(activity));
	}

	public static void inject(Object handler, PreferenceGroup preferenceGroup) {
		injectObject(handler, new ViewFinder(preferenceGroup));
	}

	public static void inject(Object handler, PreferenceActivity preferenceActivity) {
		injectObject(handler, new ViewFinder(preferenceActivity));
	}

	@SuppressWarnings("ConstantConditions")
	private static void injectObject(Object handler, ViewFinder finder) {

		Class<?> handlerType = handler.getClass();

		// inject view
		Field[] fields = handlerType.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				GinInjectView viewInject = field.getAnnotation(GinInjectView.class);
				if (viewInject != null) {
					try {
						View view = finder.findViewById(viewInject.id(), viewInject.parentId());
						if (view != null) {
							field.setAccessible(true);
							field.set(handler, view);
						}
					} catch (Throwable e) {
						LogUtils.e(e.getMessage(), e);
					}
				} else {
					ResInject resInject = field.getAnnotation(ResInject.class);
					if (resInject != null) {
						try {
							Object res = ResLoader.loadRes(resInject.type(), finder.getContext(), resInject.id());
							if (res != null) {
								field.setAccessible(true);
								field.set(handler, res);
							}
						} catch (Throwable e) {
							LogUtils.e(e.getMessage(), e);
						}
					} else {
						PreferenceInject preferenceInject = field.getAnnotation(PreferenceInject.class);
						if (preferenceInject != null) {
							try {
								Preference preference = finder.findPreference(preferenceInject.value());
								if (preference != null) {
									field.setAccessible(true);
									field.set(handler, preference);
								}
							} catch (Throwable e) {
								LogUtils.e(e.getMessage(), e);
							}
						}
					}
				}
			}
		}

		// inject event
		Method[] methods = handlerType.getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				Annotation[] annotations = method.getDeclaredAnnotations();
				if (annotations != null && annotations.length > 0) {
					for (Annotation annotation : annotations) {
						Class<?> annType = annotation.annotationType();
						if (annType.getAnnotation(EventBase.class) != null) {
							method.setAccessible(true);
							try {
								// ProGuard：-keep class * extends
								// java.lang.annotation.Annotation { *; }
								Method valueMethod = annType.getDeclaredMethod("id");
								Method parentIdMethod = null;
								try {
									parentIdMethod = annType.getDeclaredMethod("parentId");
								} catch (Throwable e) {
								}
								Object values = valueMethod.invoke(annotation);
								Object parentIds = parentIdMethod == null ? null : parentIdMethod.invoke(annotation);
								int parentIdsLen = parentIds == null ? 0 : Array.getLength(parentIds);
								int len = Array.getLength(values);
								for (int i = 0; i < len; i++) {
									ViewInjectInfo info = new ViewInjectInfo();
									info.value = Array.get(values, i);
									info.parentId = parentIdsLen > i ? (Integer) Array.get(parentIds, i) : 0;
									EventListenerManager.addEventMethod(finder, info, annotation, handler, method);
								}
							} catch (Throwable e) {
								LogUtils.e(e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 通过注解获取资源文件
	 * 
	 * @param activity
	 */
	public void injectResource(Object activity) {
		Field[] fields = activity.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(GinInjectResource.class)) {
					injectResource(activity, field);
				}
			}
		}
	}

	/**
	 * 通过注解获取资源文件
	 * 
	 * @param object
	 * @param field
	 */
	private void injectResource(Object object, Field field) {
		if (field.isAnnotationPresent(GinInjectResource.class)) {
			GinInjectResource resourceJect = field.getAnnotation(GinInjectResource.class);
			int resourceID = resourceJect.id();
			try {
				Activity activity = null;
				if (object instanceof Activity) {
					activity = (Activity) object;
				} else if (object instanceof Fragment) {
					activity = ((Fragment) object).getActivity();
				}
				field.setAccessible(true);
				Resources resources = activity.getResources();
				String type = resources.getResourceTypeName(resourceID);
				if (type.equalsIgnoreCase("string")) {
					field.set(activity, activity.getResources().getString(resourceID));
				} else if (type.equalsIgnoreCase("drawable")) {
					field.set(activity, activity.getResources().getDrawable(resourceID));
				} else if (type.equalsIgnoreCase("layout")) {
					field.set(activity, activity.getResources().getLayout(resourceID));
				} else if (type.equalsIgnoreCase("array")) {
					if (field.getType().equals(int[].class)) {
						field.set(activity, activity.getResources().getIntArray(resourceID));
					} else if (field.getType().equals(String[].class)) {
						field.set(activity, activity.getResources().getStringArray(resourceID));
					} else {
						field.set(activity, activity.getResources().getStringArray(resourceID));
					}

				} else if (type.equalsIgnoreCase("color")) {
					if (field.getType().equals(Integer.TYPE)) {
						field.set(activity, activity.getResources().getColor(resourceID));
					} else {
						field.set(activity, activity.getResources().getColorStateList(resourceID));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 手动注入
	 *
	 * @param obj
	 *            包含注解的类
	 * @param view
	 *            包含被注解的控件的View，能通过View.findViewById找到
	 */
	public static void manualInjectView(final Object obj, final View view) {
		getInstance().inject(obj, view);
	}
}
