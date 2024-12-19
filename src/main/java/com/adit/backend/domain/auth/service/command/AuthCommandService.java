package com.adit.backend.domain.auth.service.command;

import static com.adit.backend.global.error.GlobalErrorCode.*;
import static org.springframework.http.MediaType.*;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.adit.backend.domain.auth.dto.OAuth2UserInfo;
import com.adit.backend.domain.auth.dto.response.KakaoResponse;
import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.auth.repository.TokenRepository;
import com.adit.backend.domain.auth.service.query.TokenQueryService;
import com.adit.backend.domain.user.service.command.UserCommandService;
import com.adit.backend.global.error.exception.TokenException;
import com.adit.backend.global.security.jwt.exception.AuthException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCommandService {

	private final RestTemplate restTemplate;
	private final UserCommandService userCommandService;
	public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
	public static final String GRANT_TYPE_REFRESH = "refresh_token";
	private final TokenRepository tokenRepository;
	public static final String RESPONSE_TYPE = "code";
	private final TokenCommandService tokenCommandService;
	private final TokenQueryService tokenQueryService;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
	private String authorizationUri;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String tokenUri;
	@Value("${kakao.logout-url}")
	private String logoutUrl;

	private static HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(APPLICATION_JSON));
		return headers;
	}

	public String createKakaoAuthorizationUrl() {
		return UriComponentsBuilder
			.fromUriString(authorizationUri)
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("response_type", RESPONSE_TYPE)
			.build()
			.toUriString();
	}

	public KakaoResponse.TokenInfoDto exchangeKakaoAuthorizationCode(String code) {
		HttpHeaders headers = createHeaders();
		MultiValueMap<String, String> params = createAuthCodeParams(code);
		ResponseEntity<KakaoResponse.TokenInfoDto> response = executeKakaoRequest(
			tokenUri,
			new HttpEntity<>(params, headers),
			KakaoResponse.TokenInfoDto.class
		);
		tokenCommandService.saveOrUpdateToken(response.getBody());
		return response.getBody();
	}

	public OAuth2UserInfo login(String accessToken) {
		OAuth2UserInfo oAuth2UserInfo = tokenQueryService.extractAccessToken(accessToken);
		Token token = tokenRepository.findByAccessToken(accessToken)
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
		userCommandService.getOrSaveUser(oAuth2UserInfo, token);
		return oAuth2UserInfo;
	}

	public KakaoResponse.AccessTokenDto refreshKakaoToken(String refreshToken) {
		HttpHeaders headers = createHeaders();

		MultiValueMap<String, String> params = createRefreshTokenParams(refreshToken);

		ResponseEntity<KakaoResponse.AccessTokenDto> response = executeKakaoRequest(
			tokenUri,
			new HttpEntity<>(params, headers),
			KakaoResponse.AccessTokenDto.class
		);
		tokenCommandService.updateTokenEntity(response.getBody(), refreshToken);
		log.info("토큰 재발급 완료");
		return response.getBody();
	}

	public KakaoResponse.UserIdDto logout(String accessToken) {
		String tokenValue = accessToken.replace("Bearer ", ""); // Bearer 제거 필요
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenValue);

		try {
			// 먼저 카카오 로그아웃 요청
			KakaoResponse.UserIdDto response = restTemplate.exchange(
				logoutUrl,
				HttpMethod.POST,
				new HttpEntity<>(headers),
				KakaoResponse.UserIdDto.class
			).getBody();

			// 카카오 로그아웃 성공 후 토큰 삭제
			tokenCommandService.deleteToken(tokenValue);
			log.info("로그아웃 완료 및 토큰 삭제 성공: {}", tokenValue);

			return response;
		} catch (Exception e) {
			log.error("로그아웃 실패: {}", e.getMessage());
			throw new TokenException(LOGOUT_FAILED);
		}
	}

	private MultiValueMap<String, String> createAuthCodeParams(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", GRANT_TYPE_AUTH_CODE);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);
		return params;
	}

	private MultiValueMap<String, String> createRefreshTokenParams(String refreshToken) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", GRANT_TYPE_REFRESH);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("refresh_token", refreshToken);
		return params;
	}

	private <T> ResponseEntity<T> executeKakaoRequest(String url, HttpEntity<?> entity, Class<T> responseType) {
		try {
			return restTemplate.postForEntity(url, entity, responseType);
		} catch (HttpClientErrorException e) {
			log.error("Kakao API client error: {}", e.getStatusCode());
			throw new AuthException(INVALID_TOKEN);
		} catch (HttpServerErrorException e) {
			log.error("Kakao API server error: {}", e.getStatusCode());
			throw new AuthException(KAKAO_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Kakao API request failed: {}", e.getMessage());
			throw new AuthException(API_REQUEST_FAILED);
		}
	}
}

