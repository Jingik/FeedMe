package com.todoslave.feedme.DTO;

import lombok.Data;

@Data
public class FriendInfoResponseDTO {

  private int friendId;
  private String roomId;
  private String nickname;
  private String creatureNickname;
  private String creatureImg;
  private int level;
  private int exp;
  private int join;

}
