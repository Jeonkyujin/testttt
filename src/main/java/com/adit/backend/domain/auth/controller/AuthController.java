package com.adit.backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.auth.dto.model.PrincipalDetails;
import com.adit.backend.domain.auth.dto.request.AuthLoginRequest;
import com.adit.backend.domain.auth.dto.response.AuthLoginResponse;
import com.adit.backend.global.common.ApiResponse;
import com.adit.backend.global.security.jwt.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {

	private TokenService tokenService;

	@GetMapping("/kakao")
	public ResponseEntity<ApiResponse<AuthLoginResponse>> success(@Valid AuthLoginRequest authLoginRequest) {
		return ResponseEntity.ok(ApiResponse.success(AuthLoginResponse.builder()
			.accessToken(authLoginRequest.accessToken())
			.refreshToken(authLoginRequest.refreshToken())
			.build()));
	}

	@Operation(security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
		@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.noContent().build();
	}
}
