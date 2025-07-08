package com.wellmeet.WellMeet_Recommendation.embedding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextEmbedding {
    private String text;
    private List<Double> embedding;
    private Integer dimension;
    private String model;
}
