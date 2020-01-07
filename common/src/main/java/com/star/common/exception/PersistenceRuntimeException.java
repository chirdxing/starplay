package com.star.common.exception;

/**
 * 持久化模块Runtime异常
 */
public class PersistenceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -8883417695491010170L;

	public PersistenceRuntimeException() {
		super();
	}

	public PersistenceRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceRuntimeException(String message) {
		super(message);
	}

	public PersistenceRuntimeException(Throwable cause) {
		super(cause);
	}

}
