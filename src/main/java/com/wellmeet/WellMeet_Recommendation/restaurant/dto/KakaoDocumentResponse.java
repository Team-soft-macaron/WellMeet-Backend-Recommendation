package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoDocumentResponse {
    private String addressName;
    @JsonProperty("category_group_code")
    private String categoryGroupCode;
    @JsonProperty("category_group_name")
    private String categoryGroupName;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("distance")
    private String distance;
    private String id;
    private String phone;
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("place_url")
    private String placeUrl;
    @JsonProperty("road_address_name")
    private String roadAddressName;
    private String x;
    private String y;
}
