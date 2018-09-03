package com.huagao.core.db.service;

import com.huagao.core.db.exception.DatabaseException;
import com.huagao.core.db.mapper.GenericMapper;
import com.huagao.core.db.model.field.remark.MappingEm;
import com.huagao.core.db.model.field.remark.MappingTs;
import com.huagao.core.db.service.entity.PageData;
import com.huagao.core.utils.DateUtils;
import com.huagao.core.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Packgae : com.huagao.core.web.service.impl
 * @Auther : Gening
 * @Time : 2018/9/3
 * @Description :
 * @Version :
 */
public abstract class GenericServiceImpl<Model, PK> implements GenericService<Model, PK> {
	/**
	 * 定义成抽象方法,由子类实现,完成dao的注入
	 *
	 * @return GenericDao实现类
	 */
	public abstract GenericMapper<Model> getDao();

	@Override
	public int insert(Model model) {
		try {
			return getDao().insertSelective(model);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("插入数据失败", e);
		}
	}

	@Override
	public int update(Model model) {
		try {
			return getDao().updateByPrimaryKeySelective(model);
		} catch (Exception e) {
			throw new DatabaseException("修改数据失败", e);
		}
	}

	@Override
	public Model findByID(PK id) {
		Model model;
		try {
			model = getDao().selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new DatabaseException("根据主键查询数据失败", e);
		}
		if (model != null) {
			List<Field> mappingFields = this.getMappingFields(model);
			return packModel(model, mappingFields);
		}
		return null;
	}

	@Override
	public List<Model> findList(Model model) {
		List<Model> modelList = null;
		List<Map<String, Object>> resultMap = getDao().multipartSelectByExample(model);

		if (resultMap != null && resultMap.size() > 0) {
			modelList = new ArrayList<>();
			for (Map<String, Object> map : resultMap) {
				modelList.add(this.packageModel(map));
			}
		}
		return modelList;
	}

	@Override
	public List<Model> findList(Integer currentPage, Integer pageSize, Model model) {

		// 获取开始下标、每页大小，赋值到 SQLModel 中
		Integer startPos = null;
		if (currentPage != null && pageSize != null) {
			currentPage = currentPage >= 1 ? currentPage : 1;
			startPos = PageData.countOffset(pageSize, currentPage);
		}

		if (startPos != null && startPos >= 0 && pageSize > 0) {
			setPropertyValue(model, "startPos", startPos);
			setPropertyValue(model, "pageSize", pageSize);
		}

		List<Model> modelList = null;

		List<Map<String, Object>> resultMap = getDao().multipartSelectByExample(model);
		if (resultMap != null && resultMap.size() > 0) {
			modelList = new ArrayList<>();
			for (Map<String, Object> map : resultMap) {
				modelList.add(this.packageModel(map));
			}
		}

		return modelList;
	}

	@Override
	public PageData<Model> findPageInfo(Integer currentPage, Integer pageSize, Model model) {

		// 获取开始下标、每页大小，赋值到 SQLModel 中
		Integer startPos = null;
		if (currentPage != null && pageSize != null) {
			currentPage = currentPage >= 1 ? currentPage : 1;
			startPos = PageData.countOffset(pageSize, currentPage);
		}
		if (startPos != null && startPos >= 0 && pageSize > 0) {
			setPropertyValue(model, "startPos", startPos);
			setPropertyValue(model, "pageSize", pageSize);
		}

		// 查询符合条件总数量以及List数据
		int allRow = getDao().multipartSelectCountByExample(model);
		List<Map<String, Object>> resultList = getDao().multipartSelectByExample(model);

		List<Model> modelList = null;
		if (resultList != null && resultList.size() > 0) {
			modelList = new ArrayList<>();
			for (Map<String, Object> map : resultList) {
				modelList.add(this.packageModel(map));
			}
		}

		assert currentPage != null;
		return new PageData<>(modelList, allRow, currentPage, pageSize);
	}

	@Override
	public Model findOne(Model model) throws DatabaseException {
		Model result = null;
		List<Map<String, Object>> resultMap = getDao().multipartSelectByExample(model);
		if (resultMap != null && resultMap.size() > 0)
			result = this.packageModel(resultMap.get(0));
		return result;
	}

	@Override
	public int deleteById(PK id) {
		try {
			return getDao().deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new DatabaseException("根据主键删除数据失败", e);
		}
	}

	@Override
	public void deleteByModel(Model model) {
		try {
			getDao().delete(model);
		} catch (Exception e) {
			throw new DatabaseException("根据对象删除数据失败", e);
		}
	}

