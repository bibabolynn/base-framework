package com.yuan.util.pagination;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 分页工具类 Class Name: PaginationUtil.java
 * 
 * @author liuhuiqing DateTime 2014-8-7 下午1:30:59
 * @version 1.0
 */
public final class PaginationUtil {
	//当前页面往前或往后显示的最大记录数
	public static final int SIDE_SIZE = 5;
	//分页样式
	public static final String PAGE_CSS = "pageCssDefault";
	//分页页面展示元素
	public static final String FIRST = "首页";
	public static final String PREVIOUS = "上一页";
	public static final String NEXT = "下一页";
	public static final String LAST = "末页";
	public static final String NUMBER_BEFORE = "";
	public static final String NUMBER_AFTER = "";

	public static String pageCompoent(String controller, String action,
                                      PageBean<?> page, Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
        if(page==null){
            return builder.toString();
        }
		builder.append("<div class=\"").append(PAGE_CSS).append("\">")
				.append(pageLink(controller, action, page, params))
				.append("<span>共").append(getTotalPageNum(page.getTotalCount(),page.getPageSize()))
				.append("页/").append(page.getTotalCount())
				.append("条记录</span>").append("</div>");
		return builder.toString();
	}

    private static int getTotalPageNum(int totalCount,int pageSize){
        if(totalCount <= 0){
            return 0;
        }
        return totalCount%pageSize==0?totalCount/pageSize:(totalCount/pageSize)+1;
    }
	/**
	 * 
	 * @param controller
	 * @param action
	 * @param page
	 * @param params
	 * @return
	 */
	private static String pageLink(String controller, String action,
                                   PageBean<?> page, Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
		List<PageLink> pageLinks = PageLink.buildLinks(
                page.getCurrentPage(), getTotalPageNum(page.getTotalCount(),page.getPageSize()));
		for (PageLink link : pageLinks) {
			if (link.isDisplay()) {
				if (link.isClickable()) {
					Map<String, String> result = Maps.newHashMap();
					if (params != null) {
						result.putAll(params);
					}
					result.put("currentPage", link.getNumber() + "");
					result.put("pageSize", page.getPageSize() + "");
					builder.append(buildHtmlLinK(
							actionUrl(controller, action, result),
							link.getContent()));
				} else {
					builder.append("<span class=\"current\">");
					builder.append(link.getContent());
					builder.append("</span> ");
				}
			}else{
				builder.append("<span class=\"disabled\">");
				builder.append(link.getContent());
				builder.append("</span> ");
			}
		}

		return builder.toString();
	}

	/**
	 * build html link.
	 * 
	 * @param url
	 * @param context
	 * @return
	 */
	public static String buildHtmlLinK(String url, String context) {

		StringBuilder builder = new StringBuilder();

		builder.append("<a href='");
		builder.append(url);
		builder.append("'>");
		builder.append(context);
		builder.append("</a>");

		return builder.toString();

	}

	/**
	 * 基于约定用controller、action和参数生成url.
	 * 
	 * @param controller
	 * @param action
	 * @param params
	 * @return
	 */
	public static String actionUrl(String controller, String action,
			Map<String, String> params) {
		return new StringBuilder().append(controller).append("/")
				.append(action).append(searchPart(params)).toString();
	}

	/**
	 * 生成请求参数字符串?key1=value1&key2=value2.
	 * <p/>
	 * 如value为null则此
	 * 
	 * @param param
	 * @return
	 */
	public static String searchPart(Map<String, String> param) {

		if (param == null || param.isEmpty()) {
			return "";
		}

		StringBuilder paramUrl = new StringBuilder();
		Map<String, String> usefulParam = Maps.filterEntries(param,
				new Predicate<Map.Entry<String, String>>() {
					@Override
					public boolean apply(Map.Entry<String, String> item) {
						if (item == null) {
							return false;
						}
						return !Strings.isNullOrEmpty(item.getValue());
					}
				});

		paramUrl.append("?");

		paramUrl = Joiner.on("&").withKeyValueSeparator("=")
				.appendTo(paramUrl, usefulParam);

		return paramUrl.toString().replace("%", "%25");
	}

