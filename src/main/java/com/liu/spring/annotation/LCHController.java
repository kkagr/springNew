package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:04:31
* 类说明
*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHController {
	  String value() default "";
}
