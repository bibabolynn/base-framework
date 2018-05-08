package com.yuan.vo;

import java.io.Serializable;

public class HttpRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6320808217075626622L;
	
	/**
	 *  响应码
	 */
	private int code;
	
	/**
	 *  响应内容
	 */
	private String content;
	
	
	

	public HttpRes(int code, String content) {
		super();
		this.code = code;
		this.content = content;
	}



	public int getCode() {
		return code;
	}



	public void setCode(int code) {
		this.code = code;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	@Override
	public String toString() {
		return "ResponseVo [code=" + code + ", content=" + content + "]";
	}
	
	
	
	

}
