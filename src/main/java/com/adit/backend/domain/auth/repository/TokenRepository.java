package com.adit.backend.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.user.entity.User;

public interface TokenRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByAccessToken(String accessToken);

	void deleteByUser_SocialId(String userSocialId);

	Optional<Token> findByUser(User user);
}