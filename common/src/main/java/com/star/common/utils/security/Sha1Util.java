package com.star.common.utils.security;

import java.security.MessageDigest;
import java.util.Arrays;

/**
* @author star
* @version 1.0
  @date 2017年6月17日 上午11:29:51
* 类说明 :
*/
public class Sha1Util {
	
	/**
	 * 字符串排序
	 * @param strParam
	 * @return
	 */
	private static String sortStr(String strParam) {
		char[] strs = strParam.toCharArray();
		// 从小到大排序
		Arrays.sort(strs);
		String result = "";
		for(int i = 0; i < strs.length; i++) {
			// 取偶数位的值
			if (i % 2 == 0) {
				result = result + strs[i];
			}
		}
		return result;
	}
	
	/**
	 * sha1加密
	 * @param str
	 * @return
	 */
	public static String getSha1(String str){
        if(str==null||str.length()==0){
            return null;
        }
        // 排序
        String result = sortStr(str);
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
                'a','b','c','d','e','f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(result.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                // 把密文转换成十六进制的字符串形式
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];      
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }
}
