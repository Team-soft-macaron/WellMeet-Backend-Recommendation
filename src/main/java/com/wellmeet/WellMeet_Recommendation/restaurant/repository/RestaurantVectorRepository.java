package com.wellmeet.WellMeet_Recommendation.restaurant.repository;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.RestaurantVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantVectorRepository extends JpaRepository<RestaurantVector, Long> {

    @Query(value = """
            WITH similarity_calc AS (
                SELECT
                    r.restaurant_id,
                    r.vibe_vector <=> CAST(:vibeVector AS vector) AS vibe_sim,
                    r.food_vector <=> CAST(:foodVector AS vector) AS food_sim,
                    r.companion_vector <=> CAST(:companionVector AS vector) AS companion_sim,
                    r.purpose_vector <=> CAST(:purposeVector AS vector) AS purpose_sim
                FROM restaurant_vector r
            )
            SELECT
                restaurant_id,
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
    List<String> findTopRestaurantIdsByCombinedSimilarity(
            @Param("vibeVector") float[] vibeVector,
            @Param("foodVector") float[] foodVector,
            @Param("companionVector") float[] companionVector,
            @Param("purposeVector") float[] purposeVector,
            @Param("limit") int limit);
}
