package com.adit.backend.domain.commonPlace.dto;

import java.math.BigDecimal;

import com.adit.backend.domain.commonPlace.entity.CommonPlace;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonPlaceResponseDto {

    private Long id;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String addressName;
    private String roadAddressName;
    private String subCategory;
    private String url;

    public CommonPlaceResponseDto(CommonPlace place) {
        this.id = place.getId();
        this.placeName = place.getPlaceName();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.addressName = place.getAddressName();
        this.roadAddressName = place.getRoadAddressName();
        this.subCategory = place.getSubCategory();
        this.url = place.getUrl();
    }
}
