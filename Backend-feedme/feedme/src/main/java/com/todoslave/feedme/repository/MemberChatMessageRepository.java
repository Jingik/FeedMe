package com.todoslave.feedme.repository;

import com.todoslave.feedme.domain.entity.communication.MemberChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface MemberChatMessageRepository extends
    MongoRepository<MemberChatMessage, String> {

  // 메세지 불러오기
  Slice<MemberChatMessage> findByMemberChatRoomIdOrderByTransmitAtDesc(String memberChatRoomId, Pageable pageable);
  void deleteAllByMemberChatRoomId(String memberChatRoomId);

}
