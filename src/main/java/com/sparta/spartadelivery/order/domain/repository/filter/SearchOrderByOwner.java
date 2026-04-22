package com.sparta.spartadelivery.order.domain.repository.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class SearchOrderByOwner implements SearchOrderByRole{
    @Override
    public Role getAvaliableRole() {
        return Role.OWNER;
    }

    @Override
    public BooleanExpression apply(SearchOrdersVO searchOrdersVO) {
        // 단, storeId 의 소유권 validate 는 서비스에서 해야 합니다.

        return null;
                //QOrder.order.storeId.eq(searchOrdersVO.storeId());
    }
}
