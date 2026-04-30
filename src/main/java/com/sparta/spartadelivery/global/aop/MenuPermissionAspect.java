package com.sparta.spartadelivery.global.aop;

import com.sparta.spartadelivery.global.annotation.CheckMenuPermission;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.application.service.MenuPermissionPolicy;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.domain.repository.MenuRepository;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class MenuPermissionAspect {

    private final MenuPermissionPolicy menuPermissionPolicy;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // @CheckMenuPermission이 붙은 메서드 실행 전에 권한 검증 수행
    // 파라미터 중 menuId와 userPrincipal을 자동으로 바인딩함
    @Before("@annotation(checkMenuPermission) && args(menuId, userPrincipal, ..)")
    public void checkMenuPermission(
            CheckMenuPermission checkMenuPermission,
            UUID menuId,
            UserPrincipal userPrincipal
    ) {
        // 1. 대상 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new AppException(MenuErrorCode.MENU_NOT_FOUND));

        Store store = storeRepository.findById(menu.getStoreId())
                .orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));

        // 2. 통합 Policy를 호출하여 권한 검증
        menuPermissionPolicy.validateMenuModifyPermission(userPrincipal, store);
    }
}