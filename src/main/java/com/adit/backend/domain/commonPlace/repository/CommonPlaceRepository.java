package com.adit.backend.domain.commonPlace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.commonPlace.entity.CommonPlace;

public interface CommonPlaceRepository extends JpaRepository<CommonPlace, Long> {
}
