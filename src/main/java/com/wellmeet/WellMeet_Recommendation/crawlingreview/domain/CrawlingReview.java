package com.wellmeet.WellMeet_Recommendation.crawlingreview.domain;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "vibe_vector", columnDefinition = "vector(768)")
    private float[] vibeVector;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "food_vector", columnDefinition = "vector(768)")
    private float[] foodVector;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "companion_vector", columnDefinition = "vector(768)")
    private float[] companionVector;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "purpose_vector", columnDefinition = "vector(768)")
    private float[] purposeVector;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public CrawlingReview(String content, Restaurant restaurant, String hash) {
        this.content = content;
        this.restaurant = restaurant;
        this.hash = hash;
    }
}
