package com.huagao.core.db.model.field.remark;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeSearch {
	
	public String beginTime() default "";
	
	public String endTime() default "";
}
