package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:23:50
* 类说明
*/
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD,ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHAutorited {
	String value() default "";
}
