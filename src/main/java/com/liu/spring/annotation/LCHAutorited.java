package com.liu.spring.annotation;

import java.lang.annotation.*;

/**
* @author kkagr
* @version ����ʱ�䣺2018��5��6�� ����10:23:50
* ��˵��
*/
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD,ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LCHAutorited {
	String value() default "";
}
