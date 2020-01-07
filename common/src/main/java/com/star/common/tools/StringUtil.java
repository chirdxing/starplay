package com.star.common.tools;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 字符串工具类
 * @date 2020年1月7日
 * @version 1.0
 */
public class StringUtil {
	
	/**
	 * 检查指定的字符串是否为空(包括空白字符串)。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value 待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 截取指定个数的字符
	 * @param src 原字符串
	 * @param len 截取的字符数，eg:20表示20个中文字符或者40个英文字符，当然可以中英混合
	 * @return key[subString]表示截取的字符串，key[len]表示实际截取的字节数
	 */
	public static Map<String, Object> subStringByRealLen(String srcStr, int len) {
		int lenCount = 0;
		int byteLen = len * 2;
		String chinesePattern = "[\u4e00-\u9fa5]";
		StringBuilder targetStr = new StringBuilder(byteLen);
		for (int i = 0; i < srcStr.length(); i++) {
		 	String c = String.valueOf(srcStr.charAt(i));
			if (c.matches(chinesePattern)) {
				if (lenCount + 2 <= byteLen) {
					targetStr.append(c);
					lenCount += 2;
				} else {
					break;
				}
			} else {
				if (lenCount + 1 <= byteLen) {
					targetStr.append(c);
					lenCount += 1;
				} else {
					break;
				}
			}
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("subString", targetStr.toString());
		result.put("len", lenCount);
		return result;
	}
	
	/**
	 * 截取指定个数的字符
	 * @param src 原字符串
	 * @param len 截取的字符数，eg:20表示20个中文字符或者40个英文字符，当然可以中英混合
	 * @return 表示截取的字符串
	 */
	public static String subString(String srcStr, int len) {
		int lenCount = 0;
		int byteLen = len * 2;
		String chinesePattern = "[\u4e00-\u9fa5]";
		StringBuilder targetStr = new StringBuilder(byteLen);
		for (int i = 0; i < srcStr.length(); i++) {
		 	String c = String.valueOf(srcStr.charAt(i));
			if (c.matches(chinesePattern)) {
				if (lenCount + 2 <= byteLen) {
					targetStr.append(c);
					lenCount += 2;
				} else {
					break;
				}
			} else {
				if (lenCount + 1 <= byteLen) {
					targetStr.append(c);
					lenCount += 1;
				} else {
					break;
				}
			}
		}
		return targetStr.toString();
	}
	
}
