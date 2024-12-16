package com.adit.backend.domain.user.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum Role {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");

	private final String key;
}
