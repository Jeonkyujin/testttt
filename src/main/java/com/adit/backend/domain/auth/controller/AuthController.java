package com.adit.backend.domain.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.adit.backend.domain.auth.dto.response.KakaoTokenResponseDto;
import com.adit.backend.domain.auth.service.AuthService;
import com.adit.backend.global.common.ApiResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {
	private final AuthService authService;
	@Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
	private String authorizationUri;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@GetMapping("/kakao")
	public ResponseEntity<String> kakaoLogin() {
		String kakaoAuthUrl = UriComponentsBuilder
			.fromUriString(authorizationUri)
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("response_type", "code")
			.build()
			.toUriString();
		return ResponseEntity.ok("redirect:/" + kakaoAuthUrl);
	}

	@GetMapping("/kakao/callback")
	public ResponseEntity<ApiResponse<KakaoTokenResponseDto>> kakaoCallback(@RequestParam String code) {
		return ResponseEntity.ok(ApiResponse.success(authService.getKakaoAccessToken(code)));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
		authService.logout(accessToken);
		return ResponseEntity.ok().build();
	}
}