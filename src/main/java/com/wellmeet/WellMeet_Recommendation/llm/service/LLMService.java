package com.wellmeet.WellMeet_Recommendation.llm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {

    private final OpenAiChatModel chatModel;



}
