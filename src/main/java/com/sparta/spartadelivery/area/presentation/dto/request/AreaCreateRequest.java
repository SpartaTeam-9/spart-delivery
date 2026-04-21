package com.sparta.spartadelivery.area.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaCreateRequest(
        @NotBlank(message = "지역명은 필수입니다.")
        @Size(max = 100, message = "지역명은 최대 100자까지 입력할 수 있습니다.")
        String name,

        @NotBlank(message = "시/도는 필수입니다.")
        @Size(max = 50, message = "시/도는 최대 50자까지 입력할 수 있습니다.")
        String city,

        @NotBlank(message = "구/군은 필수입니다.")
        @Size(max = 50, message = "구/군은 최대 50자까지 입력할 수 있습니다.")
        String district,

        Boolean active
) {
}
