package com.yuan.util;

import java.util.Date;

import javax.annotation.Resource;

import com.yuan.util.constant.PrimKeyCode;
import com.yuan.util.constant.PubErrorCode;
import org.springframework.stereotype.Component;


/*
 * 生成各个业务主键
 */
@Component
public class ProductPrimKey {

	@Resource
	AccessService accessService;
	
	/**
	 * sequence功能查询
	 * 
	 * @param keyName ：sequence key
	 * @param maxValue 允许的最大值
	 * @return
	 * @throws TranFailException
	 * 备注：暂时写死，传递的key加上时间
	 */
	public String querySerialNumber(String keyName) throws BaseException {
		String result = "";
		String date = JavaUtil.DateToString(new Date(), "yyyyMMdd");
		String dateTime = JavaUtil.DateToString(new Date(), "yyyyMMddHHmmss");
		String flowNum = ""; // 流水编号
		if (keyName == null) {
		    keyName = PrimKeyCode.DEFAULT_NO; //默认编号
		} else {
			keyName.trim();
		}
		try {
			flowNum = accessService.countPr(keyName);
			switch (keyName) {
			case PrimKeyCode.LOAN_CONT_NO: // 借款合同
				result = PrimKeyCode.CONT_BEGIN + date + flowNum + PrimKeyCode.CONT_END; // 开头 + 日期 + 流水号 + 结尾
				break;
			case PrimKeyCode.BUSS_NO: // 业务编号
				flowNum = getFlowNewNum(flowNum);
				result = date + flowNum+"X"; // 开头 + 日期  
				break;
			default:
				result = dateTime + flowNum;
				break;
			}
		} catch (Exception e) {
		    throw LogUtil.handerEx(PubErrorCode.PUB_RIMKEY,"keyName:" + keyName + "生成主键编号失败",LogUtil.EMPTY, e);
		}
		return result;
	}

	/**
	 * 设定字符串长度为4
	 * 
	 * @param flowNum
	 * @return
	 */
	private String getFlowNewNum(String flowNum) {
		String re = "";
		if (flowNum != null && flowNum.length() < 4) {
			for (int i = 0; i < 4 - flowNum.length(); i++) {
				re += "0";
			}
			re += flowNum;
		}else{
			re = flowNum;
		}
		return re;
	}
	
	// /**返回序列号值
	// * @param keyName
	// * @return
	// * @throws TranFailException
	// */
	// public String countContNum(String keyName) throws TranFailException{
	// Connection conn = null;
	// String sql = "select f_nextval_primKey('" + keyName + "')";
	// CallableStatement cstmt = null;
	// ResultSet rs = null;
	// int queryResult = 0;
	// try{
	// conn = SqlSessionUtils.getSqlSession(sqlSession.getSqlSessionFactory(),
	// sqlSession.getExecutorType(),sqlSession.getPersistenceExceptionTranslator()).getConnection();
	// cstmt = conn.prepareCall(sql);
	// cstmt.execute();
	// rs = cstmt.getResultSet();
	// if (rs.next()) {
	// queryResult = rs.getInt(1);
	// }
	// // 如果达到最大值则重置
	// if (queryResult > PrimKeyCode.MAX_VALUE) {
	// sql = "select f_setval_recharge('" + keyName + "', 1000)";
	// cstmt = conn.prepareCall(sql);
	// cstmt.execute();
	// }
	// return queryResult+"";
	// }catch(Exception e){
	// LogUtil.error("keyName:" + keyName + "查询流水序列异常", e);
	// throw new TranFailException(ErrorCode.ERROR_PUBLIC_UTIL_1, "keyName:" +
	// keyName + "查询流水序列失败!");
	// } finally {
	// if(conn!=null){
	// try {
	// conn.close();
	// } catch (SQLException e) {
	// LogUtil.error("关闭数据库连接失败", e);
	// throw new TranFailException(ErrorCode.ERROR_PUBLIC_CLOSEJDBC, "keyName:"
	// + keyName + ",关闭数据库连接失败!");
	// }
	// }
	// }
	// }
}
