package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version ����ʱ�䣺2018��5��6�� ����10:09:26
* ��˵��
*/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHRequestMapping {
	String value() default "";
}
