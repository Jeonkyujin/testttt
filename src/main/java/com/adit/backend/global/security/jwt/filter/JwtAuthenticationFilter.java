package com.adit.backend.global.security.jwt.filter;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.adit.backend.domain.auth.dto.model.PrincipalDetails;
import com.adit.backend.domain.auth.service.PrincipalDetailsService;
import com.adit.backend.domain.user.repository.UserRepository;
import com.adit.backend.global.error.exception.BusinessException;
import com.adit.backend.global.error.exception.TokenException;
import com.adit.backend.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider tokenProvider;
	private final UserRepository userRepository;
	private final PrincipalDetailsService principalDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) {
		try {
			String accessToken = tokenProvider.extractAccessToken(request)
				.filter(tokenProvider::validateAccessToken)
				.orElse(null);
			String refreshToken = tokenProvider.extractRefreshToken(request)
				.filter(tokenProvider::validateRefreshToken)
				.orElse(null);

			if (refreshToken != null) {
				String newAccessToken = tokenProvider.checkRefreshTokenAndReIssueAccessToken(
					tokenProvider.getAuthentication(accessToken),
					refreshToken);
				setAuthentication(newAccessToken);
				return;
			} else {
				checkAccessTokenAndAuthentication(request, response, filterChain);
			}
		} catch (ServletException e) {
			throw new BusinessException(SERVLET_ERROR);
		} catch (IOException e) {
			throw new BusinessException(IO_ERROR);
		}
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.info("checkAccessTokenAndAuthentication() 호출");
		tokenProvider.extractAccessToken(request)
			.filter(tokenProvider::validateAccessToken)
			.ifPresent(accessToken -> tokenProvider.getSocialId(accessToken)
				.ifPresent(socialId -> userRepository.findBySocialId(socialId)
					.ifPresent(user -> setAuthentication(accessToken))));
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String accessToken) {
		String socialId = tokenProvider.getSocialId(accessToken).orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
		PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(socialId);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails,
			null,
			principalDetails.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
