package com.adit.backend.domain.auth.service.query;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.auth.repository.TokenRepository;
import com.adit.backend.global.error.exception.TokenException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenService {
	private final TokenRepository tokenRepository;

	public String findSocialIdByAccessToken(String accessToken) {
		return tokenRepository.findByAccessToken(accessToken)
			.map(token -> token.getUser().getSocialId())
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
	}
}