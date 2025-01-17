package com.adit.backend.domain.place.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adit.backend.domain.place.dto.request.CommonPlaceRequestDto;
import com.adit.backend.domain.place.dto.response.CommonPlaceResponseDto;
import com.adit.backend.domain.place.entity.CommonPlace;
import com.adit.backend.domain.place.service.CommonPlaceService;
import com.adit.backend.global.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonPlaceController {

	private final CommonPlaceService commonPlaceService;

	// 장소 생성 API
	@PostMapping
	public ResponseEntity<ApiResponse<CommonPlaceResponseDto>> createPlace(
		@Valid @RequestBody CommonPlaceRequestDto requestDto) {
		// 장소 정보를 받아 CommonPlaceService에서 처리
		CommonPlace place = commonPlaceService.createPlace(requestDto);
		// 생성된 장소를 응답으로 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CommonPlaceResponseDto.from(place)));
	}

	// 특정 장소 조회 API
	@GetMapping("/{placeId}")
	public ResponseEntity<ApiResponse<CommonPlaceResponseDto>> getPlace(@PathVariable Long placeId) {
		// ID로 장소를 조회
		CommonPlace place = commonPlaceService.getPlaceById(placeId);
		// 조회된 장소를 응답으로 반환
		return ResponseEntity.ok(ApiResponse.success(CommonPlaceResponseDto.from(place)));
	}

	// 장소 수정 API
	@PutMapping("/{placeId}")
	public ResponseEntity<ApiResponse<CommonPlaceResponseDto>> updatePlace(@PathVariable Long placeId,
		@Valid @RequestBody CommonPlaceRequestDto requestDto) {
		// ID로 기존 장소를 찾아 수정
		CommonPlace updatedPlace = commonPlaceService.updatePlace(placeId, requestDto);
		// 수정된 장소를 응답으로 반환
		return ResponseEntity.ok(ApiResponse.success(CommonPlaceResponseDto.from(updatedPlace)));
	}

	// 장소 삭제 API
	@DeleteMapping("/{placeId}")
	public ResponseEntity<ApiResponse<String>> deletePlace(@PathVariable Long placeId) {
		// ID로 장소를 삭제
		commonPlaceService.deletePlace(placeId);
		// 삭제 완료 메시지 응답
		return ResponseEntity.ok(ApiResponse.success("Place deleted successfully"));
	}
}
