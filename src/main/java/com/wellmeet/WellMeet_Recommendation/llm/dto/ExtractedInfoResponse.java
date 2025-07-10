package com.wellmeet.WellMeet_Recommendation.llm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExtractedInfoResponse {
    private String purpose;      // 모임 목적
    private String vibe;         // 분위기
    private String companion;    // 동행자 정보
    private String food;      // 음식
    public ExtractedInfoResponse(String purpose, String vibe, String companion, String food){
        this.purpose = purpose;
        this.vibe = vibe;
        this.companion = companion;
        this.food = food;
    }
}
