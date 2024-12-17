package com.adit.backend.domain.user.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record UserSignUpRequest(@NotBlank String accessToken, @NotBlank @Length(min = 2, max = 12) String nickName) {
}
