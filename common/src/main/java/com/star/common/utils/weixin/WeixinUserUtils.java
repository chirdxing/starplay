package com.star.common.utils.weixin;

import com.google.common.base.Strings;

public class WeixinUserUtils {
	
	/**
	 * 把头像转换为微信最小头像
	 * @param headImgUrl
	 * @return
	 */
	public static String toSmallSize(String headImgUrl){
		if (!Strings.isNullOrEmpty(headImgUrl) && !headImgUrl.endsWith("64")) {
			// 避免误处理，有些用户没有头像，地址是这样的： http://7jppma.com2.z0.glb.qiniucdn.com/50c7793d269759ee053c2cbab1fb43166d22df14.jpg
			try {
				if (headImgUrl.length() - headImgUrl.lastIndexOf("/") > 5) {
					return headImgUrl;
				}
				return headImgUrl.substring(0, headImgUrl.lastIndexOf("/")) + "/64";
			} catch (Exception e) {
				return headImgUrl;
			}
		}
		return headImgUrl;
	}
	
}
