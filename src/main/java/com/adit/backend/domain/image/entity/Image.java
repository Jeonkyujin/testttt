package com.adit.backend.domain.image.entity;

import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.place.entity.CommonPlace;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private CommonPlace place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private Event event;

	@Column(nullable = false)
	private String url;

	private String fileName;
	private String folderName;

	@Builder
	public Image(Long id, CommonPlace place, Event event, String url, String fileName, String folderName) {
		this.id = id;
		this.place = place;
		this.event = event;
		this.url = url;
		this.fileName = fileName;
		this.folderName = folderName;
	}
}
