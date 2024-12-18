package com.adit.backend.domain.auth.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.adit.backend.domain.auth.dto.response.KakaoTokenResponseDto;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

	private final RestTemplate restTemplate;
	private final String KAKAO_LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;
	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String tokenUri;

	public KakaoTokenResponseDto getKakaoAccessToken(String code) {
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

		ResponseEntity<KakaoTokenResponseDto> response = restTemplate.postForEntity(
			tokenUri,
			request,
			KakaoTokenResponseDto.class
		);

		return response.getBody();
	}

	public void logout(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken.replace("Bearer ", ""));

		HttpEntity<String> request = new HttpEntity<>(headers);

		restTemplate.postForObject(KAKAO_LOGOUT_URI, request, Void.class);
	}
}