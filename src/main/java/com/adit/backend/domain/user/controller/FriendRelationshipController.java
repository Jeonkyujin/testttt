package com.adit.backend.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.user.dto.request.FriendRequestDto;
import com.adit.backend.domain.user.dto.response.FriendRelationshipResponseDto;
import com.adit.backend.domain.user.entity.FriendRelationship;
import com.adit.backend.domain.user.service.FriendRelationshipService;
import com.adit.backend.global.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRelationshipController {

	private final FriendRelationshipService friendRelationshipService;

	// 친구 요청 보내기 API
	@PostMapping("/send")
	public ResponseEntity<ApiResponse<FriendRelationshipResponseDto>> sendFriendRequest(
		@Valid @RequestBody FriendRequestDto requestDto) {
		// 친구 요청을 처리하여 응답 반환
		FriendRelationship friendRelationship = friendRelationshipService.sendFriendRequest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(FriendRelationshipResponseDto.from(friendRelationship)));
	}

	// 친구 요청 수락 API
	@PostMapping("/accept")
	public ResponseEntity<ApiResponse<String>> acceptFriendRequest(@RequestParam Long requestId) {
		// 요청 ID로 친구 요청을 수락 처리
		friendRelationshipService.acceptFriendRequest(requestId);
		return ResponseEntity.ok(ApiResponse.success("Friend request accepted"));
	}

	// 친구 요청 거절 API
	@PostMapping("/reject")
	public ResponseEntity<ApiResponse<String>> rejectFriendRequest(@RequestParam Long requestId) {
		// 요청 ID로 친구 요청을 거절 처리
		friendRelationshipService.rejectFriendRequest(requestId);
		return ResponseEntity.ok(ApiResponse.success("Friend request rejected"));
	}

	// 친구 삭제 API
	@DeleteMapping("/{friendId}")
	public ResponseEntity<ApiResponse<String>> removeFriend(@PathVariable Long friendId) {
		// 친구 관계를 삭제
		friendRelationshipService.removeFriend(friendId);
		return ResponseEntity.ok(ApiResponse.success("Friend removed"));
	}
}
