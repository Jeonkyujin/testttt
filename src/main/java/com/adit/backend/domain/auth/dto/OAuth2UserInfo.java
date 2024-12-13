package com.adit.backend.domain.auth.dto;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.Map;

import com.adit.backend.domain.user.entity.Role;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.global.util.KeyGenerator;

import lombok.Builder;

@Builder
public record OAuth2UserInfo(
	String name,
	String nickname,
	String email,
	String profile
) {

	public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) throws AuthException {
		return switch (registrationId) {
			case "kakao" -> ofKakao(attributes);
			default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
		};
	}

	private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");

		return OAuth2UserInfo.builder()
			.name(String.valueOf(profile.get("nickname")))
			.email(String.valueOf(account.get("email")))
			.profile((String.valueOf(profile.get("profile_image_url"))))
			.build();
	}

	public User toEntity() {
		return User.builder()
			.name(name)
			.email(email)
			.profile(profile)
			.socialId(KeyGenerator.generateKey())
			.nickname("test")
			.role(Role.USER)
			.build();
	}
}