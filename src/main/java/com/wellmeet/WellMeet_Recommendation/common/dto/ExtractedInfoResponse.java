package com.wellmeet.WellMeet_Recommendation.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExtractedInfoResponse {
    private String purpose;
    private String vibe;
    private String companion;
    private String food;

    public ExtractedInfoResponse(String vibe, String food, String companion, String purpose) {
        this.purpose = purpose;
        this.vibe = vibe;
        this.companion = companion;
        this.food = food;
    }
}
