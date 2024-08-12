package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.MemberSearchResponseDTO;
import com.todoslave.feedme.DTO.MemberSignupRequestDTO;
import com.todoslave.feedme.DTO.MypageResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.FriendRequestRepository;
import com.todoslave.feedme.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional

public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FriendRequestRepository requestRepository;

    @Autowired
     FriendService friendService;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Override
    public Member insertMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member findById(int userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found by id: " + userId));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElse(null);
    }

    @Override
    public boolean authenticate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Override
    public Member registerMember(MemberSignupRequestDTO memberSignupRequestDTO) {
        Member member = new Member();
        member.setEmail(memberSignupRequestDTO.getEmail());
        member.setBirthday(memberSignupRequestDTO.getBirthday());
        member.setNickname(memberSignupRequestDTO.getNickname());
        member.setUserRole(memberSignupRequestDTO.getUserRole());

        return memberRepository.save(member);
    }

    @Override
    public Member updateMember(MemberSignupRequestDTO memberSignupRequestDTO) {
        int id = SecurityUtil.getCurrentUserId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        member.setNickname(memberSignupRequestDTO.getNickname());
        member.setBirthday(memberSignupRequestDTO.getBirthday());
        member.setUserRole(memberSignupRequestDTO.getUserRole());

        return memberRepository.save(member);
    }

    @Override
    public boolean removeMember() {
        Member member = SecurityUtil.getCurrentMember();

        if (member == null) {
            return false;
        }
        memberRepository.delete(member);
        return true;
    }

    @Override
    public List<MemberSearchResponseDTO> getMemberList(String searchvalue) {

        List<Member> members = memberRepository.findByNicknameContaining(searchvalue);
        List<MemberSearchResponseDTO> memberSearchResponse = new ArrayList<>();

        for (Member member : members) {
            MemberSearchResponseDTO mem = new MemberSearchResponseDTO();
            mem.setNickname(member.getNickname());

            if (member.getCreature() != null) {
                mem.setCreatureImg(generateCreatureImgPath(member));
            } else {
                mem.setCreatureImg("알 이미지");
            }

            if (member.getId() == SecurityUtil.getCurrentUserId()) {
                continue;
            }



            if (friendService.isFriend(member.getId(), SecurityUtil.getCurrentUserId())) {
                mem.setFriend(true);
            } else {
                mem.setFriend(false);
            }

            // 친구 신청을 보냈는지 체크
            Optional<FriendRequest> friendRequest = friendRequestRepository.findByMember_IdAndCounterpartId_Id(SecurityUtil.getCurrentUserId(), member.getId());
            mem.setRequested(friendRequest.isPresent());

            memberSearchResponse.add(mem);
        }

        return memberSearchResponse;
    }

    @Override
    public boolean checkNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    @Override
    public MypageResponseDTO getMyPage() {
        Member member = SecurityUtil.getCurrentMember();
        Creature creature = member.getCreature();

        MypageResponseDTO myPage = new MypageResponseDTO();

        myPage.setNickname(member.getNickname());
        myPage.setEmail(member.getEmail());

        Timestamp birthdayTimestamp = member.getBirthday();
        LocalDate birthdayLocalDate = birthdayTimestamp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        myPage.setBrithday(birthdayLocalDate);

        myPage.setCreatureId(creature.getId());
        myPage.setCreatureName(creature.getCreatureName());
        myPage.setExp(creature.getExp());
        myPage.setLevel(creature.getLevel());
        myPage.setImage(generateCreatureImgPath(member));

        LocalDate currentDate = LocalDate.now();
        LocalDate joinDate = member.getJoinDate().toLocalDate();
        int daysSinceJoin = Period.between(joinDate, currentDate).getDays();

        myPage.setTogetherDay(daysSinceJoin);

        return myPage;
    }

    private String generateCreatureImgPath(Member member) {
        Creature creature = member.getCreature();
        int creatureLevel = creature.getLevel();
        int creatureId = creature.getId();
        return "https://i11b104.p.ssafy.io/image/creature/" + creatureId + "_" + creatureLevel;
    }
}



//impl 없을 때
//package com.todoslave.feedme.service;

