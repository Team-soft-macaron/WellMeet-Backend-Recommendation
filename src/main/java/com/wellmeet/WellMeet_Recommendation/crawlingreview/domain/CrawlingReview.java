package com.wellmeet.WellMeet_Recommendation.crawlingreview.domain;

import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.VectorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String hash;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public CrawlingReview(String content,
            Restaurant restaurant,
            String hash,
            ReviewVector reviewVector) {
        this.content = content;
        this.restaurant = restaurant;
        this.hash = hash;
        this.vibeVector = reviewVector.getVibeVector();
        this.foodVector = reviewVector.getFoodVector();
        this.companionVector = reviewVector.getCompanionVector();
        this.purposeVector = reviewVector.getPurposeVector();
    }
}
