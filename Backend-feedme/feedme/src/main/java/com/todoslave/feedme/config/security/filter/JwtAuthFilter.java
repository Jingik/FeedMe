//package com.todoslave.feedme.config.security.filter;
//
//
//import com.todoslave.feedme.login.dto.RefreshToken;
//import com.todoslave.feedme.login.dto.TokenResponseStatus;
//import com.todoslave.feedme.login.util.SecurityUserDto;
//import com.todoslave.feedme.domain.entity.membership.Member;
//import com.todoslave.feedme.login.Handler.JWTUtill;
//import com.todoslave.feedme.repository.MemberRepository;
//import com.todoslave.feedme.repository.RefreshTokenRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.JwtException;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Slf4j
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JWTUtill jwtUtil;
//    private final MemberRepository memberRepository;
//
//    private final RefreshTokenRepository tokenRepository;
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return request.getRequestURI().contains("token/");
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        // request Header에서 AccessToken을 가져온다.
//        String atc = request.getHeader("Authorization");
//
//
//
//        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
//        if (!StringUtils.hasText(atc)) {
//            doFilter(request, response, filterChain);
//            return;
//        }
//
//        // AccessToken을 검증하고, 만료되었을경우 예외를 발생시킨다.
//        if (!jwtUtil.verifyToken(atc)) {
//
//            Optional<RefreshToken> refreshToken = tokenRepository.findByAccessToken(atc);
//
////            System.out.println("access token: " + atc + "이고요");
////            System.out.println("refresh token: " + refreshToken+"입니다");
////            System.out.println(refreshToken.toString());
////            System.out.println("refresh token: " + refreshToken.get().getRefreshToken()+"입니다");
//
//            if (refreshToken.isPresent() && jwtUtil.verifyToken(refreshToken.get().getRefreshToken())) {
//
//
//                // RefreshToken 객체를 꺼내온다.
//                RefreshToken resultToken = refreshToken.get();
//                // 권한과 아이디를 추출해 새로운 액세스토큰을 만든다.
//                String newAccessToken = jwtUtil.generateAccessToken(resultToken.getId(), jwtUtil.getRole(resultToken.getRefreshToken()));
//                // 액세스 토큰의 값을 수정해준다.
//                resultToken.updateAccessToken(newAccessToken);
//                tokenRepository.save(resultToken);
//
//                // 새로운 액세스 토큰을 헤더에 추가
//                response.setHeader("Authorization", "Bearer " + newAccessToken);
//
//                // 기존 요청을 업데이트된 토큰과 함께 다시 처리
//                request.setAttribute("Authorization", "Bearer " + newAccessToken);
//                SecurityContextHolder.getContext().setAuthentication(getAuthentication(SecurityUserDto.builder()
//                        .id(Integer.valueOf(resultToken.getId()))
//                        .email(jwtUtil.getUid(resultToken.getAccessToken()))
//                        .role(jwtUtil.getRole(resultToken.getAccessToken()))
//                        .build()));
//
//                // 기존 필터 체인을 다시 호출
//                filterChain.doFilter(request, response);
//
//            } else{
//            throw new JwtException("Refresh 토큰과 Access Token 모두 만료되었습니다."); // 로그아웃 처리
//                 }
//        }
//
//        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
//        if (jwtUtil.verifyToken(atc)) {
//
//            // AccessToken 내부의 payload에 있는 email로 user를 조회한다. 없다면 예외를 발생시킨다 -> 정상 케이스가 아님
//            Member findMember = memberRepository.findByEmail(jwtUtil.getUid(atc))
//                    .orElseThrow(IllegalStateException::new);
//
//            // SecurityContext에 등록할 User 객체를 만들어준다.
//            SecurityUserDto userDto = SecurityUserDto.builder()
//                    .id(findMember.getId())
//                    .email(findMember.getEmail())
//                    .role("ROLE_".concat(findMember.getUserRole()))
//                    .nickname(findMember.getNickname())
//                    .build();
//
//            // SecurityContext에 인증 객체를 등록해준다.
//            Authentication auth = getAuthentication(userDto);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//
//
//    public Authentication getAuthentication(SecurityUserDto member) {
//        return new UsernamePasswordAuthenticationToken(member, "",
//                List.of(new SimpleGrantedAuthority(member.getRole())));
//    }
//
//}

package com.todoslave.feedme.config.security.filter;


import com.todoslave.feedme.login.util.SecurityUserDto;
import com.todoslave.feedme.domain.entity.membership.Member;
import com.todoslave.feedme.login.Handler.JWTUtill;
import com.todoslave.feedme.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtill jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("token/refresh");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
        if (!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        // AccessToken을 검증하고, 만료되었을경우 예외를 발생시킨다.
        if (!jwtUtil.verifyToken(atc)) {
            throw new JwtException("Access Token 만료!");
        }

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (jwtUtil.verifyToken(atc)) {

            // AccessToken 내부의 payload에 있는 email로 user를 조회한다. 없다면 예외를 발생시킨다 -> 정상 케이스가 아님
            Member findMember = memberRepository.findByEmail(jwtUtil.getUid(atc))
                    .orElseThrow(IllegalStateException::new);

            // SecurityContext에 등록할 User 객체를 만들어준다.
            SecurityUserDto userDto = SecurityUserDto.builder()
                    .id(findMember.getId())
                    .email(findMember.getEmail())
                    .role("ROLE_".concat(findMember.getUserRole()))
                    .nickname(findMember.getNickname())
                    .build();

            // SecurityContext에 인증 객체를 등록해준다.
            Authentication auth = getAuthentication(userDto);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }



    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }

}