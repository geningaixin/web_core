package com.huagao.core.db.mapper.attach.sqlwriter;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

import com.huagao.core.db.exception.DatabaseException;
import com.huagao.core.db.mapper.attach.SQLField;
import com.huagao.core.db.mapper.attach.SQLHelper;
import com.huagao.core.db.model.field.remark.TimeSearch;
import com.huagao.core.utils.StringUtils;

/**
 * title: SelectWriter.java
 * description: SelectWriter
 * author GeNing
 * date 2018年8月6日
 * version 1.0
 */
public class SelectWriter<Model> {
	
	private String columns;
	
	private String tableName;
	
	private String alias;
	
	private String leftJoinOn;
	
	private String conditions;
	
	private String groupby;
	
	private String orderby;
	
	private String limit;
	
	private boolean hasPrimary = true;
	
	protected void SELECT(final Model model, String target) {
		this.columns = target;
		this.alias = StringUtils.firstCharLowerCase(model.getClass().getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	protected void SELECT(final Model model) {
		
		StringBuffer selectColumns = new StringBuffer();
		
		List<SelectColumn> selfColumns = null;
		this.alias = StringUtils.firstCharLowerCase(model.getClass().getSimpleName());
		
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("selectProperties");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				selfColumns = (List<SelectColumn>) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - SELECT error", e);
		} 
		
		// 如果当前 selfColumns 有值，查询字段则从selfColumns中循环获取
		// 如果当前 selfColumns 无值，则从对象中获取所有与数据库关联字段
		if (selfColumns != null && selfColumns.size() > 0) {
			for (int i = 0; i < selfColumns.size(); i++) {
				if (i != 0) {
					selectColumns.append(", ");
				}
				String content = this.alias + ".`" + SQLHelper.modelFieldName2DB(selfColumns.get(i).getName() + "`") + " AS `" + this.alias + "." + selfColumns.get(i).getAlias() + "`";
				selectColumns.append(content);
			}
		} else {
			List<SQLField> sqlFields = SQLHelper.getSQLContainNullFieldList(model);
			if (sqlFields == null || sqlFields.size() < 0) 
				throw new DatabaseException("SelectWriter - SELECT error", new NullPointerException());
			
			for (int i = 0; i < sqlFields.size(); i++) {
				if (i != 0) {
					selectColumns.append(", ");
				}
				String content = this.alias + ".`" + sqlFields.get(i).getDbField() + "` AS `" + this.alias + "." + sqlFields.get(i).getModelField() + "`";
				selectColumns.append(content);
			}
		}
		
		List<LeftJoin> leftJoinList = null;
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("leftJoins");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				leftJoinList = (List<LeftJoin>) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - SELECT error", e);
		} 
		
		if (leftJoinList != null && leftJoinList.size() > 0) {
			for (int i = 0; i < leftJoinList.size(); i++) {
				LeftJoin leftJoin = leftJoinList.get(i);
				
				String leftjoinModelName = leftJoin.getModelName();
				Class<?> object = null;
				try {
					Field f = model.getClass().getDeclaredField(leftjoinModelName);
					object = Class.forName(f.getType().getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new DatabaseException("SelectWriter - SELECT error", e);
				} 
				
				String joinAlias = StringUtils.firstCharLowerCase(object.getSimpleName());
				
				List<SelectColumn> joinColumns = leftJoin.getColumns();
				if (joinColumns != null && joinColumns.size() > 0) {
					for (int j = 0; j < joinColumns.size(); j++) {
						selectColumns.append(", " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(joinColumns.get(j).getName() + "`") 
							+ " AS `" + leftJoin.getModelName() + "." + joinColumns.get(j).getAlias() + "`");
					}
				}
				
			}
		}
		
		this.columns = String.valueOf(selectColumns);
	}
	
	public void FROM(final Model model) {
		this.tableName = SQLHelper.getTableName(model);
		
		try {
			model.getClass().getDeclaredField("id");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			hasPrimary = false;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - FROM error", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void LEFTJOIN(final Model model) {
		
		StringBuffer leftJoinOnBuffer = new StringBuffer();
		
		List<LeftJoin> leftJoinList = null;
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("leftJoins");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				leftJoinList = (List<LeftJoin>) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - LEFTJOIN error", e);
		} 
		
		if (leftJoinList != null && leftJoinList.size() > 0) {
			for (int i = 0; i < leftJoinList.size(); i++) {
				LeftJoin leftJoin = leftJoinList.get(i);
				
				String leftjoinModelName = leftJoin.getModelName();
				String leftjoinTableName = null;
				Class<?> object = null;
				try {
					Field f = model.getClass().getDeclaredField(leftjoinModelName);
					object = Class.forName(f.getType().getName());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new DatabaseException("SelectWriter - SELECT error", e);
				} 
				
				try {
					leftjoinTableName = SQLHelper.getTableName(object.newInstance());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new DatabaseException("SelectWriter - SELECT error, leftjoinTableName error", e);
				} 
				
				String joinAlias = StringUtils.firstCharLowerCase(object.getSimpleName());
				
				leftJoinOnBuffer.append(" LEFT JOIN `" + leftjoinTableName + "` AS " + joinAlias 
					+ " ON " 
					+ this.alias + ".`" + SQLHelper.modelFieldName2DB(leftJoin.getMainModelFieldName())
					+ "` = " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(leftJoin.getJoinModelFieldName()) + "`");
			}
		}
		
		this.leftJoinOn = String.valueOf(leftJoinOnBuffer);
	}
	
	@SuppressWarnings("unchecked")
	public void WHERE(final Model model) {
		StringBuffer conditionsBuffer = new StringBuffer();
		List<Condition> mainCondition = null;
		
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("conditions");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				mainCondition = (List<Condition>) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - LEFTJOIN error", e);
		} 
		
		if (mainCondition != null && mainCondition.size() > 0) {
			for (int i = 0; i < mainCondition.size(); i++) {
				Condition condition = mainCondition.get(i);
				
				switch (condition.getType()) {
				case IS_NULL : 
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IS NULL");
					break;
				case NOT_NULL :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IS NOT NULL");
					break;
				case IN :
					String value = condition.getValue();
					StringBuffer values = new StringBuffer();
					
					if(value.indexOf(",") <= -1) {
						values.append(value);
					} else {
						String[] strs = value.split(",");
						for (int j = 0; j < strs.length; j++) {
							if (j != 0) 
								values.append(",");
							values.append("'" + strs[j] + "'");
						}
					}
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IN(" + String.valueOf(values) + ")");
					break;
				case NOT_IN :
					String value1 = condition.getValue();
					StringBuffer values1 = new StringBuffer();
					
					if(value1.indexOf(",") <= -1) {
						values1.append(value1);
					} else {
						String[] strs = value1.split(",");
						for (int j = 0; j < strs.length; j++) {
							if (j != 0) 
								values1.append(",");
							values1.append("'" + strs[j] + "'");
						}
					}
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` NOT IN(" + String.valueOf(values1) + ")");
					break;
				case GT :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` > '" + condition.getValue() + "'");
					break;
				case GTE :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` >= '" + condition.getValue() + "'");
					break;
				case LT :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` < '" + condition.getValue() + "'");
					break;
				case LTE :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <= '" + condition.getValue() + "'");
					break;
				case LIKE :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` LIKE '%" + condition.getValue() + "%'");
					break;
				case EQ :  
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` = '" + condition.getValue() + "'");
					break;
				case NOT_EQ : 
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <> '" + condition.getValue() + "'");
					break; 
				case BETWEEN : 
					String[] values2 = condition.getValue().split(",");
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` BETWEEN '" + values2[0] + "' AND '" + values2[1] + "'");
					break; 
				case NOT_BETWEEN : 
					String[] values3 = condition.getValue().split(",");
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` NOT BETWEEN '" + values3[0] + "' AND '" + values3[1] + "'");
					break; 
				default:
					conditionsBuffer.append(" AND " + this.alias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <> '" + condition.getValue() + "'");
					break;
				}
			}
		} else {
			List<SQLField> sqlFields = SQLHelper.getSQLNotNullFieldList(model);
			
			for (int i = 0; i < sqlFields.size(); i++) {
				if (sqlFields.get(i).getDbValueObj() instanceof Timestamp) {
					Field field = null;
					try {
						field = model.getClass().getDeclaredField(sqlFields.get(i).getModelField());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new DatabaseException("SelectWriter - WHERE error", e);
					} 
					TimeSearch fm = field.getAnnotation(TimeSearch.class);
					if (fm == null || StringUtils.isNull(fm.beginTime()) || StringUtils.isNull(fm.endTime())) 
						continue;
					
					Object beginTimeVal = null;
					try {
						Field f = model.getClass().getDeclaredField(fm.beginTime());
						f.setAccessible(Boolean.TRUE);
						beginTimeVal = f.get(model);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new DatabaseException("泛型方法执行失败", e);
					} 
					
					Object endTimeVal = null;
					try {
						Field f = model.getClass().getDeclaredField(fm.endTime());
						f.setAccessible(Boolean.TRUE);
						endTimeVal = f.get(model);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new DatabaseException("泛型方法执行失败", e);
					} 
					
					if (!StringUtils.isNull(String.valueOf(beginTimeVal)) && !StringUtils.isNull(String.valueOf(endTimeVal)))
						conditionsBuffer.append(" AND " + this.alias + ".`" + sqlFields.get(i).getDbField() + "` BETWEEN '" + String.valueOf(beginTimeVal) + "' AND '" + String.valueOf(endTimeVal) + "'");
				} else {
					conditionsBuffer.append(" AND " + this.alias + ".`" + sqlFields.get(i).getDbField() + "` = '" + sqlFields.get(i).getDbValueStr() + "'");
				}
			}
		}
		
		List<LeftJoin> leftJoinList = null;
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("leftJoins");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				leftJoinList = (List<LeftJoin>) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - SELECT error", e);
		} 
		
		if (leftJoinList != null && leftJoinList.size() > 0) {
		
			for (int j = 0; j < leftJoinList.size(); j++) {
				LeftJoin leftJoin = leftJoinList.get(j);
				List<Condition> conditionList = leftJoin.getConditions();
				if (conditionList != null && conditionList.size() >= 0) {
					for (int i = 0; i < conditionList.size(); i++) {
						Condition condition = conditionList.get(i);
						
						String leftjoinModelName = leftJoin.getModelName();
						Class<?> object = null;
						try {
							Field f = model.getClass().getDeclaredField(leftjoinModelName);
							object = Class.forName(f.getType().getName());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							throw new DatabaseException("SelectWriter - SELECT error", e);
						} 
						
						String joinAlias = StringUtils.firstCharLowerCase(object.getSimpleName());
						
						switch (condition.getType()) {
						case IS_NULL : 
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IS NULL");
							break;
						case NOT_NULL :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IS NOT NULL");
							break;
						case IN :
							String value = condition.getValue();
							StringBuffer values = new StringBuffer();
							
							if(value.indexOf(",") <= -1) {
								values.append(value);
							} else {
								String[] strs = value.split(",");
								for (int n = 0; n < strs.length; n++) {
									if (n != 0) 
										values.append(",");
									values.append("'" + strs[n] + "'");
								}
							}
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` IN(" + String.valueOf(values) + ")");
							break;
						case NOT_IN :
							String value1 = condition.getValue();
							StringBuffer values1 = new StringBuffer();
							
							if(value1.indexOf(",") <= -1) {
								values1.append(value1);
							} else {
								String[] strs = value1.split(",");
								for (int n = 0; n < strs.length; n++) {
									if (n != 0) 
										values1.append(",");
									values1.append("'" + strs[n] + "'");
								}
							}
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` NOT IN(" + String.valueOf(values1) + ")");
							break;
						case GT :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` > '" + condition.getValue() + "'");
							break;
						case GTE :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` >= '" + condition.getValue() + "'");
							break;
						case LT :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` < '" + condition.getValue() + "'");
							break;
						case LTE :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <= '" + condition.getValue() + "'");
							break;
						case LIKE :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` LIKE '%" + condition.getValue() + "%'");
							break;
						case EQ :  
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` = '" + condition.getValue() + "'");
							break;
						case NOT_EQ : 
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <> '" + condition.getValue() + "'");
							break; 
						case BETWEEN : 
							String[] values2 = condition.getValue().split(",");
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` BETWEEN '" + values2[0] + "' AND '" + values2[1] + "'");
							break; 
						case NOT_BETWEEN : 
							String[] values3 = condition.getValue().split(",");
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` NOT BETWEEN '" + values3[0] + "' AND '" + values3[1] + "'");
							break; 
						default:
							conditionsBuffer.append(" AND " + joinAlias + ".`" + SQLHelper.modelFieldName2DB(condition.getName()) + "` <> '" + condition.getValue() + "'");
							break;
						}
					}
				} else {
					
					Object obj = null;
					Class<?> object = null;
					try {
						Field f = model.getClass().getDeclaredField(leftJoin.getModelName());
						f.setAccessible(Boolean.TRUE);
						obj = f.get(model);
						object = Class.forName(f.getType().getName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new DatabaseException("SelectWriter - SELECT error", e);
					} 
					if (obj == null)
						continue;
					
					String joinAlias = StringUtils.firstCharLowerCase(object.getSimpleName());
					
					List<SQLField> sqlFields = SQLHelper.getSQLNotNullFieldList(obj);
					
					for (int i = 0; i < sqlFields.size(); i++) {
						if (sqlFields.get(i).getDbValueObj() instanceof Timestamp) {
							Field field = null;
							try {
								field = obj.getClass().getDeclaredField(sqlFields.get(i).getModelField());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								throw new DatabaseException("SelectWriter - WHERE error", e);
							} 
							TimeSearch fm = field.getAnnotation(TimeSearch.class);
							if (fm == null || StringUtils.isNull(fm.beginTime()) || StringUtils.isNull(fm.endTime())) 
								continue;
							
							Object beginTimeVal = null;
							try {
								Field f = obj.getClass().getDeclaredField(fm.beginTime());
								f.setAccessible(Boolean.TRUE);
								beginTimeVal = f.get(obj);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								throw new DatabaseException("泛型方法执行失败", e);
							} 
							
							Object endTimeVal = null;
							try {
								Field f = obj.getClass().getDeclaredField(fm.endTime());
								f.setAccessible(Boolean.TRUE);
								endTimeVal = f.get(obj);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								throw new DatabaseException("泛型方法执行失败", e);
							} 
							
							if (!StringUtils.isNull(String.valueOf(beginTimeVal)) && !StringUtils.isNull(String.valueOf(endTimeVal)))
								conditionsBuffer.append(" AND " + joinAlias + ".`" + sqlFields.get(i).getDbField() + "` BETWEEN '" + String.valueOf(beginTimeVal) + "' AND '" + String.valueOf(endTimeVal) + "'");
						} else {
							conditionsBuffer.append(" AND " + joinAlias + ".`" + sqlFields.get(i).getDbField() + "` = '" + sqlFields.get(i).getDbValueStr()+ "'");
						}
					}
				}
			} 
		}
		this.conditions = String.valueOf(conditionsBuffer);
	}
	
	public void GROUPBY(final Model model) {
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("groupBy");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				this.groupby = String.valueOf(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - GROUPBY error", e);
		} 
	}
	
	public void ORDERBY(final Model model) {
		try {
			Field f = model.getClass().getSuperclass().getDeclaredField("orderBy");
			f.setAccessible(Boolean.TRUE);
			Object obj = f.get(model);
			if (obj != null)
				this.orderby = String.valueOf(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DatabaseException("SelectWriter - ORDERBY error", e);
		} 
	}
	
	public void LIMIT(final Model model) {
		Object limit1 = null;
		Object limit2 = null;
		try {
			Field field1 = model.getClass().getSuperclass().getDeclaredField("startPos");
			field1.setAccessible(Boolean.TRUE);
			limit1 = field1.get(model);
			
			Field field2 = model.getClass().getSuperclass().getDeclaredField("pageSize");
			field2.setAccessible(Boolean.TRUE);
			limit2 = field2.get(model);
			
		} catch (Exception e) {
			throw new DatabaseException("泛型方法执行失败", e);
		}
		
		if (limit1 != null && limit2 != null)
			this.limit = String.valueOf(limit1) + "," + limit2;
	}
	
	public String createMappingSQL() {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("SELECT " + columns + " ");
		sqlBuffer.append("FROM `" + tableName + "` AS " + alias);
		
		if (!StringUtils.isNull(this.leftJoinOn)) 
			sqlBuffer.append(leftJoinOn);
		
		if (!StringUtils.isNull(this.conditions))
			sqlBuffer.append(" WHERE (" + this.conditions.substring(5) + ")" + " ");
		
		if (!StringUtils.isNull(this.groupby))
			sqlBuffer.append(" GROUP BY " + this.groupby + " ");
		
		if (!StringUtils.isNull(this.orderby))
			sqlBuffer.append(" ORDER BY " + this.orderby + " ");
		else {
			if (hasPrimary)
				sqlBuffer.append(" ORDER BY " + this.alias + ".id DESC");
		}
		
		if (!StringUtils.isNull(this.limit))
			sqlBuffer.append(" LIMIT " + this.limit + " ");
		
		return String.valueOf(sqlBuffer);
	}
}
