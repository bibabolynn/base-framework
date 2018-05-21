package com.yuan.util.pagination;

import java.util.List;

public class Pagination<E> {
	public static final int DEFAULT_PAGE_SIZE = 20;
	/**
	 * 总条目数
	 */
	private int totalItemNum;
	/**
	 * 当前页码
	 */
	private int currentPageNum;
	/**
	 * 每页大小
	 */
	private int pageSize;
	/**
	 * 总页数
	 */
	private int totalPageNum;
	/**
	 * 是否有上一页
	 */
	private boolean prePage;
	/**
	 * 是否有下一页
	 */
	private boolean nextPage;
	/**
	 * 绑定的实体list
	 */
	private List<E> data;

	public Pagination() {
	}

	/**
	 * 
	 * @param totalItemNum
	 *            总条目数
	 * @param currentPageNum
	 *            当前页号
	 */
	public Pagination(int totalItemNum, int currentPageNum) {
		this(totalItemNum, currentPageNum, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 
	 * @param totalItemNum
	 *            总条目数
	 * @param currentPageNum
	 *            当前页号
	 * @param pageSize
	 *            每页大小
	 */
	public Pagination(int totalItemNum, int currentPageNum, int pageSize) {
		this.totalItemNum = totalItemNum;
		this.currentPageNum = currentPageNum;
		this.pageSize = pageSize;
		initTotalPage();
	}

	public void initTotalPage() {
		if (this.totalItemNum != 0) {
			if (this.totalItemNum % this.pageSize == 0) {
				this.totalPageNum = this.totalItemNum / this.pageSize;
			} else {
				this.totalPageNum = this.totalItemNum / this.pageSize + 1;
			}
		}
		if (this.totalPageNum <= 1) {
			this.prePage = false;
			this.nextPage = false;
		} else {
			this.prePage = true;
			this.nextPage = true;
			if (this.currentPageNum <= 1) {
				this.prePage = false;
			}
			if (this.currentPageNum == this.totalPageNum) {// 最后一页
				this.nextPage = false;
			}
		}
	}

	/**
	 * 绑定list
	 * 
	 * @param data
	 */
	public void bindData(List<E> data) {
		this.data = data;
	}

	public int getStartPosition() {
		return (this.currentPageNum - 1) * this.pageSize;
	}

	public int getCurrentPageNum() {
		return currentPageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalItemNum() {
		return totalItemNum;
	}

	public int getTotalPageNum() {
		if (totalPageNum == 0)
			return 1;
		return totalPageNum;
	}

	public boolean isPrepage() {
		return this.prePage;
	}

	public boolean isNextpage() {
		return this.nextPage;
	}

	public List<E> getData() {
		return data;
	}

	public void setTotalItemNum(int totalItemNum) {
		this.totalItemNum = totalItemNum;
	}

	public void setCurrentPageNum(int currentPageNum) {
		this.currentPageNum = currentPageNum;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalPageNum(int totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public void setPrePage(boolean prePage) {
		this.prePage = prePage;
	}

	public void setNextPage(boolean nextPage) {
		this.nextPage = nextPage;
	}

	public void setData(List<E> data) {
		this.data = data;
	}
}
