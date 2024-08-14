package com.todoslave.feedme.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatureChatResponseDTO {

    LocalDate day;
    String content;

}
