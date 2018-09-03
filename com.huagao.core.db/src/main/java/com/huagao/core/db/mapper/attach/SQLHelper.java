package com.huagao.core.db.mapper.attach;

import com.huagao.core.db.exception.DatabaseException;
import com.huagao.core.utils.DateUtils;
import com.huagao.core.utils.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @title: SqlHelper.java
 * @description: SQL 帮助类 包括  获取数据库映射表名、实体类字段名 映射到 数据库字段名、数据库字段名 映射到 实体类字段名等
 * @author GeNing
 * @date 2018年8月6日  
 * @version 1.0
 */
public class SQLHelper {
	
	/**
	 * 获取数据库映射表名
	 * @param model
	 * @return
	 */
	public static <Model> String getTableName(final Model model) {
		if (model == null) 
			throw new DatabaseException("Get Database TableName Error : model is null", new NullPointerException());
		Table meta = model.getClass().getAnnotation(Table.class);
		String tableName = meta.name();
		if (StringUtils.isNull(tableName))
			throw new DatabaseException("Get Database TableName Error", new NullPointerException());
		return tableName;
	}
	
	/**
	 * 实体类字段名 映射到 数据库字段名
	 * @param modelFieldName
	 * @return
	 */
	public static String modelFieldName2DB(String modelFieldName) {
		
		if (StringUtils.isNull(modelFieldName))
			throw new DatabaseException("modelField2DB error, modelField is null", new NullPointerException());
		
		StringBuffer dbFieldName = new StringBuffer();
		
		// 循环每个char，当遇到大写字母时就追加 下划线及转换小写，当遇到小写字母时则直接追加
    	for (int i = 0; i < modelFieldName.length(); i++) {
    			
    		char[] chars = new char[1];  
        	chars[0] = modelFieldName.charAt(i);
        	String temp = new String(chars);  
        	
        	if (chars[0] >= 'A' && chars[0] <= 'Z')
        		dbFieldName.append("_" + temp.toLowerCase());
        	else
        		dbFieldName.append(temp);
		}
    	
    	return String.valueOf(dbFieldName);
	}
	
	/**
	 * 数据库字段名 映射到 实体类字段名
	 */
	public static String dbFieldName2Model(String dbFieldName) {
		
		if (StringUtils.isNull(dbFieldName))
			throw new DatabaseException("dbFieldName2Model error, dbFieldName is null", new NullPointerException());
		
		StringBuffer modelFieldName = new StringBuffer();
		boolean isUp = false;
		
		// 循环每个char，当遇到下划线时进入下次循环并标记下次为大写
		for (int i = 0; i < dbFieldName.length(); i ++) {
    		char[] chars = new char[1];
        	chars[0] = dbFieldName.charAt(i);
        	String temp = new String(chars);  
        	
        	if (chars[0] == '_') {
        		isUp = true;
        		continue;
        	}
        	
        	if (isUp) {
        		modelFieldName.append(temp.toUpperCase());
        		isUp = false;
        	} else {
        		modelFieldName.append(temp);
        	}
		}
		
		return String.valueOf(modelFieldName);
	}
	
	/**
	 * 根据对象参数获取对象关联数据库字段集合
	 * @param model
	 * @return
	 */
	public static <Model> List<SQLField> getSQLNotNullFieldList(final Model model) {
		if (model == null) 
			throw new DatabaseException("getSQLFieldList Error : model is null", new NullPointerException());
		
		// 获取类下所有字段集合
		Field[] fields = model.getClass().getDeclaredFields();
		if (fields == null || fields.length < 0)
			throw new DatabaseException("getSQLFieldList Error : model fields null", new NullPointerException());
		
		// 定义返回结果集
		List<SQLField> resultList = new ArrayList<SQLField>();
		
		for (int i = 0; i < fields.length; i++) {
			
			Field f = fields[i];
			
			// 获取当前字段的属性注解，如果是非数据库关联字段跳出
			Transient meta = f.getAnnotation(Transient.class);
			if (meta != null) {
				continue;
			}
			
			// 字段值
			Object fieldVal = null;
			String fieldStr = null;
			try {
				f.setAccessible(Boolean.TRUE);
				fieldVal = f.get(model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new DatabaseException("泛型方法执行失败", e);
			}
			
			// 如果字段值不为空，则实例化后添加到结果集中
			if (fieldVal != null) {
				SQLField sqlField = new SQLField();
				sqlField.setModelField(f.getName());
				sqlField.setDbField(modelFieldName2DB(f.getName()));
				sqlField.setExpression("#{" + f.getName() + "}");
				sqlField.setDbValueObj(fieldVal);
				
				// 如果当前字段类型为时间类型
				String type = f.getType().getName();
				if (type.equals("java.sql.Timestamp")) 
					fieldStr = DateUtils.parseFullTime((Timestamp) fieldVal);
				else
					fieldStr = String.valueOf(fieldVal);
				
				sqlField.setDbValueStr(fieldStr);
				
				resultList.add(sqlField);
			}
		}
		
		return resultList;
	}

	public static <Model> List<SQLField> getSQLContainNullFieldList(final Model model) {
		if (model == null) 
			throw new DatabaseException("getSQLFieldList Error : model is null", new NullPointerException());
		
		// 获取类下所有字段集合
		Field[] fields = model.getClass().getDeclaredFields();
		if (fields == null || fields.length < 0)
			throw new DatabaseException("getSQLFieldList Error : model fields null", new NullPointerException());
		
		// 定义返回结果集
		List<SQLField> resultList = new ArrayList<SQLField>();
		
		for (int i = 0; i < fields.length; i++) {
			
			Field f = fields[i];
			
			// 获取当前字段的属性注解，如果是非数据库关联字段跳出
			Transient meta = f.getAnnotation(Transient.class);
			if (meta != null) {
				continue;
			}
			
			// 字段值
			Object fieldVal = null;
			String fieldStr = null;
			try {
				f.setAccessible(Boolean.TRUE);
				fieldVal = f.get(model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new DatabaseException("泛型方法执行失败", e);
			}
			
			SQLField sqlField = new SQLField();
			sqlField.setModelField(f.getName());
			sqlField.setDbField(modelFieldName2DB(f.getName()));
			sqlField.setExpression("#{" + f.getName() + "}");
			
			// 如果字段值不为空，则实例化后添加到结果集中
			if (fieldVal != null) {
				sqlField.setDbValueObj(fieldVal);
				
				// 如果当前字段类型为时间类型
				String type = f.getType().getName();
				if (type.equals("java.sql.Timestamp")) 
					fieldStr = DateUtils.parseFullTime((Timestamp) fieldVal);
				else
					fieldStr = String.valueOf(fieldVal);
				
				sqlField.setDbValueStr(fieldStr);
			}
			resultList.add(sqlField);
		}
		
		return resultList;
	}
}
