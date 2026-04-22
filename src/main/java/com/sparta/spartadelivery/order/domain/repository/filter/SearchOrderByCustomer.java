package com.sparta.spartadelivery.order.domain.repository.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class SearchOrderByCustomer implements SearchOrderByRole{
    @Override
    public Role getAvaliableRole() {
        return Role.CUSTOMER;
    }

    @Override
    public BooleanExpression apply(SearchOrdersVO searchOrdersVO) {
        return null;
                //QOrder.order.customerId.eq(searchOrdersVO.requesterId());
    }
}
