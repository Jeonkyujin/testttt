package com.adit.backend.domain.friendRelationship.dto;

import com.adit.backend.domain.friendRelationship.entity.FriendRelationship;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRelationshipResponseDto {

    private Long id;
    private String status;
    private Long fromUserId;
    private Long toUserId;

    public FriendRelationshipResponseDto(FriendRelationship friendRelationship) {
        this.id = friendRelationship.getId();
        this.status = friendRelationship.getStatus();
        this.fromUserId = friendRelationship.getFromUser().getId();
        this.toUserId = friendRelationship.getToUser().getId();
    }
}
