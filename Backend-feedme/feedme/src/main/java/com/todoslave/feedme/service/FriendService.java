package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.FriendInfoResponseDTO;
import com.todoslave.feedme.DTO.FriendRequestDTO;
import com.todoslave.feedme.DTO.FriendReqResponseDTO;
import com.todoslave.feedme.DTO.FriendResponseDTO;
import com.todoslave.feedme.DTO.MemberChatListResponseDTO;
import com.todoslave.feedme.DTO.PaginationRequestDTO;

import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface FriendService {

    // 친구 추가
    void requestFriend(FriendRequestDTO friendRequestDTO) throws IOException;

    // 친구 삭제
    void deleteFriend(FriendRequestDTO friendRequestDTO);

    // 친구 정보 불러오기
    FriendInfoResponseDTO getFriendInfo(FriendRequestDTO friendRequestDTO);

    // 친구 목록 조회
    List<FriendResponseDTO> getFriends();

    // 친구 요청 목록 조회
    List<FriendReqResponseDTO> getRequestFriend();

    // 친구 수락
    MemberChatListResponseDTO insertFriendship(int requestId);

    // 친구 거절
    void deleteRequestFriend(int requestId);

    //친구인지 확인
    boolean isFriend(int memberId, int friendId);

}
