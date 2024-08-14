package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.CreatureChatResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.membership.Member;

import java.util.List;

public interface CreatureChatService {

    // 날자와, 한 일을 도려줄건데

    List<CreatureChatResponseDTO> getCreatureChat();




}
