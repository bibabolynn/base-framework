package com.yuan.controller.interceptor;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * 请求检测拦截器。
 */
public class ServerApiInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 服务器端请求检测拦截器
	 * 1、获取请求数据
	 * 2、验证session
	 * 3、验证请求权限
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BaseException {
		try{
		    /*
		     * 1、获取请求信息
		     */
		    requestParam(request);
			return true;
		}catch(Exception e){
			throw LogUtil.handerEx(PubErrorCode.PUB_SERVER_REQUEST, "服务器端请求拦截异常" ,LogUtil.ERROR, e);
		}
	}
	
	/**处理服务器端请求信息
	 * 1、获取请求参数
	 * 2、安全验证
	 * 3、响应过滤完的参数
	 * @param request
	 * @throws BaseException
	 */
	public void requestParam(HttpServletRequest request) throws BaseException{
	    try{
	        //String res = HttpUtil.getRequestString(request);
	        JSONObject json = new JSONObject();
	        json.put("username","chengzh");
	        json.put("pwd","123456");
	        request.setAttribute("jsonStr", json);
	    }catch(Exception e){
	        throw LogUtil.handerEx(PubErrorCode.PUB_SERVER_PARAM, "处理服务器端请求失败" ,LogUtil.EMPTY, e);
	    }
	    
	}
}