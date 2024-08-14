package com.todoslave.feedme.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class FriendReqResponseDTO {

  // 친구 요청 ID
  private int id;

  // 상대방 닉네임
  private String counterpartNickname;

  // 상대방 크리쳐 이미지
  private String creatureImg;

}
