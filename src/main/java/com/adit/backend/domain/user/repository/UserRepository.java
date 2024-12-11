package com.adit.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
