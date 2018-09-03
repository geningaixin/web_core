package com.huagao.core.db.mapper.attach.sqlwriter;

public class SelectColumn {
	
	// 查询的属性名
	private String name;
	
	// 属性名别名
	private String alias;

	public SelectColumn(String name) {
		this.name = name;
		this.alias = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