	/**
	 * 分页链接信息
	 * 
	 * 用于：生成分页具体link 包含的信息有：link内容、link是否能点击、link是否显示
	 */
	private static abstract class PageLink {
		protected int number;
		protected int current;
		protected int total;

		/**
		 * 唯一取得PageLink方法
		 * 
		 * @param currentPageNum
		 * @param totalPageNum
		 * @return
		 */
		static List<PageLink> buildLinks(int currentPageNum, int totalPageNum) {
			LinkedList<PageLink> pageLinks = Lists.newLinkedList();

			int preCount = 0;
			for (int number = currentPageNum; number > 0; number--) {
				preCount++;
				if (preCount > SIDE_SIZE) {
					break;
				}
				pageLinks.addFirst(new NumberLink(number, currentPageNum,
						totalPageNum));
			}

			int nexCount = 0;
			for (int number = currentPageNum + 1; number <= totalPageNum; number++) {
				nexCount++;
				if (nexCount >= SIDE_SIZE) {
					break;
				}
				pageLinks.addLast(new NumberLink(number, currentPageNum,
						totalPageNum));
			}

			pageLinks.addFirst(new PreviousLink(currentPageNum, totalPageNum));
			pageLinks.addFirst(new FirstLink(currentPageNum, totalPageNum));

			pageLinks.addLast(new NextLink(currentPageNum, totalPageNum));
			pageLinks.addLast(new LastLink(currentPageNum, totalPageNum));

			return pageLinks;
		}

		/**
		 * protected ctor for children.
		 * 
		 * @param number
		 * @param current
		 * @param total
		 */
		protected PageLink(int number, int current, int total) {
			this.number = number;
			this.current = current;
			this.total = total;
		}

		/**
		 * 能否点击.
		 * 
		 * @return
		 */
		boolean isClickable() {
			return number != current;
		}

		int getNumber() {
			return this.number;
		}

		/**
		 * 链接内容.
		 * 
		 * @return
		 */
		abstract String getContent();

		/**
		 * 是否显示.
		 * 
		 * @return
		 */
		abstract boolean isDisplay();

		// =================具体Link====================

		private static class FirstLink extends PageLink {

			protected FirstLink(int current, int total) {
				super(1, current, total);
			}

			@Override
			String getContent() {
				return FIRST;
			}

			@Override
			boolean isDisplay() {
				return current > 1;
			}
		}

		private static class LastLink extends PageLink {

			protected LastLink(int current, int total) {
				super(total, current, total);
			}

			@Override
			String getContent() {
				return LAST;
			}

			@Override
			boolean isDisplay() {
				return current < total;
			}
		}

		private static class NextLink extends PageLink {

			protected NextLink(int current, int total) {
				super(current + 1, current, total);
			}

			@Override
			String getContent() {
				return NEXT;
			}

			@Override
			boolean isDisplay() {
				return current < total;
			}
		}

		private static class PreviousLink extends PageLink {

			protected PreviousLink(int current, int total) {
				super(current - 1, current, total);
			}

			@Override
			String getContent() {
				return PREVIOUS;
			}

			@Override
			boolean isDisplay() {
				return current > 1;
			}
		}

		private static class NumberLink extends PageLink {

			protected NumberLink(int number, int current, int total) {
				super(number, current, total);
			}

			@Override
			String getContent() {
				if (isClickable()) {
					return new StringBuilder().append(NUMBER_BEFORE)
							.append(number).append(NUMBER_AFTER).toString();
				} else {
					return String.valueOf(number);
				}
			}

			@Override
			boolean isDisplay() {
				return true;
			}
		}

	}

}
