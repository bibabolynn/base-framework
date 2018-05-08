package com.yuan.util;

import com.yuan.util.constant.PubErrorCode;
import com.yuan.util.encrypt.AESUtil;
import net.sf.json.JSONObject;

import org.apache.shiro.crypto.hash.Sha256Hash;


public class CheckMsg {

    /**
     * 验证请求报文，并且转换为JSON对象
     * 
     * @param str 请求报文
     * @return JSONObject ： 请求报文JSON对象
     * @throws BaseException
     * 
     */
    public static JSONObject check(String str) throws BaseException {
        JSONObject jsobj = new JSONObject(); // 验证后的请求json
        String ming = "";
        try {
            /*
             * 1、解密验证
             */
            str = AESUtil.Decrypt(str, SysParamUtil.getSysParValue("AES_KEY")); // 解密数据 = 签文 + 明文
            String sign = str.substring(0, 64); // 签文
            ming = str.substring(64); // 明文
            LogUtil.info("请求报文:" + ming);
            /*
             * 2、验证摘要
             */
            checkSign(sign, ming); // 验证摘要
            LogUtil.info("摘要验证通过");
            jsobj = JSONObject.fromObject(ming); // 请求报文转换成JSON对象
            LogUtil.info("明文转换成json数据：" + jsobj);
        } catch (Exception e) {
        	throw LogUtil.handerEx(PubErrorCode.PUB_REQUEST_CHECK, "获取解密后的请求报文[json单个对象]失败，加密请求报文为：" + str, e);
        }
        return jsobj;
    }

    /**
     * 验证摘要
     * @param sign 签文
     * @param ming 请求明文
     * @throws BaseException
     */
    public static void checkSign(String sign, String ming) throws BaseException {
        try {
            String degist = new Sha256Hash(ming + SysParamUtil.getSysParValue("REQ_SALT")).toString(); // 摘要 = 明文 + 盐后
            if (!sign.equals(degist)) {
                BaseException tre = new BaseException(PubErrorCode.PUB_SIGN_ERROR, "摘要验证错误，不是有效摘要信息，签文为：" + sign + "明文为：" + ming);
                throw tre;
            }
        } catch (BaseException e) {
        	throw LogUtil.handerEx(PubErrorCode.PUB_SIGN_EXCEPTION, "验证摘要失败，签文为：" + sign + "明文为：" + ming, e);
        }
    }
}
