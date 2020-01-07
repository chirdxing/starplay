package com.star.common.constant;

/**
 * 平台统一异常常量
 * @date 2020年1月7日
 * @version 1.0
 */
public enum ErrorCode {
	
	/* ------------------------------------- 平台异常  ------------------------------------- */
	SUCCESS(0, "正常"),
	
	SERVER_ERROR(-1, "服务器异常"),
	
	USER_NOT_EXISTS(-2, "用户不存在"),
	
	USER_WRONG_PASSWORD(-3, "密码错误"),
	
	USER_LOCKED(-4, "用户帐户被冻结"),
	
	DATA_NOT_EXISTS(-5, "数据不存在"),
	
	USER_ALREADY_EXISTS(-6, "用户名已经存在"),
	
	USER_ACCOUNT_UNCHECKED(-7, "用户账号尚未激活不能登录"),
	
	ILLEGAL_USERNAME(-8, "用户名不合"),
	
	RAND_CODE_ERROR(-10, "验证码错误"),
	
	RAND_CODE_BUSI(-11, "验证码发送过于频繁"),
	
	USER_OR_PASSWORD_ERROR(-12, "您输入的帐号密码有误，请确认！"),
	
	USER_TOKEN_EXPIRE(-13, "用户token已过期"),
	
	USER_TELEPHONE_EXISTS(-14, "手机号码已经被注册");
	
	/* ------------------------------------- 业务模块异常  ------------------------------------- */
	
	
	private int code; // 错误状态码
	
	private String message; // 错误提示信息

	ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
