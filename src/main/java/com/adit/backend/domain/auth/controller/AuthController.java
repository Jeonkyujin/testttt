package com.adit.backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.auth.dto.model.PrincipalDetails;
import com.adit.backend.domain.auth.dto.response.LoginResponse;
import com.adit.backend.domain.auth.service.TokenService;
import com.adit.backend.global.common.ApiResponse;

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

	private final TokenService tokenService;

	@GetMapping("/success")
	public ResponseEntity<ApiResponse<LoginResponse>> success(@Valid LoginResponse loginResponse) {
		return ResponseEntity.ok(ApiResponse.success(loginResponse));
	}

	@Operation(security = {@SecurityRequirement(name = "bearerAuth")})
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
		@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken) {
		tokenService.logout(principalDetails.getUser().getSocialId(), accessToken);
		return ResponseEntity.noContent().build();
	}
}
