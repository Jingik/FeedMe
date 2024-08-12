package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.CreatureInfoResponseDTO;
import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.membership.Emotion;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.CreatureRepository;
import com.todoslave.feedme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class CreatureServiceImpl implements CreatureService {

    @Autowired
    final private MemberRepository memberRepository;
    @Autowired
    final private CreatureRepository creatureRepository;

    //크리쳐 만들기
    @Override
    public Creature createFristCreature(String keyword, String photo, String creatureName) {
        //멤버 가져오고
        Member member = SecurityUtil.getCurrentMember();
        //크리쳐 만들고
        Creature creature = new Creature();
        //이름 설정하고
        creature.setCreatureName(creatureName);
        //멤버와 매핑 시켜주고
        creature.setMember(member);
        //경험치와 레벨은 자동 0으로 설정

        creatureRepository.save(creature); //저장

        //여기서 사진 만들라고 명령 내리시고!!!!!!!!!!!!!!!!!!!!!!

        return creature;
    }

    // 크리쳐 보기
    @Override

    public CreatureInfoResponseDTO creatureInfo(Member member) {

        CreatureInfoResponseDTO creatureInfoResponseDTO = new CreatureInfoResponseDTO();
        creatureInfoResponseDTO.setName(member.getCreature().getCreatureName());
        creatureInfoResponseDTO.setLevel(member.getCreature().getLevel());
        creatureInfoResponseDTO.setExp(member.getCreature().getExp());
        creatureInfoResponseDTO.setImg(generateCreatureImgPath(member));

        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();
        // 가입 날짜 가져오기 (Timestamp를 LocalDate로 변환)
        LocalDate joinDate = member.getJoinDate().toLocalDate();
        // 가입한 날로부터 며칠째인지 계산
        int daysSinceJoin = Period.between(joinDate, currentDate).getDays();

        // day 설정
        creatureInfoResponseDTO.setDay(daysSinceJoin);

        return creatureInfoResponseDTO;
    }

    //크리쳐 삭제(재발급을 위한)
    @Override
    public boolean removeCreature() {
        Member member = SecurityUtil.getCurrentMember();
        Creature creature = member.getCreature();
        creatureRepository.delete(creature);
        member.setCreature(null);
        memberRepository.save(member);
        return true;
    }

    //크리쳐 성장
    @Override
    public void expUp(int toDoCnt) {

        Creature creature = SecurityUtil.getCurrentMember().getCreature();

        System.out.println(creature.toString());

        // 하루 오를 수 있는 최대한의 경험치 제한
        if (toDoCnt > 7) {
            toDoCnt = 7;
        }

        int nowExp = creature.getExp() + toDoCnt;

        // 현재 레벨에 따른 경험치와 레벨업 조건 처리
        switch (creature.getLevel()) {
            case 0: // 알
                if (nowExp >= 10) { // 레벨업 조건
                    creature.setLevel(1);
                    creature.setExp(nowExp - 10);
                } else {
                    System.out.println(nowExp);
                    creature.setExp(nowExp);
                }
                break;
            case 1: // 1레벨
                if (nowExp >= 30) { // 레벨업 조건
                    creature.setLevel(2);
                    creature.setExp(nowExp - 30);
                } else {
                    creature.setExp(nowExp);
                }
                break;
            case 2: // 2레벨
                if (nowExp >= 100) { // 레벨업 조건
                    creature.setLevel(3);
                    creature.setExp(nowExp - 100);
                } else {
                    creature.setExp(nowExp);
                }
                break;
            case 3: // 3레벨
            default:
                creature.setExp(nowExp);
                break;
        }
        creatureRepository.save(creature);
    }


    //크리쳐 이미지 주소
    private String generateCreatureImgPath(Member member) {
        Creature creature = member.getCreature();
        int creatureLevel = creature.getLevel();
        int creatureId = creature.getId();
        return "https://i11b104.p.ssafy.io/image/creature/" + creatureId + "_" +creatureLevel;
    }
}
