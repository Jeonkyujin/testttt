package com.adit.backend.domain.auth.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.adit.backend.domain.auth.dto.OAuth2UserInfo;
import com.adit.backend.domain.auth.dto.response.KakaoResponse;
import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.auth.repository.TokenRepository;
import com.adit.backend.domain.user.service.UserService;
import com.adit.backend.global.error.exception.TokenException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

	private final RestTemplate restTemplate;
	private final UserService userService;
	private final TokenRepository tokenRepository;
	private final String KAKAO_LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";
	private final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

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

	public String getKakaoAuthUrl() {
		return UriComponentsBuilder
			.fromUriString(authorizationUri)
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("response_type", "code")
			.build()
			.toUriString();
	}

	public KakaoResponse getKakaoAccessToken(String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request =
			new HttpEntity<>(params, headers);

		ResponseEntity<KakaoResponse> response = restTemplate.postForEntity(
			tokenUri,
			request,
			KakaoResponse.class
		);
		getOrSaveToken(response.getBody());
		return response.getBody();
	}

	public OAuth2UserInfo signup(String accessToken) {
		OAuth2UserInfo oAuth2UserInfo = extractAccessToken(accessToken);
		Token token = tokenRepository.findByAccessToken(accessToken)
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
		userService.getOrSaveUser(oAuth2UserInfo, token);
		return oAuth2UserInfo;
	}

	public KakaoResponse.AccessTokenDto renewToken(String refreshToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("refresh_token", refreshToken);

		HttpEntity<MultiValueMap<String, String>> request =
			new HttpEntity<>(params, headers);

		ResponseEntity<KakaoResponse.AccessTokenDto> response = restTemplate.postForEntity(
			tokenUri,
			request,
			KakaoResponse.AccessTokenDto.class
		);
		log.info(response.getBody().toString());
		renewToken(response.getBody(), refreshToken);
		log.info("토큰 재발급 완료");
		return response.getBody();
	}

	public void logout(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken.replace("Bearer ", ""));
		HttpEntity<String> request = new HttpEntity<>(headers);
		restTemplate.postForObject(KAKAO_LOGOUT_URI, request, Void.class);
	}

	private ResponseEntity<JsonNode> callKakaoApi(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Void> request = new HttpEntity<>(headers);
		return restTemplate.exchange(KAKAO_USER_INFO_URI, HttpMethod.GET, request, JsonNode.class);
	}

	public OAuth2UserInfo extractAccessToken(String accessToken) {
		ResponseEntity<JsonNode> response = callKakaoApi(accessToken);
		JsonNode userInfo = response.getBody();
		String name = userInfo.path("kakao_account").path("profile").path("nickname").asText();
		String email = userInfo.path("kakao_account").path("email").asText();
		String profileImageUrl = userInfo.path("kakao_account").path("profile").path("thumbnail_image_url").asText();
		return OAuth2UserInfo.builder()
			.name(name)
			.nickname("GUEST")
			.email(email)
			.profile(profileImageUrl)
			.build();
	}

	private Token getOrSaveToken(KakaoResponse response) {
		Token token = tokenRepository.findByAccessToken(response.accessToken())
			.orElseGet(response::toEntity);
		log.info("Saving token: {}", token);
		return tokenRepository.save(token);
	}

	private Token renewToken(KakaoResponse.AccessTokenDto response, String refreshToken) {
		Token token = tokenRepository.findTokenByRefreshToken(refreshToken)
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
		token.updateAccessToken(response.accessToken(), response.expiresIn());
		log.info("Updating token: {}", token);
		return tokenRepository.save(token);
	}
}

