package com.star.common.constant;

/**
 * 用户账号类型
 * @date 2020年1月7日
 * @version 1.0
 */
public enum UserType {
	
	MOBILE_USER((short) 1, "手机用户");
	
	
	private short type;

	private String desc;
	
	private UserType(short type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	public short getType() {
		return this.type;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public static UserType fromType(short type) {
		for (UserType userType : values()) {
			if (userType.getType() == type) {
				return userType;
			}
		}
		return null;
	}
	
}
