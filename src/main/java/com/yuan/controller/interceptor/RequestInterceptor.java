package com.yuan.controller.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * 请求检测拦截器。
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 请求检测拦截器。
	 * 1、特殊权限放行
	 * 2、验证session
	 * 3、验证请求权限
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BaseException {
		try{
			/*
			 * 1、验证session
			 */
			Object userObj = request.getSession().getAttribute("user");
			if (userObj == null) {
				response.sendRedirect(request.getContextPath() + "");
				return false;
			}
			
			HttpSession hsession = request.getSession();
			String menuId = request.getParameter("menuId");
			hsession.setAttribute("menuId", menuId);
			/*
			 * 2、验证请求权限
			 */
            List<String> fnActions = (List<String>) request.getSession().getAttribute("resList"); //获取用户权限集合
            String fnAction = request.getServletPath(); // 请求路径，例如：/service/login/
            fnAction = fnAction.substring(1, fnAction.length());
			if (fnActions!=null&&fnActions.contains(fnAction.trim())){
				return true;
			} else {
				throw LogUtil.handerEx(PubErrorCode.PUB_REQUEST_RIGHT, "用户无此操作权限："+fnAction.trim() ,LogUtil.EMPTY);
			}
		}catch(Exception e){
			throw LogUtil.handerEx(PubErrorCode.PUB_REQUEST, "请求拦截异常" ,LogUtil.ERROR, e); 
		}
	}
}