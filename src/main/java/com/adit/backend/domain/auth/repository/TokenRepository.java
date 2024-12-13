package com.adit.backend.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.auth.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByAccessToken(String accessToken);

	boolean existsByUser_SocialId(String socialId);

	void deleteByAccessToken(String accessToken);
}