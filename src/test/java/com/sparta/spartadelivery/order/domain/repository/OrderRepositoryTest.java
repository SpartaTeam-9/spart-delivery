package com.sparta.spartadelivery.order.domain.repository;

import com.sparta.spartadelivery.address.config.TestConfig;
import com.sparta.spartadelivery.global.infrastructure.config.QueryDSLConfig;
import com.sparta.spartadelivery.order.domain.entity.Order;
import com.sparta.spartadelivery.order.domain.entity.OrderItem;
import com.sparta.spartadelivery.order.domain.entity.OrderStatus;
import com.sparta.spartadelivery.order.domain.repository.filter.SearchOrderByCustomer;
import com.sparta.spartadelivery.order.domain.repository.filter.SearchOrderByManager;
import com.sparta.spartadelivery.order.domain.repository.filter.SearchOrderByMaster;
import com.sparta.spartadelivery.order.domain.repository.filter.SearchOrderByOwner;
import com.sparta.spartadelivery.order.domain.repository.impl.OrderQueryRepositoryImpl;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({QueryDSLConfig.class,
        TestConfig.class,
        OrderQueryRepositoryImpl.class,
        SearchOrderByCustomer.class,
        SearchOrderByOwner.class,
        SearchOrderByManager.class,
        SearchOrderByMaster.class})
@DisplayName("Order Repository test - jpa & querydsl")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("성공: CUSTOMER는 본인이 주문한 내역만 조회되어야 한다")
    void searchOrders_Customer_Success() {
        // given
        Long myId = 100L;
        Long otherId = 200L;
        saveOrder(myId, UUID.randomUUID(), OrderStatus.PENDING);
        saveOrder(otherId, UUID.randomUUID(), OrderStatus.PENDING);

        SearchOrdersVO searchVO = new SearchOrdersVO(myId, Role.CUSTOMER, null, null);

        // when
        Page<Order> result = orderRepository.searchOrders(searchVO, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCustomerId()).isEqualTo(myId);
    }

    @Test
    @DisplayName("성공: OWNER는 본인 가게의 주문만 조회하며 상태 필터가 적용된다")
    void searchOrders_Owner_WithStatus_Success() {
        // given
        UUID myStoreId = UUID.randomUUID();
        saveOrder(999L, myStoreId, OrderStatus.PENDING);
        saveOrder(999L, myStoreId, OrderStatus.CANCELED);
        saveOrder(999L, UUID.randomUUID(), OrderStatus.PENDING); // 다른 가게 주문

        SearchOrdersVO searchVO = new SearchOrdersVO(1L, Role.OWNER, myStoreId, OrderStatus.PENDING);

        // when
        Page<Order> result = orderRepository.searchOrders(searchVO, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStoreId()).isEqualTo(myStoreId);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("성공: MASTER는 특정 가게의 모든 주문을 조회할 수 있다")
    void searchOrders_Master_FilterByStore() {
        // given
        UUID targetStoreId = UUID.randomUUID();
        saveOrder(1L, targetStoreId, OrderStatus.PENDING);
        saveOrder(2L, targetStoreId, OrderStatus.DELIVERED);
        saveOrder(3L, UUID.randomUUID(), OrderStatus.PENDING);

        SearchOrdersVO searchVO = new SearchOrdersVO(1L, Role.MASTER, targetStoreId, null);

        // when
        Page<Order> result = orderRepository.searchOrders(searchVO, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(o -> o.getStoreId().equals(targetStoreId));
    }

    @Test
    @DisplayName("실패: 조건에 맞는 주문이 없는 경우 빈 페이지를 반환한다")
    void searchOrders_NoResult_ReturnEmpty() {
        // given
        saveOrder(1L, UUID.randomUUID(), OrderStatus.PENDING);
        SearchOrdersVO searchVO = new SearchOrdersVO(1L, Role.CUSTOMER, null, OrderStatus.CANCELED);

        // when
        Page<Order> result = orderRepository.searchOrders(searchVO, pageable);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    // 테스트 데이터 생성 편의 메서드
    private void saveOrder(Long customerId, UUID storeId, OrderStatus status) {
        OrderItem item = OrderItem.create(UUID.randomUUID(), "테스트메뉴", 1, 10000);
        Order order = Order.create(customerId, storeId, UUID.randomUUID(), List.of(item), "요청사항");

        // 1. 먼저 저장 (Auditing에 의해 createdAt이 채워짐)
        orderRepository.saveAndFlush(order);

        // 2. 취소 상태를 만들어야 하는 경우, 저장된 객체의 시간을 기준으로 취소 로직 실행
        if (status == OrderStatus.CANCELED) {
            // 엔티티 내부에서 getCreatedAt()을 사용하므로,
            // 1번 과정에서 flush가 일어났기 때문에 더이상 null이 아닙니다.
            order.cancel(LocalDateTime.now());
            orderRepository.saveAndFlush(order); // 변경 감지 및 상태 업데이트
        }
    }
}
