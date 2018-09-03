package com.huagao.core.db.model;

import com.huagao.core.db.mapper.attach.sqlwriter.Condition;
import com.huagao.core.db.mapper.attach.sqlwriter.LeftJoin;
import com.huagao.core.db.mapper.attach.sqlwriter.SelectColumn;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;


/**
 * @title: SQLModel.java
 * @description: 实现自定义查询 Model
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class SQLModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7258001053988930952L;

	// 查询指定字段，值如 "id,name,password"
	@Transient
	private List<SelectColumn> selectProperties;
	
	// 关联的表
	@Transient
	private List<LeftJoin> leftJoins;
	
	// 查询条件
	@Transient
	private List<Condition> conditions;
	
	@Transient
	private String groupBy;
	
	// 排序条件，值如"a.id DESC"
	@Transient
	private String orderBy;
	
	// 开始下标
	@Transient
	private Integer startPos;
	
	// 每页多少
	@Transient
	private Integer pageSize;
	
	public List<SelectColumn> getSelectProperties() {
		return selectProperties;
	}

	public void setSelectProperties(List<SelectColumn> selectProperties) {
		this.selectProperties = selectProperties;
	}

	public List<LeftJoin> getLeftJoins() {
		return leftJoins;
	}

	public void setLeftJoins(List<LeftJoin> leftJoins) {
		this.leftJoins = leftJoins;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getStartPos() {
		return startPos;
	}

	public void setStartPos(Integer startPos) {
		this.startPos = startPos;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
