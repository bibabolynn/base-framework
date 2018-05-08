package com.yuan.util.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.StringUtil;
import com.yuan.util.constant.PubErrorCode;
import com.yuan.vo.HttpRes;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


public class HttpClientUtil {

	private static final Log Logger = LogFactory.getLog(HttpClientUtil.class);

	public static ThreadLocal<HttpClientContext> LOCAL = new ThreadLocal<HttpClientContext>();
	
	public static ThreadLocal<Boolean> ISPROXY = new ThreadLocal<Boolean>();
	/**
	 * 默认连接超时时间
	 */
	private  static int DEFAULT_CONNECTION_TIME_OUT = 10000;
	/**
	 *
	 */
	private  static int DEFAULT_SOCKET_TIME_OUT = 60000;

	/***
	 * 默认字符编码
	 */
	private final static String DEFAULT_CHARSET_UTF_8 = "UTF-8";

	
	/**http的post请求【不带文件】
     * @param param：参数
     * @param url：请求地址
     * @return
     * @throws BaseException
     */
    public static HttpRes post(Map<String, String> param, String url) throws BaseException {
        String res = "";
        int code = 0;
        HttpResponse response = null;
        HttpPost post = null;
        try {
            /*
             * 1、获取客户端连接
             */
            HttpClient httpClient = getHttpClient();
            /*
             * 2、设置http请求头信息
             */
            HttpContext context = LOCAL.get();
            post = new HttpPost(url);
            post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            post.setHeader("Accept", "*/*");
            post.setHeader(HTTP.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            /*
             * 3、把请求参数拼接成post参数格式
             * 注：数据拼接成=和&符号
             */
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (param != null) {
                Set<Entry<String, String>> entrySet = param.entrySet();
                Iterator<Entry<String, String>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> next = iterator.next();
                    params.add(new BasicNameValuePair(next.getKey(), next.getValue()));
                }
            }
            /*
             * 4、发送请求，获取响应码和响应内容
             */
            post.setEntity(new UrlEncodedFormEntity(params, DEFAULT_CHARSET_UTF_8));
            response = httpClient.execute(post, context);
            code = response.getStatusLine().getStatusCode();
            Logger.debug("Headers-->" + Arrays.toString(response.getAllHeaders()));
            res = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET_UTF_8);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_HTTP_CLIENT_POST, "http请求异常,响应码:" + code + ",错误信息:"+e.getMessage()+ ",响应内容:" + StringUtil.handerBigLog(res,1000 ), e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        Logger.info("HTTP:url地址:[" + url + "],参数:[" + param + "],响应内容:[" + StringUtil.handerBigLog(res,1000 ) + "], 响应码:" + code);
        return new HttpRes(code, res);
    }
    
    /**http模式浏览器发送post请求，带有文件
     * @param param ：请求内容
     * @param binaryParam：文件map
     * @param url
     * @return
     * @throws BaseException
     */
    public static HttpRes post(Map<String, String> param, Map<String, File> binaryParam, String url)throws BaseException {
        HttpPost post = new HttpPost(url);
        post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        post.setHeader("Accept", "*/*");
        post.setHeader(HTTP.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null, Charset.forName("UTF-8"));
        if (binaryParam != null) {
            Set<Entry<String, File>> entrySet = binaryParam.entrySet();
            Iterator<Entry<String, File>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<String, File> next = iterator.next();
                FileBody file = new FileBody(next.getValue());
                reqEntity.addPart(next.getKey(), file);
            }

        }
        if (param != null) {
            Set<Entry<String, String>> entrySet = param.entrySet();
            Iterator<Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<String, String> next = iterator.next();
                // 创建待处理的表单域内容文本
                StringBody bodyValue = null;
                try {
                    bodyValue = new StringBody(next.getValue(), Charset.forName(DEFAULT_CHARSET_UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Logger.warn("HTTP:异常," + e.getMessage(), e);
                }
                reqEntity.addPart(next.getKey(), bodyValue);
            }
        }
        String res = "";
        int code = 0;
        try {
            post.setEntity(reqEntity);
            HttpContext context = LOCAL.get();
            HttpResponse response = getHttpClient().execute(post, context);
            code = response.getStatusLine().getStatusCode();
            Logger.debug("Headers-->" + Arrays.toString(response.getAllHeaders()));
            res = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET_UTF_8);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_HTTP_CLIENT_POST_FILE, "http请求异常,响应码:" + code + ",错误信息:"+e.getMessage()+ ",响应内容:" + StringUtil.handerBigLog(res,1000 ), e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        Logger.info("HTTP:url地址:[" + url + "],参数:[" + param + "],响应内容:[" + StringUtil.handerBigLog(res,1000 ) + "], 响应码:" + code);
        return new HttpRes(code, res);
    }
	
