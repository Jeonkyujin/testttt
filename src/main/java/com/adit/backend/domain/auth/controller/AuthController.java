package com.adit.backend.domain.auth.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.auth.dto.OAuth2UserInfo;
import com.adit.backend.domain.auth.dto.response.KakaoResponse;
import com.adit.backend.domain.auth.service.AuthService;
import com.adit.backend.global.common.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {
	private final AuthService authService;

	@GetMapping("/kakao")
	public ResponseEntity<ApiResponse<String>> kakaoLogin(HttpServletResponse response) throws IOException {
		String kakaoAuthUrl = authService.getKakaoAuthUrl();
		response.sendRedirect(kakaoAuthUrl);
		return ResponseEntity.ok(ApiResponse.success("카카오 로그인 페이지 로딩 성공"));
	}

	@GetMapping("/kakao/callback")
	public ResponseEntity<ApiResponse<KakaoResponse>> kakaoCallback(@RequestParam String code) {
		return ResponseEntity.ok(ApiResponse.success(authService.getKakaoAccessToken(code)));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
		authService.logout(accessToken);
		return ResponseEntity.ok(ApiResponse.success("로그아웃 완료"));
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<OAuth2UserInfo>> signup(@RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.ok(ApiResponse.success(authService.signup(accessToken)));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<KakaoResponse.AccessTokenDto>> renewToken(@RequestHeader("Authorization-refresh") String refreshToken) {
		return ResponseEntity.ok(ApiResponse.success(authService.renewToken(refreshToken)));
	}

}