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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {
	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";
	private final AuthCommandService authCommandService;

	@GetMapping("/kakao")
	public ResponseEntity<ApiResponse<String>> kakaoLogin(HttpServletResponse response) throws IOException {
		String kakaoAuthUrl = authCommandService.createKakaoAuthorizationUrl();
		response.sendRedirect(kakaoAuthUrl);
		return ResponseEntity.ok(ApiResponse.success("카카오 로그인 페이지 로딩 성공"));
	}

	@GetMapping("/join")
	public ResponseEntity<ApiResponse<UserResponse.InfoDto>> joinAuth(KakaoRequest.AuthDto request,
		HttpServletResponse response) {
		return ResponseEntity.ok(
			ApiResponse.success(authCommandService.joinAuth(request.code(), response)));
	}

	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<KakaoResponse.AccessTokenDto>> reissueToken(
		@CookieValue(name = "refreshToken") KakaoRequest.RefreshTokenDto request, HttpServletResponse response) {
		return ResponseEntity.ok(
			ApiResponse.success(authCommandService.reIssueKakaoToken(request.refreshToken(), response)));
	}

	@DeleteMapping("/logout")
	@SecurityRequirement(name = "accessTokenAuth")
	public ResponseEntity<ApiResponse<KakaoResponse.UserIdDto>> logout(
		@RequestHeader(ACCESS_TOKEN_HEADER) KakaoRequest.AccessTokenDto request) {
		return ResponseEntity.ok(ApiResponse.success(authCommandService.logout(request.accessToken())));
	}

}