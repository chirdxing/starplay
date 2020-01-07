package com.star.common.exception;

/**
 * Dao层异常
 * @date 2020年1月7日
 * @version 1.0
 */
public class DaoException extends Exception{
	private static final long serialVersionUID = 1L;

	
	public DaoException() {
    }
	
	public DaoException(String message) {
		super(message);
	}
	
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