	/**获取连接客户端
	 * @return
	 * @throws BaseException
	 */
	public static HttpClient getHttpClient() throws BaseException {
	    HttpClient httpClient = null;
	    try{
	        /*
	         * 1、设置连接数
	         */
	        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	        cm.setMaxTotal(200); // 连接池最大生成连接数200
	        cm.setDefaultMaxPerRoute(20);// 默认设置route最大连接数为20
	        /*
	         * 2、设置连接超时时间，如果系统参数配置超时时间以配置为准，如没有配置以默认为准
	         */
	        String default_connection_time_out = SysParamUtil.getSysParValue("CONNECTION_TIME_OUT");
	        if(StringUtils.isNotBlank(default_connection_time_out)){
	            DEFAULT_CONNECTION_TIME_OUT = Integer.valueOf(default_connection_time_out);
	        }
	        /*
	         * 3、设置请求超时时间，如果系统参数配置超时时间以配置为准，如没有配置以默认为准
	         */
	        String default_socket_time_out = SysParamUtil.getSysParValue("SOCKET_TIME_OUT");
	        if(StringUtils.isNotBlank(default_socket_time_out)){
	            DEFAULT_SOCKET_TIME_OUT = Integer.valueOf(default_socket_time_out);
	        }
	        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIME_OUT)
	                .setConnectTimeout(DEFAULT_CONNECTION_TIME_OUT).build();// 设置请求和传输超时时间
	        /*
	         * 4、获取连接客户端
	         */
	        HttpClientBuilder builder = HttpClients.custom();
	        httpClient =  builder.setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
	    }catch(Exception e){
	        throw LogUtil.handerEx(PubErrorCode.PUB_HTTP_CLIENT, "获取http连接客户端失败",LogUtil.EMPTY, e);
	    }
	    return httpClient;
	}
	
	

	public static HttpRes get(String urlStr) throws BaseException {
		return get(null, urlStr);
	}

	public static HttpRes get(Map<String, String> params, String urlStr) throws BaseException {
		return get(params, urlStr, null,null);
	}

	public static HttpRes get(Map<String, String> params, String urlStr, HttpContext context,HttpClient httpClient) throws BaseException {
		String res = "";
		int code = 0;
		HttpGet get = null;
		HttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(urlStr);
			if (params != null) {
				Iterator<Entry<String, String>> itor = params.entrySet().iterator();
				while (itor.hasNext()) {
					Entry<String, String> entry = itor.next();
					String key = entry.getKey();
					String val = entry.getValue();
					builder.addParameter(key, val);
				}
			}

			URI uri = builder.build();
			get = new HttpGet(uri);
			get.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
			get.setHeader("Accept", "*/*");
			get.setHeader(HTTP.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (context == null) {
				context = LOCAL.get();
			}
			HttpClient client = null;			
			if(httpClient==null){
				client = getHttpClient();
			}else{
				client = httpClient;
			}
			response = client.execute(get, context);
			code = response.getStatusLine().getStatusCode();
			Logger.debug("Headers-->" + Arrays.toString(response.getAllHeaders()));
			res = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET_UTF_8);
			Logger.info("HTTP-GET:url地址:[" + urlStr + "],参数:[" + params + ", 响应码:[" + code + "],响应内容:[" + StringUtil.handerBigLog(res,1000 )+ "]");
			HttpRes vo = new HttpRes(code, res);
			return vo;
		} catch (Exception e) {
			Logger.warn("HTTP:异常,url地址:[" + urlStr + "],参数:[" + params + "],异常原因:" + e.getMessage(), e);
			throw new BaseException(PubErrorCode.PUB_HTTP_CLIENT_GET, "http请求异常,响应码:" + code+ ",错误信息:"+e.getMessage() + ",响应内容:" + StringUtil.handerBigLog(res,1000 ), e);
		}
	}
}
