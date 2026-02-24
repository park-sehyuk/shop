package com.jpa.market.dto;

import lombok.Getter;
import lombok.Setter;

// 프론트 -> 백(데이터를 입력용 dto)
@Getter
@Setter
public class LoginRequestDto {

    private String LoginId;
    private String password;


}
