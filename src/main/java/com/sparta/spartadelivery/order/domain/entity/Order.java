package com.sparta.spartadelivery.order.domain.entity;

import com.sparta.spartadelivery.address.domain.entity.Address;
import com.sparta.spartadelivery.global.entity.BaseEntity;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.order.exception.OrderErrorCode;
import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    // 주문자 (N : 1)
    @Column(name = "customer_id", nullable = false)
    private Long customerId;


    // 현재 이로 대체합니다.
    @Column(name = "store_id", nullable = false)
    private UUID storeId;


    // 배송지 (N : 1)
    @Column(name = "address_id", nullable = false)
    private UUID addressId;


    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderType orderType = OrderType.ONLINE; // 기본값은 ONLINE으로 설정

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // 기본값은 PENDING으로 설정

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(columnDefinition = "TEXT")
    private String request;

    @Builder
    public Order(Long customerId, UUID storeId, Integer totalPrice, String request) {
        validateTotalPrice(totalPrice);
        this.customerId = customerId;
        this.storeId = storeId;
        this.totalPrice = totalPrice;
        this.request = request;
        this.status = OrderStatus.PENDING;
    }


    public void cancel(LocalDateTime createdAt,LocalDateTime now) {
        if(now.isAfter(createdAt.plusMinutes(5))) {
            throw new AppException(OrderErrorCode.ORDER_CANCEL_TIMEOUT)
        }
        this.status = OrderStatus.CANCELED;
    }

    private void validateTotalPrice(Integer totalPrice) {
        if (totalPrice == null || totalPrice < 0) {
            throw new AppException(OrderErrorCode.TOTAL_PRICE_OVER_ZERO);
        }
    }

}
