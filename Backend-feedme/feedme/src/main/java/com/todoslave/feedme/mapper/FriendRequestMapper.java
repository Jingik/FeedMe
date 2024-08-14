package com.todoslave.feedme.mapper;

import com.todoslave.feedme.DTO.FriendReqResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.repository.CreatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestMapper {

  private final CreatureRepository creatureRepository;

  @Autowired
  public FriendRequestMapper(CreatureRepository creatureRepository) {
    this.creatureRepository = creatureRepository;
  }

  public FriendReqResponseDTO toDto(FriendRequest friendRequest) {
    FriendReqResponseDTO dto = new FriendReqResponseDTO();
    dto.setId(friendRequest.getId());
    dto.setCounterpartNickname(friendRequest.getCounterpartId().getNickname());
    Creature creature = creatureRepository.findByMemberId(friendRequest.getId());

    dto.setCreatureImg("https://i11b104.p.ssafy.io/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());

    return dto;
  }

}
