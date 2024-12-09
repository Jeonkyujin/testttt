package com.adit.backend.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.place.dto.CommonPlaceRequestDto;
import com.adit.backend.domain.place.entity.CommonPlace;
import com.adit.backend.domain.place.repository.CommonPlaceRepository;
import com.adit.backend.global.error.exception.BusinessException;
import com.adit.backend.global.error.exception.GlobalErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class CommonPlaceService {

    private final CommonPlaceRepository commonPlaceRepository;

    // 새로운 장소 생성
    public CommonPlace createPlace(CommonPlaceRequestDto requestDto) {
        CommonPlace place = new CommonPlace();
        place.setPlaceName(requestDto.getPlaceName());
        place.setLatitude(requestDto.getLatitude());
        place.setLongitude(requestDto.getLongitude());
        place.setAddressName(requestDto.getAddressName());
        place.setRoadAddressName(requestDto.getRoadAddressName());
        place.setSubCategory(requestDto.getSubCategory());
        place.setUrl(requestDto.getUrl());
        // DB에 저장하고 반환
        return commonPlaceRepository.save(place);
    }

    // 장소 ID로 조회
    public CommonPlace getPlaceById(Long placeId) {
        return commonPlaceRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException("Place not found", GlobalErrorCode.NOT_FOUND_ERROR));
    }

    // 장소 정보 업데이트
    public CommonPlace updatePlace(Long placeId, CommonPlaceRequestDto requestDto) {
        CommonPlace place = getPlaceById(placeId);
        place.setPlaceName(requestDto.getPlaceName());
        place.setLatitude(requestDto.getLatitude());
        place.setLongitude(requestDto.getLongitude());
        place.setAddressName(requestDto.getAddressName());
        place.setRoadAddressName(requestDto.getRoadAddressName());
        place.setSubCategory(requestDto.getSubCategory());
        place.setUrl(requestDto.getUrl());
        // 업데이트된 장소를 다시 DB에 저장
        return commonPlaceRepository.save(place);
    }

    // 장소 삭제
    public void deletePlace(Long placeId) {
        CommonPlace place = getPlaceById(placeId);
        commonPlaceRepository.delete(place);
    }
}
