package com.todoslave.feedme.repository;

import com.todoslave.feedme.domain.entity.task.Todo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {

  @Query("SELECT t FROM Todo t WHERE t.member.id = :memberId AND FUNCTION('DATE', t.createdAt) = :createdAt")
  List<Todo> findAllByMemberIdAndCreatedAt(@Param("memberId") int memberId, @Param("createdAt") LocalDate createdAt);

  @Query("SELECT t FROM Todo t WHERE t.member.id = :memberId AND FUNCTION('DATE', t.createdAt) = :createdAt AND t.isCompleted = :isCompleted")
  List<Todo> findAllByMemberIdAndCreatedAtIsCompleted(@Param("memberId") int memberID, @Param("createdAt") LocalDate createdAt,@Param("isCompleted") int isCompleted);

  List<Integer> findMemberIdAllByCreatedAtAndIsCompleted(LocalDate createdAt, int isCompleted);

  @Query("SELECT COUNT(t) FROM Todo t WHERE t.createdAt = :date AND t.isCompleted = :isCompleted")
  long countTodoByDateAndIsCompleted(@Param("date") LocalDate date, @Param("isCompleted") int isCompleted);

  List<Todo> findByMemberIdAndCreatedAt(int memberId, LocalDate createdAt);

  List<Todo> findByMemberId(int id);
}
