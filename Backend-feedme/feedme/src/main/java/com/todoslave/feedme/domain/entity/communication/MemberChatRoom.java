package com.todoslave.feedme.domain.entity.communication;

import com.todoslave.feedme.domain.entity.membership.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "memberchatroom")
public class MemberChatRoom {

    @Id
    private String id;
    @Field("participant_ids")
    private List<Integer> participantIds;
    @Field("receive_time")
    private LocalDateTime receiveTime;

    public MemberChatRoom(){
        receiveTime = LocalDateTime.of(1970, 1, 1, 0, 0);
    }

}
