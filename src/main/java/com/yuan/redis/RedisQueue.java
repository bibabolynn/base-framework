package com.yuan.redis;

import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ubillion.dao.RedisQueueLogMapper;
import com.ubillion.model.RedisQueueLog;
import com.ubillion.quartz.ClusterQuartzJobProvider;
import com.ubillion.redis.handle.MessageContants;
import com.ubillion.util.JavaUtil;
import com.ubillion.util.LogUtil;
import com.ubillion.util.StringUtil;
import com.ubillion.util.SysParamUtil;
import com.ubillion.util.TranFailException;
import com.ubillion.util.constant.PubErrorCode;

@Service
public class RedisQueue {
	@Resource
	RedisQueueLogMapper mapper;
	@Resource(name="persist")
	ListOperations<String, String> listOps;
	
	private static final String QUEUE_NAME = "com.ubillion.redis.invoke.RedisQueue";
	/**
	 * 放入redis队列内,并存入数据库流水
	 * 
	 * @param msg:消息对象，包含：处理消息的类、方法、消息类型、消息参数等
	 * @throws TranFailException
	 */
	public void putRedisQueue(Message msg) throws TranFailException {
	    RedisQueueLog log = new RedisQueueLog();
		try {
		    /*
		     * 1、准备好需要消费的记录信息，并存储入库
		     */
		    Date now = new Date();
		    String nowStr = JavaUtil.DateToString(now, "yyyyMMddHHmmss");
		    msg.setStorageTime(now.getTime());
		    log.setContent(msg.getContent());
		    log.setOperatorName(msg.getOperatorName());
		    log.setOperatorNo(msg.getOperatorNo());
		    log.setMessageSystem(msg.getMessageType().substring(0, 3)); //系统
		    log.setMessageType(msg.getMessageType().substring(3, 6)); //类型
		    log.setTitle(msg.getTitle());
		    log.setRemark(msg.getRemark());
		    log.setExecuteTime(nowStr);
		    log.setModifyTime(nowStr);
		    log.setStatus("01");// 待处理
			String allMethed = SysParamUtil.getAppDisplay(MessageContants.REDIS_QUEUE_MESSAGE_TYPE,msg.getMessageType());
			if (StringUtils.isBlank(allMethed)) {
				throw new TranFailException(PubErrorCode.ERROR_PUT_REDIS_QUEUE_PARAM, "数据库没有配置:" + msg.getMessageType());
			}
			String[] classNameAndMethedName = allMethed.split("\\|");
			log.setClassName(classNameAndMethedName[0]);
			log.setMethedName(classNameAndMethedName[1]);
			log.setStorageTime(nowStr);
			mapper.insert(log);
			/*
			 * 2、存入消息到redis队列
			 */
			msg.setId(log.getId());
			String msgStr = JSONObject.toJSONString(msg);
			Long push = listOps.rightPush(QUEUE_NAME, msgStr);
			LogUtil.info("RedisQueue_V1队列内容放入成功:" + msgStr+",返回index:"+push);
		} catch (Exception e) {
			log.setStatus("03");// 存放失败
			log.setErrorCode(PubErrorCode.ERROR_PUT_REDIS_QUEUE);
			log.setErrorMsg("redis队列放入失败" + e.getMessage());
			mapper.updateByPrimaryKey(log);
			throw LogUtil.handerEx(PubErrorCode.ERROR_PUT_REDIS_QUEUE, "redis队列放入失败", LogUtil.ERROR, e);
		}
	}

	/**
	 * 消费队列中的内容 用quarz来调用
	 * 
	 * @throws TranFailException
	 */
	public void consumeRedisQueue() throws TranFailException {
		RedisQueueLog log = new RedisQueueLog();
		try {
		    /*
		     * 1、获取队列信息
		     */
		    Date now = new Date();
	        String dateStr = JavaUtil.DateToString(now, "yyyyMMddHHmmss");
			// 获取参数对象Message msg
			String msgStr = listOps.leftPop(QUEUE_NAME);
			if (StringUtils.isBlank(msgStr)) {
				return;
			}
			/*
			 * 2、存放时间超过10秒再进行消费
			 */
			Message msg = JSONObject.parseObject(msgStr, Message.class);
			// 10秒后才能处理队列中的内容
			if((now.getTime()-msg.getStorageTime())<10*1000){
				listOps.rightPush(QUEUE_NAME, msgStr);
				return;
			}
			/*
			 * 3、执行消费方法
			 */
			String allMethed = SysParamUtil.getAppDisplay(MessageContants.REDIS_QUEUE_MESSAGE_TYPE,msg.getMessageType());// 调用消费方法【业务逻辑】
			 // 全类名与方法规定按照'|'分割,如:com.ubillion.service.CommonService|selectBussImage
			String[] classNameAndMethedName = allMethed.split("\\|");
			Class<?> clz = Class.forName(classNameAndMethedName[0]);
			Object otargetObject = ClusterQuartzJobProvider.getBean(clz);
			Method method = otargetObject.getClass().getMethod(classNameAndMethedName[1],new Class[] { String.class });
			/*
			* 4、处理消费日志，存放操作人姓名和编号到执行参数中
			*/
			String contentString = msg.getContent()==null||"".equals(msg.getContent())?"{}":msg.getContent(); //消息内容
			String opName = msg.getOperatorName();//操作人姓名
			String opNo = msg.getOperatorNo();//操作人编号
			net.sf.json.JSONObject contJson =  net.sf.json.JSONObject.fromObject(contentString); 
			contJson.put("opName", opName);
			contJson.put("opNo", opNo);
			Object result = method.invoke(otargetObject, new Object[] {contJson.toString() });
			log.setId(msg.getId());
            log.setExecuteTime(dateStr);
            log.setModifyTime(dateStr);
			log.setStatus("02");// 处理成功
			log.setRemark(msg.getRemark() + "|" + String.valueOf(result));
			log.setErrorMsg(log.getErrorMsg());
		   /*
	        * 5、更新消费日志
	        */
			mapper.updateByPrimaryKeySelective(log);
		} catch (Exception e) {
		    LogUtil.handerEx(PubErrorCode.ERROR_REDISQUEUE, "队列日志数据更新失败,更新内容:"+log.getContent()+"执行方法为："+log.getClassName(), LogUtil.ERROR, e);
		    TranFailException tfe = (TranFailException) e;
            String errorCode = tfe.getErrorCode();
            String errorMsg = tfe.getErrorMsg();
            /*
             * 消息处理失败更新数据
             */
            log.setErrorCode(StringUtil.handerBigLog(errorCode,45));
            log.setErrorMsg(StringUtil.handerBigLog(errorMsg,2000));
            log.setStatus("03");// 处理失败
            mapper.updateByPrimaryKeySelective(log);
		}
	}
}
