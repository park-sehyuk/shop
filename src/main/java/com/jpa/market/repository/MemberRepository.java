package com.jpa.market.repository;

import com.jpa.market.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // findBy... : DB에서 조건에 맞는 엔티티를 조회할 때 사용
    //              반환타입에 다라 다르게 동작
    // existsBy... : 조건에 맞는 데이터의 존재 여부만 확인
    //               반환타입 -> boolean
    //              select count(*) > 0 from member where logindId = "abc"
    // id 중복검사
    boolean existsByLoginId(String loginId);

    // 기존 가입자인지 확인하기 위해 이메일 중복 검사
    boolean existsByEmail(String email);

    // 로그인 처리(비밀번호는 시큐리티가 알아서 처리)
    // Optional : null이 돌아올 수도 있음을 타입으로 강제적으로 지정
    Optional<Member> findByLoginId(String loginId);
}
