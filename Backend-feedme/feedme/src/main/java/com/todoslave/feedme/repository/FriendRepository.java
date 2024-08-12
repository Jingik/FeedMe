package com.todoslave.feedme.repository;

import com.todoslave.feedme.domain.entity.communication.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {


    List<Friend> findAllByMemberId(int memberId);
    Friend findByMemberIdAndCounterpartId(int memberId, int counterpartId);

    boolean existsByMemberIdAndCounterpartId(int memberId, int counterpartId);
    boolean existsByCounterpartIdAndMemberId(int counterpartId, int memberId);

}
