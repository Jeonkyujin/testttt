package com.adit.backend.domain.auth.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;
import static io.netty.handler.codec.http.HttpHeaderValidationUtil.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.auth.repository.TokenRepository;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.domain.user.repository.UserRepository;
import com.adit.backend.global.error.exception.BusinessException;
import com.adit.backend.global.error.exception.TokenException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class TokenService {
	private final TokenRepository tokenRepository;
	private final UserRepository userRepository;

	public void deleteToken(String socialId) {
		tokenRepository.deleteByUser_SocialId((socialId));
	}

	public void saveOrUpdate(String socialId, String refreshToken, String accessToken) {
		log.info("Received socialId for saveOrUpdate: {}", socialId);
		validateToken(socialId);
		User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
		tokenRepository.findByUser(user)
			.ifPresentOrElse(
				existingToken -> {
					existingToken.updateRefreshToken(refreshToken);
					existingToken.updateAccessToken(accessToken);
					tokenRepository.save(existingToken);
				},
				() -> {
					Token newToken = new Token(user, refreshToken, accessToken);
					tokenRepository.save(newToken);
				}
			);
	}

	public Token findByAccessTokenOrThrow(String accessToken) {
		return tokenRepository.findByAccessToken(accessToken)
			.orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
	}

	public void updateToken(String accessToken, Token token) {
		token.updateAccessToken(accessToken);
		tokenRepository.save(token);
	}

}