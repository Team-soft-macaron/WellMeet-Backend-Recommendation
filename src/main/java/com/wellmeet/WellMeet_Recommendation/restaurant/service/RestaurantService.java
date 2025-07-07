package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> findWithBoundBox(BoundingBox boundingBox) {
        return restaurantRepository.findWithBoundBox(boundingBox);
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
    }
}
