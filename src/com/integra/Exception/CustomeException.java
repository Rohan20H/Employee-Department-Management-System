package com.integra.Exception;

public class CustomeException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String DATABASE_FILE_NOT_FOUND = "Database File Not Found";
	public static final String IO_EXCEPTION_OCCURRED = "Io exception";
	public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occured";
	public static final String PROPERTIES_NOT_LOADED = "Properties not found";
	public static final String NOT_READABLE = "File cannot be readed";

	public static final String ERR_CREATING_CONNECTION = "Not Connected";

	public static final String ERR_INVALID_PROPERTIES = "properties are invalid";

	public static final String ERR_LOADING_PROPERTIES = "properties not loaded";

	public static final String ERROR_INVALID_DB_CONFIG = "Invalid db configuration";

	public String message;
	public String errorCode;
	public Throwable cause;

	public CustomeException(String message, String errorCode, Throwable cause) {
		this.message = message;
		this.errorCode = errorCode;
		this.cause = cause;

	}

	public CustomeException(String message, String errorCode) {
		this.message = message;
		this.errorCode = errorCode;

	}

	public CustomeException(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;

	}

	public CustomeException(String message) {
		super(message);
	}

}
