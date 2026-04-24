package com.sparta.spartadelivery.order.application.OrderSearch;

import com.sparta.spartadelivery.order.presentation.dto.request.OrderSearchCheck;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchStrategy {

    boolean isSupport(Role role);

    // Role에 따라 Dto 변화
    Page<?> search(OrderSearchCheck check, Pageable pageable);
}
