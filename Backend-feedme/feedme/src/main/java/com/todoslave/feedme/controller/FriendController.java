package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.FriendInfoResponseDTO;
import com.todoslave.feedme.DTO.FriendReqResponseDTO;
import com.todoslave.feedme.DTO.FriendRequestDTO;
import com.todoslave.feedme.DTO.FriendResponseDTO;
import com.todoslave.feedme.DTO.MemberChatListResponseDTO;
import com.todoslave.feedme.DTO.PaginationRequestDTO;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.service.AlarmService;
import com.todoslave.feedme.service.FriendService;
import com.todoslave.feedme.service.MemberChatService;
import com.todoslave.feedme.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    MemberService memberService;
    @Autowired
    FriendService friendService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    MemberChatService memberChatService;

    // 친구 요청하기
    @PostMapping
    public ResponseEntity<Void> addFriend(@RequestBody FriendRequestDTO friendReqRequestDTO) throws IOException {

        friendService.requestFriend(friendReqRequestDTO);
        return ResponseEntity.noContent().build();
    }

    // 친구 삭제하기
    @DeleteMapping()
    public ResponseEntity<Void> removeFriend(@RequestParam("counterpartNickname") String counterpartNickname){
        FriendRequestDTO friendRequestDTO = new FriendRequestDTO();
        friendRequestDTO.setCounterpartNickname(counterpartNickname);
        friendService.deleteFriend(friendRequestDTO);
        return ResponseEntity.noContent().build();
    }

    //친구 정보 얻기
    @Operation(summary = "친구 닉네임 검색")
    @GetMapping("/info")
    public ResponseEntity<FriendInfoResponseDTO> findFriendInfo(@RequestParam String counterpartNickname){
        FriendRequestDTO friendRequestDTO = new FriendRequestDTO();
        friendRequestDTO.setCounterpartNickname(counterpartNickname);
        return ResponseEntity.ok(friendService.getFriendInfo(friendRequestDTO));
    }

    // 친구 목록 불러오기
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponseDTO>> findFriends(){
        return ResponseEntity.ok(friendService.getFriends());
    }

    // 친구 요청 목록 조회
    @GetMapping("/request")
    public ResponseEntity<List<FriendReqResponseDTO>> findRequestFriend() {
        return ResponseEntity.ok(friendService.getRequestFriend());
    }

    // 친구 요청 수락하기
    @PostMapping("/accept/{id}")
    public ResponseEntity<MemberChatListResponseDTO> acceptFriendship(@PathVariable("id") int requestId){
        return ResponseEntity.ok(friendService.insertFriendship(requestId));
    }

    // 친구 요청 거절하기
    @PostMapping("/reject/{id}")
    public ResponseEntity<Void> rejectFriendship(@PathVariable("id") int requestId){
        friendService.deleteRequestFriend(requestId);
        return ResponseEntity.noContent().build();
    }


}
