package com.jpa.market.service;

import com.jpa.market.constent.OAuthType;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

// DefaultOAuth2UserService : 소셜 서버에 가서 액세스 토큰을 이용하여 사용자의 정보를 가져옴
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registId = userRequest.getClientRegistration().getRegistrationId();

        String providerId = "";
        String nickName = "";
        String email = "";
        OAuthType oAuthType = null;
        String nameAttributeKey = "";

        if("kakao".equals(registId)){
            providerId = attributes.get("id").toString();

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            nickName = (String) profile.get("nickname");
            oAuthType = OAuthType.KAKAO;
            nameAttributeKey = "id";
        }else {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 방식입니다.");
        }

        String loginId = oAuthType.name() + "_" + providerId;

        final String finalNickname = nickName;
        final String finalEmail = email;
        final OAuthType finalOAuthType = oAuthType;

        Member member = memberRepository.findByLoginId(loginId)
                .orElseGet(() -> {
                    String encodePwd = passwordEncoder.encode("OAUTH_" + UUID.randomUUID());
                    Member newMember = Member.createOAuthMember(loginId, finalNickname, finalEmail, encodePwd, finalOAuthType);

                    return  memberRepository.save(newMember);
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                attributes,
                nameAttributeKey
        );
    }
}
