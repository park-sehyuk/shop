package com.jpa.market.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.jpa.market.dto.KakaoTokenDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class KakaoService {

    public KakaoTokenDto getKakaoAccessToken(String code){
        // 처리에 필요한 url 주소
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        // 스프링에서 제공하는 객체로 브라우저 없이 http요청을 처리할 수 있음.
        RestTemplate rt = new RestTemplate();

        // HttpHeaders (http 요청 헤더 생성)
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

        // http Body 생성(전달해야하는 데이터를 추가)
        // MultiValueMap : 값을 리스트형태로 저장
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id","4d80a21e9267242abe7d8d915ad68ff7");
        params.add("client_secret","uAgnQxQWrDmDmZwfQQTFfFNDapwYMdY2");
        params.add("redirect_uri","http://localhost:8000/auth/members/kakao");
        params.add("code", code);

        // header와 body를 하나로 합침
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, httpHeaders);

        // 실제 요청 보내기
        ResponseEntity<String> response = rt.exchange(
                reqUrl,             // 엑세스 토큰 요청 주소
                HttpMethod.POST,    // 요청 방식
                kakaoTokenRequest,  // 요청 헤더와 바디
                String.class        // 응답받을 타입
        );

        // json과 java의 변환기
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(response.getBody(), KakaoTokenDto.class);
        }catch (JsonProcessingException e){
            throw new RuntimeException("카카오 토큰 가져오기 실패", e);
        }

        // return response.getBody();      // 전체 읃답을 리턴
    }

    public String getKakaoUserInfo(KakaoTokenDto tokenDto){
        // 처리에 필요한 url 주소
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        // 스프링에서 제공하는 객체로 브라우저 없이 http요청을 처리할 수 있음.
        RestTemplate rt = new RestTemplate();

        // HttpHeaders (http 요청 헤더 생성)
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        httpHeaders.add("Authorization","Bearer " + tokenDto.getAccess_token());

        // header와 body를 하나로 합침
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(httpHeaders);

        // 실제 요청 보내기
        ResponseEntity<String> response = rt.exchange(
                reqUrl,             // 엑세스 토큰 요청 주소
                HttpMethod.POST,    // 요청 방식
                kakaoProfileRequest,  // 요청 헤더와 바디
                String.class        // 응답받을 타입
        );

        return response.getBody();      // 전체 읃답을 리턴
    }


}
