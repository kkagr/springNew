package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:09:26
* 类说明
*/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHRequestMapping {
	String value() default "";
}
