package com.todoslave.feedme.domain.entity.membership;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.todoslave.feedme.domain.entity.avatar.Creature;
import com.todoslave.feedme.domain.entity.Feed.Feed;
import com.todoslave.feedme.domain.entity.Feed.FeedComment;
import com.todoslave.feedme.domain.entity.Feed.FeedLike;
import com.todoslave.feedme.domain.entity.Feed.FeedRecomment;

import com.todoslave.feedme.domain.entity.alarm.Alarm;
import com.todoslave.feedme.domain.entity.communication.Friend;
import com.todoslave.feedme.domain.entity.communication.FriendRequest;
import com.todoslave.feedme.domain.entity.diary.PictureDiary;
import com.todoslave.feedme.domain.entity.task.CreatureTodo;
import com.todoslave.feedme.domain.entity.task.DayOff;
import com.todoslave.feedme.domain.entity.task.Todo;
import com.todoslave.feedme.domain.entity.task.TodoCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member { //유저 디테일은 사용자 인증 정보를 담아두는 인터페이스이다.

    //회원 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //GeneratedValue에 관하여 설명    /**
    //     DB마다 다른데 어떤 DB는 seq를 만들기도 하고(MYSQL) 어떤 애들은 테이블을 만드는데, 이러면 항상 ID값이 보장이 된다.
    //     persist 할때 필요 => 영속성 컨텍스트에 값을 딱 넣어야 하는데, 그때 이제 키/밸류가 되는데
    //
    //     **/

    //이메일
    @Column(nullable = false)
    private String email;

    //닉네임
    @Column (nullable = false)
    private String nickname;

    //생일
    @Column (nullable = false)
    private LocalDate birthday;

    // 상태
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Emotion status = Emotion.BASIC; // BASIC, JOY, SAD

    //위도
    @Column
    private Double latitude;

    //경도
    @Column
    private Double longitude;

    //가입일
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    //유저 인증 정보
    @Column(name = "user_role")
    private String userRole;

    //여기부터 1대 N

    //크리쳐와 매핑
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true) //1:1 중에 1을 맡는다.
    @JsonManagedReference
    private Creature creature;

    //친구와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Friend> friends = new ArrayList<>();

    //친구요청과 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FriendRequest> friendRequests = new ArrayList<>();

    // 투두와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Todo> todos = new ArrayList<>();

    // 투두 카테고리와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TodoCategory> todoCategories = new ArrayList<>();

    // 크리쳐 숙제와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CreatureTodo> creatureTodos = new ArrayList<>();

//    //크리쳐와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Creature> creatures = new ArrayList<>();

    // 그림일기와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PictureDiary> pictureDiary = new ArrayList<>();

    // 피드와 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Feed> feeds = new ArrayList<>();

    //좋아요 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private  List<FeedLike> feedLikes = new ArrayList<>();

    //피드 댓글과 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FeedComment> feedComments = new ArrayList<>();

    //피드 대댓글과 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FeedRecomment> feedRecomments = new ArrayList<>();

    //알람과 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<com.todoslave.feedme.domain.entity.alarm.Alarm> alarms = new ArrayList<>();

    //끝내는 날과 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DayOff>  dayOffs= new ArrayList<>();

}

// 메인에 있던건데, 뭐에 쓰는교?!
//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {
//         return List.of(new SimpleGrantedAuthority("MEMBER"));
//     }

//     @Override
//     public String getPassword() {
//         return "";
//     }

//     @Override
//     public String getUsername() {
//         return "";
//     }



//package com.todoslave.feedme.domain.entity.membership;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.todoslave.feedme.domain.entity.avatar.Creature;
//import com.todoslave.feedme.domain.entity.Feed.Feed;
//import com.todoslave.feedme.domain.entity.Feed.FeedComment;
//import com.todoslave.feedme.domain.entity.Feed.FeedLike;
//import com.todoslave.feedme.domain.entity.Feed.FeedRecomment;
//import com.todoslave.feedme.domain.entity.check.Alarm;
//import com.todoslave.feedme.domain.entity.communication.Friend;
//import com.todoslave.feedme.domain.entity.communication.FriendRequest;
//import com.todoslave.feedme.domain.entity.diary.PictureDiary;
//import com.todoslave.feedme.domain.entity.task.CreatureTodo;
//import com.todoslave.feedme.domain.entity.task.Todo;
//import com.todoslave.feedme.domain.entity.task.TodoCategory;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.io.Serializable;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@Table(name = "member")
//@NoArgsConstructor
//@AllArgsConstructor
//public class Member implements UserDetails { //유저 디테일은 사용자 인증 정보를 담아두는 인터페이스이다.
//
//    //회원 ID
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    //GeneratedValue에 관하여 설명
//    /**
//    DB마다 다른데 어떤 DB는 seq를 만들기도 하고(MYSQL) 어떤 애들은 테이블을 만드는데, 이러면 항상 ID값이 보장이 된다.
//     persist 할때 필요 => 영속성 컨텍스트에 값을 딱 넣어야 하는데, 그때 이제 키/밸류가 되는데
//
//     **/
//
//    //이메일
//    @Column(nullable = false)
//    private String email;
//
//    //닉네임
//    @Column //(nullable = false)
//    private String nickname;
//
//    //생일
//    @Column //(nullable = false)
//    private Timestamp birthday;
//
//    //토큰
//    @Column
//    private String token;
//
//    //유저를 하나로 합침
//
//    // 경험치
//    @Column(name = "exp", nullable = false, updatable = false)
//    private int exp = 0 ;
//
//    // 상태
//    @Column(name = "status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Emotion status = Emotion.BASIC; // BASIC, JOY, SAD
//
//    //위도
//    @Column
//    private Double latitude;
//
//    //경도
//    @Column
//    private Double longitude;
//
//    //가입일
//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime joinDate;
//
//    //유저 인증 정보
//    @Column(name = "user_role")
//    private String userRole;
//
//    //여기부터 1대 N
//
//    //친구와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Friend> friends = new ArrayList<>();
//
//    //친구요청과 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<FriendRequest> friendRequests = new ArrayList<>();
//
//    // 투두와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Todo> todos = new ArrayList<>();
//
//    // 투두 카테고리와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<TodoCategory> todoCategories = new ArrayList<>();
//
//    // 크리쳐 숙제와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<CreatureTodo> creatureTodos = new ArrayList<>();
//
//    //크리쳐와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Creature> creatures = new ArrayList<>();
//
//    // 그림일기와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<PictureDiary> pictureDiary = new ArrayList<>();
//
//    // 피드와 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Feed> feeds = new ArrayList<>();
//
//    //좋아요 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private  List<FeedLike> feedLikes = new ArrayList<>();
//
//    //피드 댓글과 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<FeedComment> feedComments = new ArrayList<>();
//
//    //피드 대댓글과 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<FeedRecomment> feedRecomments = new ArrayList<>();
//
//    //알람과 매핑
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Alarm> alarms = new ArrayList<>();
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("MEMBER"));
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//}
