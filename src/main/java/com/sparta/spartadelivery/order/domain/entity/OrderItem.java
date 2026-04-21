package com.sparta.spartadelivery.order.domain.entity;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.order.domain.entity.Order;
import com.sparta.spartadelivery.order.exception.OrderErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order_item")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID menuId;

    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice; // 주문 당시 단기 (스냅샷)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;

    // 이는 Order에서만 제어가 가능합니다.(PRIVATE)
    @Builder
    private OrderItem(Order order, UUID menuId, Integer quantity, Integer unitPrice) {
        validateQuantity(quantity);
        this.order = order;
        this.menuId = menuId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new AppException(OrderErrorCode.INVALID_QUANTITY);
        }
    }

    public Integer getSubTotal() {
        return this.unitPrice * this.quantity;
    }
}
