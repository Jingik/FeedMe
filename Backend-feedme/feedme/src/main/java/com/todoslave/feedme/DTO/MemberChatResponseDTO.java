package com.todoslave.feedme.DTO;

import lombok.Data;

import java.util.List;

@Data
public class MemberChatResponseDTO {
    private String question;
    private int memberId;
    private List<CreatureChatResponseDTO> chatData;

}