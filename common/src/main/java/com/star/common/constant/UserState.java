package com.star.common.constant;

/**
 * 用户账号状态
 * @date 2020年1月7日
 * @version 1.0
 */
public enum UserState {

	UNCHECKED_STATE((short) 0, "账号未激活"),
	
	RUNING_STATE((short) 1, "账号正常使用中！"),
	
	FROZEN_STATE((short) 2, "账号已经被冻结！"),
	
	DELETE_STATE((short) 3, "账号已经被删除！");
	
	
	private short state;
	
	private String message;
	
	private UserState(short state, String message) {
		this.state = state;
		this.message = message;
	}
	
	
	public short getState() {
		return this.state;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public static UserState fromState(short state) {
		for (UserState userState : values()) {
			if (userState.getState() == state) {
				return userState;
			}
		}
		return null;
	}
	
}
