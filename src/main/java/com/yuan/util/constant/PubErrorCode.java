package com.yuan.util.constant;

public class PubErrorCode {
	/*
	 * 1、公共异常
	 */
	public static final String PUB_EXCEPTION = "000000";// 公共异常
	public static final String PUB_REQUEST = "000001";// 浏览器请求拦截异常
	public static final String PUB_REQUEST_RIGHT = "000002";// 请求权限异常
	public static final String PUB_EXCEPTION_OTHER = "999999";// 未知的异常
	public static final String PUB_INITIA = "000003";// 初始化参数失败
	public static final String PUB_QUARTZ_ERROR = "000004";// 执行定时调度失败
	public static final String PUB_QUERY_SYSPAR_MAP = "000005";// 查询系统参数成map失败
	public static final String PUB_QUERY_SYSPAR_JSON = "000006";// 查询系统参数成json失败
	public static final String PUB_QUERY_APPSYS_MAP = "000007";// 查询应用参数转换成map失败
	public static final String PUB_QUERY_SYS_ONE = "000007";// 查询单个系统参数失败
	public static final String PUB_QUERY_APP_ONE = "000007";// 查询单个应用参数失败
    public static final String PUB_PAGE_SEVE_SIZE="000010";//保存当前页码数据Controller错误
    public static final String PUB_PAGE_DATA_SIZE="000011";//当前页码数据Controller错误
    
