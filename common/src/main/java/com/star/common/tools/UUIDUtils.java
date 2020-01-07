package com.star.common.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 生成加密后的32位UUID
 * @date 2020年1月7日
 * @version 1.0
 */
public class UUIDUtils {
	
	/**
	 * 生成32位UUID字符串
	 * @return
	 */
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
	
	/**
	 * 批量生成指定数量的UUID
	 * @param nums
	 * @return
	 */
	public static List<String> batchGenerateUUID(int nums) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < nums; i++) {
			list.add(generateUUID());
		}
		return list;
	}


	/**
	 * 创建指定数量的随机字符串
	 * @param length 指定数量
	 * @return
	 */
	public static String randomCode(int length) {
		String retStr = "";
		// abcdefghijkmnpqrstuvwxyz
		String strTable = "1234567890";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}

}
