package com.adit.backend.domain.image.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adit.backend.domain.commonPlace.entity.CommonPlace;
import com.adit.backend.domain.commonPlace.repository.CommonPlaceRepository;
import com.adit.backend.domain.event.EventRepository;
import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.image.dto.ImageRequestDto;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.image.repository.ImageRepository;
import com.adit.backend.global.error.exception.BusinessException;
import com.adit.backend.global.error.exception.GlobalErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final CommonPlaceRepository commonPlaceRepository;
    private final EventRepository eventRepository;

    // 이미지 업로드

    public Image uploadImage(ImageRequestDto requestDto) {
        CommonPlace place = commonPlaceRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new BusinessException("Place not found", GlobalErrorCode.NOT_FOUND_ERROR));

        Event event = requestDto.getEventId() != null ? eventRepository.findById(requestDto.getEventId())
                .orElseThrow(() -> new BusinessException("Event not found", GlobalErrorCode.NOT_FOUND_ERROR)) : null;

        Image image = new Image();
        image.setPlace(place);
        image.setEvent(event);
        image.setUrl(requestDto.getUrl());
        image.setFileName(requestDto.getFileName());
        image.setFolderName(requestDto.getFolderName());

        return imageRepository.save(image);
    }

    // 이미지 조회
    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("Image not found", GlobalErrorCode.NOT_FOUND_ERROR));
    }

    // 이미지 삭제
    public void deleteImage(Long imageId) {
        Image image = getImageById(imageId);
        imageRepository.delete(image);
    }
}
