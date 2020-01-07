package com.star.common.utils.validator;

import java.util.HashMap;
import java.util.Map;

public class IDValidator {
	private static Map<String, IDCodeInfo> cache = new HashMap<String, IDCodeInfo>();

	/**
	 * 验证身份证是否有效
	 * @param id 身份证号码
	 * @return
	 */
	public boolean isValid(String id) {
		IDCodeInfo code = Utils.checkArg(id);
		if (code == null) {
			return false;
		}
		// 查询cache
		if (cache.containsKey(id)) {
			return cache.get(id).isValid();
		}

		Utils.parseCode(code);

		if (!(Utils.checkAddr(code.getAddrCode())
				&& Utils.checkBirth(code.getBirthCode()) && Utils
					.checkOrder(code.getOrder()))) {
			code.setValid(false);
			cache.put(id, code);
			return false;
		}

		// 15位不含校验码
		if (code.getType() == 15) {
			code.setValid(true);
			cache.put(id, code);
			return true;
		}

		// 校验位部分，位置加权
		int[] posWeight = new int[17];
		for (int i = 18; i > 1; i--) {
			int wei = Utils.weight(i);
			posWeight[18 - i] = wei;
		}

		// 累加body部分与位置加权的积
		int bodySum = 0;
		String[] bodyArr = code.getBody().split("");
		for (int j = 0; j < bodyArr.length; j++) {
			bodySum += (Integer.valueOf(bodyArr[j], 10) * posWeight[j]);
		}

		// 得出校验码
		int tempCheckBit = 12 - (bodySum % 11);
		String checkBit = String.valueOf(tempCheckBit);
		if (tempCheckBit == 10) {
			checkBit = "X";
		} else if (tempCheckBit > 10) {
			checkBit = String.valueOf(tempCheckBit % 11);
		}

		// 查验校验码
		if (!checkBit.equals(code.getCheckBit())) {
			code.setValid(false);
			cache.put(id, code);
			return false;
		} else {
			code.setValid(true);
			cache.put(id, code);
			return true;
		}
	}
}
