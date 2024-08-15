package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.CreatureChatResponseDTO;
import com.todoslave.feedme.DTO.MemberChatResponseDTO;
import com.todoslave.feedme.DTO.RagResponseDTO;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.domain.entity.task.CreatureTodo;
import com.todoslave.feedme.domain.entity.task.Todo;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.CreatureTodoReposito;
import com.todoslave.feedme.repository.TodoRepository;
import com.todoslave.feedme.service.CreatureChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CreatureChatServiceImpl implements CreatureChatService {

    private final TodoRepository todoRepository;
    private final CreatureTodoReposito creatureTodoReposito;
    private final RestTemplate restTemplate;  // RestTemplate 주입

    @Override
    public RagResponseDTO getCreatureChat(String question) {

        Member member = SecurityUtil.getCurrentMember();
        int memberId = member.getId();
        Map<LocalDate, List<String>> groupedData = new HashMap<>();

        // 멤버의 모든 Todo 리스트 가져오기
        List<Todo> todoList = todoRepository.findByMemberId(memberId);
        for (Todo todo : todoList) {
            if (todo.getIsCompleted() == 1) {
                LocalDate date = todo.getCreatedAt();
                groupedData.computeIfAbsent(date, k -> new ArrayList<>()).add(todo.getContent());
            }
        }

        // 멤버의 모든 CreatureTodo 리스트 가져오기
        List<CreatureTodo> creatureTodoList = creatureTodoReposito.findByMemberId(memberId);
        for (CreatureTodo creatureTodo : creatureTodoList) {
            if (creatureTodo.getIsCompleted() == 1) {
                LocalDate date = creatureTodo.getCreatedAt();
                groupedData.computeIfAbsent(date, k -> new ArrayList<>()).add(creatureTodo.getContent());
            }
        }

        // 날짜별 데이터 리스트를 생성
        List<CreatureChatResponseDTO> chatData = groupedData.entrySet().stream()
                .map(entry -> {
                    CreatureChatResponseDTO dto = new CreatureChatResponseDTO();
                    dto.setDay(entry.getKey());
                    dto.setContents(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());

        // 최종 DTO 생성 및 반환
        MemberChatResponseDTO requestDTO = new MemberChatResponseDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setQuestion(question);
        requestDTO.setChatData(chatData);

//        System.out.println(requestDTO);
//        return requestDTO;

        // Flask API와 통신하기 위한 설정
        String flaskUrl = "https://magnetic-ram-brave.ngrok-free.app/rag";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MemberChatResponseDTO> entity = new HttpEntity<>(requestDTO, headers);

        // Flask 서버로 POST 요청 보내기
        ResponseEntity<RagResponseDTO> response = restTemplate.postForEntity(flaskUrl, entity, RagResponseDTO.class);

        // Flask로부터 받은 응답 반환
        return response.getBody();
    }
}
