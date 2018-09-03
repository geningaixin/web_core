package com.huagao.core.db.mapper.attach.sqlwriter;

import java.util.List;

import com.huagao.core.db.exception.DatabaseException;
import com.huagao.core.db.mapper.attach.SQLField;
import com.huagao.core.db.mapper.attach.SQLHelper;

public class UpdateWriter<Model> {

	private String tableName;
	
	private String runningSet;
	
	private String mappingSet;
	
	private String conditions;
	
	public void UPDATE(final Model model) {
		this.tableName = SQLHelper.getTableName(model);
	}
	
	public void SET(final Model model) {
		final List<SQLField> contentList = SQLHelper.getSQLNotNullFieldList(model);
		if (contentList == null || contentList.size() <= 0)
			throw new DatabaseException("insert error, SQLField is null", new NullPointerException());
		
		StringBuffer mapping = new StringBuffer(" (");
		StringBuffer running = new StringBuffer(" (");
			
		for (int i = 0; i < contentList.size(); i ++) {
			if (i != 0) {
				mapping.append(", ");
				running.append(", ");
			}
			mapping.append(contentList.get(i).getDbField() + " = " + contentList.get(i).getExpression());
			running.append(contentList.get(i).getDbField() + " = " + "'" + contentList.get(i).getDbValueStr() + "'");
		}
		mapping.append(")");
		running.append(")");
		
		this.runningSet = String.valueOf(running);
		this.mappingSet = String.valueOf(mapping);
	}
	
	public void WHERE(final String where) {
		this.conditions = where;
	}
	
	public String createMappingSQL() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("UPDATE " + tableName + " ");
		sqlBuffer.append("SET " + mappingSet + " ");
		sqlBuffer.append("WHERE (" + this.conditions + ")" + " ");
		return String.valueOf(sqlBuffer);
	}
	
	public String createRunningSQL() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("UPDATE " + tableName + " ");
		sqlBuffer.append("SET " + runningSet + " ");
		sqlBuffer.append("WHERE (" + this.conditions + ")" + " ");
		return String.valueOf(sqlBuffer);
	}
}
