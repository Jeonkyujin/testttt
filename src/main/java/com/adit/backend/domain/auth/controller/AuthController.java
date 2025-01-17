package com.adit.backend.domain.auth.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.auth.dto.request.KakaoRequest;
import com.adit.backend.domain.auth.dto.response.KakaoResponse;
import com.adit.backend.domain.auth.service.command.AuthCommandService;
import com.adit.backend.domain.user.dto.response.UserResponse;
import com.adit.backend.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {
	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	private final AuthCommandService authCommandService;

	@Operation(summary = "카카오 회원가입/로그인 요청")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 Redirect"),
	})
	@GetMapping("/kakao")
	public void kakaoLogin(HttpServletResponse response) throws IOException {
		String kakaoAuthUrl = authCommandService.createKakaoAuthorizationUrl();
		response.sendRedirect(kakaoAuthUrl);
	}

	@Operation(summary = "카카오 회원가입/로그인 결과", description = "request는 https로 요청되기 때문에 때문에 로컬환경, SSL 인증 전 CORS 오류 존재, 직접 링크를 작성하여 접속")
	@Parameter(name = "code", description = "카카오 인가 코드", required = true)
	@GetMapping("/join")
	public ResponseEntity<ApiResponse<UserResponse.InfoDto>> joinAuth(KakaoRequest.AuthDto request,
		HttpServletResponse response) {
		return ResponseEntity.ok(
			ApiResponse.success(authCommandService.joinAuth(request.code(), response)));
	}

	@Operation(summary = "카카오 토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<KakaoResponse.AccessTokenDto>> reissueToken(
		@CookieValue(name = "refreshToken") KakaoRequest.RefreshTokenDto request, HttpServletResponse response) {
		return ResponseEntity.ok(
			ApiResponse.success(authCommandService.reIssueKakaoToken(request.refreshToken(), response)));
	}

	@Operation(summary = "카카오 로그아웃")
	@DeleteMapping("/logout")
	@SecurityRequirement(name = "accessTokenAuth")
	public ResponseEntity<ApiResponse<KakaoResponse.UserIdDto>> logout(
		@RequestHeader(ACCESS_TOKEN_HEADER) KakaoRequest.AccessTokenDto request, HttpServletResponse response) {
		return ResponseEntity.ok(ApiResponse.success(authCommandService.logout(request.accessToken(), response)));
	}

}