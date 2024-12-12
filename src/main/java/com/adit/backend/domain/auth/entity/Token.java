package com.adit.backend.domain.auth.entity;

import com.adit.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", referencedColumnName = "socialId", unique = true)
	private User user;

	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;

	@Column(name = "access_token", nullable = false, unique = true)
	private String accessToken;

	@Builder
	public Token(User user, String refreshToken, String accessToken) {
		this.user = user;
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
	}

	public Token updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public void updateAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}