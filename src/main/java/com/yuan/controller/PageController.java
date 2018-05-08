package com.yuan.controller;


import java.util.Map;







import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.Pager;
import com.yuan.util.constant.PubErrorCode;

public class PageController {
	int pSize = 10;

	protected Pager setPageInfo(Page<?> page, Object obj, Map<String, Object> map)
			throws BaseException {

		try {
			Pager pager = new Pager();
			pager.setCurrentPage(page.getPageNum());
			pager.setShowCount(page.getPageSize());
			pager.setTotalPage(page.getPages());
			pager.setTotalResult(page.getTotal());
			pager.setListObject(obj);
			pager.setMapObj(map);
			return pager;
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_PAGE_SEVE_SIZE, "保存当前页码数据Controller错误，参数为："+map, LogUtil.ERROR, e);
		}
	}

	protected Page<?> setPageStart(int pStart, int currentPage, int totalPage,
			int otype, int goPage) throws BaseException {
		try {
			if (otype == -7)
				return PageHelper.startPage(pStart, pSize, true).pageSizeZero(
						true);
			if (otype == 3) {
				pStart = goPage;
			} else {
				pStart = pStart + otype;
			}

			if (pStart < 1) {
				pStart = 1;
			}
			if (pStart >= totalPage) {
				pStart = totalPage;
			}
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_PAGE_DATA_SIZE, "当前页码数据Controller错误，参数为：", LogUtil.ERROR, e);
		}

		return PageHelper.startPage(pStart, pSize, true).pageSizeZero(true);
	}

}
