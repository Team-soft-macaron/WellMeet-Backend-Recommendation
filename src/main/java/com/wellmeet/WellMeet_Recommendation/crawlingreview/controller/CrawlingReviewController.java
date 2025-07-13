package com.wellmeet.WellMeet_Recommendation.crawlingreview.controller;

import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.CrawlingReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CrawlingReviewController {

    private final CrawlingReviewService crawlingReviewService;

    @PostMapping("/api/crawling-reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public CrawlingReviewResponse saveReview(@RequestBody CrawlingReviewSaveRequest request) {
        return crawlingReviewService.saveReview(request);
    }
}
