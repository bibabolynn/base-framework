package com.yuan.util;


import com.yuan.util.constant.PubErrorCode;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * FTP文件上传
 * 
 *
 */
public class FtpFileUploadUtil {

    private String ip;// FTP服务器的IP
    private int port;// FTP服务器的端口
    private String userName;// FTP服务器的userName
    private String pwd;// FTP服务器的pwd

    public FtpFileUploadUtil(String ip, int port, String userName, String pwd) {
        super();
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.pwd = pwd;
    }

    /**
     * ftp上传文件到指定路径下
     * 
     * @param fileAllPath:本地文件路径（全路径）+ 文件名称
     * @param saveFilePath：服务器文件路径 （注：去除FTP用户的根路径，每个用户都会有个根路径）
     * @param saveFileName：服务器文件名
     * @throws BaseException
     */
    public void ftpUpload(String fileAllPath, String saveFilePath, String saveFileName) throws BaseException {
        FTPClient ftp = null;
        try {
            ftp = this.connectFtp(ip, port, userName, pwd); // 获取FTP连接
            this.fileUpload(fileAllPath, saveFilePath, saveFileName, ftp); // 上传文件
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP, "FTP处理异常，文件名：" + fileAllPath, LogUtil.EMPTY, e);
        }
    }

    /**
     * FTP上传单个文件 localFilePath:本地文件路径，包含文件名 webFilePath:服务器文件路径 fileName：服务器存储文件名
     * 
     * @throws BaseException
     */
    public void fileUpload(String localFilePath, String webFilePath, String fileName, FTPClient ftpClient) throws BaseException {
        FileInputStream fis = null;
        boolean success = false;
        try {
            ftpClient.enterLocalPassiveMode(); // 设置被动模式
            File srcFile = new File(localFilePath); // 获得本地文件
            fis = new FileInputStream(srcFile);
            this.creditWebPath(ftpClient, webFilePath); // 服务器上创建路径
            ftpClient.changeWorkingDirectory(webFilePath); // cd到该目录下
            ftpClient.setBufferSize(1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件类型（二进制）
            String newFileName = new String(fileName.getBytes("gbk"), "iso-8859-1");
            success = ftpClient.storeFile(newFileName, fis);
            LogUtil.info(fileName + "的文件上传结果为：" + success);
        } catch (IOException e) {
            throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_UP, "FTP上传文件失败，参数文件名：" + localFilePath + webFilePath + ",存储文件名：" + fileName,LogUtil.EMPTY, e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
                throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_DIS, "FTP销毁失败，参数文件名：" + localFilePath + webFilePath + ",存储文件名：" + fileName,LogUtil.EMPTY, e);
            }
        }
    }

    /**
     * FTP创建制定路径的文件
     * 
     * @param ftp
     * @param dir
     * @throws BaseException
     */
    public void creditWebPath(FTPClient ftp, String dir) throws BaseException {
        try {
            StringBuffer fullDirectory = new StringBuffer();
            StringTokenizer toke = new StringTokenizer(dir, "/");
            while (toke.hasMoreElements()) {
                String currentDirectory = (String) toke.nextElement();
                fullDirectory.append(currentDirectory);
                ftp.makeDirectory(fullDirectory.toString());
                if (toke.hasMoreElements()) {
                    fullDirectory.append('/');
                }
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_CF, "FTP创建文件失败，参数文件：" + dir, LogUtil.EMPTY, e);
        }
    }
    /**
     * 建立FTP连接
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @return
     * @throws BaseException
     */
    public FTPClient connectFtp(String host, int port, String username, String password) throws BaseException {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.setConnectTimeout(60 * 1000);
            ftpClient.setDefaultTimeout(5 * 60 * 1000);
            // ftpClient.setSoTimeout(60*1000);
            ftpClient.setDataTimeout(5 * 60 * 1000);
            ftpClient.connect(host, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_CON, "FTP服务器连接失败", LogUtil.EMPTY);
            }
            Boolean loginFlag = ftpClient.login(username, password);
            if (!loginFlag) {
                throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_LOG, "FTP服务器登录失败", LogUtil.EMPTY, null);
            }
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_COE, "FTP服务器连接异常", LogUtil.EMPTY, e);
        }
        return ftpClient;
    }

    /**
     * 断开连接
     * 
     * @param ftpClient
     * @throws BaseException
     */
    public void cancelConnect(FTPClient ftpClient) throws BaseException {
        try {
            if (ftpClient != null) {
                ftpClient.logout();
            }
        } catch (Exception e) {
            LogUtil.handerEx(PubErrorCode.ERROR_PUBLIC_FTP_CAC, "FTP断开连接异常", LogUtil.ERROR, e);
        }
    }
}