package com.yuan.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yuan.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService{
	
//	@Resource
//	MUsersMapper usersMapper;

	
//	@Override
//	public MUsers getUserByUserIdAndPwd(String userId, String userPwd) throws TranFailException {
//		MUsers user = new MUsers();
//		try {
//			String aesPwd = AESUtil.Decrypt(userPwd); 				 // 可逆解密
//			String encPwd = EncryptPwd.encryPtionStr(aesPwd);	 // 不可逆加密
//			user = usersMapper.findExistUser(userId, encPwd);
//		} catch (Exception e) {
//		    throw LogUtil.handerEx(PubErrorCode.ERROR_LOGIN_SERVICE, "登陆失败,用户id为："+userId, LogUtil.EMPTY, e);
//		}
//		return user;
//	}

}
