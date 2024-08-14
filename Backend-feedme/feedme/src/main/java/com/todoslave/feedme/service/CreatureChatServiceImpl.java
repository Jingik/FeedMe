package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.CreatureChatResponseDTO;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.domain.entity.task.CreatureTodo;
import com.todoslave.feedme.domain.entity.task.Todo;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.CreatureTodoReposito;
import com.todoslave.feedme.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreatureChatServiceImpl implements CreatureChatService {

    private final TodoRepository todoRepository;
    private final CreatureTodoReposito creatureTodoReposito;

    @Override
    public List<CreatureChatResponseDTO> getCreatureChat() {

        Member member = SecurityUtil.getCurrentMember();
        List<CreatureChatResponseDTO> responseDTOList = new ArrayList<>();

        // 멤버의 모든 Todo 리스트 가져오기
        List<Todo> todoList = todoRepository.findByMemberId(member.getId());
        for (Todo todo : todoList) {
            if(todo.getIsCompleted()==1){
            CreatureChatResponseDTO dto = new CreatureChatResponseDTO();
            dto.setDay(todo.getCreatedAt()); // LocalDate로 변환
            dto.setContent(todo.getContent());
            responseDTOList.add(dto);}
        }

        // 멤버의 모든 CreatureTodo 리스트 가져오기
        List<CreatureTodo> creatureTodoList = creatureTodoReposito.findByMemberId(member.getId());
        for (CreatureTodo creatureTodo : creatureTodoList) {
            if(creatureTodo.getIsCompleted()==1){
            CreatureChatResponseDTO dto = new CreatureChatResponseDTO();
            dto.setDay(creatureTodo.getCreatedAt()); // LocalDate로 변환
            dto.setContent(creatureTodo.getContent());
            responseDTOList.add(dto);
            }
        }

        return responseDTOList;
    }
}
