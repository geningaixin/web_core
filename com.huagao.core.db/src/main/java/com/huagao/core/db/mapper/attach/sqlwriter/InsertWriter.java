package com.huagao.core.db.mapper.attach.sqlwriter;

import com.huagao.core.db.exception.DatabaseException;
import com.huagao.core.db.mapper.attach.SQLField;
import com.huagao.core.db.mapper.attach.SQLHelper;

import java.util.List;

/**
 * @title: InsertWriter.java
 * @description: InsertWriter
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class InsertWriter<Model> {
	
	private String tableName;
	
	private String runningValues;
	
	private String mappingValues;
	
	public void INSERT_INTO(final Model model) {
		this.tableName = SQLHelper.getTableName(model);
	}
	
	public void VALUES(final Model model) {
		final List<SQLField> contentList = SQLHelper.getSQLNotNullFieldList(model);
		if (contentList == null || contentList.size() <= 0)
			throw new DatabaseException("insert error, SQLField is null", new NullPointerException());
		
		StringBuffer mapping1 = new StringBuffer(" (");
		StringBuffer mapping2 = new StringBuffer(" (");
		StringBuffer running  = new StringBuffer(" (");
			
		for (int i = 0; i < contentList.size(); i ++) {
			if (i != 0) {
				mapping1.append(", ");
				mapping2.append(", ");
			}
			mapping1.append(contentList.get(i).getDbField());
			mapping2.append(contentList.get(i).getExpression());
			running.append("'" + contentList.get(i).getDbValueStr() + "'");
		}
		mapping1.append(")");
		mapping2.append(")");
		running.append(")");
		this.runningValues = String.valueOf(mapping1.append("VALUES").append(running));
		this.mappingValues = String.valueOf(mapping1.append("VALUES").append(mapping2));
	}
	
	public String createMappingSQL() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("INSERT INTO " + this.tableName);
		sqlBuffer.append(mappingValues);
		return String.valueOf(sqlBuffer);
	}
	
	public String createRunningSQL() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("INSERT INTO " + this.tableName);
		sqlBuffer.append(runningValues);
		return String.valueOf(sqlBuffer);
	}
}
