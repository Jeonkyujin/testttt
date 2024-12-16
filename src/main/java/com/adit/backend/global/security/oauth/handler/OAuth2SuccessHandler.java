package com.adit.backend.global.security.oauth.handler;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.adit.backend.domain.auth.dto.model.PrincipalDetails;
import com.adit.backend.domain.auth.service.TokenService;
import com.adit.backend.global.error.exception.BusinessException;
import com.adit.backend.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final String REDIRECT_URI = "/api/auth/success";
	private static final String BEARER = "Bearer ";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String HEADER_REFRESH_TOKEN = "Refresh-Token";

	private final JwtTokenProvider tokenProvider;
	private final TokenService tokenService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) {
		try {
			PrincipalDetails userDetails = getUserDetails(authentication);
			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(authentication, accessToken);

			tokenService.saveOrUpdate(userDetails.getUsername(), refreshToken, accessToken);

			// 프론트엔드 리다이렉트 URL에 토큰 정보를 포함
			String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
				.queryParam("accessToken", accessToken)
				.queryParam("refreshToken", refreshToken)
				.build().toUriString();

			response.sendRedirect(targetUrl);
		} catch (IOException e) {
			throw new BusinessException(IO_ERROR);
		}
	}

	private PrincipalDetails getUserDetails(Authentication authentication) {
		return (PrincipalDetails)authentication.getPrincipal();
	}

}