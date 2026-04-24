package com.sparta.spartadelivery.order.application;

import com.sparta.spartadelivery.order.domain.repository.OrderRepository;
import com.sparta.spartadelivery.order.domain.repository.OrderSearchValidator;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderDetailInfo;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderSearch.OrderValidateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderService {

    private final OrderRepository orderRepository;

    private final OrderSearchValidator orderSearchValidator;

    public OrderDetailInfo getOrderById(Long userId, UUID orderId) {
        // 본인 or 본인 가게 or manager/master 만 가능하다.
        OrderValidateResult result = orderSearchValidator.validOrderDetails(userId, orderId);

        return OrderDetailInfo.from(result.order(), result.address().getAddress(), result.address().getDetail());
    }

}
