package com.yuan.util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import com.yuan.util.constant.Constants;
import com.yuan.util.constant.PubErrorCode;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.io.FileUtils;



import freemarker.template.utility.StringUtil;

/* 1、两数计算结果四舍五入（例为除）:getNumTwo
 * 2、过滤特殊字符：StringFilter
 * 3、得到GBK编码：toGBK
 * 4、创建文件路径，已存在返回true：createDirectory
 * 5、数字转换成大写：digitUppercase
 * 6、字符串转换成日期：transferString
 * 7、日期转换成字符串：DateToString
 * 8、计算两个日期相隔的天数：computeDays
 * 9、用于判断是数字，还是汉字或者名称：isInt
 * 10、根据根据日期算出，该月的总天数：getDayByDate
 * 11、根据一个日期，修改已有的日期链;setPayDate
 * 12、根据文件夹路径获取文件内文件结合；FileList
 * 13、复制文件；copyfile
 * 14、返回指定编码格式的输入流：inputReaderIo
 * 15、返回指定编码格式的输出流：outputWriterIo
 * 16、获取系统日期的前count天:  getBeforeDate
 * 17、封装request成map对象：getMap
 * 18、指定日期，加上相应的类型（年月日），返还新的日期
 * 19、金额进行格式化处理，去除财务记法及万元转换成元
 * 20、MD5生成
 * 21、得到所有区域的名称
 * 注：后面为方法名
 */
