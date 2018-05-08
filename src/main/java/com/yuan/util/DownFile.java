package com.yuan.util;


import com.yuan.util.constant.PubErrorCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class DownFile {
    /**
     * 文件下载 注：参数Map，存放了文件存储名称，文件实际名称，已经文件所在的文件夹路径
     * 
     * @param path : 文件所在路径
     * @param fileName ： 文件名称
     * @param titleName ： 文件下载显示名称
     * @param response ： http响应
     * @param fileType ： 响应文件类型（例如 "image/*)
     * @return
     * @throws BaseException
     */
    public static boolean downFile(String path, String fileName,String titleName, HttpServletResponse response, String fileType) throws BaseException {
        boolean flag = true;
        OutputStream output = null;
        FileInputStream fis = null;
        try {
            File file = new File(path + fileName);
            if (file.canRead()) { // 判断文件是否存在
                response.reset();
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + new String(titleName.getBytes("utf-8"), "iso8859-1"));
                if (fileType != null) {
                    response.setContentType(fileType + ";charset=UTF-8");
                }
                output = response.getOutputStream();
                fis = new FileInputStream(file);
                byte[] b = new byte[1024];
                int i = 0;
                while ((i = fis.read(b, 0, b.length)) != -1) {
                    output.write(b, 0, i);
                }
                output.flush();
                response.flushBuffer();
            } else {
                flag = false;
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_DOWN_FILE, "下载文件失败,文件地址为："+(path+fileName), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw LogUtil.handerEx(PubErrorCode.PUB_FILEINPUT_DOWN, "下载文件时关闭FileInputStream流失败！",e);
                }
                fis = null;
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw LogUtil.handerEx(PubErrorCode.PUB_FILEOUTPUT_DOWN, "下载文件时关闭OutputStream流失败！", e);
                }
                output = null;
            }
        }
        return flag;
    }
}
