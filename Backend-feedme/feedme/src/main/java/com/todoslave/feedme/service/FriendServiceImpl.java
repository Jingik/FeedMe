package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.CreatureInfoResponseDTO;
import com.todoslave.feedme.DTO.FriendInfoResponseDTO;
import com.todoslave.feedme.DTO.FriendRequestDTO;
import com.todoslave.feedme.DTO.FriendReqResponseDTO;
import com.todoslave.feedme.DTO.FriendResponseDTO;
import com.todoslave.feedme.DTO.MemberChatListResponseDTO;
import com.todoslave.feedme.DTO.PaginationRequestDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.Friend;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.mapper.FriendRequestMapper;
import com.todoslave.feedme.repository.*;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService{


//    private final MemberChatService memberChatService;
//    private final CreatureService creatureService;
//    private final MemberRepository memberRepository;
//    private final FriendRepository friendRepository;
//    private final FriendRequestRepository friendRequestRepository;




    //유틸 닉네임으로 찾기
    private Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElse(null);
    }

     @Autowired
     MemberChatService memberChatService;
     @Autowired
     CreatureService creatureService;
     @Autowired
     MemberRepository memberRepository;

     @Autowired
     AlarmService alarmService;

     @Autowired
     FriendRepository friendRepository;
     @Autowired
     FriendRequestRepository friendRequestRepository;


    private final FriendRequestMapper friendRequestMapper;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberChatMessageRepository memberChatMessageRepository;

    // 친구 요청
    @Override
    public void requestFriend(FriendRequestDTO friendRequestDTO) throws IOException {

        Member member = SecurityUtil.getCurrentMember();
        Member counterpart = findByNickname(friendRequestDTO.getCounterpartNickname());

        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setMember(counterpart);
        friendRequest.setCounterpartId(member);

        FriendRequest request = friendRequestRepository.save(friendRequest);

        FriendReqResponseDTO friendReqResponseDTO = new FriendReqResponseDTO();
        friendReqResponseDTO.setId(request.getId());
        friendReqResponseDTO.setCounterpartNickname(request.getCounterpartId().getNickname());
        friendReqResponseDTO.setCreatureImg(generateCreatureImgPath(request.getCounterpartId()));

        alarmService.requestFriendship(friendReqResponseDTO, counterpart.getId());
    }

    // 친구 삭제
    @Override
    @Transactional
    public void deleteFriend(FriendRequestDTO friendRequestDTO) {

        Member friend = memberRepository.findByNickname(friendRequestDTO.getCounterpartNickname()).orElseThrow();
        int memberId = SecurityUtil.getCurrentUserId();
        Friend friendId = friendRepository.findByMemberIdAndCounterpartId(memberId, friend.getId());
        friendRepository.deleteById(friendId.getId());

    }

    // 친구 정보 불러오기
    public FriendInfoResponseDTO getFriendInfo(FriendRequestDTO friendRequestDTO){

        FriendInfoResponseDTO response = new FriendInfoResponseDTO();

        Member member = findByNickname(friendRequestDTO.getCounterpartNickname());

        response.setFriendId(member.getId());
        response.setNickname(member.getNickname());

        List<Integer> members = new ArrayList<>();

        members.add(SecurityUtil.getCurrentUserId());
        members.add(member.getId());

        response.setRoomId(memberChatRoomRepository.findByParticipantIdsContainingAll(members).getId());

        CreatureInfoResponseDTO creatureInfoResponseDTO = creatureService.creatureInfo(member);
        response.setCreatureNickname(creatureInfoResponseDTO.getName());
        response.setCreatureImg(creatureInfoResponseDTO.getImg());
        response.setLevel(creatureInfoResponseDTO.getLevel());
        response.setExp(creatureInfoResponseDTO.getExp());
        response.setJoin(creatureInfoResponseDTO.getDay());

        return response;
    }

    // 친구 목록 불러오기
    @Override
    public List<FriendResponseDTO> getFriends() {

        int memberId = SecurityUtil.getCurrentUserId();

        List<Friend> f = friendRepository.findAllByMemberId(memberId);
        List<FriendResponseDTO> friends = new ArrayList<>();

        for(Friend friend : f){

            Member counterpart = friend.getCounterpart();
            FriendResponseDTO friendResponseDTO = new FriendResponseDTO();
            friendResponseDTO.setFriendId(friend.getId());
            friendResponseDTO.setCounterpartNickname(counterpart.getNickname());
//            friendResponseDTO.setCreatureimg(imageUtill.);
            friends.add(friendResponseDTO);

        }

        return friends;
    }

    //크리쳐 이미지 주소
    private String generateCreatureImgPath(Member member) {
        Creature creature = member.getCreature();
        int creatureLevel = creature.getLevel();
        int creatureId = creature.getId();
        return "https://i11b104.p.ssafy.io/image/creature/" + creatureId + "_" +creatureLevel;
    }



    // 친구 요청 불러오기
    public List<FriendReqResponseDTO> getRequestFriend() {

        int memberId = SecurityUtil.getCurrentUserId();

        List<FriendRequest> friendRequests = friendRequestRepository.findAllByMemberId(memberId);
        List<FriendReqResponseDTO> friendResponses = new ArrayList<>();

        for(FriendRequest f : friendRequests){

            FriendReqResponseDTO dto = new FriendReqResponseDTO();
            dto.setId(f.getId());
            dto.setCounterpartNickname(f.getCounterpartId().getNickname());
            String img = generateCreatureImgPath(f.getCounterpartId());
            dto.setCreatureImg(img);

            friendResponses.add(dto);

        }

        // Mapper 인스턴스를 사용하여 변환
        return friendResponses;
    }

    // 친구 수락
    @Override
    public MemberChatListResponseDTO insertFriendship(int requestId) {

        FriendRequest friendRequest = friendRequestRepository.findById(requestId);

        Friend friend = new Friend();

        Member member = SecurityUtil.getCurrentMember();
        Member counterpart = friendRequest.getMember();

        friend.setMember(member);
        friend.setCounterpart(counterpart);

        // 친구 추가
        friendRepository.save(friend);

        friend.setMember(counterpart);
        friend.setCounterpart(member);

        friendRepository.save(friend);

        friendRequestRepository.deleteById(requestId);

        List<Integer> members = new ArrayList<>();

        members.add(member.getId());
        members.add(counterpart.getId());

        // 채팅방 생성
        return memberChatService.insertChatRoom(members);
    }

    // 친구 거절
    @Override
    public void deleteRequestFriend(int requestId) {

        friendRequestRepository.deleteById(requestId);

    }

    // 친구 인지 확인하기
    public boolean isFriend(int memberId, int friendId) {
        return friendRepository.existsByMemberIdAndCounterpartId(memberId, friendId) ||
            friendRepository.existsByCounterpartIdAndMemberId(friendId, memberId);
    }
}
