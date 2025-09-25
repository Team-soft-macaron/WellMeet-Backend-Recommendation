package com.wellmeet.WellMeet_Recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WellMeetRecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellMeetRecommendationApplication.class, args);
	}

}
