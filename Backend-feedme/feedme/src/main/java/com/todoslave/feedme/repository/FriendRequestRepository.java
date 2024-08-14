package com.todoslave.feedme.repository;

import com.todoslave.feedme.domain.entity.communication.Friend;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.domain.entity.membership.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    FriendRequest findById(int id);
    List<FriendRequest> findAllByMemberId(int memberId);
    Optional<FriendRequest> findByMember_IdAndCounterpartId_Id(int memberId, int counterpartId);
}
