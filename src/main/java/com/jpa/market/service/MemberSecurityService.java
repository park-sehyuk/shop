package com.jpa.market.service;

import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberSecurityService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        );

        // roles()를 사용하여 권한을 설정하면
        // new SimpleGrontedAuthority("ROLE_" + )를 시큐리티가 대신 생성하여 설정
        return User.builder()
                .username(member.getLoginId())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
