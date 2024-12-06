package com.adit.global.error.exception;

import org.apache.http.HttpStatus;

public interface ErrorCode {
	String name();

	HttpStatus getHttpStatus();

	String getMessage();
}
