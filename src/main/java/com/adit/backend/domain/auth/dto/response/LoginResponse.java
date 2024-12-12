package com.adit.backend.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(@NotBlank String accessToken) {
}
