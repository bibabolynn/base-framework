package com.yuan.util.http;

import com.yuan.util.BaseException;
import com.yuan.util.JSONUtil;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.BussErrorCode;
import com.yuan.util.constant.PubErrorCode;
import net.sf.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HttpUtil {
    private  static String JSON_START = "{";  //JSON数据格式开头
    private  static String RESPONSE_CODE = "resultCode";  //响应报文码
    private  static String SUCCESS_CODE = "0000";  //响应报文码
    
    /**
     * 读取request请求，并返回请求报文
     * @param request
     * @return String 请求报文
     * @throws BaseException
     */
    public static String getRequestString(HttpServletRequest request) throws BaseException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        String str = null;
        try{
             br = request.getReader();
             String line = br.readLine();
             while (line != null) {
                 sb.append(line);
                 line = br.readLine();
             }
             LogUtil.info("请求报文读流完毕");
             str = new String(sb.toString().getBytes(), "UTF-8");
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_REQUEST_PARAM, "解析request成请求报文字符串失败", LogUtil.EMPTY,e);
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new BaseException(PubErrorCode.PUB_REQUEST_COLSE,"关闭解析报文流失败",e);
                }
            }
        }
        return str;
    }
    /**http响应信息
     * @param response
     */
    public static void httpResponse(JSONObject jsonObject, HttpServletResponse response){
        PrintWriter writer = null;
        try {
            /*
             * 1、处理响应码和响应信息
             */
            if(!(jsonObject.has(RESPONSE_CODE))){
                jsonObject.put(RESPONSE_CODE,SUCCESS_CODE);
            }
            if(!(jsonObject.has("resultMsg"))){
                jsonObject.put("resultMsg","success");
            }
            /*
             * 2、返回响应报文
             */
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
            String encode = "UTF-8";
            String req = jsonObject.toString();
            writer.print(new String(req.getBytes(encode), encode));
            writer.flush();
        } catch (Exception e) {
            LogUtil.handerEx(PubErrorCode.PUB_RESPONSE_PARAM ,"返回报文" + jsonObject.toString() + "写入流失败", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    /**组装请求报文，发送指定系统
     * @param contJson ： 请求内容
     * @throws BaseException
     */
    public static JSONObject sendHttpRequest(JSONObject contJson) throws BaseException{
        JSONObject resultJson = new JSONObject();
        try{
            /*
             * 1、拼接请求报文
             */
            JSONObject reqJson = new JSONObject();
            JSONObject headerJson = new JSONObject();
            reqJson.put("body", contJson);
            reqJson.put("header", headerJson);
            /*
             * 2、发生短信获取响应报文
             */
            String sysUrl ="" ; //系统地址
            String api = "";//接口号
            String url = sysUrl + api;
            String result = JSONUtil.sendPost(url,reqJson.toString());
            /*
             * 3、处理响应报文，返回响应内容体
             */
            resultJson = dealResponse(result);
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_RESPONSE_PARAM ,"返回报文" + contJson.toString() + "写入流失败", e);
        }
        return resultJson;
    }
    
    /**处理响应报文
     * @param result ： 响应内容
     * @throws BaseException
     */
    public static JSONObject dealResponse(String result) throws BaseException{
        JSONObject resultJson = new JSONObject();
        try{
            if(result.startsWith(JSON_START)&&result.contains(RESPONSE_CODE)) { // 是否以标准返回为开头
              resultJson = JSONObject.fromObject(result);
              String errorCode = resultJson.getString(RESPONSE_CODE);
              if (!(SUCCESS_CODE.equals(errorCode))) { // 是否成功
                  throw LogUtil.handerEx(PubErrorCode.PUB_RETCODE_EXCEPTION, "报文响应处理失败，响应值为：" + result);
              }
          } else {
              throw LogUtil.handerEx(PubErrorCode.PUB_DATA_FORMAT, "报文响应格式错误，响应值为：" + result);
          }
          resultJson.remove("resultCode");
          resultJson.remove("resultMsg");
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_RESPONSE_PARAM ,"返回报文" + result + "写入流失败", e);
        }
        return resultJson;
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
