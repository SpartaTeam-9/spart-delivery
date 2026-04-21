package com.sparta.spartadelivery.order.presentation.dto.response;

public record OrderItemResponse(
        String menuName,

        Integer quantity,

        Integer unitPrice
) {
}
