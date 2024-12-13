package com.adit.backend.global.security.oauth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.adit.backend.domain.auth.service.TokenService;
import com.adit.backend.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final String REDIRECT_URI = "/api/auth/success";
	private final JwtTokenProvider tokenProvider;
	private final TokenService tokenService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		String accessToken = tokenProvider.generateAccessToken(authentication);
		tokenProvider.generateRefreshToken(authentication, accessToken);

		String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
			.queryParam("accessToken", accessToken)
			.build().toUriString();

		response.sendRedirect(redirectUrl);
	}
}

