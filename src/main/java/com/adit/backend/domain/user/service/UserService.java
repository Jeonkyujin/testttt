package com.adit.backend.domain.user.service;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.user.dto.request.UserSignUpRequest;
import com.adit.backend.domain.user.dto.response.UserInfoResponse;
import com.adit.backend.domain.user.entity.User;
import com.adit.backend.domain.user.exception.UserException;
import com.adit.backend.domain.user.repository.UserRepository;
import com.adit.backend.global.error.exception.TokenException;
import com.adit.backend.global.security.jwt.util.JwtTokenProvider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

	private final UserRepository userRepository;
	private final JwtTokenProvider tokenProvider;

	public UserInfoResponse changeNickname(UserSignUpRequest userSignUpRequest) {
		User user = findUserBySocialId(userSignUpRequest.accessToken());
		validateNickName(userSignUpRequest.nickName());
		user.changeNickName(userSignUpRequest.nickName());
		return UserInfoResponse.builder()
			.email(user.getEmail())
			.name(user.getName())
			.nickname(user.getNickname())
			.socialId(user.getSocialId())
			.build();
	}

	private User findUserBySocialId(String accessToken) {
		String socialId = tokenProvider.getSocialId(accessToken)
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
		return userRepository.findBySocialId(socialId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
	}

	private void validateNickName(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new UserException(NICKNAME_ALREADY_EXIST);
		}
	}
}
