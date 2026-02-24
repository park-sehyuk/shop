package com.jpa.market.controller;

import com.jpa.market.dto.LoginRequestDto;
import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 인증을 관리할 매니저 주입(SecurityConfig에 등록)
    private final AuthenticationManager authenticationManager;

    // 회원가입
    // @RequestBody : Body에 담겨있는 http요청 정보를 java객체로 변환
    @PostMapping("/join")
    public ResponseEntity<Long> join(@RequestBody @Valid MemberJoinDto dto){
        Long memberId = memberService.joinMember(dto);

        return ResponseEntity.ok(memberId);
    }

    // ? : 와일드카드, 결과가 텍스트 또는 객체일 수도 있을 때 사용
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request,
                                   HttpServletRequest httpRequest){

        try{
            // 로그인을 시도하기 위해 토큰을 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword());

            // 인증을 수
            // 매니저가 토큰을 넘겨받아서 DB에서 조회, 비밀번호를 비교하도록 함
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 인증에 성공하면 SecurityContext에 저장(서버에서 인증된 해당 유저를 기억)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증 정보를 유질할 수 있도록 세션을 생성
            HttpSession session = httpRequest.getSession(true);

            // 세션에 저장
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // 로그인 성공에 대한 정보를 프론트로 전달
            return ResponseEntity.ok().body(Map.of(
                    "message", "로그인 성공",
                    "loginId", authentication.getName(),    // UserDetails.getUsername()의 리턴값
                    "role", authentication.getAuthorities() // 로그인한 사용자의 권한 목록
                            .stream()                       // 시큐리티는 기본적으로 권한 목록을 객체로 관리
                                                            // 프론트에서 사용하기 쉽도록 객체를 가공할 수 있도록 해줌
                            .map(a -> a.getAuthority())     // 객체에서 문자열을 꺼내어 저장
                            .toList()                       // 꺼내온 정보를 List로 변환하여 전달 - List.of("ROLE_USER", "ROLE_ADMIN")
            ));
        }catch (Exception e){
            return ResponseEntity.status(401).body(Map.of(
                    "message", "아이디 또는 비밀번호가 틀렸습니다."
            ));
        }

    }

}
