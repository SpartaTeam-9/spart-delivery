package com.sparta.spartadelivery.user.presentation.dto.request;

import com.sparta.spartadelivery.user.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReqUpdateUserRoleDto(
        @Schema(description = "권한을 수정할 사용자의 현재 권한", example = "OWNER")
        @NotNull(message = "권한은 필수입니다.")
        Role role
) {
}
