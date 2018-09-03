package com.huagao.core.db.exception;

/**
 * title : DatabaseException.java
 * description: 数据库操作异常
 * author GeNing
 * date 2018年8月6日
 * version v1.0
 */
public class DatabaseException extends RuntimeException {

	private DatabaseExceptionHandler databaseExceptionHandler;
	private String errCode;
	private String errMsg;

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
		this.errCode = "10001";
		this.errMsg = "数据操作异常，请稍后再试";
		databaseExceptionHandler.excute(this);
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public DatabaseExceptionHandler getDatabaseExceptionHandler() {
		return databaseExceptionHandler;
	}

	public void setDatabaseExceptionHandler(DatabaseExceptionHandler databaseExceptionHandler) {
		this.databaseExceptionHandler = databaseExceptionHandler;
	}
}
