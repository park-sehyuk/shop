package com.jpa.market.config.jpa;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// 작성자 또는 변경자가 필요할 때 호출
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {

        // 현재 로그인한 사용자의 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";

        // 이전 정보에서 가용자의 이름을 가져와서 userId 변수에 저장(loginId)
        if(authentication != null)
            userId = authentication.getName();

        // 사용자가 없을수도 있음을 표시
        return Optional.of(userId);
    }
}
