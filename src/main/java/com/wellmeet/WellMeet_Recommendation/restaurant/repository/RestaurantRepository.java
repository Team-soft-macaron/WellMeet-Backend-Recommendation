package com.wellmeet.WellMeet_Recommendation.restaurant.repository;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
        Optional<Restaurant> findByPlaceId(String restaurantId);

        @Query(value = """
                        SELECT r
                        FROM Restaurant r
                        WHERE r.latitude BETWEEN :#{#boundingBox.minLatitude} AND :#{#boundingBox.maxLatitude}
                        AND r.longitude BETWEEN :#{#boundingBox.minLongitude} AND :#{#boundingBox.maxLongitude}
                        """)
        List<Restaurant> findWithBoundBox(@Param("boundingBox") BoundingBox boundingBox);

        @Query(value = """
                        WITH similarity_calc AS (
                            SELECT r.*,
                                   r.vibe_vector <=> CAST(:vibeVector AS vector) AS vibe_sim,
                                   r.food_vector <=> CAST(:foodVector AS vector) AS food_sim,
                                   r.companion_vector <=> CAST(:companionVector AS vector) AS companion_sim,
                                   r.purpose_vector <=> CAST(:purposeVector AS vector) AS purpose_sim
                            FROM restaurant r
                        )
                        SELECT *,
                               (
                                   CASE WHEN vibe_sim = 'NAN' THEN 1 ELSE vibe_sim END +
                                   CASE WHEN food_sim = 'NAN' THEN 1 ELSE food_sim END +
                                   CASE WHEN companion_sim = 'NAN' THEN 1 ELSE companion_sim END +
                                   CASE WHEN purpose_sim = 'NAN' THEN 1 ELSE purpose_sim END
                               ) AS combined_similarity
                        FROM similarity_calc
                        ORDER BY combined_similarity
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Restaurant> findTopByCombinedSimilarity(
                        @Param("vibeVector") float[] vibeVector,
                        @Param("foodVector") float[] foodVector,
                        @Param("companionVector") float[] companionVector,
                        @Param("purposeVector") float[] purposeVector,
                        @Param("limit") int limit);
}
