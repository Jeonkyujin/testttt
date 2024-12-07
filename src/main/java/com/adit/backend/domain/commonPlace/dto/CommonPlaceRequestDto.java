package com.adit.backend.domain.commonPlace.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonPlaceRequestDto {

    @NotNull(message = "Place name cannot be null")
    private String placeName;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String addressName;
    private String roadAddressName;
    private String subCategory;

    private String url;
}
