package com.todoslave.feedme.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class CreatureTodoResponseDTO {
    private int id;
    private String content;
    private LocalDate createdAt;
    private int isCompleted;
//    private boolean diaryIsCompleted;
}
