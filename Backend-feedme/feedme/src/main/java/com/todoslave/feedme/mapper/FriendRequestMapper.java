package com.todoslave.feedme.mapper;

import com.todoslave.feedme.DTO.FriendReqResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.repository.CreatureRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendRequestMapper {

  private static CreatureRepository creatureRepository;

  public static FriendReqResponseDTO toDto(FriendRequest friendRequest){
    FriendReqResponseDTO dto = new FriendReqResponseDTO();
    dto.setId(friendRequest.getId());
    dto.setCounterpartNickname(friendRequest.getCounterpartId().getNickname());
    Creature creature = creatureRepository.findByMemberId(friendRequest.getId());
    dto.setCreatureImg("https://i11b104.p.ssafy.io/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());
    return dto;
  }

}
