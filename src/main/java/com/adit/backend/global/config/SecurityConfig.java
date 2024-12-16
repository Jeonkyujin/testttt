package com.adit.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adit.backend.global.security.jwt.CustomAuthenticationEntryPoint;
import com.adit.backend.global.security.jwt.filter.JwtAuthenticationFilter;
import com.adit.backend.global.security.jwt.filter.TokenExceptionFilter;
import com.adit.backend.global.security.jwt.handler.CustomAccessDeniedHandler;
import com.adit.backend.global.security.oauth.CustomOAuth2UserService;
import com.adit.backend.global.security.oauth.handler.OAuth2FailureHandler;
import com.adit.backend.global.security.oauth.handler.OAuth2SuccessHandler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * The type Security config.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final JwtAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(
				"/error",
				"/favicon.ico",
				"/v3/api-docs/**",
				"/swagger-ui/**",
				"/swagger-resources/**");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.headers(c -> c.frameOptions(
				HeadersConfigurer.FrameOptionsConfig::disable).disable())
			// 세션 정책 수정
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// OAuth2 설정 중복 제거
			.oauth2Login(oauth2 -> {
				oauth2.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService));
				oauth2.successHandler(oAuth2SuccessHandler);
				oauth2.failureHandler(new OAuth2FailureHandler());
			})
			// 인증 경로 설정 수정
			.authorizeHttpRequests(request -> request
				.requestMatchers(
					"/",
					"/api/auth/success",
					"/api/auth/**",
					"/oauth2/**",
					"/login/**",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/webjars/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.accessDeniedHandler(new CustomAccessDeniedHandler()));

		return http.build();
	}
}
