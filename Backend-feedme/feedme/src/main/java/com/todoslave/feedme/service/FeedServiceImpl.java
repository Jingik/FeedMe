package com.todoslave.feedme.service;

import com.todoslave.feedme.DTO.*;
import com.todoslave.feedme.domain.entity.Feed.Feed;
import com.todoslave.feedme.domain.entity.Feed.FeedComment;
import com.todoslave.feedme.domain.entity.Feed.FeedLike;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.util.SecurityUtil;
import com.todoslave.feedme.repository.FeedLikeRepository;
import com.todoslave.feedme.repository.FeedRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService{

    @PersistenceContext
    private EntityManager entityManager;

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;


    //피드 생성
    @Override
    public FeedResponseDTO insertFeed(FeedRequestDTO feedRequestDTO) {
        Feed feed = new Feed();
        feed.setContent(feedRequestDTO.getContent());
        feed.setMember(SecurityUtil.getCurrentMember());
        feed.setDiaryDay(feedRequestDTO.getDiaryDate());
        feed.setNickname(SecurityUtil.getCurrentMember().getNickname());

        feedRepository.save(feed);
        return convertToDTO(feed);
    }

    //피드 수정
    @Override
    public FeedResponseDTO modifyFeed(int feedId, FeedModifyRequest feedModifyRequest) {
        Feed feed = feedRepository.findById(feedId).orElse(null);
        if (feed == null) {return null;}

        feed.setContent(feedModifyRequest.getContent());
        feed.setUpdatedAt(LocalDateTime.now());
        feedRepository.save(feed);
        return convertToDTO(feed);
    }

    //피드 삭제
    @Override
    public boolean delectFeed(int feedId) {
        Feed feed = feedRepository.findById(feedId).orElse(null);
        if (feed == null) {return false;}
        if(SecurityUtil.getCurrentMember().equals(feed.getMember())) {
            feedRepository.delete(feed);
            return true;
        }
        return false;
    }

    //피드 좋아요
    @Transactional
    public void toggleLike(int feedId) {
        Member member = SecurityUtil.getCurrentMember();
        Feed feed = feedRepository.findById(feedId).orElseThrow(() ->
                new IllegalArgumentException("해당 피드가 존재하지 않습니다."));

        // 이미 좋아요를 눌렀는지 확인
        FeedLike existingFeedLike = feedLikeRepository.findByMemberAndFeed(member, feed);

        if (existingFeedLike != null) {
            // 이미 좋아요를 눌렀으므로 삭제
            feed.setLikeCount(feed.getLikeCount() -1);
            feedRepository.save(feed);
            feedLikeRepository.delete(existingFeedLike);
        } else {
            // 좋아요를 누르지 않았으므로 새로운 좋아요 추가
            feed.setLikeCount(feed.getLikeCount() + 1);
            FeedLike feedLike = new FeedLike();
            feedLike.setMember(member);
            feedLike.setFeed(feed);
            feedRepository.save(feed);
            feedLikeRepository.save(feedLike);
        }
    }

    // 친구의 피드만 가져오는 메서드
    public List<FeedDTO> getRecentFeedsByFriends() {
        int memberId = SecurityUtil.getCurrentMember().getId();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Feed> recentFeedsByFriends = feedRepository.findRecentFeedsByFriendsAndMe(thirtyDaysAgo, memberId);

        return recentFeedsByFriends.stream()
                .map(this::convertToFeedDTO)
                .collect(Collectors.toList());
    }


    //피드 가져오기
    public List<FeedDTO> getRecentFeeds() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Feed> recentFeeds = feedRepository.findRecentFeeds(thirtyDaysAgo);
        // 친구껏만 feed오는 뭔가!!!

       //

        return recentFeeds.stream()
                .map(this::convertToFeedDTO)
                .collect(Collectors.toList());
    }

    private FeedDTO convertToFeedDTO(Feed feed) {
        FeedDTO feedDTO = new FeedDTO();

        FeedLike existingFeedLike = feedLikeRepository.findByMemberAndFeed(SecurityUtil.getCurrentMember(), feed);

        if (existingFeedLike != null) {
            feedDTO.setMyLike(false);
        }else {
            feedDTO.setMyLike(true);
        }

        feedDTO.setEmail(feed.getMember().getEmail());
        feedDTO.setFeedId(feed.getId());
        feedDTO.setNickname(feed.getNickname());
        feedDTO.setImg("https://i11b104.p.ssafy.io/image/pictureDiary/"+SecurityUtil.getCurrentUserId()+"_"+feed.getDiaryDay()); // 이미지 처리 로직 필요
        feedDTO.setCaption(feed.getContent());
        feedDTO.setLastCreateTime(feed.getUpdatedAt());
        feedDTO.setLikes(feed.getLikeCount());

        feedDTO.setComments(feed.getFeedComments().stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList()));
        return feedDTO;
    }

    private FeedCommentDTO convertToCommentDTO(FeedComment comment) {
        FeedCommentDTO commentDTO = new FeedCommentDTO();
        commentDTO.setFeedComentId(comment.getId());
        commentDTO.setNickname(comment.getMember().getNickname());
        commentDTO.setComment(comment.getContent());
        commentDTO.setTime(comment.getCreatedAt().toString());
        commentDTO.setEmail(comment.getMember().getEmail());
        return commentDTO;
    }

    // Feed 엔티티를 FeedResponseDTO로 변환하는 메서드
    public FeedResponseDTO convertToDTO(Feed feed) {
        FeedResponseDTO dto = new FeedResponseDTO();
        dto.setId(feed.getId());
        dto.setImg("https://i11b104.p.ssafy.io/image/pictureDiary/"+SecurityUtil.getCurrentUserId()+"_"+feed.getDiaryDay()); // 이미지 URL이 엔티티에 없다면 필요에 따라 설정
        dto.setContent(feed.getContent());
        dto.setAuthor(feed.getNickname());
        dto.setLikeCnt(String.valueOf(feed.getLikeCount()));
        dto.setCreatedAt(feed.getCreatedAt());
        return dto;
    }

}


