package com.sparta.spartadelivery.order.domain.repository;

import com.sparta.spartadelivery.order.domain.entity.Order;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;

public interface OrderQueryRepository {

    /**
     * CUSTOMER : 본인 주문 목록 조회
     * OWNER : 본인 가게 주문 조회
     * MANAGER/MASTER : 전체 주문 조회(storeId, status 필터 가능)
     */
    Page<Order> searchOrders(SearchOrdersVO searchOrdersVO, Pageable pageable);

}
