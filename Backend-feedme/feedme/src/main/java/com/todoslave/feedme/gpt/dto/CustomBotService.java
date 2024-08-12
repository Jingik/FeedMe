package com.todoslave.feedme.gpt.dto;

import com.todoslave.feedme.gpt.dto.ChatGPTRequest;
import com.todoslave.feedme.gpt.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomBotService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    public String chat(String prompt) {
        // ChatGPT 요청 생성 및 응답 받기
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
        String responseContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        // 요청에 포함된 형식에 맞춰 데이터를 가공
        return processRequestBasedOnFormat(prompt, responseContent);
    }

    private String processRequestBasedOnFormat(String prompt, String responseContent) {
        // 요청에 "20개의 자료", " ,로 이어서" 등 특정 패턴이 있는지 검사
        if (prompt.contains("20개의 자료") && prompt.contains(",로 이어서")) {
            // 데이터를 20개로 나누고 콤마로 연결
            List<String> dataList = Arrays.asList(responseContent.split("\\s+")).subList(0, Math.min(20, responseContent.split("\\s+").length));
            return String.join(", ", dataList);
        }
        // 다른 형식의 처리도 추가 가능
        return responseContent;
    }
}
