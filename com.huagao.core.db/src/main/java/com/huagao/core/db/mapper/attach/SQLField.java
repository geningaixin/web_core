package com.huagao.core.db.mapper.attach;

/**
 * @title: SQLField.java
 * @description: 字段Model定义
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class SQLField {
	
	// 数据库字段名：如 username、password
	private String dbField;
	
	// 实体类字段名：如 username、password
	private String modelField;
	
	// 表达式：如 #{ username }
	private String expression;
	
	// 具体值，Obj型
	private Object dbValueObj;
	
	// 具体值，字符串型
	private String dbValueStr;
	
	/**
	 * 默认构造
	 */
	public SQLField() {
		
	}

	public String getDbField() {
		return dbField;
	}

	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	public String getModelField() {
		return modelField;
	}

	public void setModelField(String modelField) {
		this.modelField = modelField;
	}

	public Object getDbValueObj() {
		return dbValueObj;
	}

	public void setDbValueObj(Object dbValueObj) {
		this.dbValueObj = dbValueObj;
	}

	public String getDbValueStr() {
		return dbValueStr;
	}

	public void setDbValueStr(String dbValueStr) {
		this.dbValueStr = dbValueStr.replace("\'", "\'\'");
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
