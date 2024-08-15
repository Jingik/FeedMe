package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.MemberChatResponseDTO;
import com.todoslave.feedme.DTO.RagResponseDTO;
import com.todoslave.feedme.service.CreatureChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class CreatureChatController {

    private final CreatureChatService creatureChatService;

    @GetMapping("/creature")
    public ResponseEntity<RagResponseDTO> getCreatureChatData(@RequestParam("ragQuestion")String ragQuestion) {
        RagResponseDTO chatData = creatureChatService.getCreatureChat(ragQuestion);
        return ResponseEntity.ok(chatData);
    }
}