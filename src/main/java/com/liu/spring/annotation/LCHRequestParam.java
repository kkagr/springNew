package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:12:40
* 类说明
*/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHRequestParam {
	String value() default "";
}
