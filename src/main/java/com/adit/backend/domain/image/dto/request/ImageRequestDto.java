package com.adit.backend.domain.image.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageRequestDto {

    @NotNull
    private Long placeId;

    private Long eventId;
    private String url;
    private String fileName;
    private String folderName;
}
