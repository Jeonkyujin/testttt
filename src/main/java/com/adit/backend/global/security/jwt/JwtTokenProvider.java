package com.adit.backend.global.security.jwt;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.auth.service.TokenService;
import com.adit.backend.global.error.exception.TokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class JwtTokenProvider {
	private static final String KEY_ROLE = "role";
	private final TokenService tokenService;

	@Value("${jwt.key}")
	private String key;

	private SecretKey secretKey;
	@Value("${jwt.access.token.expiration}")
	private long accessTokenExpireTime;
	@Value("${jwt.refresh.token.expiration}")
	private long getAccessTokenExpireTime;

	@PostConstruct
	private void setSecretKey() {
		secretKey = Keys.hmacShaKeyFor(key.getBytes());
	}

	public String generateAccessToken(Authentication authentication) {
		return generateToken(authentication, accessTokenExpireTime);
	}

	// 1. refresh token 발급
	public String generateRefreshToken(Authentication authentication, String accessToken) {
		String refreshToken = generateToken(authentication, getAccessTokenExpireTime);
		tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken);
		return refreshToken;
	}

	private String generateToken(Authentication authentication, long expireTime) {
		Date now = new Date();
		Date expiredDate = new Date(now.getTime() + expireTime);

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining());

		return Jwts.builder()
			.subject(authentication.getName())
			.claim(KEY_ROLE, authorities)
			.issuedAt(now)
			.expiration(expiredDate)
			.signWith(secretKey, Jwts.SIG.HS512)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		try {
			Claims parsedClaims = parseClaims(token);
			return createAuthentication(parsedClaims, token);
		} catch (ExpiredJwtException e) {
			String newAccessToken = reissueAccessToken(token);
			if (newAccessToken != null) {
				Claims reissuedClaims = parseClaims(newAccessToken);
				return createAuthentication(reissuedClaims, newAccessToken);
			}
			throw new TokenException(TOKEN_EXPIRED);
		}
	}

	private Authentication createAuthentication(Claims claims, String token) {
		List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
		User principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
		return Collections.singletonList(new SimpleGrantedAuthority(
			claims.get(KEY_ROLE).toString()));
	}

	// 3. accessToken 재발급
	public String reissueAccessToken(String accessToken) {
		if (StringUtils.hasText(accessToken)) {
			Token token = tokenService.findByAccessTokenOrThrow(accessToken);
			String refreshToken = token.getRefreshToken();

			if (validateRefreshToken(refreshToken)) {
				String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
				tokenService.updateToken(reissueAccessToken, token);
				return reissueAccessToken;
			} else {
				throw new TokenException(INVALID_TOKEN);
			}
		}
		return null;
	}

	public boolean validateRefreshToken(String refreshToken) {
		try {
			Claims claims = parseClaims(refreshToken);
			return claims.getExpiration().after(new Date());
		} catch (ExpiredJwtException e) {
			throw new TokenException(REFRESH_TOKEN_EXPIRED);  // 리프레시 토큰 만료 시 재로그인 요구
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean validateAccessToken(String accessToken) {
		try {
			if (!StringUtils.hasText(accessToken)) {
				throw new TokenException(TOKEN_NOT_FOUND);
			}

			Claims claims = parseClaims(accessToken);

			if (claims.getExpiration().before(new Date())) {
				throw new TokenException(TOKEN_EXPIRED);
			}

			return true;
		} catch (SecurityException | MalformedJwtException e) {
			throw new TokenException(INVALID_JWT_SIGNATURE);
		} catch (ExpiredJwtException e) {
			throw new TokenException(TOKEN_EXPIRED);
		} catch (UnsupportedJwtException e) {
			throw new TokenException(TOKEN_UNSURPPORTED);
		} catch (IllegalArgumentException e) {
			throw new TokenException(INVALID_TOKEN);
		}
	}

	private Claims parseClaims(String token) {
		if (!StringUtils.hasText(token)) {
			throw new TokenException(INVALID_TOKEN);
		}
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		} catch (MalformedJwtException e) {
			throw new TokenException(INVALID_TOKEN);
		} catch (SecurityException e) {
			throw new TokenException(INVALID_JWT_SIGNATURE);
		}
	}

	public String getSocialId(String token) {
		Claims claims = parseClaims(token);
		return claims.getSubject();
	}
}