package com.huagao.core.db.service.entity;

import java.io.Serializable;

public class PageInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5741003088887718458L;

	// 总记录数
	private Integer allRow;

	// 总页数
	private Integer totalPage;

	// 当前页
	private Integer currentPage;

	// 每页记录数
	private Integer pageSize;
	
	public PageInfo() {}
	
	public PageInfo(Integer allRow, Integer pageSize) {
		this.allRow = allRow;
		this.totalPage = countTotalPage(pageSize, allRow);
		this.pageSize = pageSize;
	}
	
	/**
	 * 计算总页数，静态方法，供外部直接通过类名调用 pageSize 每页记录数 allRow 总记录数
	 */
	public static int countTotalPage(final int pageSize, final int allRow) {
		int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow
				/ pageSize + 1;
		return totalPage;
	}
	
	/**
	 * 计算当前页第一条元素的下标
	 * pageSize 每页记录数 currentPage 当前第几页
	 */
	public static int countOffset(final int pageSize, final int currentPage) {
		final int offset = pageSize * (currentPage - 1);
		return offset;
	}
	
	public Integer getAllRow() {
		return allRow;
	}
	public void setAllRow(Integer allRow) {
		this.allRow = allRow;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
