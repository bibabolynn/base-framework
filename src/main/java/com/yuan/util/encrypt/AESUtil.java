package com.yuan.util.encrypt;

/**AES加解密算法
 * */
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * @author apple
 *
 */
@SuppressWarnings("restriction")
public class AESUtil {

	public static final String sKey = "1234567890abcdef";// 加密传输的秘钥

	/**
	 * 加密算法
	 * 1、AES加密，2、BASE64转码
	 * @param sSrc 要加密的内容
	 * @return 加密后的字符串
	 * */
	public static String Encrypt(String sSrc) throws Exception {
		try {
			if (sKey == null) {
				LogUtil.warn("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				LogUtil.warn("Key为空null");
				return null;
			}
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
			IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] content = sSrc.getBytes("UTF-8");
			sSrc = new String(content, "UTF-8");
			System.out.println("sSrc:" + sSrc);
			byte[] encrypted;
			encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
			return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_AES_ENCRYPT, "aes加密失败", LogUtil.EMPTY, e);
		}
		
	}
	/**
	 * 解密算法(S)
	 * 1、BASE64转码 2、AES解密
	 * @param sSrc 要解密的内容
	 * @return 解密后的字符串
	 * */
	public static String Decrypt(String sSrc) throws Exception {
		try {
			// 判断Key是否正确
			if (sKey == null) {
				LogUtil.warn("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				LogUtil.warn("Key为空null");
				return null;
			}
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "UTF-8");
			return originalString;
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_AES_DECRYPT, "后台aes解密失败", LogUtil.EMPTY, e);
		}
	}
	/**
	 * @param sSrc：请求字符串
	 * @param key：解密秘钥
	 * @return
	 * @throws BaseException
	 */
	public static String Decrypt(String sSrc, String key) throws BaseException {
		try {
			// 判断Key是否正确
			if (key == null) {
				LogUtil.error("解密秘钥不正确，值为空");
				throw new BaseException(PubErrorCode.PUB_AES_KEY, "aes解密KEY为空");
			}
			// 判断Key是否为16位
			if (key.length() != 16) {
				throw new BaseException(PubErrorCode.PUB_AES_KEY, "Key长度不是16位，当前key值为：" + key);
			}
			byte[] raw = key.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(key.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "UTF-8");
			return originalString;
		} catch (Exception e) {
			throw LogUtil.handerEx(PubErrorCode.PUB_AES_DECRYPT_KEY, "后台aes带有key值解密错误", LogUtil.EMPTY, e);
		}
	}

}