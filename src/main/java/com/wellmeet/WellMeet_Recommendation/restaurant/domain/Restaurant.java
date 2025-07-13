package com.wellmeet.WellMeet_Recommendation.restaurant.domain;

import com.wellmeet.WellMeet_Recommendation.common.domain.BaseEntity;
import com.wellmeet.WellMeet_Recommendation.common.dto.ReviewVector;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@Entity
@Table(name = "restaurant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String thumbnail;

    // @JdbcTypeCode(SqlTypes.VECTOR)
    @Type(VectorType.class)
    @Column(name = "vibe_vector", columnDefinition = "vector(768)")
    private float[] vibeVector;

    @Type(VectorType.class)
    @Column(name = "food_vector", columnDefinition = "vector(768)")
    private float[] foodVector;

    @Type(VectorType.class)
    @Column(name = "companion_vector", columnDefinition = "vector(768)")
    private float[] companionVector;

    @Type(VectorType.class)
    @Column(name = "purpose_vector", columnDefinition = "vector(768)")
    private float[] purposeVector;

    public Restaurant(String placeId, String name, String address,
            double latitude, double longitude, String thumbnail,
            ReviewVector reviewVector) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
        this.vibeVector = reviewVector.getVibeVector();
        this.foodVector = reviewVector.getFoodVector();
        this.companionVector = reviewVector.getCompanionVector();
        this.purposeVector = reviewVector.getPurposeVector();
    }

    public void updateVectors(ReviewVector reviewVector) {
        this.vibeVector = reviewVector.getVibeVector();
        this.foodVector = reviewVector.getFoodVector();
        this.companionVector = reviewVector.getCompanionVector();
        this.purposeVector = reviewVector.getPurposeVector();
    }

}
