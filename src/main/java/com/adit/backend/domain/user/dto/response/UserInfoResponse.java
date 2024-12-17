package com.adit.backend.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserInfoResponse(String email, String name, String nickname, String socialId) {
}
