package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.CreatureChatResponseDTO;
import com.todoslave.feedme.service.CreatureChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/creature-chat")
public class CreatureChatController {

    private final CreatureChatService creatureChatService;

    @GetMapping
    public ResponseEntity<List<CreatureChatResponseDTO>> getCreatureChat() {
        List<CreatureChatResponseDTO> responseDTOList = creatureChatService.getCreatureChat();
        return ResponseEntity.ok(responseDTOList);
    }
}
