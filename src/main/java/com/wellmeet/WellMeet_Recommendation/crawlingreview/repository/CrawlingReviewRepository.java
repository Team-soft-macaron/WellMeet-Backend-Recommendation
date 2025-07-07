package com.wellmeet.WellMeet_Recommendation.crawlingreview.repository;

import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingReviewRepository extends JpaRepository<CrawlingReview, Long> {
}
