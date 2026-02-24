package com.jpa.market;

import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public MemberJoinDto createMember(){
        MemberJoinDto dto = new MemberJoinDto();

        dto.setLoginId("yong1");
        dto.setPassword("1234");
        dto.setName("강일용");
        dto.setEmail("yongyong@naver.com");
        dto.setAddress("김해시");

        return dto;
    }

    @Test
    public void saveMemberTest(){
        MemberJoinDto dto = createMember();

        Long savedId = memberService.joinMember(dto);

        // Optional : 값이 있을수도 있고 없을수도 있는 객체를 의미
        //             값이 없으면 예외가 발생하는데 , Optional은 예외를 안전하게 처리
//        Optional<Member> savedMember = memberRepository.findById(savedId);
//
//        if(savedMember == null)
//            throw new NoSuchElementException("예외 발생");

        // 년경
        // orElseThrow() : 결과값이 없으면 예외를 발생시키고, 없으면 객체를 반환
        Member savedMember = memberRepository.findById(savedId).orElseThrow();

        assertThat(savedMember.getLoginId()).isEqualTo(dto.getLoginId());

    }

    @Test
    public void saveMemberTest2(){
        MemberJoinDto dto1 = createMember();
        MemberJoinDto dto2 = createMember();

        memberService.joinMember(dto1);

        try {
            memberService.joinMember(dto2);
        }catch (IllegalStateException e){
            assertThat("이미 사용중인 아이디입니다.").isEqualTo(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "test1", roles = "USER")
    public void auditingTest(){
        MemberJoinDto dto = new MemberJoinDto();
        dto.setName("테스트");
        dto.setLoginId("auding");
        dto.setPassword("1234");

        Member newMember = Member.createMember(dto, passwordEncoder);
        memberRepository.save(newMember);

        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId()).orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreateBy());
        System.out.println("update member : " + member.getModifiedBy());

    }

}
