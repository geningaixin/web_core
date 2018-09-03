package com.huagao.core.db.service;

import com.huagao.core.db.service.entity.PageData;

import java.util.List;

public interface GenericService<Model, PK> {

	// 插入对象
	int insert(Model model);

	// 更新对象
	int update(Model model);

	// 通过主键, 查询对象
	Model findByID(PK id);

	// 查询对象列表，不参与分页
	List<Model> findList(Model model);

	// 查询对象列表，参与分页
	List<Model> findList(Integer currentPage, Integer pageSize, Model model);

	// 查询 分页数据+列表数据 信息，用于前台接口通用分页查询
	PageData<Model> findPageInfo(Integer currentPage, Integer pageSize, Model model);

	// 根据条件查询单个对象
	Model findOne(Model model);

	// 根据主键，删除对象
	int deleteById(PK id);

	// 根据对象，删除对象
	void deleteByModel(Model model);
}
