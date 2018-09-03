package com.huagao.core.db.exception;

/**
 * 系统异常处理器
 */
public interface DatabaseExceptionHandler {

	/**
	 * 自定义异常如何处理
	 */
	void excute(DatabaseException databaseException);
}
