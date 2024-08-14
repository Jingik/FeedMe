package com.todoslave.feedme.DTO;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberSignupRequestDTO {

    private String email;

    private String nickname;

    private String userRole = "ROLE_USER";

//    Timestamp birthday;
    private LocalDate birthday;
}
