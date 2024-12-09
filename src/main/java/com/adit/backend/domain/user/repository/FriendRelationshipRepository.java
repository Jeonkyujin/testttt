package com.adit.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.user.entity.FriendRelationship;

public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, Long> {
}
