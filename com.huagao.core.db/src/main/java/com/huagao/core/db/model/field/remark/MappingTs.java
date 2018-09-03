package com.huagao.core.db.model.field.remark;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingTs {
	
	/**
	 * 映射到哪个字段
	 * @return
	 */
	public String where() default "";
}
