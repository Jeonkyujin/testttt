package com.adit.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.user.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
