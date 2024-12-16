package com.adit.backend.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthLoginResponse(@NotBlank String accessToken, @NotBlank String refreshToken) {
}
