package com.adit.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.user.dto.request.UserSignUpRequest;
import com.adit.backend.domain.user.dto.response.UserInfoResponse;
import com.adit.backend.domain.user.service.UserService;
import com.adit.backend.global.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserController {

	private final UserService userService;

	@PostMapping("/nickname")
	public ResponseEntity<ApiResponse<UserInfoResponse>> changeNickname(
		@Valid @RequestBody UserSignUpRequest userSignUpRequest) {
		return ResponseEntity.ok(ApiResponse.success(userService.changeNickname(userSignUpRequest)));
	}
}
