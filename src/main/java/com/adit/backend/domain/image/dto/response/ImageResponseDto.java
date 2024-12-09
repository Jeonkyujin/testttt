package com.adit.backend.domain.image.dto.response;

import com.adit.backend.domain.image.entity.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageResponseDto {

    private Long id;
    private Long placeId;
    private Long eventId;
    private String url;
    private String fileName;
    private String folderName;

    public ImageResponseDto(Image image) {
        this.id = image.getId();
        this.placeId = image.getPlace() != null ? image.getPlace().getId() : null;
        this.eventId = image.getEvent() != null ? image.getEvent().getId() : null;
        this.url = image.getUrl();
        this.fileName = image.getFileName();
        this.folderName = image.getFolderName();
    }
}
