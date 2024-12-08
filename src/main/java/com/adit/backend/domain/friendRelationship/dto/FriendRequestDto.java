package com.adit.backend.domain.friendRelationship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {

    @NotNull
    private Long fromUserId;

    @NotNull
    private Long toUserId;
}
