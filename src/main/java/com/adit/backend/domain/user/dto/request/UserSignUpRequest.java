package com.adit.backend.domain.user.dto.request;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestHeader;

public record UserSignUpRequest(@RequestHeader("Authorization") String accessToken,
								@Length(min = 2, max = 12) String nickname) {
}
