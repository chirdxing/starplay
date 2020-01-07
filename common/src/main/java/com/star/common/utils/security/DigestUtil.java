package com.star.common.utils.security;

import java.security.MessageDigest;

/**
 * 【消息摘要算法加密】
 * 使用java的原生类java.security.MessageDigest实现数字签名算法MD5和SHA
 * 生成的是二进制数组，需要转化为十六进制（Hex）、MD5返回32为字符，SHA返回40位字符
 * 两者都是用hash散列计算出来，MD5速度快但抗攻击强大不如SHA-1
 * 【hash算法很牛叉的】
 * @date 2020年1月7日
 * @version 1.0
 */
public class DigestUtil {
	private static final String MD5 = "MD5";
	private static final String SHA = "SHA-1";
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * 将二进制数组转化为十六进制字符串
	 * @param byteArray
	 * @return
	 */
	public static String byteArrayToHex(byte[] byteArray) {
    	StringBuffer sb = new StringBuffer(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++) {
            sb.append(Character.forDigit((byteArray[i] & 240) >> 4, 16)); 
            sb.append(Character.forDigit(byteArray[i] & 15, 16));
        }
        return sb.toString();
    }

	/**
	 * 摘要算法加密
	 * @param algorithm 算法类型
	 * @param content 加密内容
	 * @param charset 加密内容编码
	 * @return 算法出错或编码出错返回null
	 */
	public static String digestToHex(String algorithm, String content, String charset) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			if (null == charset || "".equals(charset)) {
				// 不传参默认就是UTF-8编码
				md.update(content.getBytes());
			} else {
				md.update(content.getBytes(charset));
			}
			return byteArrayToHex(md.digest());
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String md5ToHex(String content, String charset) {
		return digestToHex(MD5, content, charset);
	}
	
	public static String md5ToHex(String content) {
		return digestToHex(MD5, content, DEFAULT_CHARSET);
	}
	
	public static String sha1ToHex(String content, String charset) {
		return digestToHex(SHA, content, charset);
	}
	
	public static String sha1ToHex(String content) {
		return digestToHex(SHA, content, DEFAULT_CHARSET);
	}
	
	
//	public static void main(String[] args) {
//		String content = "中文test";
//		System.out.println(md5ToHex(content));
//		System.out.println(md5ToHex(content, "GBK"));
//		
//		System.out.println(sha1ToHex(content));
//		System.out.println(sha1ToHex(content, "GBK"));
//	}
	
}
