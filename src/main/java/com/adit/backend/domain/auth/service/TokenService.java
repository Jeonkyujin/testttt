package com.adit.backend.domain.auth.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

	private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";
	private final TokenRepository tokenRepository;
	private final UserRepository userRepository;
	private final RestTemplate restTemplate;

	public void saveOrUpdate(String socialId, String refreshToken, String accessToken) {
		log.info("Processing token saveOrUpdate for socialId: {}", socialId);
		validateToken(accessToken);
		User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
		Token token = tokenRepository.findByAccessToken(accessToken)
			.map(o -> o.updateRefreshToken(refreshToken))
			.orElseGet(() -> Token.builder()
				.user(user)
				.refreshToken(refreshToken)
				.accessToken(accessToken)
				.build());
		tokenRepository.save(token);
		log.info("Token successfully saved or updated for socialId: {}", socialId);
	}

	private void validateToken(String socialId) {
		if (tokenRepository.existsByUser_SocialId(socialId)) {
			throw new TokenException(TOKEN_ALREADY_EXIST);
		}
	}

	public Token findByAccessTokenOrThrow(String refreshToken) {
		return tokenRepository.findTokenByRefreshToken(refreshToken)
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
	}

	public void updateAccessToken(String accessToken, Token token) {
		token.updateAccessToken(accessToken);
		tokenRepository.save(token);
	}

	public void updateRefreshToken(String refreshToken, Token token) {
		token.updateRefreshToken(refreshToken);
		tokenRepository.save(token);
	}

	public void logout(String socialId, String accessToken) {
/*		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		try {
			deleteToken(accessToken);
			restTemplate.postForEntity(KAKAO_LOGOUT_URL, entity, String.class);
		} catch (Exception e) {
			throw new TokenException(INTERNAL_SERVER_ERROR);
		}*/
		deleteToken(accessToken);
	}

	public void deleteToken(String accessToken) {
		tokenRepository.deleteByAccessToken(accessToken);
	}

}