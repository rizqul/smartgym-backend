package com.gymmick.smartgym.services.ai.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.ai.GroqAiResponse;
import com.gymmick.smartgym.services.ai.GroqService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroqServiceImpl implements GroqService {

  private final ChatClient chatClient;

  @Override
  public GroqAiResponse generateWorkout(String request) {

    BeanOutputConverter<GroqAiResponse> converter = new BeanOutputConverter<>(GroqAiResponse.class);

    String rawText = chatClient.prompt()
        .user(u -> u.text("""
              {request}
              {format}
            """)
            .param("request", request)
            .param("format", converter.getFormat()))
        .call()
        .content();

    log.info("Raw AI response text: {}", rawText);

    GroqAiResponse result = chatClient.prompt()
        .user(request)
        .call()
        .entity(GroqAiResponse.class);

    return result;
  }

}
