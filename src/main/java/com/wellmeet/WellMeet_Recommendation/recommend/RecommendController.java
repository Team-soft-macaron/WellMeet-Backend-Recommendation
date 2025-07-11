package com.wellmeet.WellMeet_Recommendation.recommend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

}
