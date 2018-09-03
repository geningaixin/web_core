package com.huagao.core.db.mapper.attach.sqlwriter;

import java.util.List;

/**
 * @title: LeftJoin.java
 * @description: sql LeftJoin 对象
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class LeftJoin {
	
	private Class<?> modelCls;
	
	// 关联对象，如：UserInfo
	private String modelName;
	
	// 关联对象需要查询的字段及别名
	private List<SelectColumn> columns;
	
	// 主表 on
	private String mainModelFieldName;
	
	// 关联表 on
	private String joinModelFieldName;
	
	// where条件
	private List<Condition> conditions;
	
	public LeftJoin() {
		
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public List<SelectColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<SelectColumn> columns) {
		this.columns = columns;
	}

	public String getMainModelFieldName() {
		return mainModelFieldName;
	}

	public void setMainModelFieldName(String mainModelFieldName) {
		this.mainModelFieldName = mainModelFieldName;
	}

	public String getJoinModelFieldName() {
		return joinModelFieldName;
	}

	public void setJoinModelFieldName(String joinModelFieldName) {
		this.joinModelFieldName = joinModelFieldName;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public Class<?> getModelCls() {
		return modelCls;
	}

	public void setModelCls(Class<?> modelCls) {
		this.modelCls = modelCls;
		this.modelName = modelCls.getSimpleName();
	}
}
