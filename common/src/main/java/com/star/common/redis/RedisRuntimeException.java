package com.star.common.redis;

public class RedisRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RedisRuntimeException() {
		super();
	}

	public RedisRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RedisRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisRuntimeException(String message) {
		super(message);
	}

	public RedisRuntimeException(Throwable cause) {
		super(cause);
	}

}
