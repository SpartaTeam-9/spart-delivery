package com.sparta.spartadelivery.order.application.implementation;

import com.sparta.spartadelivery.order.application.OrderItemUsecase;
import com.sparta.spartadelivery.order.domain.repository.OrderItemRepository;
import com.sparta.spartadelivery.order.presentation.dto.request.OrderItemRequest;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemUsecaseImpl implements OrderItemUsecase {

    private final OrderItemRepository orderItemRepository;

    // private final MenuRepository menuRepository;

    @Override
    public void createOrderItems(UUID orderId, List<OrderItemRequest> itemRequests) {

    }

    @Override
    public List<OrderItemResponse> getItemsByOrderId(UUID orderID) {
        return null;
    }

    @Override
    public void deleteOrderItems(UUID orderId) {

    }
}
