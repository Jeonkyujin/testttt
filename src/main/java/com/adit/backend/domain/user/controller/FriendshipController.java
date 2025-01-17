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
import com.adit.backend.domain.user.dto.response.FriendshipResponseDto;
import com.adit.backend.domain.user.entity.Friendship;
import com.adit.backend.domain.user.service.FriendshipService;
import com.adit.backend.global.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendshipController {

	private final FriendshipService friendshipService;

	// 친구 요청 보내기 API
	@PostMapping("/send")
	public ResponseEntity<ApiResponse<FriendshipResponseDto>> sendFriendRequest(
		@Valid @RequestBody FriendRequestDto requestDto) {
		// 친구 요청을 처리하여 응답 반환
		Friendship friendship = friendshipService.sendFriendRequest(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(FriendshipResponseDto.from(friendship)));
	}

	// 친구 요청 수락 API
	@PostMapping("/accept")
	public ResponseEntity<ApiResponse<String>> acceptFriendRequest(@RequestParam Long requestId) {
		// 요청 ID로 친구 요청을 수락 처리
		friendshipService.acceptFriendRequest(requestId);
		return ResponseEntity.ok(ApiResponse.success("Friend request accepted"));
	}

	// 친구 요청 거절 API
	@PostMapping("/reject")
	public ResponseEntity<ApiResponse<String>> rejectFriendRequest(@RequestParam Long requestId) {
		// 요청 ID로 친구 요청을 거절 처리
		friendshipService.rejectFriendRequest(requestId);
		return ResponseEntity.ok(ApiResponse.success("Friend request rejected"));
	}

	// 친구 삭제 API
	@DeleteMapping("/{friendId}")
	public ResponseEntity<ApiResponse<String>> removeFriend(@PathVariable Long friendId) {
		// 친구 관계를 삭제
		friendshipService.removeFriend(friendId);
		return ResponseEntity.ok(ApiResponse.success("Friend removed"));
	}
}
