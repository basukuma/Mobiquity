package com.mobiquityinc.exception;

/**
 * The APIException wraps all checked standard Java exception
 * 
 * @author BSukumar
 */
public class APIException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */

	public APIException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public APIException(String message) {
		super(message);

	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of cause
	 *
	 * @param cause
	 *            the cause
	 */
	public APIException(Throwable cause) {
		super(cause);

	}

}
