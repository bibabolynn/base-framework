package com.yuan.util;

import java.util.Map;

public class Pager {
	// 专门放一些自定义的数据，例如字符串信息、变量等
	private Map<String, Object> mapObj;
	private int currentPage;
	private int showCount;
	private int totalPage;
	private long totalResult;
	private Object listObject;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public long getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(long totalResult) {
		this.totalResult = totalResult;
	}

	public Object getListObject() {
		return listObject;
	}

	public void setListObject(Object listObject) {
		this.listObject = listObject;
	}

	public Map<String, Object> getMapObj() {
		return mapObj;
	}

	public void setMapObj(Map<String, Object> mapObj) {
		this.mapObj = mapObj;
	}

}