//
//import com.todoslave.feedme.DTO.MemberSearchResponseDTO;
//import com.todoslave.feedme.DTO.MemberSignupRequestDTO;
//import com.todoslave.feedme.DTO.MypageResponseDTO;
//import com.todoslave.feedme.domain.entity.avatar.Creature;
//import com.todoslave.feedme.domain.entity.membership.Emotion;
//import com.todoslave.feedme.domain.entity.membership.Member;
//import com.todoslave.feedme.login.util.SecurityUtil;
//import com.todoslave.feedme.repository.MemberRepository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.Period;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
////@Transactional(readOnly = true) //조회에선
//@RequiredArgsConstructor // 생성자 만들어 주는 얘
//public class MemberService {
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    FriendService friendService;
//
//
//    //그냥 가입 시켜주는 얘
//    public Member insertMember (Member member) {
//        return memberRepository.save(member);
//    }
//
//    // 회원 전체 조회
//    public List<Member> findMembers() {
//        return memberRepository.findAll();
//    }
//
//    //아이디로 맴버 찾기
//    public Member findById(int userId) {
//        return memberRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Member not found by id: " + userId));
//    }
//
//    //이메일로 찾기
//    public Optional<Member> findByEmail(String email) {
//        return memberRepository.findByEmail(email);
//    }
//
//    // 닉네임으로 맴버 찾기
//    public Member findByNickname(String Nickname) {
//        return memberRepository.findByNickname(Nickname).orElse(null);
//    }
//
//    public boolean authenticate(String email) {
//        return memberRepository.findByEmail(email).isPresent();
//    }
//
//    //회원가입
//    public Member registerMember(MemberSignupRequestDTO memberSignupRequestDTO) {
////        memberRepository.findByEmail(memberSignup.getEmail()).orElseThrow(() -> new RuntimeException("Member not found by email: " + memberSignup.getEmail()));
//
//        Member member = new Member();
//        member.setEmail(memberSignupRequestDTO.getEmail());
//        member.setBirthday(memberSignupRequestDTO.getBirthday());
//        member.setNickname(memberSignupRequestDTO.getNickname());
//        member.setUserRole(memberSignupRequestDTO.getUserRole());
//
//        memberRepository.save(member);
//        return member;
//    }
//
//    //맴버 정보 수정
//    public Member updateMember(MemberSignupRequestDTO memberSignupRequestDTO) {
//        int id = SecurityUtil.getCurrentUserId();
//
//        Member member = memberRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//
//        member.setNickname(memberSignupRequestDTO.getNickname());
//        member.setBirthday(memberSignupRequestDTO.getBirthday());
//        member.setUserRole(memberSignupRequestDTO.getUserRole());
//
//        return memberRepository.save(member);
//    }
//
//    //탈퇴
//    public boolean removeMember() {
//        Member member = SecurityUtil.getCurrentMember();
//
//        if (member == null) {
//            return false;
//        }
//        memberRepository.delete(member);
//        return true;
//    }
//
//    //닉네임에 맞는 친구 데려오기
//    public List<MemberSearchResponseDTO> getMemberList(String searchvalue) {
//
//        List<Member> members = memberRepository.findByNicknameContaining(searchvalue);
//        List<MemberSearchResponseDTO> memberSearchResponse = new ArrayList<>();
//
//        for (Member member : members) {
//
//            MemberSearchResponseDTO mem = new MemberSearchResponseDTO();
//            mem.setNickname(member.getNickname());
//
//            // 크리쳐 존재 여부를 확인
//            if (member.getCreature() != null) {
//                mem.setCreatureImg(generateCreatureImgPath(member));
//            } else {
//                // 크리쳐가 없는 경우 수행할 다른 액션
//                mem.setCreatureImg("알 이미지");
//            }
//
//            if(member.getId() == SecurityUtil.getCurrentUserId()) {
//                continue;
//            }
//
//            if(friendService.isFriend(member.getId(), SecurityUtil.getCurrentUserId())) {
//                mem.setFriend(true);
//            } else {
//                mem.setFriend(false);
//            }
//
//            memberSearchResponse.add(mem);
//        }
//
//        return memberSearchResponse;
//    }
//
//
//    //닉네임 중복 체크
//    public boolean checkNickname(String nickname) {
//
//        return memberRepository.findByNickname(nickname).isPresent();
//
//    }
//
//    //크리쳐 저장 장소
//    private String generateCreatureImgPath(Member member) {
//            Creature creature = member.getCreature();
//            int creatureLevel = creature.getLevel();
//            int creatureId = creature.getId();
//            return "http://localhost:8080/image/creature/" + creatureId + "_" +creatureLevel;
//    }
//
//    //마이 페이지 불러오기
//    public MypageResponseDTO getMyPage() {
//        Member member = SecurityUtil.getCurrentMember();
//        Creature creature = member.getCreature();
//
//        MypageResponseDTO myPage = new MypageResponseDTO();
//
//        myPage.setNickname(member.getNickname());
//        myPage.setEmail(member.getEmail());
//
//
//        //생일 바꾸기
//        Timestamp birthdayTimestamp = member.getBirthday();
//
//        // Timestamp를 LocalDate로 변환
//        LocalDate birthdayLocalDate = birthdayTimestamp.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//
//        // 변환된 LocalDate를 myPage에 설정
//        myPage.setBrithday(birthdayLocalDate);
//
//        myPage.setCreatureId(creature.getId());
//        myPage.setCreatureName(creature.getCreatureName());
//        myPage.setExp(creature.getExp());
//        myPage.setLevel(creature.getLevel());
//        myPage.setImage(generateCreatureImgPath(member));
//
//
//        LocalDate currentDate = LocalDate.now();
//        // 가입 날짜 가져오기 (Timestamp를 LocalDate로 변환)
//        LocalDate joinDate = member.getJoinDate().toLocalDate();
//        // 가입한 날로부터 며칠째인지 계산
//        int daysSinceJoin = Period.between(joinDate, currentDate).getDays();
//
//        myPage.setTogetherDay(daysSinceJoin);
//
//        return myPage;
//    }
//
//}