	/**
	 * 获取当前Dao对应的类实例
	 */
	@SuppressWarnings("unchecked")
	public Model getModel() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType p = (ParameterizedType)type;
		Class<Model> entityClass = (Class<Model>) p.getActualTypeArguments()[0];
		Model model;
		try {
			model = entityClass.newInstance();
		} catch (Exception e) {
			throw new DatabaseException("泛型获取实体类实例失败", e);
		}
		return model;
	}

	/**
	 * 包装对象
	 */
	@SuppressWarnings("unchecked")
	private Model packageModel(Map<String, Object> resultMap) {
		Model model = getModel();

		if (resultMap == null || resultMap.size() <= 0)
			return null;

		String mainKey = StringUtils.firstCharLowerCase(model.getClass().getSimpleName());
		HashMap<String, Object> mainMap = (HashMap<String, Object>) resultMap.get(mainKey);

		if (mainMap == null || mainMap.size() <= 0)
			return null;

		for (String key : mainMap.keySet()) {
			Object val = mainMap.get(key);

			if (val == null)
				continue;

			try {
				// 字段赋值
				Field field = model.getClass().getDeclaredField(key);
				field.setAccessible(Boolean.TRUE);
				field.set(model, val);

				// 为当前字段做属性映射，如TIMESTAMP需映射到相关字符串
				setEnumValue(model, field, val);
			} catch (Exception e) {
				throw new DatabaseException("字段赋值失败", e);
			}
		}

		for (String key : resultMap.keySet()) {

			if (key.equals(mainKey))
				continue;

			// 当key有.时是因为对象里有其他对象的关联属性，如：如 UserInfo中有DeptInfo关联，这里Key是映射DeptInfo字段值，则key为 deptInfo.name
			// 获取前半段即deptInfo
			Field field;
			try {
				field = model.getClass().getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				continue;
			} catch (SecurityException e) {
				throw new DatabaseException("", e);
			}

			try {
				field.setAccessible(Boolean.TRUE);
				Object object = field.get(model);

				// 如果获取的 deptInfo是空，则需要先实例化
				if (object == null) {
					Class<?> clazz = Class.forName(field.getType().getName());
					object = clazz.newInstance();
					field.set(model, object);
				}

				HashMap<String, Object> branchMap = (HashMap<String, Object>) resultMap.get(key);

				for (String branchKey : branchMap.keySet()) {
					Object branchVal = branchMap.get(branchKey);

					if (branchVal == null)
						continue;

					// 再为 deptInfo下的name赋值
					Field f = object.getClass().getDeclaredField(branchKey);
					f.setAccessible(Boolean.TRUE);
					f.set(object, branchVal);

					// 为当前字段做属性映射，如TIMESTAMP需映射到相关字符串
					setEnumValue(object, f, branchVal);
				}
			} catch (Exception e) {
				throw new DatabaseException("泛型方法执行失败", e);
			}
		}

		return model;
	}

	/**
	 * 获取当前泛型对象的映射字段
	 */
	private List<Field> getMappingFields(Model model) {

		List<Field> mappingFields = new ArrayList<>();

		// 通过泛型获取 Model所有字段集合
		Field[] fields = model.getClass().getDeclaredFields();

		if (fields != null && fields.length > 0) {
			for (Field field : fields) {

				// 获取当前字段的映射注解
				MappingTs mappingTs = field.getAnnotation(MappingTs.class);
				MappingEm mappingEm = field.getAnnotation(MappingEm.class);

				if (mappingTs != null || mappingEm != null)
					mappingFields.add(field);

			}
		}
		return mappingFields;
	}

	/**
	 * 根据当前类的映射字段对相关字段进行映射赋值
	 */
	private Model packModel(Model model, List<Field> mappingFields) {

		if (mappingFields != null && mappingFields.size() > 0) {

			for (Field field : mappingFields) {

				// 获取当前字段
				// 字段值
				Object value;
				try {
					field.setAccessible(Boolean.TRUE);
					value = field.get(model);
				} catch (Exception e) {
					throw new DatabaseException("泛型方法执行失败", e);
				}

				if (value == null || StringUtils.isNull(String.valueOf(value)))
					continue;

				setEnumValue(model, field, value);
			}
		}
		return model;
	}

	private void setPropertyValue(Object model, String propertyName, Object value) {
		try {
			Field field = model.getClass().getSuperclass().getDeclaredField(propertyName);
			field.setAccessible(Boolean.TRUE);
			field.set(model, value);
		} catch (Exception e) {
			throw new DatabaseException("泛型方法执行失败", e);
		}
	}

	private void setEnumValue(Object model, Field field, Object value) {

		// 获取当前字段 FieldMapping 注解
		MappingTs mappingTs = field.getAnnotation(MappingTs.class);

		if (mappingTs != null) {

			// 如果当前字段注解的映射类型为 TIMESTAMP
			boolean setValue = false;
			Method[] methods = model.getClass().getMethods();
			String setMethodName = "set" + StringUtils.firstCharUpperCase(mappingTs.where());

			for (Method m : methods) {
				if (m.getName().equals(setMethodName)) {
					m.setAccessible(true);
					try {
						m.invoke(model, value);
					} catch (Exception e) {
						throw new DatabaseException("泛型方法执行失败", e);
					}
					setValue = true;
					break;
				}
			}

			if (setValue)
				return;

			try {
				Field f = model.getClass().getDeclaredField(mappingTs.where());
				f.setAccessible(Boolean.TRUE);
				f.set(model, DateUtils.parseFullTime((Timestamp) value));
			} catch (Exception e) {
				throw new DatabaseException("泛型方法执行失败", e);
			}
			return;
		}

		MappingEm mappingEm = field.getAnnotation(MappingEm.class);

		if (mappingEm != null) {

			// 如果当前字段注解的映射类型为 ENUM
			Enum<?> enums[];
			try {
				String getMethodName = "values";
				Method method = mappingEm.enumCls().getMethod(getMethodName);
				enums = (Enum<?>[]) method.invoke(mappingEm.enumCls());
			} catch (Exception e) {
				throw new DatabaseException("泛型方法执行失败", e);
			}

			for (Enum<?> anEnum : enums) {
				Integer v;
				String t;
				try {
					String getVMethod = "getValue";
					String getTMethod = "getText";
					Method method1 = anEnum.getClass().getMethod(getVMethod);
					Method method2 = anEnum.getClass().getMethod(getTMethod);
					v = Integer.parseInt(String.valueOf(method1.invoke(anEnum)));
					t = String.valueOf(method2.invoke(anEnum));
				} catch (Exception e) {
					throw new DatabaseException("泛型方法执行失败", e);
				}

				if (v.equals(value)) {
					try {
						Field f = model.getClass().getDeclaredField(mappingEm.where());
						f.setAccessible(Boolean.TRUE);
						f.set(model, t);
					} catch (Exception e) {
						throw new DatabaseException("泛型方法执行失败", e);
					}
				}
			}
		}
	}
}
