package com.yuan.service;

import com.yuan.util.BaseException;

public interface AccessService {
	/**
	 * @param keyName: 序列名称
	 */
	public String countPr(String keyName) throws BaseException;
		
}
