package com.star.common.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 【AES加解密算法】 -- 代替DES算法
 * 对称加密算法AES，可逆的加密算法，目前SSL（google暴露不安全，要用第三版TSL了）中常用的加密算法
 * AES理论上支持128位、192位、256位秘钥，但JDK7只实现了128位，
 * 如果需要256位秘钥调用组件bouncycastle（下载新的jar包覆盖jdk的security下的包）实现。
 * AES加密后得到的二进制流用Base64算法（sum.msic包有长度限制且私有，建议用common-codec包的api）编码，方便网络传输
 * @date 2020年1月7日
 * @version 1.0
 */
public class AESUtil {
	// 秘钥生成指定AES算法
	private static final String KEY_ALGORITHM = "AES";
	// ECB模式较简单，但容易被攻击，CBC模式不容易被攻击
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	// 使用CBC模式需要初始化IV，且必须是16位的
	private static final String IV_KEY = "1029384756012581";
	// 默认编码
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * 通过AES算法对明文加密并返回base64编码的密文
	 * @param key 秘钥
	 * @param content 明文
	 * @param charset 编码
	 * @return
	 * @throws Exception
	 */
    private static String encryptToBase64(String key, String content, 
    		String charset) throws Exception {
        SecretKeySpec skeySpec = getKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(IV_KEY.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(charset));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 先将base64密文解码，再通过AES算法解密出明文
     * @param key
     * @param content
     * @param charset
     * @return
     * @throws Exception
     */
    private static String decryptFromBase64(String key, String content, 
    		String charset) throws Exception {
        SecretKeySpec skeySpec = getKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(IV_KEY.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = Base64.decodeBase64(content);
        return new String(cipher.doFinal(encrypted1), charset);
    }
    
    /**
     * 转化字符串秘钥为AES秘钥（即便字符串秘钥没有16位也没有关系）
     * @param key 秘钥字符串
     * @return
     * @throws Exception
     */
    private static SecretKeySpec getKey(String key) throws Exception {
    	// 创建一个空的16位字节数组（默认值为0）
        byte[] keyBytes = new byte[16];
        byte[] bTmp = key.getBytes();
        for (int i = 0; i < bTmp.length && i < keyBytes.length; i++) {
        	keyBytes[i] = bTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        return skeySpec;
    }
    
    /**
     * 初始化一个128位的AES秘钥
     * @return
     * @throws Exception
     */
    public static SecretKeySpec initKey() throws Exception {
    	// 获取秘钥生成器
    	KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
    	// 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
    	keyGenerator.init(128);
    	// 生成密钥
    	SecretKey secretKey = keyGenerator.generateKey();
    	SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        return skeySpec;
    }
    
    public static String encrypt(String key, String content) throws Exception {
    	return encryptToBase64(key, content, DEFAULT_CHARSET);
    }
    
    public static String encrypt(String key, String content, String charset) throws Exception {
    	return encryptToBase64(key, content, charset);
    }
    
    public static String decrypt(String key, String content) throws Exception {
    	return decryptFromBase64(key, content, DEFAULT_CHARSET);
    }
    
    public static String decrypt(String key, String content, String charset) throws Exception {
    	return decryptFromBase64(key, content, charset);
    }
    
    
//    public static void main(String[] args) {
//    	try {
//        	String content = "{'name': '太阳之光', 'password': '123456', 'sex':'true'}";
//        	String key = "5d41402abc4b2a76"; // 16位秘钥
//        	System.out.println("AES算法加密前的明文：" + content);
//        	System.out.println("AES算法加密用的秘钥：" + key);
//    		
//			String encryptStr = encrypt(key, content, DEFAULT_CHARSET);
//			System.out.println("AES算法加密后的密文：" + encryptStr);
//			
//			System.out.println("AES算法加密后的密文：" + encrypt("5d41402abc4b3476", content, DEFAULT_CHARSET));
//			String decryptStr = decrypt(key, encryptStr, DEFAULT_CHARSET);
//			System.out.println("AES算法解密后的明文：" + decryptStr);
//		} catch (Exception e) {
//		}
//	}
	
}
