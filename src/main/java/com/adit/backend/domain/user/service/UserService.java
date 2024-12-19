package com.adit.backend.domain.user.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.auth.dto.OAuth2UserInfo;
import com.adit.backend.domain.auth.entity.Token;
import com.adit.backend.domain.auth.service.query.TokenService;
import com.adit.backend.domain.user.dto.request.UserSignUpRequest;
import com.adit.backend.domain.user.dto.response.UserInfoResponse;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.domain.user.exception.UserException;
import com.adit.backend.domain.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

	private final UserRepository userRepository;
	private final TokenService tokenService;

	public UserInfoResponse changeNickname(UserSignUpRequest userSignUpRequest) {
		User user = findUserByAccessToken(userSignUpRequest.accessToken());
		validateNickName(userSignUpRequest.nickname());
		user.changeNickName(userSignUpRequest.nickname());
		return UserInfoResponse.builder()
			.email(user.getEmail())
			.name(user.getName())
			.nickname(user.getNickname())
			.socialId(user.getSocialId())
			.build();
	}

	private User findUserByAccessToken(String accessToken) {
		return userRepository.findUserByToken_AccessToken(accessToken)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
	}

	private void validateNickName(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new UserException(NICKNAME_ALREADY_EXIST);
		}
	}

	public User getOrSaveUser(OAuth2UserInfo oAuth2UserInfo, Token token) {
		User user = userRepository.findByEmail(oAuth2UserInfo.email())
			.orElseGet(oAuth2UserInfo::toEntity);
		user.addToken(token);
		log.info("Saving user: {}", user);
		return userRepository.save(user);
	}
}
