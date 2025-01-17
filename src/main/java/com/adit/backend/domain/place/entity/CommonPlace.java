package com.adit.backend.domain.place.entity;

import java.math.BigDecimal;

import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonPlace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String placeName;

	private BigDecimal latitude;
	private BigDecimal longitude;

	private String addressName;
	private String roadAddressName;
	private String subCategory;

	private String url;

	@Builder
	public CommonPlace(Long id, String placeName, BigDecimal latitude, BigDecimal longitude, String addressName,
		String roadAddressName, String subCategory, String url) {
		this.id = id;
		this.placeName = placeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.addressName = addressName;
		this.roadAddressName = roadAddressName;
		this.subCategory = subCategory;
		this.url = url;
	}

	public void updatePlace(String placeName, BigDecimal latitude, BigDecimal longitude, String addressName,
		String roadAddressName, String subCategory, String url) {
		this.placeName = placeName;
		this.addressName = addressName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.roadAddressName = roadAddressName;
		this.subCategory = subCategory;
		this.url = url;
	}
}
