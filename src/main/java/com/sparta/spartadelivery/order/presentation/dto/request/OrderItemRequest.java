package com.sparta.spartadelivery.order.presentation.dto.request;

import com.sparta.spartadelivery.order.domain.entity.OrderItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderItemRequest(

        @NotNull(message = "menuId는 필수입니다.")
        UUID menuId,

        @NotBlank(message = "메뉴 이름은 필수입니다.")
        String menuName,

        @NotEmpty(message = "수량은 0 이상이어야 합니다.") @Min(0)
        Integer quantity,

        @Min(value = 0, message = "단가는 0원 이상이어야 합니다.")
        Integer unitPrice
) {
}
