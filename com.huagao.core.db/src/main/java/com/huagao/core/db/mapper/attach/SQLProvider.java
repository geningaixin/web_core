package com.huagao.core.db.mapper.attach;

import com.huagao.core.db.mapper.attach.sqlwriter.SelectWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @title: SQLProvider.java
 * @description: SQL语句 供应类
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class SQLProvider<Model> {

	protected final Log logger = LogFactory.getLog(getClass());
	
	/*public String insert(final Model model) {
		InsertWriter<Model> writer = new InsertWriter<Model>() {{
			INSERT_INTO(model);
			VALUES(model);
		}};
		logger.info("mapping sql : " + writer.createMappingSQL());
		logger.info("running sql : " + writer.createRunningSQL());
		return writer.createMappingSQL();
	}
	
	public String update(final Model model) {
		UpdateWriter<Model> writer = new UpdateWriter<Model>(){{
			UPDATE(model);
			SET(model);
			WHERE("id = #{id}");
		}};
		logger.info("mapping sql : " + writer.createMappingSQL());
		logger.info("running sql : " + writer.createRunningSQL());
		return writer.createMappingSQL();
	}*/
	
	public String selectCount(final Model model) {
		SelectWriter<Model> writer = new SelectWriter<Model>(){{
			SELECT(model, "count(*)");
			FROM(model);
			LEFTJOIN(model);
			WHERE(model);
			GROUPBY(model);
		}};
		logger.info("select sql : " + writer.createMappingSQL());
		return writer.createMappingSQL();
	}
	
	public String select(final Model model) {
		SelectWriter<Model> writer = new SelectWriter<Model>(){{
			SELECT(model);
			FROM(model);
			LEFTJOIN(model);
			WHERE(model);
			GROUPBY(model);
			ORDERBY(model);
			LIMIT(model);
		}};
		logger.info("select sql : " + writer.createMappingSQL());
		return writer.createMappingSQL();
	}
}
