package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import com.wellmeet.WellMeet_Recommendation.embedding.service.EmbeddingService;
import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.llm.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.llm.service.LLMService;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantCreateRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;

    public List<Restaurant> findWithBoundBox(BoundingBox boundingBox) {
        return restaurantRepository.findWithBoundBox(boundingBox);
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    public RestaurantResponse saveRestaurant(RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant(
                request.getPlaceId(),
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getThumbnail(),
                new float[768],
                new float[768],
                new float[768],
                new float[768]
        );

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponse(savedRestaurant);
    }

    public List<RestaurantResponse> recommendRestaurant(String query){
        ExtractedInfoResponse extractedInfoResponse = llmService.extractUserRequest(query);
        float[] vibeVector = extractedInfoResponse.getVibe().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getVibe());
        float[] foodVector = extractedInfoResponse.getFood().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getFood());
        float[] companionVector = extractedInfoResponse.getCompanion().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getCompanion());
        float[] purposeVector = extractedInfoResponse.getPurpose().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getPurpose());

        // 데이터베이스에서 직접 합산된 유사도로 정렬
        List<Restaurant> topRestaurants = restaurantRepository.findTopByCombinedSimilarity(
                vibeVector,
                foodVector,
                companionVector,
                purposeVector,
                5
        );

        return topRestaurants.stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }
}
