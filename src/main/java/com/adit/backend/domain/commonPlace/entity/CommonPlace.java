package com.adit.backend.domain.commonPlace.entity;

import java.math.BigDecimal;

import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
}
