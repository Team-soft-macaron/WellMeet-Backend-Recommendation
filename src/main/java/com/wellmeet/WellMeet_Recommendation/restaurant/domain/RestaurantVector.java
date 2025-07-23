package com.wellmeet.WellMeet_Recommendation.restaurant.domain;

import com.wellmeet.WellMeet_Recommendation.common.domain.BaseEntity;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@Entity
@Table(name = "restaurant_vector")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantVector extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String restaurantId;

    @Column(unique = true)
    private String placeId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

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

    public RestaurantVector(String restaurantId, String placeId, double latitude, double longitude,
            ReviewVector reviewVector) {
        this.restaurantId = restaurantId;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public ReviewVector createReviewVector() {
        return new ReviewVector(vibeVector, foodVector, companionVector, purposeVector);
    }

}
