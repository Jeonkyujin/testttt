package com.adit.backend.domain.image.dto.response;

import java.io.Serializable;

import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.image.entity.Image;
import com.adit.backend.domain.place.entity.CommonPlace;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link Image}
 */
public record ImageResponseDto(@NotNull(message = "Image ID must not be null") Long id, CommonPlace place, Event event,
							   String url, String fileName, String folderName)
	implements Serializable {
	public static ImageResponseDto from(Image image) {
		return new ImageResponseDto(
			image.getId(),
			image.getPlace(),
			image.getEvent(),
			image.getUrl(),
			image.getFileName(),
			image.getFolderName()
		);
	}
}