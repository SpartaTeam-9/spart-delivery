package com.sparta.spartadelivery.auth.presentation.dto.request;

import com.sparta.spartadelivery.user.domain.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReqSignupDto(

        @NotBlank(message = "사용자 이름은 필수입니다.")
        @Size(min = 2, max = 10, message = "사용자 이름은 2~10자여야 합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,15}$",
                message = "비밀번호는 8~15자의 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 100, message = "닉네임은 최대 100자까지 입력할 수 있습니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 255, message = "이메일은 최대 255자까지 입력할 수 있습니다.")
        String email,

        @NotNull(message = "권한은 필수입니다.")
        Role role
) {
}
