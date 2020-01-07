package com.star.common.exception;

/**
 * Service层基类异常，所有业务模块异常均继承自此类
 * @date 2020年1月7日
 * @version 1.0
 */
public class ServiceException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * 错误码(补充说明：错误信息用Exception的message)
	 */
	private int code;
	
	
	public ServiceException() {
    }
	
	public ServiceException(int code) {
        this.code = code;
    }
	
	public ServiceException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public ServiceException(int code, String message, 
			Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	
	/**
	 * 获取错误状态码
	 * @return
	 */
	public int getCode() {
		return this.code;
	}
	
}
