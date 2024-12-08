package com.adit.backend.domain.friendRelationship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.friendRelationship.entity.FriendRelationship;

public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, Long> {
}
