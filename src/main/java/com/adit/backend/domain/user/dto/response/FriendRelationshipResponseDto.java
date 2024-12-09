package com.adit.backend.domain.user.dto.response;

import java.io.Serializable;

import com.adit.backend.domain.user.entity.FriendRelationship;
import com.adit.backend.domain.user.entity.User;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO for {@link FriendRelationship}
 */
@Builder
public record FriendRelationshipResponseDto(Long id, @NotNull(message = "From User ID must not be nul") User fromUser,
											@NotNull(message = "To User ID must not be nul") User toUser, String status)
	implements Serializable {
	public static FriendRelationshipResponseDto from(FriendRelationship friendRelationship) {
		return new FriendRelationshipResponseDto(
			friendRelationship.getId(),
			friendRelationship.getFromUser(),
			friendRelationship.getToUser(),
			friendRelationship.getStatus()
		);
	}
}