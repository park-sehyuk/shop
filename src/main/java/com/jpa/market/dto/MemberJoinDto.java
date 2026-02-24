package com.jpa.market.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberJoinDto {

    @NotBlank(message = "아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Length(min = 1)
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일주소는 필수입니다.")
    private String email;

    private String address;

}