    public static final String ERROR_SYSTEM_LIST = "000012";// 查询系统参数列表Controller出错
    public static final String ERROR_SYSTEMPAR_SERVICE = "000013";// 查询系统参数列表service出错
    public static final String ERROR_UPDATE_SYSTEMPAR = "000014";// 修改系统参数Controller出错
    public static final String ERROR_UPDATE_SYSTEMPAR_SERVICE = "000015";// 修改系统参数service出错
    public static final String ERROR_APP_LIST = "000016";// 查询应用参数列表controller出错
    public static final String ERROR_APPPAR_SERVICE = "000017";// 查询应用参数列表Service出错
    public static final String ERROR_UPDATE_APPPAR = "000018";// 修改应用参数Controller出错
    public static final String ERROR_UPDATE_APPPAR_SERVICE = "000019";// 修改系统参数service出错
	/*
	 * 2、通信类异常
	 */
	public static final String PUB_SERVER_REQUEST = "PUB_SERVER_REQUEST";// 浏览器请求拦截异常
	public static final String PUB_SERVER_PARAM = "PUB_SERVER_PARAM";// 处理服务器端请求参数异常
	public static final String PUB_AES_ENCRYPT = "000008";// AES加密失败
	public static final String PUB_AES_DECRYPT = "000009";// AES解密失败
	public static final String PUB_AES_KEY = "000010";// AES密钥失败
	public static final String PUB_AES_DECRYPT_KEY = "000011";// AES带有key加密失败
	public static final String PUB_IRREVERSIBLE_ENCRYPT = "000012";// 不可逆加密失败
	public static final String PUB_HTTP_CLIENT_POST = "000013";// http通信失败
	public static final String PUB_HTTP_CLIENT_POST_FILE = "000014";//带有文件 http传输失败
	public static final String PUB_HTTP_CLIENT_GET = "000015";//HTT的get请求失败
	public static final String PUB_RETCODE_ERROR = "PUB_RETCODE_ERROR";//HTT请求约定成功标记错误
	public static final String PUB_RETCODE_EXCEPTION = "PUB_RETCODE_EXCEPTION";//HTT请求约定成功标记异常
	public static final String PUB_DATA_FORMAT = "PUB_DATA_FORMAT";//HTT响应的数据格式不正确
	public static final String PUB_HTTP_400 = "PUB_HTTP_400";//HTT请求400的错误
	public static final String PUB_HTTP_504 = "PUB_HTTP_504";//HTT请求504的错误
	public static final String PUB_HTTP_OTHERS = "PUB_HTTP_OTHERS";//HTT请求内部错误
	public static final String PUB_HTTP_REQUST_POST = "PUB_HTTP_REQUST_POST";//http请求失败
	public static final String PUB_REQUEST_PARAM = "PUB_REQUEST_PARAM";//解析request成字符串数据失败
	public static final String PUB_REQUEST_COLSE = "PUB_REQUEST_COLSE";//关闭request解析的解析失败
	public static final String PUB_RESPONSE_PARAM = "PUB_RESPONSE_PARAM";//http响应失败
	public static final String PUB_REQUEST_CHECK =  "PUB_REQUEST_CHECK";//验证请求报文失败
	public static final String PUB_SIGN_ERROR =  "PUB_SIGN_ERROR";//摘要验证不正确
	public static final String PUB_SIGN_EXCEPTION =  "PUB_SIGN_EXCEPTION";//摘要验证失败
	public static final String PUB_HTTP_CLIENT =  "PUB_HTTP_CLIENT";//获取http的客户端失败
	/*
	 * 3、java工具类异常
	 */
	public static final String PUB_JAVA_UTIL = "PUB_JAVA_UTIL"; //java工具失败
    public static final String PUB_REQUEST_MAP = "PUB_REQUEST_MAP"; //request转换成map失败
    public static final String PUB_DEL_FOLDER = "PUB_DEL_FOLDER"; //删除文件夹下的所有文件
    public static final String PUB_RIMKEY = "PUB_RIMKEY"; //生成主键失败
    public static final String PUB_RIMKEY_SERVICE = "PUB_RIMKEY_SERVICE"; //生成主键service失败
    public static final String PUB_DOWN_EXCEL = "000019";//导出EXCEL失败
    public static final String PUB_TEMP_BUILD = "000020";//创建word的模板文件失败
    public static final String PUB_CLOSE_TEMP = "000021";//关闭word的模板流失败
    public static final String PUB_LOAD_TEMP = "000022";//加载temp失败
    public static final String PUB_DOWN_FILE =  "000026";//文件下载失败
    public static final String PUB_FILEINPUT_DOWN =  "000027";//下载文件关闭输入流失败
    public static final String PUB_FILEOUTPUT_DOWN =  "000028";//下载文件关闭输入流失败
    public static final String PUB_IMG_COMPRESS = "000028";//压缩图片失败
    public static final String PUB_REFLECT = "PUB_REFLECT";//反射调用失败
    public static final String PUB_FILE_UPLOAD = "PUB_FILE_UPLOAD";//文件上传失败
    public static final String PUB_EXCEL_ROW = "PUB_EXCEL_ROW";//读取excel的列值失败
    public static final String PUB_EXCEL_ANALYSIS = "PUB_EXCEL_ANALYSIS";//解析excel失败
	/*
	 * 4、ftp异常
	 */
    public static final String ERROR_PUBLIC_FTP = "FTP00001";// ftp异常
    public static final String ERROR_PUBLIC_FTP_UP = "FTP00002";// ftp上传异常
    public static final String ERROR_PUBLIC_FTP_DIS = "FTP00003";// ftp销货失败
    public static final String ERROR_PUBLIC_FTP_CF = "FTP00004";// ftp创建文件失败
    public static final String ERROR_PUBLIC_FTP_CAC = "FTP00005";// FTP断开连接异常
    public static final String ERROR_PUBLIC_FTP_CON = "FTP00006";// FTP连接失败
    public static final String ERROR_PUBLIC_FTP_LOG = "FTP00007";// FTP登录异常
    public static final String ERROR_PUBLIC_FTP_COE = "FTP00008";// FTP连接异常
    /*
     * 5、数据转换异常
     */
    public static final String PUB_BEAN_TO_MAP = "PUB_BEAN_TO_MAP";// JAVAbean转换成map失败
    public static final String PUB_BEAN_TO_JSON = "PUB_BEAN_TO_JSON";// JAVAbean转换成json失败
    public static final String PUB_JSON_TO_BEAN = "PUB_JSON_TO_BEAN";// JSON转换成JAVEBEAN
    public static final String PUB_JSON_TO_MAP  = "PUB_JSON_TO_MAP";// JSON转换成MAP
    public static final String PUB_MAP_TO_JSON  = "PUB_MAP_TO_JSON";// map转换成json
    public static final String PUB_MAP_TO_GETPARAM  = "PUB_MAP_TO_GETPARAM";// map转换成GET请求参数
    public static final String PUB_MAP_TOSORTMAP  = "PUB_MAP_TOSORTMAP";// map转换成升序后的map
	/*
	 * 6、redis参数管理异常
	 */
	public static final String PUB_REDIS_INIT = "000004";// 初始化redis参数失败
	public static final String PUB_REDIS_INIT_SYS = "000005";// 初始化redis系统参数失败
	public static final String PUB_REDIS_INIT_APP = "000006";// 初始化redis应用参数失败
	public static final String PUB_REDIS_GET_SYS = "000007";// 获取系统参数失败
	public static final String PUB_REDIS_GET_DBSYS = "000008";// 获取数据库系统参数失败
	public static final String PUB_REDIS_GET_APP = "000009";// 获取应用参数失败
	public static final String PUB_REDIS_GET_DBAPP = "000010";// 获取数据库应用参数失败
	public static final String PUB_REDIS_GET_APPMAP = "000011";// 获取应用参数集合失败
	public static final String ERROR_PUT_REDIS_QUEUE = "000012"; //存放redis队列失败
	public static final String ERROR_PUT_REDIS_QUEUE_PARAM = "000013"; //存放队列参数失败
	public static final String ERROR_REDISQUEUE = "000014"; //消费队列失败
	public static final String ERROR_REDIS_QUERY = "000015"; //查询redis失败
	public static final String ERROR_REDIS_SET = "000016"; //存放redis指定时间失败
	public static final String ERROR_REDIS_DEL_SOME = "000017"; //删除redis中一组key的值失败
	public static final String ERROR_REDIS_DEL_ONE = "000018"; //删除redis中一个key的值失败
	public static final String ERROR_REDIS_EXISTS = "000019"; //验证redis中key是否存在失败
	public static final String ERROR_REDIS_QUEUE_PUB = "ERROR_REDIS_QUEUE_PUB"; //存放队列公共方法是把
	public static final String ERROR_REDIS_QUEUE_BUSS = "ERROR_REDIS_QUEUE_BUSS"; //存放某业务的队列失败
	/*
	 * 7、业务逻辑异常[业务逻辑001开头，第四位：control：0开头，service：1开头，后两位代表码值
	 */
	public static final String ERROR_LOGIN_CONTROL = "001001"; //登陆失败
	public static final String ERROR_LOGIN_SERVICE = "001101"; //登陆失败
}
