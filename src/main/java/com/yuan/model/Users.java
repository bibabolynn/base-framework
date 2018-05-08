package com.yuan.model;

import java.io.Serializable;
import java.util.List;

public class Users implements Serializable {


	private static final long serialVersionUID = 1L;

	private String userId;

	private String userName;

	private String userPwd;

	/**
	 * 判断首次登录标志
	 */
	private String firstLogin;
	
	/**
	 * 判断用户是否失效标志
	 */
	private String userValid;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd == null ? null : userPwd.trim();
	}

	public String getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}


	public String getUserValid() {
		return userValid;
	}

	public void setUserValid(String userValid) {
		this.userValid = userValid;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}