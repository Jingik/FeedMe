package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.MemberSearchResponseDTO;
import com.todoslave.feedme.DTO.MemberSignupRequestDTO;
import com.todoslave.feedme.DTO.MypageResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.FriendRepository;
import com.todoslave.feedme.repository.FriendRequestRepository;
import com.todoslave.feedme.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final FriendRepository friendRepository;

    private final FriendRequestRepository friendRequestRepository;

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




    // 친구 인지 확인하기!!!
    public boolean isFriend(int memberId, int friendId) {
        return friendRepository.existsByMemberIdAndCounterpartId(memberId, friendId) ||
                friendRepository.existsByCounterpartIdAndMemberId(friendId, memberId);
    }

    @Override //맴버 리스트 검색 가져오기
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


            if (isFriend(member.getId(), SecurityUtil.getCurrentUserId())) {
                mem.setFriend(true);
            } else {
                mem.setFriend(false);
            }

            // 친구 신청을 보냈는지 체크
            Optional<FriendRequest> friendRequest = friendRequestRepository.findByMember_IdAndCounterpartId_Id(member.getId(),SecurityUtil.getCurrentUserId());
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


        LocalDate birthdayTimestamp = member.getBirthday();

        myPage.setBrithday(birthdayTimestamp);

        myPage.setSendId(member.getId());
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
