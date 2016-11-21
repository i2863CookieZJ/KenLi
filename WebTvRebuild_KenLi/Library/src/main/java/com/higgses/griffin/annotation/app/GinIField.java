package com.higgses.griffin.annotation.app;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Title TAPrimaryKey 主键配置
 * @Package com.ta.core.util.db.annotation
 * @Description 不配置的时候默认找类的id或_id字段作为主键，column不配置的是默认为字段名，如果不设置主键，自动生成ID
 * @author 白猫
 * @date 2013-1-20
 * @version V1.0
 */
@Target(
{ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GinIField
{
	/**
	 * 设置配置的名
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * 设置配置的默认值
	 * 
	 * @return
	 */
	public String defaultValue() default "";

}