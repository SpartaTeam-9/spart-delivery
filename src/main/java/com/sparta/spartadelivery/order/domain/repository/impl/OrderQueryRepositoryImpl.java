package com.sparta.spartadelivery.order.domain.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.order.domain.entity.Order;
import com.sparta.spartadelivery.order.domain.entity.OrderStatus;
import com.sparta.spartadelivery.order.domain.entity.QOrder;
import com.sparta.spartadelivery.order.domain.entity.QOrderItem;
import com.sparta.spartadelivery.order.domain.repository.OrderQueryRepository;
import com.sparta.spartadelivery.order.domain.repository.filter.SearchOrderByRole;
import com.sparta.spartadelivery.order.exception.OrderErrorCode;
import com.sparta.spartadelivery.order.presentation.dto.request.SearchOrdersVO;
import com.sparta.spartadelivery.user.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final Map<Role, SearchOrderByRole> orderByRoleMap;

    // for singleton
    private static final QOrder Q_ORDER = QOrder.order;
    private static final QOrderItem Q_ORDER_ITEM = QOrderItem.orderItem;

    // page size is 10, 30, 50
    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 30, 50);

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("createdAt");

    @Override
    public Page<Order> searchOrders(SearchOrdersVO searchOrdersVO, Pageable pageable) {

        validatePageSize(pageable.getPageSize());

        BooleanExpression role = resolveRoleFilter(searchOrdersVO);
        BooleanExpression store = storeIdEqual(searchOrdersVO.storeId());
        BooleanExpression status = statusEqual(searchOrdersVO.status());
        OrderSpecifier<?> sort = resolveSort(pageable);

        // ID 만 페이징 (fetchJoin + Pagination 에서 문제 생길 수 있기에)
        List<UUID> orderIds = jpaQueryFactory
                .select(Q_ORDER.id)
                .from(Q_ORDER)
                .where(
                        isNotDeleted(),
                        role,
                        store,
                        status
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort)
                .fetch();

        if (orderIds.isEmpty()) {
            return PageableExecutionUtils.getPage(
                    Collections.emptyList(), pageable, () -> 0L
            );
        }

        // 2. id 기반 fetch join (N+1 문제 제거)
        List<Order> content = jpaQueryFactory
                .selectFrom(Q_ORDER)
                .leftJoin(Q_ORDER.orderItems, Q_ORDER_ITEM).fetchJoin()
                .where(Q_ORDER.id.in(orderIds))
                .orderBy(sort)
                .fetch();

        // 3. 마지막 페이지라면 쿼리를 새략해야 합니다.
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(Q_ORDER.count())
                .from(Q_ORDER)
                .where(
                        isNotDeleted(),
                        role,
                        store,
                        status
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    // 전략 패턴 기반 -> Role로 위임
    private BooleanExpression resolveRoleFilter(SearchOrdersVO vo) {
        SearchOrderByRole strategy = orderByRoleMap.get(vo.requesterRole());

        if (strategy == null) {
            throw new AppException(OrderErrorCode.ORDER_SEARCH_FORBIDDEN);
        }
        return strategy.apply(vo);
    }

    // helper
    private BooleanExpression isNotDeleted() {
        return Q_ORDER.deletedAt.isNull();
    }

    private BooleanExpression storeIdEqual(UUID storeId) {
        return storeId != null ? Q_ORDER.storeId.eq(storeId) : null;
    }

    private BooleanExpression statusEqual(OrderStatus status) {
        return status != null ? Q_ORDER.status.eq(status) : null;
    }

    // sort 관련
    private OrderSpecifier<?> resolveSort(Pageable pageable) {
        // 기본값은 createdAt 이며 DESC로 변경 가능
        if (pageable.getSort().isUnsorted()) {
            return Q_ORDER.createdAt.desc();
        }

        // Stream과 Optional을 버리고 직관적인 for문으로 변경
        for (Sort.Order sortOrder : pageable.getSort()) {
            if (ALLOWED_SORT_PROPERTIES.contains(sortOrder.getProperty())) {
                return toOrderSpecifier(sortOrder); // 첫 번째로 일치하는 조건 반환
            }
        }

        // 조건에 맞는 게 없으면 기본 정렬 반환
        return Q_ORDER.createdAt.desc();
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order sortOrder) {
        // 현재는 정렬 기준이 생성일 기준으로만 되어 있습니다.
        // 변경을 원한다면 위의 화이트리스트에서 수정 및 아래에서 다시 전략 패턴을 적용해야 합니다.
        return sortOrder.isAscending()
                ? Q_ORDER.createdAt.asc()
                : Q_ORDER.createdAt.desc();
    }


    // validator
    private void validatePageSize(int size) {
        if(!ALLOWED_PAGE_SIZES.contains(size)) {
            throw new AppException(OrderErrorCode.INVALID_PAGE_SIZE);
        }
    }
}
