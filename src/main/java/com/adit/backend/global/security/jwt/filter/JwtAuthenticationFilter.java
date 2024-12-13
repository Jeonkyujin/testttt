package com.adit.backend.global.security.jwt.filter;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.adit.backend.domain.auth.dto.model.PrincipalDetails;
import com.adit.backend.domain.auth.service.PrincipalDetailsService;
import com.adit.backend.global.error.exception.TokenException;
import com.adit.backend.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtTokenProvider tokenProvider;
	private final PrincipalDetailsService principalDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = extractAccessToken(request);
			if (StringUtils.hasText(accessToken)) {
				if (tokenProvider.validateAccessToken(accessToken)) {
					setAuthentication(accessToken);
				} else {
					handleRefreshToken(request, response);
				}
			}
			filterChain.doFilter(request, response);
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(INTERNAL_SERVER_ERROR);
		}
	}

	private String extractAccessToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
			return token.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	private void setAuthentication(String accessToken) {
		String socialId = tokenProvider.getSocialId(accessToken);
		PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(socialId);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails,
			null,
			principalDetails.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
		if (StringUtils.hasText(refreshToken)) {
			if (tokenProvider.validateRefreshToken(refreshToken)) {
				String newAccessToken = tokenProvider.reissueAccessToken(refreshToken);
				if (StringUtils.hasText(newAccessToken)) {
					setAuthentication(newAccessToken);
					response.setHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + newAccessToken);
				} else {
					throw new TokenException(TOKEN_NOT_FOUND);
				}
			} else {
				throw new TokenException(REFRESH_TOKEN_EXPIRED);
			}
		} else {
			throw new TokenException(TOKEN_NOT_FOUND);
		}
	}
}
