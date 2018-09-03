package com.huagao.core.db.mapper;

import com.huagao.core.db.mapper.attach.SQLProvider;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Packgae : com.huagao.core.db.mapper
 * Auther : Gening
 * Time : 2018/9/3
 * Description :
 * Version :
 */
public interface GenericMapper<Model> extends Mapper<Model> {

	@SelectProvider(type = SQLProvider.class, method = "selectCount")
	int multipartSelectCountByExample(Model model);

	@SelectProvider(type = SQLProvider.class, method = "select")
	List<Map<String, Object>> multipartSelectByExample(Model model);
}
