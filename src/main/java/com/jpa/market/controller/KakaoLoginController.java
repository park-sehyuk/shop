package com.jpa.market.controller;

import com.jpa.market.dto.KakaoTokenDto;
import com.jpa.market.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/auth/members/kakao")
    public @ResponseBody String kakaoCallBack(@RequestParam("code") String code){
        // 토큰요청
        KakaoTokenDto assessToken = kakaoService.getKakaoAccessToken(code);

        String userInfo = kakaoService.getKakaoUserInfo(assessToken);

        // 토큰 정보가 json형태로 출력
        return "카카오 토큰 정보 : " + userInfo;
    }

}
