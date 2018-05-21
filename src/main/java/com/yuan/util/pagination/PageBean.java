package com.yuan.util.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lynn on 2018/5/15.
 */
public class PageBean<T> implements Serializable {
    private int currentPage;
    private int startIndex;
    private int pageSize;
    private int totalCount;
    private List<T> resultList;
    public static final int DEFAULT_PAGESIZE = 20;

    public PageBean() {
    }

    public PageBean(int currentPage, int pageSize) {
        if(currentPage <= 0) {
            currentPage = 1;
        }

        if(pageSize <= 0) {
            pageSize = 20;
        }

        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.startIndex = (currentPage - 1) * pageSize;
    }

    public PageBean(int totalCount, List<T> list) {
        this.totalCount = totalCount;
        this.addAll(list);
    }

    public PageBean(int currentPage, int pageSize, int totalCount, List<T> list) {
        if(currentPage <= 0) {
            currentPage = 1;
        }

        if(pageSize <= 0) {
            pageSize = 20;
        }

        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.startIndex = (currentPage - 1) * pageSize;
        this.totalCount = totalCount;
        this.addAll(list);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getStartIndex() {
        if(this.currentPage <= 0) {
            this.currentPage = 1;
        }

        if(this.pageSize <= 0) {
            this.pageSize = 20;
        }

        this.startIndex = (this.currentPage - 1) * this.pageSize;
        return this.startIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public List<T> getResultList() {
        return this.resultList;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void addAll(List<T> result) {
        this.resultList = new ArrayList();
        this.resultList.addAll(result);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PageBean{");
        sb.append("currentPage=").append(this.currentPage);
        sb.append(", startIndex=").append(this.startIndex);
        sb.append(", pageSize=").append(this.pageSize);
        sb.append(", totalCount=").append(this.totalCount);
        sb.append(", resultList=").append(this.resultList);
        sb.append('}');
        return sb.toString();
    }
}