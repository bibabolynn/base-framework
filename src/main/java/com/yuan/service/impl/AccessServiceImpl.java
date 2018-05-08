package com.yuan.service.impl;

import javax.annotation.Resource;

import com.yuan.service.AccessService;
import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.springframework.stereotype.Service;


@Service
public class AccessServiceImpl implements AccessService {
//	@Resource
//	private MUsersMapper musersMapper; //用户对象
	/**
	 * @param keyName: 序列名称
	 * @return
	 */
	public String countPr(String keyName) throws BaseException{
		String seqNum = "";
		try {
//			seqNum = musersMapper.countPr(keyName);
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_RIMKEY_SERVICE, "查询序列名为：" + keyName + "的序列账号失败", e);
		}
		return seqNum;
	}
}
