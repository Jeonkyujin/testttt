package com.adit.backend.global.error.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.
 * Custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.
 *
 * @author lee
 */

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
	/**
	 * ******************************* Global Error CodeList ***************************************
	 * HTTP Status Code
	 * 400 : Bad Request
	 * 401 : Unauthorized
	 * 403 : Forbidden
	 * 404 : Not Found
	 * 500 : Internal Server Error
	 * *********************************************************************************************
	 */
	// 잘못된 요청
	BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "Bad Request Exception"),

	// @RequestBody 데이터 미 존재
	REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Required request body is missing"),

	// 유효하지 않은 타입
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G003", " Invalid Type Value"),

	// Request Parameter 로 데이터가 전달되지 않을 경우
	MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "G004", "Missing Servlet RequestParameter Exception"),

	// 입력/출력 값이 유효하지 않음
	IO_ERROR(HttpStatus.BAD_REQUEST, "G005", "I/O Exception"),

	// 권한이 없음
	FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G008", "Forbidden Exception"),

	// 서버로 요청한 리소스가 존재하지 않음
	NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G009", "Not Found Exception"),

	// NULL Point Exception 발생
	NULL_POINT_ERROR(HttpStatus.NOT_FOUND, "G010", "Null Point Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_ERROR(HttpStatus.NOT_FOUND, "G011", "handle Validation Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "G012", "Header에 데이터가 존재하지 않는 경우 "),

	// 서버가 처리 할 방법을 모르는 경우 발생
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "Internal Server Error Exception"),

	/**
	 * ******************************* Custom Error CodeList ***************************************
	 */
	// Transaction Insert Error
	INSERT_ERROR(HttpStatus.OK, "9999", "Insert Transaction Error Exception"),

	// Transaction Update Error
	UPDATE_ERROR(HttpStatus.OK, "9999", "Update Transaction Error Exception"),

	// Transaction Delete Error
	DELETE_ERROR(HttpStatus.OK, "9999", "Delete Transaction Error Exception");

	// 에러 코드의 '코드 상태'을 반환한다.
	private final HttpStatus httpStatus;

	// 에러 코드의 '코드간 구분 값'을 반환한다.
	private final String code;

	// 에러 코드의 '코드 메시지'을 반환한다.
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}