public class JavaUtil {
    /*
     * 1、保留两位小数，并且四舍五入
     */
    public Object getNumTwo(float value1, float value2) {
        NumberFormat fm = NumberFormat.getNumberInstance();
        fm.setMaximumFractionDigits(2); // 保留2为小数，四舍五入
        fm.setGroupingUsed(false); // true为采用用逗号分开的形式；false为不采用
        fm.setMinimumIntegerDigits(1); // 整数位数最少0位
        return fm.format(value1 / value2);
    }
    /*
     * 2、过滤特殊字符
     */
    public static String StringFilter(String str) throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";// 清除掉所有特殊字符
        // String regEx = "[\r\n|\r|\n|\n\r)]"; // 去掉回车
        // errorMessage = errorMessage.replaceAll("(\r\n|\r|\n|\n\r)", " ");
        // //替换回车
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
    /*
     * 3、得到GBK字符编码
     */
    public static String toGBK(String str) throws BaseException {
        String gbk = "";
        if (str != null) {
            try {
                gbk = new String(str.getBytes("ISO_8859_1"), "GBK");
            } catch (UnsupportedEncodingException e) {
                throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "得到GBK字符编码失败：" + str, LogUtil.EMPTY, e);
            }
        }
        return gbk;
    }
    /*
     * 4、创建文件路径,如果文件不存在就创建，如果存在就返回true
     */
    public static boolean createDirectory(String path) throws BaseException {
        boolean flag = true;
        try {
            File wf = new File(path);
            if (!wf.exists()) {
                wf.mkdirs();
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "创建文件路径失败，文件路径为：" + path, LogUtil.EMPTY, e);
        }
        return flag;
    }
    /*
     * 5、数字转大写
     */
    public static String digitUppercase(double v) {
        String UNIT = "万仟佰拾亿仟佰拾万仟佰拾元角分";
        String DIGIT = "零壹贰叁肆伍陆柒捌玖";
        double MAX_VALUE = 9999999999999.99D;
        if (v < 0 || v > MAX_VALUE) {
            return "参数非法!";
        }
        long l = Math.round(v * 100);
        if (l == 0) {
            return "零元整";
        }
        String strValue = l + "";
        // i用来控制数
        int i = 0;
        // j用来控制单位
        int j = UNIT.length() - strValue.length();
        String rs = "";
        boolean isZero = false;
        for (; i < strValue.length(); i++, j++) {
            char ch = strValue.charAt(i);
            if (ch == '0') {
                isZero = true;
                if (UNIT.charAt(j) == '亿' || UNIT.charAt(j) == '万' || UNIT.charAt(j) == '元') {
                    rs = rs + UNIT.charAt(j);
                    isZero = false;
                }
            } else {
                if (isZero) {
                    rs = rs + "零";
                    isZero = false;
                }
                rs = rs + DIGIT.charAt(ch - '0') + UNIT.charAt(j);
            }
        }
        if (!rs.endsWith("分")) {
            rs = rs + "整";
        }
        rs = rs.replaceAll("亿万", "亿");
        return rs;
    }
    /*
     * 6、字符串转换成日期
     */
    public static Date StringToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     * 7、日期转换成字符串
     * 
     * @param date 日期对象
     * @param pattern 转换成字符串格式 例如："yyyyMMddHHmmss"
     * @return
     * @throws BaseException
     */
    public static String DateToString(Date date, String pattern) throws BaseException {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "日期转换成字符串失败", LogUtil.EMPTY, e);
        }
    }
    /*
     * 8、计算两个日期相隔的天数，不分顺序，
     */
    public static int computeDays(Date date1, Date date2) throws BaseException {
        // 计算date1
        String day1 = DateToString(date1, "yyyyMMddHHmmss"); // 把日期转换成这种格式的字符串。注：如果参数与传的是字符串无需转换
        String day2 = DateToString(date2, "yyyyMMddHHmmss");
        if (day1 == null || day2 == null) {
            return -1;
        }
        day1 = day1.trim();
        day2 = day2.trim();
        if ("".equals(day1) || "".equals(day2)) {
            return -1;
        }
        if (day1.length() != 14 || day2.length() != 14) {
            return -1;
        }
        int num = 0;
        String y1 = day1.substring(0, 4); // 年
        String m1 = day1.substring(4, 6); // 月
        String d1 = day1.substring(6, 8); // 日
        String h1 = day1.substring(8, 10); // 时
        String me1 = day1.substring(10, 12); // 分
        String s1 = day1.substring(12, 14); // 秒

        String y2 = day2.substring(0, 4);
        String m2 = day2.substring(4, 6);
        String d2 = day2.substring(6, 8);
        String h2 = day2.substring(8, 10); // 时
        String me2 = day2.substring(10, 12); // 分
        String s2 = day2.substring(12, 14); // 秒
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(Integer.parseInt(y1), Integer.parseInt(m1) - 1, Integer.parseInt(d1), Integer.parseInt(h1), Integer.parseInt(me1),
                Integer.parseInt(s1));
        c2.set(Integer.parseInt(y2), Integer.parseInt(m2) - 1, Integer.parseInt(d2), Integer.parseInt(h2), Integer.parseInt(me2),
                Integer.parseInt(s2));
        if (c1.after(c2)) {
            for (; c2.before(c1);) {
                c2.add(Calendar.DATE, 1);
                num++;
            }
        } else {
            for (; c1.before(c2);) {
                c1.add(Calendar.DATE, 1);
                num++;
            }
        }
        return num;
    }

    /*
     * 9、用于判断是数字，还是汉字或者名称
     */
    public static boolean isInt(String paramter) {
        char[] c = paramter.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] < '0' || c[i] > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 10、根据根据日期算出，该月的总天数
     */
    public int getDayByDate(Date dateTest) {
        int year = dateTest.getYear();
        int month = dateTest.getMonth() + 1;
        int day = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            case 2:
                if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                    day = 29;
                } else {
                    day = 28;
                }
                break;
        }
        return day;
    }

    /*
     * 11、根据一个日期，修改已有的日期链 例如原有日期：2012年5月1日，6月1日，7月1日。。。。，修改成5月10日、6月10日。。。。。。 注：一般用事物进行修改
     */
    public void setPayDate(Date date) {
        Map outputMap = new HashMap();
        List<Map> paylines = null;
        try {
            int leaseTerm = 2; // 还款的周期，例如是两个月一次
            paylines = null; // 支付明细表,sql查询出，原来的支付表信息
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int index = 0;
            Date endDate = null;
            for (Map payline : paylines) {
                calendar.setTime(date);
                int num = 0;
                if (payline.get("PERIOD_NUM") != null) {
                    num = Integer.parseInt(payline.get("PERIOD_NUM").toString());
                }
                if (index != 0) {
                    calendar.add(Calendar.MONTH, leaseTerm * (num - 1));
                }
                payline.put("PAY_DATE", calendar.getTime());
                endDate = calendar.getTime();
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getPostfix(String path) {
        if (path == null || "".equals(path.trim())) {
            return "";
        }
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf(".") + 1,path.length());
        }
        return "";
    }
    /*
     * 12、根据文件夹路径获取文件内文件结合 参数说明：文件夹路径
     */
    public File[] FileList(String filePath) {
        File foldFile = new File(filePath);
        File[] fileList = null;
        boolean fileExit = foldFile.exists();
        if (!fileExit || !foldFile.isDirectory() || !foldFile.canRead()) {
            try {
                foldFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fileList = foldFile.listFiles();
        }
        return fileList;
    }

    /*
     * 13、复制文件 filePath:为原文件路径 copyPath：目的文件路径
     */
    public static void copyfile(String filePath, String copyPath) {
        File sPath = new File(filePath);
        File dPath = new File(copyPath);
        BufferedInputStream bread = null;
        BufferedOutputStream bwrite = null;
        try {
            bread = new BufferedInputStream(new FileInputStream(sPath));
            bwrite = new BufferedOutputStream(new FileOutputStream(dPath));
            int index = 0;
            while ((index = bread.read()) != -1) {
                bwrite.write(index);
            }
        } catch (IOException ex) {

        } finally {
            try {
                bread.close();
                bwrite.close();
            } catch (IOException ex) {

            }
        }
    }

    /*
     * 14、返回指定编码格式的输入流 file:要读取的文件 codeing：编码格式
     */
    public BufferedReader inputReaderIo(File file, String codeing) {
        BufferedReader freader = null;
        try {
            freader = new BufferedReader(new InputStreamReader(new FileInputStream(file), codeing));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return freader;
    }

    /*
     * 15、返回指定编码格式的输出流 file:要读取的文件 codeing：编码格式
     */
    public BufferedWriter outputWriterIo(File file, String codeing) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), codeing));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return writer;
    }

    /*
     * 16、获取系统日期的前count天
     */
    public String getBeforeDate(Date date, int count) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - count);
        return df.format(c.getTime());
    }
    /*
     * 17、封装request成map对象 参数名作为key，参数值作为value
     */
    public static Map<String, Object> getMap(HttpServletRequest request) throws BaseException {
        Map<String, Object> bm = new HashMap<String, Object>();
        try {
            Map<String, String[]> tmp = request.getParameterMap();
            if (tmp != null) {
                for (String key : tmp.keySet()) {
                    key = key.trim();
                    String[] values = tmp.get(key);
                    Object obj = values.length == 1 ? values[0].trim() : values;
                    bm.put(key, obj == null ? "" : obj);
                }
            }
            int currentPage =
                    ((bm.get("currentPage") == null || "".equals(bm.get("currentPage"))) ? 1 : Integer.parseInt(bm.get("currentPage").toString())); // 如果当前页为空则赋值1
            bm.put("currentPage", currentPage);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "封装request成map对象失败", LogUtil.EMPTY, e);
        }
        return bm;
    }

    /**
     * data 数据 将格式为format1格式的字符串，转为format2格式。
     * 
     * @param data ：日期值
     * @param format1 ：被转换格式
     * @param format2 ：转换格式
     * @return
     * @throws BaseException
     */
    public static String stringToDateString(String data, String format1, String format2) throws BaseException {
        String dateResult = null;
        try {
            if (data == null || "".equals(data)) { // 如果日期为空
                return dateResult;
            } else {
                SimpleDateFormat sdf1 = new SimpleDateFormat(format1);
                SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
                dateResult = sdf2.format(sdf1.parse(data));
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "日期为：" + data + ",由这个" + format1 + "格式转换成" + format2 + "失败", LogUtil.EMPTY, e);
        }
        return dateResult;
    }
    /**
     * 指定日期，期限单位，期限计算日期
     * 
     * @param beginDate 指定日期
     * @param type 期限单位  // 1：月，2：年，3：日
     * @param value 期限
     * @param pattern 日期格式，例如yyyyMMddHHmmss,默认：yyyyMMddHHmmss
     * @return
     * @throws ParseException
     * @throws BaseException
     */
    public static String dateTerm(Date beginDate, int type, int value,String pattern) throws ParseException, BaseException {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDate);
            if (type == 1) {
                calendar.add(Calendar.MONTH, value);
            } else if (type == 2) {
                calendar.add(Calendar.YEAR, value);
            } else if (type == 3) {
                calendar.add(Calendar.DATE, value);
            }
            return DateToString(calendar.getTime(), pattern==null?"yyyyMMddHHmmss":pattern);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "日期为" + beginDate + "计算类型为：" + type + "加上" + value + "失败", e);
        }
    }

    /**
     * 格式化金额，单位由元转换成万元
     * 
     * @param amount 金额
     * @return
     * @throws BaseException
     */
    public String formatAmount(Object amount) throws BaseException {
        LogUtil.info("需要格式化的金额值为：" + amount);
        String amountStr = "0";
        try {
            if (amount != null) {
                BigDecimal big = new BigDecimal(amount == null || "".equals(amount) ? "0" : amount.toString().replace(",", ""));
                big = big.multiply(new BigDecimal(10000));
                amountStr = big.toString();
            }
            return amountStr;
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "格式化金额失败，格式化的金额为："+amount, e);
        }
    }
    /**
     * 将金额从元单位转换成万元
     * 
     * @return
     * @throws BaseException
     */
    public static BigDecimal divideAmt(BigDecimal amt) throws BaseException {
        BigDecimal bd = null;
        try {
            amt = (amt == null || "".equals(amt)) ? new BigDecimal("0") : amt;
            bd = amt.divide(new BigDecimal(10000));
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "将金额从元单位转换成万元失败,需要转换的金额值为：" + amt, e);
        }
        return bd;
    }

    /**
     * 去掉右侧的0 如果没有小数点直接返回
     * 
     * @param str
     * @param rpx
     * @return
     */
    public static String trimUtil(String str, char rpx) {
        int len = str.length();
        int st = 0;
        char[] val = str.toCharArray();
        /*
         * while ((st < len) && (val[st] == rpx)) { st++; }
         */
        while ((st < len) && (val[len - 1] == rpx)) {
            len--;
        }
        String strVal = ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
        if (strVal.lastIndexOf(".") == strVal.length() - 1)
            strVal = strVal + "00";
        return strVal;

    }


    /**
     * 判断map键是否存在
     * 
     * @author wuyonghui
     */
    public static boolean isExist(String key, Map map) {
        boolean bool = false;
        Set keys = map.keySet();
        if (keys != null) {
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Object keyI = iterator.next();
                if (keyI.equals(key)) {
                    bool = true;
                }
            }
        }
        return bool;
    }

    /**
     * 根据URL下载文件
     * 
     * @param url 地址
     * @param dir 本地路径
     * @return
     */
    public static File downloadFromUrl(String url, String dir) {
        File f = null;
        try {
            URL httpurl = new URL(url);
            String fileSaveName = JavaUtil.DateToString(new Date(), "yyyyMMddhhmmss") + ".txt";
            f = new File(dir + fileSaveName);
            FileUtils.copyURLToFile(httpurl, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 获取文件名
     * 
     * @param url 文件地址
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        String name = new Long(System.currentTimeMillis()).toString() + ".X";
        int index = url.lastIndexOf("/");
        if (index > 0) {
            name = url.substring(index + 1);
            if (name.trim().length() > 0) {
                return name;
            }
        }
        return name;
    }

    // 获取当天n点
    public static Date getInstanceTime(String n) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(n));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = new Date((calendar.getTimeInMillis() / 1000) * 1000);
        return date;
    }

    /**
     * 支持十亿以下转化为简体中文
     * 
     * @param n
     * @return
     */
    public static String UpperToSimple(int n) {
        String fraction[] = { "角", "分" };
        String digit[] = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String unit[][] = { { "元", "万", "亿" }, { "", "十", "百", "千" } };
        String head = n < 0 ? "负" : "";
        n = Math.abs(n);
        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1) {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head
                + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整").replaceAll("整", "")
                        .replaceAll("元", "");
    }


    public static int getMonth(String date1, String date2) throws ParseException, BaseException {
        int result = 0;
        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMddHHmmss");
        /*
         * int num = 14-date1.length(); for(int i=0;i<num;i++){ date1+="0"; }
         */
        date1 = StringUtil.rightPad(date1, 14, "0");
        int days = Math.abs(computeDays(Format.parse(date1), Format.parse(date2)));
        double day = days / Double.valueOf(30);
        result = (int) Math.ceil(day);
        result = result == 0 ? 1 : result;
        return result;
    }

    /**
     * 删除文件夹 以及文件夹下的所有内容
     * 
     * @param folderPath
     */
    public static void delFolder(String folderPath) throws BaseException {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "删除文件夹 以及文件夹下的所有内容失败 参数：" + folderPath, LogUtil.EMPTY, e);
        }
    }

    /**
     * 删除指定文件夹下所有文件 param path 文件夹完整绝对路径
     * 
     * @param path
     * @return
     * @throws BaseException
     */
    public static boolean delAllFile(String path) throws BaseException {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获得日期的下一个星期一的日期
     *
     * @param date
     * @return
     */
    public static Calendar getNextMonday(Calendar date) {
        Calendar result = null;
        result = date;
        do {
            result = (Calendar) result.clone();
            result.add(Calendar.DATE, 1);
        } while (result.get(Calendar.DAY_OF_WEEK) != 2);
        return result;
    }

    /**
     * 获取两个日期之间相隔的自然日天数 两个参数先后顺序无所谓，默认d2>d1 ,如果d1>d2 会自动互换d1，d2
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static int getDaysBetween(Calendar d1, Calendar d2) {
        // swap dates so that d1 is start and d2 is end
        if (d1.after(d2)) {
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    /**
     * 计算2个日期之间的相隔的自然日天数 结果 >=0 两个参数先后顺序无所谓，默认d2>d1 ,如果d1>d2 会自动互换d1，d2
     * 
     * @param d1 date类型
     * @param d2 date类型
     * @return
     */
    public static int getDaysBetween(Date d1, Date d2) {
        int result = 0;
        if (d1 != null && d2 != null) {
            Calendar cl1 = Calendar.getInstance();
            cl1.setTime(d1);
            Calendar cl2 = Calendar.getInstance();
            cl2.setTime(d2);
            result = getDaysBetween(cl1, cl2);
        }
        return result;
    }

    /**
     * 计算2个日期之间的相隔的工作日天数 结果 >=0 两个参数先后顺序无所谓，默认d2>d1 ,如果d1>d2 会自动互换d1，d2
     * 
     * @param d1 Calendar类型
     * @param d2 Calendar类型
     * @return
     */
    public static int getWorkingDay(Calendar d1, Calendar d2) {
        int result = -1;
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int charge_start_date = 0;// 开始日期的日期偏移量
        int charge_end_date = 0;// 结束日期的日期偏移量
        // 日期不在同一个日期内
        int stmp;
        int etmp;
        stmp = 7 - d1.get(Calendar.DAY_OF_WEEK);
        etmp = 7 - d2.get(Calendar.DAY_OF_WEEK);
        if (stmp != 0 && stmp != 6) {// 开始日期为星期六和星期日时偏移量为0
            charge_start_date = stmp - 1;
        }
        if (etmp != 0 && etmp != 6) {// 结束日期为星期六和星期日时偏移量为0
            charge_end_date = etmp - 1;
        }
        result = (getDaysBetween(getNextMonday(d1), getNextMonday(d2)) / 7) * 5 + charge_start_date - charge_end_date;
        return result;
    }

    /**
     * 计算2个日期之间的相隔的工作日天数 结果 >=0 两个参数先后顺序无所谓，默认d2>d1 ,如果d1>d2 会自动互换d1，d2
     * 
     * @param d1 date类型
     * @param d2 date类型
     * @return
     */
    public static int getWorkingDay(Date d1, Date d2) {
        int result = 0;
        if (d1 != null && d2 != null) {
            Calendar cl1 = Calendar.getInstance();
            cl1.setTime(d1);
            Calendar cl2 = Calendar.getInstance();
            cl2.setTime(d2);
            getWorkingDay(cl1, cl2);
        }
        return result;
    }

    /**
     * 根据标记类型 和大写数值，获取对应位数的大写值
     * 
     * @param flag //参数类型 ：0 :个位 1：十位 2：百位，3：千位；4：十分位；5：百分位
     * @param uppVal 传递的大写数值 小于1万
     * @return
     */
    public static String getUpperAFlag(String flag, String uppVal) {

        String result = "零";

        if (flag != null && !"".equals(flag) && uppVal != null && !"".equals(uppVal)) {
            // 千位值
            if ("3".equals(flag) && uppVal.contains("仟")) {
                result = uppVal.substring(0, uppVal.indexOf("仟"));
            } else if ("2".equals(flag)) { // 百位值
                if (uppVal.contains("仟") && uppVal.contains("佰")) {
                    result = uppVal.substring(uppVal.indexOf("仟") + 1, uppVal.indexOf("佰"));
                } else if (!uppVal.contains("仟") && uppVal.contains("佰")) {
                    result = uppVal.substring(0, uppVal.indexOf("佰"));
                }
            } else if ("1".equals(flag)) { // 十位值
                if (uppVal.contains("佰") && uppVal.contains("拾")) {
                    result = uppVal.substring(uppVal.indexOf("佰") + 1, uppVal.indexOf("拾"));
                } else if (!uppVal.contains("佰") && uppVal.contains("拾")) {
                    if (uppVal.contains("仟")) {
                        result = uppVal.substring(uppVal.indexOf("仟") + 2, uppVal.indexOf("拾"));
                    } else {
                        result = uppVal.substring(0, uppVal.indexOf("拾"));
                    }
                }
            } else if ("0".equals(flag)) {// 个位值
                result = uppVal.substring(uppVal.length() - 1);
                if ("仟".equals(result) || "佰".equals(result) || "拾".equals(result)) {
                    result = "零";
                }
            } else if ("4".equals(flag) && uppVal.contains("角")) { // 十分位
                result = uppVal.substring(0, uppVal.indexOf("角"));
            } else if ("5".equals(flag)) { // 百分位
                if (!uppVal.contains("角") && uppVal.contains("分")) {
                    result = uppVal.substring(0, uppVal.indexOf("分"));
                } else if (uppVal.contains("角") && uppVal.contains("分")) {
                    result = uppVal.substring(uppVal.indexOf("角") + 1, uppVal.indexOf("分"));
                }
            }
        }

        return result;
    }

    /**
     * 创建ZIP文件
     * 
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) throws BaseException {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "创建ZIP文件失败，文件路径："+sourcePath, LogUtil.EMPTY, e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "创建ZIP文件失败，文件路径："+sourcePath, LogUtil.EMPTY, e);
            }
        }
    }
    /**
     * 把制定文件下的所有文件进行zip压缩
     * @param file : 某个文件或者文件夹
     * @param parentPath：父级目录
     * @param zos
     * @throws BaseException
     */
    private static void writeZip(File file, String parentPath, ZipOutputStream zos) throws BaseException {
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                if (file.isDirectory()) {// 处理文件夹
                    parentPath += file.getName() + File.separator;
                    File[] files = file.listFiles();
                    for (File f : files) {
                        writeZip(f, parentPath, zos);
                    }
                } else {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                       zos.write(content, 0, len);
                       zos.flush();
                    }
                }
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "压缩某个文件失败", LogUtil.EMPTY, e);
        }finally{
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "关闭压缩文件流失败", LogUtil.EMPTY, e);
            }
        }
    }
    /**
     * 取得两个日期之间相隔年，向下取整
     * 
     * @param dateStart 精确到日
     * @param dateEnd 精确到日
     * @return
     */

    public static int completeYears(Date dateStart, Date dateEnd) {
        int year = 0;
        int yearStart = dateStart.getYear();
        int monthStart = dateStart.getMonth();
        int dayStart = dateStart.getDate();
        int yearEnd = dateEnd.getYear();
        int monthEnd = dateEnd.getMonth();
        int dayEnd = dateEnd.getDate();
        year = yearEnd - yearStart;
        if ((monthEnd - monthStart) < 0) {
            year = year - 1;
        } else {
            if ((dayEnd - dayStart) < 0) {
                year = year - 1;
            }
        }
        return year;
    }

    /**
     * 比较两个日期， 若date1>date2 ，返回true
     * 
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static boolean compareDate(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        boolean flag = false;
        if (sdf.parse(date1).getTime() > sdf.parse(date2).getTime()) { // date1>date2
            flag = true;
        }
        return flag;
    }


    /**
     * 处理null或空值
     * 
     * @param objValue 源对象
     * @param repalceValue 要替换的字符串
     * @return 返回字符串
     */
    public static String noeConverStr(Object objValue, String repalceValue) {
        if (objValue == null || objValue.toString().length() == 0) {
            objValue = repalceValue;
        }
        return objValue.toString();
    }

    /**
     * 返回字符串 四位一空格
     * 
     * @param orgStr 源字符串
     * @return
     */
    public static String fourEmpty(Object orgStr, int scal) {
        if (orgStr == null || orgStr.toString().length() == 0) {
            return "";
        }
        char c[] = orgStr.toString().toCharArray();
        String resultVal = "";
        for (int i = 0; i < c.length; i++) {
            if (i % scal == 0 && i != 0) {
                resultVal += " " + c[i];
            } else {
                resultVal += c[i];
            }
        }
        return resultVal;
    }

    /*
     * 生成验证码
     */
    public static String identifyCode() {
        String str = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
        String str2[] = str.split(",");// 将字符串以,分割
        Random rand = new Random();// 创建Random类的对象rand
        int index = 0;
        String randStr = "";// 创建内容为空字符串对象randStr
        randStr = "";// 清空字符串对象randStr中的值
        for (int i = 0; i < 4; ++i) {
            index = rand.nextInt(str2.length - 1);// 在0到str2.length-1生成一个伪随机数赋值给index
            randStr += str2[index];// 将对应索引的数组与randStr的变量值相连接
        }
        return randStr;
    }

    /**
     * 两个实体之间转化空值处理
     * 
     * @return
     */
    public static BeanUtilsBean registerDefaultValue() {
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
        beanUtilsBean.getConvertUtils().register(new org.apache.commons.beanutils.converters.BigDecimalConverter(null), BigDecimal.class);
        beanUtilsBean.getConvertUtils().register(new org.apache.commons.beanutils.converters.DateConverter(null), Date.class);
        return beanUtilsBean;
    }

    /**
     * 把第二个map的值合并到另外第一个map中
     * 
     * @param map
     * @param param
     * @param filter:不需要合并 数据格式为key|key1|key2
     * @return
     */
    public static Map<String, Object> mergeMap(Map<String, Object> map, Map<String, Object> param, String filter) {
        if (param != null) {
            String[] filterArray = {};
            if (filter != null) {
                filterArray = filter.split("|");
            }
            for (String key : param.keySet()) {
                key = key.trim();
                boolean putFlag = false; // 是否过滤，true过滤
                for (int i = 0; i < filterArray.length; i++) {
                    String keyFilter = filterArray[i];
                    putFlag = key.equals(keyFilter);
                }
                if (!putFlag) {
                    Object value = param.get(key) == null ? null : param.get(key);
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * 计算两个日期之间相差的毫秒数
     * 
     * @param frontDate
     * @param afterDate
     * @return
     * @throws ParseException
     */
    public static long dateDiff(String frontDate, String afterDate) throws BaseException {
        long diffSec = 0;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
            long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
            long nh = 1000 * 60 * 60;// 一小时的毫秒数
            long nm = 1000 * 60;// 一分钟的毫秒数
            long ns = 1000;// 一秒钟的毫秒数
            long diff;
            diff = sd.parse(afterDate).getTime() - sd.parse(frontDate).getTime();
            long day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒//输出结果
            diffSec = sec + min * 60 + hour * 3600 + day * 24 * 3600;
            if (diffSec < 0) {
                diffSec = 0 - diffSec;
            }
        } catch (Exception e) {
            throw LogUtil.handerEx("ERROR_AMONG_DATE", "计算两个日期相隔的毫米数失败,参数为:" + frontDate + "|" + afterDate, LogUtil.EMPTY, e);
        }
        return diffSec;
    }
    /**反射工具类
     * @param className:类名
     * @param methodName：方法名
     * @param contJson：方法的执行参数
     * @return
     * @throws BaseException
     */
    public static Object reflectUtil(String className,String methodName,JSONObject contJson) throws BaseException{
        Object result = null;
        try{
//            Class<?> clz = Class.forName(className); //加载类
//            Object otargetObject = ClusterQuartzJobProvider.getBean(clz); //获取bean
//            Method method = otargetObject.getClass().getMethod(methodName,new Class[] { String.class }); //获取动态执行方法
//            result = method.invoke(otargetObject, new Object[] {contJson.toString() });
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "反射调用失败，参数为:"+ className + "|"+methodName, LogUtil.EMPTY, e);
        }
        return result;
    }

    /**
     * 将一个bean转换为Map
     * 
     * @param bean
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Map<String, Object> modelToMap(Object bean) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = bean.getClass().getDeclaredFields();
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (Field field : fields) {
            field.setAccessible(true);
            data.put(field.getName(), field.get(bean));
        }
        return data;
    }

    /**
     * 将一个Object类型转换为Map
     * 
     * @param type
     * @param map
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static Object convertMap(Class type, Map map) throws IntrospectionException, IllegalAccessException, InstantiationException,
            InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象
        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }
    
    /**获取session中的用户，获取不到默认系统自动
     * @return
     * @throws BaseException
     */
    public static String getSessionOpNo() throws BaseException{
        String operatorNo = null;
        try{
            operatorNo = SessionUtil.getCurrentUser()==null? Constants.OP_NO:SessionUtil.getCurrentUser().getUserId();
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "获取session中操作员编号出错", LogUtil.EMPTY, e);
        }
        return operatorNo;
    }
    /**获取session中的用户，获取不到默认系统自动
     * @return
     * @throws BaseException
     */
    public static String getSessionOpName() throws BaseException{
        String opName = null;
        try{
            opName = SessionUtil.getCurrentUser()==null?Constants.OP_NAME:SessionUtil.getCurrentUser().getUserName();
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_JAVA_UTIL, "获取session中操作员姓名出错", LogUtil.EMPTY, e);
        }
        return opName;
    }
}
