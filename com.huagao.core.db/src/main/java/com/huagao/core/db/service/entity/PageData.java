package com.huagao.core.db.service.entity;

import java.io.Serializable;
import java.util.List;

public class PageData<T> implements Serializable {
	
	private static final long serialVersionUID = 6288375236131788743L;

	// 要返回的某一页的记录列表
	private List<T> list;

	// 总记录数
	private Integer allRow;

	// 总页数
	private Integer totalPage;

	// 当前页
	private Integer currentPage;

	// 每页记录数
	private Integer pageSize;

	private boolean isFirstPage;
	
	private boolean isLastPage;
	
	private boolean hasNextPage;
	
	private boolean hasPrePage;
	
	public PageData() {}
	
	public PageData(List<T> list, long allRow, Integer currentPage, Integer pageSize) {
		this.list = list;
		this.allRow = (int) allRow;
		this.totalPage = countTotalPage(pageSize, (int) allRow);
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.isFirstPage = currentPage.equals(1) ? true : false;
		this.isLastPage  = this.currentPage == this.totalPage ? true : false;
		this.hasNextPage = this.currentPage < this.totalPage ? true : false;
		this.hasPrePage  = this.currentPage > 1 ? true : false;
	}
	
	/**
	 * 计算总页数，静态方法，供外部直接通过类名调用 pageSize 每页记录数 allRow 总记录数
	 */
	public static int countTotalPage(final int pageSize, final int allRow) {
		int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
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
	
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
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
	public boolean isFirstPage() {
		return isFirstPage;
	}
	public void setFirstPage(boolean isFirstPage) {
		this.isFirstPage = isFirstPage;
	}
	public boolean isLastPage() {
		return isLastPage;
	}
	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}
	public boolean isHasNextPage() {
		return hasNextPage;
	}
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
	public boolean isHasPrePage() {
		return hasPrePage;
	}
	public void setHasPrePage(boolean hasPrePage) {
		this.hasPrePage = hasPrePage;
	}
}
