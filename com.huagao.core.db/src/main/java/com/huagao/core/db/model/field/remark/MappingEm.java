package com.huagao.core.db.model.field.remark;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingEm {
	
	/**
	 * 映射到哪个字段
	 * @return String
	 */
	String where() default "";
	
	/**
	 * 映射枚举Class
	 * @return Class<?>
	 */
	Class<?> enumCls();
}
