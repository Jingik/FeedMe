package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.MemberChatListResponseDTO;
import com.todoslave.feedme.DTO.MemberChatMessageRequestDTO;
import com.todoslave.feedme.DTO.MemberChatMessageResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.MemberChatMessage;
import com.todoslave.feedme.domain.entity.communication.MemberChatRoom;
import com.todoslave.feedme.domain.entity.communication.MemberChatRoomChecked;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.mapper.MessageMapper;
import com.todoslave.feedme.repository.CreatureRepository;
import com.todoslave.feedme.repository.MemberChatMessageRepository;
import com.todoslave.feedme.repository.MemberChatRoomCheckedRepository;
import com.todoslave.feedme.repository.MemberChatRoomRepository;
import com.todoslave.feedme.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberChatServiceImpl implements MemberChatService{

  @Autowired
  private final MemberChatMessageRepository messageRepository;
  @Autowired
  private final MemberChatRoomRepository roomRepository;
  @Autowired
  private final MemberChatRoomCheckedRepository roomCheckedRepository;
  @Autowired
  private final AlarmService alarmService;
  @Autowired
  private final MemberRepository memberRepository;
  @Autowired
  private final MemberChatRoomCheckedRepository memberChatRoomCheckedRepository;
  @Autowired
  private final CreatureRepository creatureRepository;

  private final MessageMapper messageMapper;

  Map<String, int[]> rooms = new HashMap<>();

  // 채팅방 목록들 가져오기
  @Override
  public List<MemberChatListResponseDTO> getChatRooms() {

    int memberId = SecurityUtil.getCurrentUserId();

    MemberChatRoom memberChatRoom = new MemberChatRoom();
    List<Integer> members = new ArrayList<>();
    members.add(memberId);

    List<MemberChatRoom> rooms = roomRepository.findAllByParticipantIdsContainingOrderByReceiveTime(members);
    List<MemberChatListResponseDTO> chatListResponse = new ArrayList<>();

    for(MemberChatRoom room : rooms){

      MemberChatListResponseDTO chatResponse = new MemberChatListResponseDTO();
      chatResponse.setId(room.getId());

      members = room.getParticipantIds();
      int counterPartId = 0;

      for(int m : members){
        if(m!=memberId){
          counterPartId=m;
        }
      }

      String nickname = memberRepository.findById(counterPartId).orElseThrow().getNickname();
      chatResponse.setNickname(nickname);
//      Creature creature = creatureRepository.findByMemberId(counterPartId);
//
//      chatResponse.setCreatureImage(
//          "http://localhost:8080/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());
      MemberChatRoomChecked checked = memberChatRoomCheckedRepository.findByMemberChatRoomIdAndMemberId(room.getId(),memberId);
      chatResponse.setIsChecked(checked.getIsChecked());
      chatListResponse.add(chatResponse);

    }

    return chatListResponse;
  }

  // 채팅방 생성
  public MemberChatListResponseDTO insertChatRoom(List<Integer> members){

    MemberChatRoom room = new MemberChatRoom();
    room.setParticipantIds(members);
    room = roomRepository.save(room);

    MemberChatRoomChecked checked1 = new MemberChatRoomChecked();
    checked1.setMemberId(members.get(0));
    checked1.setIsChecked(1);
    checked1.setMemberChatRoomId(room.getId());
    roomCheckedRepository.save(checked1);

    MemberChatRoomChecked checked2 = new MemberChatRoomChecked();
    checked2.setMemberId(members.get(1));
    checked2.setIsChecked(1);
    checked2.setMemberChatRoomId(room.getId());
    roomCheckedRepository.save(checked2);

    Member countpart = null;

    System.out.println("member 리스트 : "+members);

    for(int m : members){
      if(m!=SecurityUtil.getCurrentUserId()){
        countpart = memberRepository.findById(m).orElseThrow();
      }
    }

    Creature creature = creatureRepository.findByMemberId(countpart.getId());

    MemberChatListResponseDTO memberChatListResponseDTO = new MemberChatListResponseDTO();
    memberChatListResponseDTO.setId(room.getId());
    memberChatListResponseDTO.setNickname(countpart.getNickname());
    memberChatListResponseDTO.setCreatureImage(
        "https://i11b104.p.ssafy.io/image/creature/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());
    memberChatListResponseDTO.setIsChecked(1);

    return memberChatListResponseDTO;
  }

  // 채팅방 메세지 불러오기
  @Transactional
  public Slice<MemberChatMessageResponseDTO> getChatMessage(String roomId, int skip, int limit) {

    if (roomRepository.findById(roomId).orElseThrow() == null) {
      return null;
    }

    System.out.println("receive message? service");
    Pageable pageable = PageRequest.of(skip / limit, limit);

    Slice<MemberChatMessage> messages = messageRepository.findByMemberChatRoomIdOrderByTransmitAtDesc(roomId, pageable);

    int memberId = 1;

    System.out.println("memberId is : " + memberId);
    System.out.println("Room Id is : " + roomId);

    MemberChatRoomChecked checked = memberChatRoomCheckedRepository.findByMemberChatRoomIdAndMemberId(roomId, memberId);
    checked.setIsChecked(1);

    return messages.map(messageMapper::toDto); // MessageMapper 인스턴스를 사용하여 map 메서드 호출
  }

  // 채팅방 메세지 저장
  public MemberChatMessageResponseDTO insertChatMessage(String roomId, MemberChatMessageRequestDTO message)
      throws IOException {
    MemberChatMessage memberChatMessage = new MemberChatMessage();

    int memberId = message.getSendId();
    Member member = memberRepository.findById(memberId).orElseThrow();

    MemberChatRoom memberChatRoom = roomRepository.findById(roomId).orElseThrow();
    String counterpartNickname = null;
    int counterPartId = 0;

    for(int m : memberChatRoom.getParticipantIds()){
      if(m!=memberId){
        counterPartId = m;
        counterpartNickname = memberRepository.findById(m).orElseThrow().getNickname();
      }
    }

    memberChatMessage.setMemberChatRoomId(roomId);
    memberChatMessage.setContent(message.getContent());
    memberChatMessage.setSendId(memberId);
    System.out.println("memberID is :"+memberId);

    // 메세지 저장
    memberChatMessage = messageRepository.save(memberChatMessage);

    int[] members = rooms.get(roomId);

    int connect = 0;

    for(int i=0; i<2; i++){
      if(members[i]==counterPartId){
        connect = 1;
        break;
      }
    }

    if(connect==0){
      MemberChatRoomChecked checked = roomCheckedRepository.findByMemberChatRoomIdAndMemberId(roomId, counterPartId);
      checked.setIsChecked(0);
    }

    MemberChatListResponseDTO memberChatListResponseDTO = new MemberChatListResponseDTO();
    memberChatListResponseDTO.setId(roomId);
    memberChatListResponseDTO.setNickname(counterpartNickname);
    Creature creature = creatureRepository.findByMemberId(counterPartId);

    memberChatListResponseDTO.setCreatureImage("https://i11b104.p.ssafy.io/image/creature/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());

    // 채팅방 갱신 (나)
    alarmService.renewChattingRoom(memberChatListResponseDTO, memberId,1);

    memberChatListResponseDTO.setNickname(member.getNickname());
    creature = creatureRepository.findByMemberId(memberId);

    memberChatListResponseDTO.setCreatureImage("https://i11b104.p.ssafy.io/image/creature/image/creature/"+creature.getMember().getId()+"_"+creature.getLevel());

    // 채팅방 갱신 (상대)
    alarmService.renewChattingRoom(memberChatListResponseDTO, counterPartId, 0);


    MemberChatMessageResponseDTO response = new MemberChatMessageResponseDTO();

    response.setMessage(memberChatMessage.getContent());
    response.setTransmitAt(memberChatMessage.getTransmitAt());
//    response.setSendNickname(SecurityUtil.getCurrentMember().getNickname());

    return response;
  }

  @Override
  @Transactional
  public void enterTheRoom(String roomId) {

    if(rooms.get(roomId)==null){
      int[] members = new int[2];
      members[0] = SecurityUtil.getCurrentUserId();
      rooms.put(roomId, members);
    }else{
      int[] members = rooms.get(roomId);
      for(int i=0; i<2; i++){
        if(members[i]==-1){
          members[i] = SecurityUtil.getCurrentUserId();
          break;
        }
      }
    }

    MemberChatRoomChecked checked = roomCheckedRepository.findByMemberChatRoomIdAndMemberId(roomId, SecurityUtil.getCurrentUserId());
    checked.setIsChecked(1);

    System.out.println("enter the room : "+rooms.get(roomId));

  }

  @Override
  public void exitTheRoom(String roomId) {

    if(rooms.get(roomId)!=null) {
      int[] members = rooms.get(roomId);

      for (int i = 0; i < 2; i++) {
        if (members[i] == SecurityUtil.getCurrentUserId()) {
          members[i] = -1;
          break;
        }
      }
    }

  }

}