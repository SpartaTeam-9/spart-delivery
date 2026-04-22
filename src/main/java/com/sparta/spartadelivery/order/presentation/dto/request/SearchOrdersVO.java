package com.sparta.spartadelivery.order.presentation.dto.request;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;
import com.sparta.spartadelivery.user.domain.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SearchOrdersVO {
    private final Long requesterId;

    private final Role requesterRole;

    private final UUID storeId;

    private final OrderStatus status;
}
