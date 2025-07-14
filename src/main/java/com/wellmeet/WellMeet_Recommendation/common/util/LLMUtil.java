package com.wellmeet.WellMeet_Recommendation.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import java.util.List;
import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class LLMUtil {

    private final OpenAiChatModel chatModel;

    public ExtractedInfoResponse extractUserRequest(String userRequest) {
        var outputConverter = new BeanOutputConverter<>(ExtractedInfoResponse.class);

        String systemPrompt = """
                당신은 한국어 모임 요청을 분석하는 전문가입니다.
                사용자의 요청을 분석하여 정확히 4가지 정보만 추출해주세요.

                추출할 정보:
                1. purpose (목적): 모임의 목적 - 생일, 기념일, 회식, 데이트, 가족모임 등
                2. vibe (분위기): 원하는 분위기 - 조용한, 활기찬, 로맨틱한, 편안한, 고급스러운 등
                3. companion (동행자): 함께 가는 사람 - 가족, 친구, 연인, 동료, 부모님 등
                4. food (음식): 선호하는 음식 종류 - 한식, 일식, 양식, 중식, 이탈리안 등

                응답 규칙:
                - 모든 값은 반드시 한글 String으로 작성
                - 여러 특성이 있으면 "~고"로 연결 (예: "조용하고 편안한")
                - 언급되지 않은 정보는 ""으로 표시
                - JSON 형식으로만 응답

                """ + outputConverter.getFormat();

        var prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage("다음 요청을 분석해주세요: " + userRequest)));

        ChatResponse response = chatModel.call(prompt);
        String content = response.getResult().getOutput().getText();
        log.info("ChatResponse: {}", content);
        return outputConverter.convert(content);
    }

}