package com.yuan.controller.interceptor;



import java.util.List;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.pagehelper.MSUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.SqlUtil;
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PageHelperInterceptor  extends PageHelper {

	public static String CUSTOM_COUNT = "_CustomCount";

	public static String CUSTOM_PAGE = "_CustomPage";
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		String id = ms.getId();
		if(id.endsWith(CUSTOM_COUNT)){
			return invocation.proceed();
		}else if(id.endsWith(CUSTOM_PAGE)){
			return CustomPage(invocation, ms);
		}else{
			return super.intercept(invocation);
		}
	}


	private Object CustomPage(Invocation invocation, MappedStatement ms) throws Throwable {
		Page<?> page = SqlUtil.getLocalPage();
		if(page==null){
			return super.intercept(invocation);
		}
		try {
			MetaObject metaObject = SystemMetaObject.forObject(this);
			MSUtils msUtils = (MSUtils) metaObject.getValue("sqlUtil.msUtils");
			SqlSource sqlSource = ms.getSqlSource();
			msUtils.processPageMappedStatement(ms, sqlSource, page, invocation.getArgs());
			Object result = invocation.proceed();
			page.addAll((List) result);
			return page;
		} finally{
			SqlUtil.clearLocalPage();
		}
	}
}
