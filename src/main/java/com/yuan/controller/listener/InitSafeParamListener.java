package com.yuan.controller.listener;


import javax.annotation.Resource;

import com.yuan.redis.RedisTemplateCache;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;


/*
 * Tomcat启动时，初始化安全系统参数
 * 实现ApplicationListener的onApplicationEvent方法能在项目启动时执行。
 */

@Repository
public class InitSafeParamListener implements ApplicationListener<ContextRefreshedEvent> {
	@Resource
	private RedisTemplateCache redisService;
	private  static boolean LOAD_AGING = false;
	
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			/*
			 * 1、是否已经初始化完毕
			 */
			if(LOAD_AGING){
				return;
			}
			LOAD_AGING = true;// 用于解决项目启动加载重复问题
			/*
			 * 2、验证渠道安全
			 */
			LogUtil.info("安全监听已启动");
			/*
			 * 3、验证交易安全
			 */
			
		} catch (Exception e) {
			LogUtil.handerEx(PubErrorCode.PUB_INITIA, "项目启动初始化系统参数失败" ,LogUtil.ERROR, e);
		}
	}
}
