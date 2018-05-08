package com.yuan.util;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.yuan.util.constant.PubErrorCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONUtil {
    public static Map<String, String> bankMap = new HashMap<String, String>();


    public static HashMap<String, String> sysType = new HashMap<String, String>() {
        {
            put("USER_MANAGE", "000001"); //用户管理系统
        }
    };

    
   
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws IOException
     */
    public static String sendPost(String url, String param) throws BaseException {
        String result = "";
        long startTime = System.currentTimeMillis();
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            LogUtil.info("请求路径为：" + url);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 在系统参数里取连接超时时间
            String connectTimeout ="";
//                    (SysParamUtil.getSysParValue("CONNECT_TIMEOUT") == null) || (SysParamUtil.getSysParValue("CONNECT_TIMEOUT") == "") ? "10000"
//                            : SysParamUtil.getSysParValue("CONNECT_TIMEOUT");
            LogUtil.info("http链接超时时间为：" + Integer.parseInt(connectTimeout) / 1000 + "秒");
            // 在系统参数里取读取连接响应的超时时间
            String readTimeout = "";
//                    (SysParamUtil.getSysParValue("READ_TIMEOUT") == null) || (SysParamUtil.getSysParValue("READ_TIMEOUT") == "") ? "20000"
//                            : SysParamUtil.getSysParValue("READ_TIMEOUT");
            LogUtil.info("http链接响应时间为：" + Integer.parseInt(readTimeout) / 1000 + "秒");
            // 设置连接超时时间
            conn.setConnectTimeout(Integer.parseInt(connectTimeout));
            // 设置连接响应超时时间
            conn.setReadTimeout(Integer.parseInt(readTimeout));
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            String encode = "UTF-8";
            String newParam = new String(param.getBytes(encode), encode);
            LogUtil.info("发送数据为：" + newParam );
            out.print(newParam);
            out.flush();// flush输出流的缓冲
            int respCode = conn.getResponseCode();
            LogUtil.info("响应码:" + conn.getResponseCode());
            if (respCode == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));// 定义BufferedReader输入流来读取URL的响应
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                LogUtil.info("返回result数据为：" + result);
                long endTime = System.currentTimeMillis();// 响应结束时间
                LogUtil.info("处理请求时间为：" + (endTime - startTime) + "ms");
            } else if (respCode == 400) {
                LogUtil.info("请求错误，http响应码：" + respCode + ",http响应信息：请求错误");
                throw new BaseException(PubErrorCode.PUB_HTTP_400, "请求错误");
            } else if (respCode == 504) {
                LogUtil.info("网关超时，http响应码：" + respCode + ",http响应信息：网关超时");
                throw new BaseException(PubErrorCode.PUB_HTTP_504, "网关超时错误");
            } else {
                LogUtil.info("未知错误，http响应码：" + respCode + ",http响应信息：未知错误");
                throw new BaseException(PubErrorCode.PUB_HTTP_OTHERS, "服务器内部错误");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                BaseException tfe = new BaseException(e);
                tfe.setErrorCode("ESBTimeOut");
                tfe.setErrorMsg("访问地址：" + url + ",请求超时");
                throw tfe;
            } else if (e instanceof BaseException) {
                BaseException tfe = (BaseException) e;
                throw tfe;
            } else {
                throw new BaseException(PubErrorCode.PUB_HTTP_REQUST_POST, "请求路径为:" + url + ",参数为：" + param + ",响应失败！", e);
            }
        } finally {// 使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new BaseException(PubErrorCode.PUB_HTTP_REQUST_POST, "关闭请求流失败", ex);
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送GET方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws IOException
     */
    public static String sendGet(String url, String param, String sysCode) throws BaseException {
        String result = "";
        long startTime = System.currentTimeMillis();
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        JSONObject paramJson = new JSONObject();
        String paramStr = "";
        try {
            // 处理参数，拼接到后面
            paramJson = JSONObject.fromObject(param);
            for (Iterator<?> iter = paramJson.keys(); iter.hasNext();) {
                String key = (String) iter.next();
                if (iter.hasNext() == true) {
                    paramStr += key + "=" + URLEncoder.encode(paramJson.getString(key), "utf-8") + "&";
                } else {
                    paramStr += key + "=" + URLEncoder.encode(paramJson.getString(key), "utf-8");
                }
            }
            url += "?" + paramStr;
            LogUtil.info("Get请求路径为：" + url);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 在系统参数里取连接超时时间
            String connectTimeout ="";
//                    (SysParamUtil.getSysParValue("CONNECT_TIMEOUT") == null) || (SysParamUtil.getSysParValue("CONNECT_TIMEOUT") == "") ? "10000"
//                            : SysParamUtil.getSysParValue("CONNECT_TIMEOUT");
            LogUtil.info("http链接超时时间为：" + Integer.parseInt(connectTimeout) / 1000 + "秒");
            // 在系统参数里取读取连接响应的超时时间
            String readTimeout ="";
//                    (SysParamUtil.getSysParValue("READ_TIMEOUT") == null) || (SysParamUtil.getSysParValue("READ_TIMEOUT") == "") ? "20000"
//                            : SysParamUtil.getSysParValue("READ_TIMEOUT");
            LogUtil.info("http链接响应时间为：" + Integer.parseInt(readTimeout) / 1000 + "秒");
            // 设置连接超时时间
            conn.setConnectTimeout(Integer.parseInt(connectTimeout));
            // 设置连接响应超时时间
            conn.setReadTimeout(Integer.parseInt(readTimeout));
            // 获取URLConnection对象对应的输出流
            conn.connect();
            int respCode = conn.getResponseCode();
            LogUtil.info("响应码:" + conn.getResponseCode());
            if (respCode == 200) {
                // 定义BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }

                LogUtil.warn("Get返回result数据为：" + result);
                if (sysCode != null & sysCode != "") {
                    String sucKey = "";
//                            SysParamUtil.getAppDisplay("SUC_KEY", sysCode);
                    String sucVal = "";
//                            SysParamUtil.getAppDisplay("SUC_VAL", sysCode);
                    String sucTemp = "";
//                            SysParamUtil.getAppDisplay("SUC_TEMP", sysCode);
                    String noThrowCode ="";
//                            SysParamUtil.getAppDisplay("NO_THROW_CODE", sysCode) == null ? "" : SysParamUtil.getAppDisplay("NO_THROW_CODE", sysCode);// 4000006,4001014,4001011
                    if (result.startsWith(sucTemp)) { // 是否以标准返回为开头
                        if (result.contains(sucKey)) { // 是否包含正确返回码 例如retcode
                            JSONObject resultjson = new JSONObject();
                            resultjson = JSONObject.fromObject(result);
                            if (!(sucVal.equals(resultjson.get(sucKey).toString()))) { // 不是约定的成功标志
                                if (noThrowCode.contains(resultjson.get(sucKey).toString())) {
                                    LogUtil.warn("返回retcode不是2000000，错误信息为：" + result);
                                } else {
                                    Map<String, String> errorMap = new HashMap<String, String>();
                                    String errorCode = resultjson.get(sucKey).toString(); // 返回报文约定成功标志
                                    String errorMsg = ""; // 返回报文信息
                                    if (resultjson.has("msg")) {
                                        errorMsg = resultjson.getString("msg").toString();
                                    }
                                    errorMap.put("errorCode", errorCode);
                                    errorMap.put("errorMsg", errorMsg);
                                    throw new BaseException(PubErrorCode.PUB_RETCODE_ERROR, "向" + sysType.get(sysCode) + "发送数据失败，" + sucKey + "值不是"
                                            + sucVal + ",响应值为：" + result, errorMap);
                                }
                            }
                        } else {
                            throw new BaseException("错误码:" + PubErrorCode.PUB_RETCODE_EXCEPTION, "错误信息:向" + sysType.get(sysCode)
                                    + "发送数据失败，返回数据 不包含标记信息" + sucKey + "，响应值为：" + result);
                        }
                    } else {
                        throw new BaseException("错误码:" + PubErrorCode.PUB_DATA_FORMAT, "错误信息:向" + sysType.get(sysCode) + "发送数据失败，返回数据不是以"
                                + sucTemp + "为开始得格式，响应值为：" + result);
                    }
                }
                // 响应结束时间
                long endTime = System.currentTimeMillis();
                LogUtil.info("处理请求时间为：" + (endTime - startTime) + "ms");
            } else if (respCode == 400) {
                LogUtil.info("请求错误，http响应码：" + respCode + ",http响应信息：请求错误");
                throw new BaseException(PubErrorCode.PUB_HTTP_400, "请求错误");
            } else if (respCode == 504) {
                LogUtil.info("网关超时，http响应码：" + respCode + ",http响应信息：网关超时");
                throw new BaseException(PubErrorCode.PUB_HTTP_504, "网关超时错误");
            } else {
                LogUtil.info("未知错误，http响应码：" + respCode + ",http响应信息：未知错误");
                throw new BaseException(PubErrorCode.PUB_HTTP_OTHERS, "服务器内部错误");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                BaseException tfe = new BaseException(e);
                tfe.setErrorCode("ESBTimeOut");
                tfe.setErrorMsg("访问地址：" + url + ",ESB请求超时");
                throw tfe;
            } else if (e instanceof BaseException) {
                BaseException tfe = (BaseException) e;
                throw tfe;
            } else {
                throw new BaseException(PubErrorCode.PUB_HTTP_REQUST_POST, "请求路径为:" + url + ",参数为：" + param + ",响应失败！", e);
            }
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new BaseException(PubErrorCode.PUB_HTTP_REQUST_POST, "关闭流失败！", ex);
            }
        }
        return result;
    }

    /**
     * javabean转化为json
     * 
     * @param bean
     * @return json
     * @throws BaseException
     */
    public static JSONObject beanToJson(Object bean) throws BaseException {
        try {
            JSONObject json = new JSONObject();
            if (bean != null) {
                /* 转化部分 */
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    // 过滤class属性
                    if (!key.equals("class")) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        Object value = getter.invoke(bean);
                        json.put(key, value);
                    }
                }
            }
            return json;
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_BEAN_TO_JSON, "bean转换成json失败", LogUtil.EMPTY, e);
        }
    }
    /**
     * javabean转化为map
     * 
     * @param bean
     * @return json
     * @throws BaseException
     */
    public static Map<String, Object> beanToMap(Object bean) throws BaseException {
        try {
            Map<String, Object> json = new HashMap<>();
            if (bean != null) {
                /* 转化部分 */
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();
                    // 过滤class属性
                    if (!key.equals("class")) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        Object value = getter.invoke(bean);
                        json.put(key, value);
                    }
                }
            }
            return json;
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_BEAN_TO_MAP, "javabean转换成map失败", LogUtil.EMPTY, e);
        }
    }
    /**
     * json转javabean
     * 
     * @param json
     * @param bean: 参数传递例如：MUsers.class
     * @return
     * @throws BaseException
     */
    public static Object jsonToBean(JSONObject json, Class<?> bean) throws BaseException {
        try {
            String methodName = null;
            Object object =  bean.newInstance();
            Method[] methods = bean.getMethods();
            String key = null;
            Iterator<?> iterator = json.keys();
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                methodName = "set" + key.toUpperCase().charAt(0) + key.substring(1);
                for (Method method : methods) {
                    if (methodName.equals(method.getName())) {
                        method.invoke(object, new Object[] { json.get(key) == null || json.get(key).equals("null") ? "" : json.get(key) });
                    }
                }
            }
            return object;
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JSON_TO_BEAN, "json转javabean异常,json:" + json + ",Class:" + bean, LogUtil.EMPTY, e);
        }
    }
    
    /**
     * json转list<T>
     * @param <T>
     * @param json
     * @return
     * @throws BaseException
     */
    public static <T> List<?> jsonToList(JSONObject json, Class<T> bean, String listName) throws BaseException {
        List<T> list = new ArrayList<T>();
        try{
            JSONArray array = json.getJSONArray(listName);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jobj = array.getJSONObject(i);
                Object obj = JSONUtil.jsonToBean(jobj, bean);
                list.add((T)obj);
            }
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_JSON_TO_BEAN, "json转javabean异常,json:" + json + ",Class:" + bean, LogUtil.EMPTY, e);
        }
        return list;
    }
    /**
     * 将json格式的字符串解析成Map对象 <li>
     * json格式：{"name":"admin","retries":"3fff","testname" :"ddd","testretries":"fffffffff"}
     * 
     * @throws BaseException
     */
    public static Map<String, Object> jsonToMap(JSONObject jsonObject) throws BaseException {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            Iterator<?> it = jsonObject.keys();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = jsonObject.get(key) == null || "".equals(jsonObject.get(key).toString()) ? "" : jsonObject.get(key).toString();
                data.put(key, value);
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JSON_TO_MAP, "json转map失败,需要转换的json为:" + jsonObject, LogUtil.EMPTY, e);
        }
        return data;
    }
    /**
     * map转化为JSON
     * 
     * @param map
     * @return
     * @throws BaseException
     */
    public static JSONObject mapToJSon(Map<String, Object> map) throws BaseException {
        JSONObject json = new JSONObject();
        try {
            Iterator<?> it = map.keySet().iterator();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = map.get(key) == null || "".equals(map.get(key).toString()) ? "" : map.get(key).toString();
                json.put(key, value);
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_MAP_TO_JSON, "MAP转JSON 失败,需要转换的MAP为：" + map, LogUtil.EMPTY, e);
        }
        return json;
    }
    /**
     * map转化为get请求参数，数据格式为：aa=123&bb=234
     * 
     * @param map
     * @return
     * @throws BaseException
     */
    public static String mapToPostStr(Map<String, Object> map) throws BaseException {
        String paramStr = "";
        try {
            Iterator<?> it = map.keySet().iterator();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = map.get(key) == null ? "" : map.get(key).toString();
                paramStr += (key + "=" + value + "&");
            }
            if (!"".equals(paramStr)) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_MAP_TO_GETPARAM, "MAP转String 失败,需要转换的MAP为：" + map, LogUtil.EMPTY, e);
        }
        return paramStr;
    }
    /**
     * map按key值排序(升序) 返回会value字符串
     * 
     * @param map
     * @return
     * @throws BaseException
     */
    public static String mapSortValueToStr(Map<String, Object> map) throws BaseException {
        String paramStr = "";
        try {
            List<Map.Entry<String, Object>> sortSet = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            // 升序排序
            Collections.sort(sortSet, new Comparator<Map.Entry<String, Object>>() {
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return ((o1.getKey()).toString().compareTo(o2.getKey().toString()));
                }
            });
            // 返回升序后的value字符串
            for (Iterator<?> it = sortSet.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                paramStr += entry.getValue();
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_MAP_TOSORTMAP,"map按key值升序排序返回value串失败,需要转换的MAP为：" + map, LogUtil.EMPTY, e);
        }
        return paramStr;
    }
}
