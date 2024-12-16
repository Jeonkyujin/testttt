package com.adit.backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;


public record AuthLoginRequest(@NotBlank String accessToken, @NotBlank String refreshToken) {
}
