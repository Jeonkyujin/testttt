package com.adit.backend.domain.user.dto.response;

import lombok.Builder;

public record UserResponse() {

	@Builder
	public record InfoDto(String email, String name, String nickname, String socialId) {
	}
}