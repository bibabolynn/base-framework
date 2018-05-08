package com.yuan.util;


import com.yuan.util.constant.PubErrorCode;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

/**
 * @author apple
 *
 */
public class ImgUtil {

    /**
     * 压缩文件
     * 
     * @param file2
     * @param descFilePath
     * @return
     * @throws IOException
     */
    public static void compressPic(File file2, String descFilePath) throws BaseException {
        FileOutputStream out = null;
        ImageWriter imgWrier;
        try {
            
            BufferedImage src = null;
            ImageWriteParam imgWriteParams;
            LogUtil.info("传入的文件信息为" + file2 == null ? "" : file2 + "，文件名为：" + file2 == null ? "" : file2.getName());
            Image image = ImageIO.read(file2);
            String fileName = file2.getName();
            String extendName = fileName.split("\\.")[1];
            long fileLen = file2.length();
            long srcHeight = image.getHeight(null);
            long srcWidth = image.getWidth(null);
            float quality = getff(fileLen, srcHeight, srcWidth);
            if (fileLen < (100 * 1024)) {
                quality = (float) 0.5;
            }
            imgWrier = ImageIO.getImageWritersByFormatName(extendName).next();// 指定写图片的方式为 jpg
            imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
            imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
            // 这里指定压缩的程度，参数qality是取值0~1范围内，
            imgWriteParams.setCompressionQuality(quality);
            imgWriteParams.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
            File file =  file2;
            src = ImageIO.read(file);
            out = new FileOutputStream(descFilePath);
            imgWrier.reset();
            // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
            // OutputStream构造
            imgWrier.setOutput(ImageIO.createImageOutputStream(out));
            imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);// 调用write方法，就可以向输入流写图片
            out.flush();
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_IMG_COMPRESS, "压缩图片失败，文件为：" + file2, LogUtil.EMPTY, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw LogUtil.handerEx(PubErrorCode.PUB_IMG_COMPRESS, "压缩图片关闭流失败", LogUtil.EMPTY, e);
                }
            }
        }
    }

    public static float getff(long fileLen, double srcHeight, double srcWidth) {
        float result = 0;
        double x = fileLen / 1024.0 / 64.0;
        result = (float) Math.pow((1 / x), 1.0 / 3);
        return result;
    }

    public static void main(String[] args) throws BaseException {
        File file = new File("C:/Users/admin/Desktop/图片/1A.jpg");
        compressPic(file, "C:/Users/admin/Desktop/image/1A.jpg");

    }

}
