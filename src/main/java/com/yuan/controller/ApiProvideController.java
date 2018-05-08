package com.yuan.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.BussErrorCode;
import com.yuan.util.http.HttpUtil;
import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 风控系统API接口控制器
 *
 */
@RestController
@RequestMapping("/server/api/")
public class ApiProvideController {

//   private ApiProvideServiceImpl apiServerService;

    /**
     * 添加或更新客户信息，返回客户编号
     * @param request
     * @param response
     */
    @RequestMapping(value = "")
    public void method(HttpServletRequest request,HttpServletResponse response){
        JSONObject json = new JSONObject();//响应报文
        try {
        	JSONObject reqJson = deCodeReq(request);  //客户端请求报文
		} catch (Exception e) {
			BaseException tfe = LogUtil.handerEx("111", "新增或修改借款人失败", LogUtil.ERROR, e);
            json.put("resultCode", tfe.errorCode);
            json.put("resultMsg", tfe.errorMsg);
		}finally{
			LogUtil.info("新增或修改客户，响应报文："+json.toString());
			HttpUtil.httpResponse(json, response);
		}
    }
    
    /** 处理请求request，包含：解密验证、验签验证、转换请求成JSON对象
     * @param request
     * @return
     * @throws BaseException
     */
    public JSONObject deCodeReq(HttpServletRequest request) throws BaseException{
        JSONObject jsonObj = new JSONObject();
        try{
          String res = request.getAttribute("jsonStr")==null?"":request.getAttribute("jsonStr").toString();
          jsonObj = JSONObject.fromObject(res);
        }catch(Exception e){
            throw LogUtil.handerEx(BussErrorCode.ERROR_REQUEST_CONTROL, "公共获取请求端参数失败",LogUtil.EMPTY, e);
        }
        return jsonObj;
    }
}