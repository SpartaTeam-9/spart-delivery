package com.sparta.spartadelivery.order.domain.repository.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import com.sparta.spartadelivery.user.domain.entity.Role;

public interface SearchOrderByRole {

    Role getAvaliableRole();

    BooleanExpression apply(SearchOrdersVO searchOrdersVO);
}
