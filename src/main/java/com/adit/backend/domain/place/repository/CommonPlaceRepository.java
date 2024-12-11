package com.adit.backend.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adit.backend.domain.place.entity.CommonPlace;

public interface CommonPlaceRepository extends JpaRepository<CommonPlace, Long> {
}
