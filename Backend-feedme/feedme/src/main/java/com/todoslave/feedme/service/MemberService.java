package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.MemberSearchResponseDTO;
import com.todoslave.feedme.DTO.MemberSignupRequestDTO;
import com.todoslave.feedme.DTO.MypageResponseDTO;
import com.todoslave.feedme.domain.entity.membership.Member;

import java.util.List;
import java.util.Optional;

// import java.util.stream.Collectors;

//@Service
// @Transactional
// public class MemberService {

//     @Autowired
//     MemberRepository memberRepository;
//     @Autowired
//     FriendService friendService;


public interface MemberService {

    // 아이디로 회원 찾기
    Member findById(int userId);

    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    // 닉네임으로 회원 찾기
    Member findByNickname(String nickname);

    // 회원 가입 처리
    Member registerMember(MemberSignupRequestDTO memberSignupRequestDTO);

    // 회원 정보 수정
    Member updateMember(MemberSignupRequestDTO memberSignupRequestDTO);

    // 회원 탈퇴
    boolean removeMember();

    // 닉네임으로 회원 검색
    List<MemberSearchResponseDTO> getMemberList(String searchvalue);

    // 닉네임 중복 체크
    boolean checkNickname(String nickname);

    // 마이페이지 불러오기
    MypageResponseDTO getMyPage();
}
