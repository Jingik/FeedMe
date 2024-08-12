package com.todoslave.feedme.controller;

import com.todoslave.feedme.DTO.*;
import com.todoslave.feedme.domain.entity.communication.MemberChatMessage;
import com.todoslave.feedme.domain.entity.communication.MemberChatRoom;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.service.MemberChatService;
import com.todoslave.feedme.service.MemberService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friends/chats")
public class MemberChatController {

  @Autowired
  private final MemberChatService chatService;
  @Autowired
  private final MemberService memberService;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    // 추가적인 연결 초기화 로직을 여기에 작성할 수 있습니다.

    String destination = headerAccessor.getDestination();

    if (destination != null && destination.startsWith("/chatRoom/")) {
      String[] pathSegments = destination.split("/");
      if (pathSegments.length > 2) {
        String roomId = pathSegments[pathSegments.length - 1];
      }
    }

  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    // 연결 종료 시 처리할 로직을 여기에 작성할 수 있습니다.

  }


  // 유저의 채팅방 목록들 불러오기
  @GetMapping
  public ResponseEntity<List<MemberChatListResponseDTO>> findChatRoomList(){
    return ResponseEntity.ok(chatService.getChatRooms());
  }

  // 메세지 불러오기
  @MessageMapping("/loadMessages/{roomId}")
  @SendTo("/chatRoom/loadMessages/{roomId}")
  public Slice<MemberChatMessage> findMessages(@DestinationVariable String roomId,
                                              @Payload PaginationRequestDTO request){
    Slice<MemberChatMessage> messages = chatService.getChatMessage(roomId, request.getSkip(), request.getLimit());

    for (MemberChatMessage message : messages) {
      System.out.println(message.toString());
    }

    return messages;
  }

  // 메세지 저장
  @MessageMapping("/messages/{roomId}")
  @SendTo("/chatRoom/messages/{roomId}")
  public MemberChatMessageResponseDTO sendMessage(@DestinationVariable String roomId, @Payload String content)
      throws IOException {
    return chatService.insertChatMessage(roomId, content);
  }



}
