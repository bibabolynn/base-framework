package com.yuan.util;

public class StringUtil {
    /**
     * 处理较大的消息
     * 
     * @param log
     * @param big
     * @return
     */
    public static String handerBigLog(String log, int big) {

        if (log == null) {
            return "";
        }
        if (log.length() > big) {
            return log.substring(0, big);
        }
        return log;
    }

    /**
     * 将emoji表情替换成*
     *
     * @param source
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source) {
        if (source == null) {
            return null;
        }
        source = source.replaceAll("[^\\u0000-\\uFFFF]", "*");
        return source;
    }

}
