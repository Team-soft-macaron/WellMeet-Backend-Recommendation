package com.wellmeet.WellMeet_Recommendation.restaurant.repository;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = """
            SELECT r
            FROM Restaurant r
            WHERE r.latitude BETWEEN :#{#boundingBox.minLatitude} AND :#{#boundingBox.maxLatitude}
            AND r.longitude BETWEEN :#{#boundingBox.minLongitude} AND :#{#boundingBox.maxLongitude}
            """)
    List<Restaurant> findWithBoundBox(@Param("boundingBox") BoundingBox boundingBox);

    @Query(value = """
            SELECT * FROM restaurant r
            ORDER BY (
                (1 - (r.vibe_vector <=> CAST(:vibeVector AS vector))) +
                (1 - (r.food_vector <=> CAST(:foodVector AS vector))) +
                (1 - (r.companion_vector <=> CAST(:companionVector AS vector))) +
                (1 - (r.purpose_vector <=> CAST(:purposeVector AS vector)))
            ) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Restaurant> findTopByCombinedSimilarity(
            @Param("vibeVector") float[] vibeVector,
            @Param("foodVector") float[] foodVector,
            @Param("companionVector") float[] companionVector,
            @Param("purposeVector") float[] purposeVector,
            @Param("limit") int limit);
}
