package com.yuan.controller.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


/**
 * 异常拦截器
 */
public class ExceptionInterceptor implements HandlerExceptionResolver {
	/**
	 * 拦截异常做公共处理
	 *
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request,HttpServletResponse response, Object arg2, Exception exception) {
		ModelAndView mav = new ModelAndView("error/error");
		try{
			/*
			 * 1、判断异常类型是否为自定义异常，
			 * （1）如果是抛出自定义错误码和错误信息
			 * （2）如果不是抛出指定的错误码和错误信息
			 */
			if (exception instanceof BaseException) {
				BaseException tfe = (BaseException) exception;
				boolean ajaxRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With") ); //判断是否是ajax请求
				/*
				 * 1.1、是否是ajax请求
				 * （1）如果是抛出指定的标识ec=-1
				 * （2）如果不是抛出指定的错误码和错误信息
				 */
				if(ajaxRequest){
					JSONObject json = new JSONObject();
					response.setContentType("application/json; charset=utf-8");
				    PrintWriter writer = response.getWriter();
				    json.put("ec", "-1");  //错误标记
				    json.put("errorCode", tfe.errorCode);  //错误码
				    json.put("errorMsg", tfe.errorMsg);  //错误信息
				    writer.write(json.toString());  
				    writer.flush();
				    writer.close();
				    return null;
				}else if(PubErrorCode.ERROR_LOGIN_CONTROL.equals(((BaseException) exception).errorCode)){
				    mav = new ModelAndView("error/error");
				}else{
					mav.addObject("errorcode", tfe.errorCode); //错误码
					mav.addObject("errorMsg",tfe.errorMsg);  //错误信息
				}
			} else {
				mav.addObject("errorcode", PubErrorCode.PUB_EXCEPTION_OTHER);
				mav.addObject("errorMsg", "未知错误");
			}
		}catch(Exception e){
			LogUtil.handerEx(PubErrorCode.PUB_EXCEPTION, "异常拦截器处理异常失败" ,LogUtil.ERROR, e);
			mav.addObject("errorcode", PubErrorCode.PUB_EXCEPTION);
			mav.addObject("errorMsg", "未知错误");
		}
		return mav;
	}
}
