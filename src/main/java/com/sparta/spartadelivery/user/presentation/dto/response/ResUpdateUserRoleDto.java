package com.sparta.spartadelivery.user.presentation.dto.response;

import com.sparta.spartadelivery.user.domain.entity.Role;
import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;

public record ResUpdateUserRoleDto(
        @Schema(description = "권한이 수정된 사용자의 식별자", example = "1")
        Long id,
        @Schema(description = "권한이 수정된 사용자의 이름", example = "홍길동")
        String username,
        @Schema(description = "변경된 사용자 권한", example = "OWNER")
        Role role
) {

    public static ResUpdateUserRoleDto from(UserEntity user) {
        return new ResUpdateUserRoleDto(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
