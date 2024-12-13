package com.adit.backend.domain.user.dto.response;

import java.io.Serializable;

import com.adit.backend.domain.user.entity.Friendship;
import com.adit.backend.domain.user.entity.User;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO for {@link Friendship}
 */
@Builder
public record FriendshipResponseDto(Long id, @NotNull(message = "From User ID must not be nul") User fromUser,
									@NotNull(message = "To User ID must not be nul") User toUser, String status)
	implements Serializable {
	public static FriendshipResponseDto from(Friendship friendship) {
		return new FriendshipResponseDto(
			friendship.getId(),
			friendship.getFromUser(),
			friendship.getToUser(),
			friendship.getStatus()
		);
	}
}