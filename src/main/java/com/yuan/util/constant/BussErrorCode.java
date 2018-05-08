package com.yuan.util.constant;

public class BussErrorCode {
	/*
	 * 1、业务逻辑异常[业务逻辑001开头，第四位：control：0开头，service：1开头，后两位代表码值
	 * 例如：public static final String ERROR_LOGIN_CONTROL = "001001"; //登陆失败
	 */
    public static final String ERROR_REQUEST_CONTROL = "001001"; //公共处理接口请求参数失败
    public static final String ERROR_MANAGE_USER_F8 = "001002"; //修改登录密码失败
    
   
}
