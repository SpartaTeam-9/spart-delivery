package com.sparta.spartadelivery.order.domain.repository;

import com.sparta.spartadelivery.address.domain.entity.Address;
import com.sparta.spartadelivery.address.domain.repository.AddressRepository;
import com.sparta.spartadelivery.address.exception.AddressErrorCode;
import com.sparta.spartadelivery.auth.exception.AuthErrorCode;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.order.domain.entity.Order;
import com.sparta.spartadelivery.order.exception.OrderErrorCode;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderSearch.OrderValidateResult;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.user.domain.entity.Role;
import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import com.sparta.spartadelivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderSearchValidator {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final StoreRepository storeRepository;

    public OrderValidateResult validOrderDetails (Long userId, UUID orderId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AuthErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        Address address = addressRepository.findById(order.getAddressId())
                .orElseThrow(() -> new AppException(AddressErrorCode.ADDRESS_NOT_FOUND));

        Role userRole = user.getRole();

        if (userRole == Role.CUSTOMER) {
            if (!Objects.equals(order.getCustomerId(), userId)) {
                throw new AppException(OrderErrorCode.UNAUTHORIZED_ORDER_ACCESS);
            }
        }

        if (userRole == Role.OWNER) {
            Store store = storeRepository.findByOwner(user);

            if (store.getId() != order.getStoreId()) {
                throw new AppException(OrderErrorCode.UNAUTHORIZED_ORDER_ACCESS);
            }
        }

        return new OrderValidateResult(order, address);

    }

}
