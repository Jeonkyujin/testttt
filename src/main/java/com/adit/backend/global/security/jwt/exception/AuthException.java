package com.adit.backend.global.security.jwt.exception;

import com.adit.backend.global.error.GlobalErrorCode;
import com.adit.backend.global.error.exception.BusinessException;

public class AuthException extends BusinessException {
	public AuthException(GlobalErrorCode errorCode) {
		super(errorCode);
	}

}