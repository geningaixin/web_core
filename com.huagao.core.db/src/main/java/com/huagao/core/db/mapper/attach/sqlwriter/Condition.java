package com.huagao.core.db.mapper.attach.sqlwriter;

/**
 * @title: Condition.java
 * @description: sql 条件查询对象
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class Condition {
	
	// 属性名
	private String name;
	
	// 等于、大于、在xxx之间
	private SelectConditionType type;
	
	// 对应值
	private String value;

	public Condition(){
		
	}
	
	public Condition(String name, SelectConditionType type, String value) {
		this.name = name;
		this.type = type;
		this.value = value.replace("\'", "\'\'");
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SelectConditionType getType() {
		return type;
	}

	public void setType(SelectConditionType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 用户操作日志类型
	 * @author GeNing
	 * @since  2016.08.16
	 */
	public enum SelectConditionType {
		IS_NULL,
		NOT_NULL,
		IN,
		NOT_IN,
		GT,
		GTE,
		LT,
		LTE,
		LIKE,
		EQ,
		NOT_EQ,
		BETWEEN,
		NOT_BETWEEN;
	}
}
