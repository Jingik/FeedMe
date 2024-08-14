package com.todoslave.feedme.repository;

import com.todoslave.feedme.domain.entity.alarm.Alarm;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

  List<Alarm> findByMemberId(int memberId);

}

