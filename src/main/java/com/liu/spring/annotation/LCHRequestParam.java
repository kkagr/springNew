package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version ����ʱ�䣺2018��5��6�� ����10:12:40
* ��˵��
*/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHRequestParam {
	String value() default "";
}
