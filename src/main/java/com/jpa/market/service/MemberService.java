package com.jpa.market.service;

import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 패스워드 암호화 처리를 위해서 주입
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public Long joinMember(MemberJoinDto dto){
        checkMember(dto);
        Member member = Member.createMember(dto, passwordEncoder);
        memberRepository.save(member);

        return member.getId();
    }

    public void checkMember(MemberJoinDto dto){
        if(memberRepository.existsByLoginId(dto.getLoginId()))
            // IllegalStateException : 메서드 호출 시 객체의 상태가 등록할 수 없는 경우에 발생
            throw new IllegalStateException("이미 사용중인 아이디입니다.");

        if(memberRepository.existsByLoginId(dto.getEmail()))
            // IllegalStateException : 메서드 호출 시 객체의 상태가 등록할 수 없는 경우에 발생
            throw new IllegalStateException("이미 사용중인 이메일입니다.");

    }
}
