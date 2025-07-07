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
}
