package com.sparta.spartadelivery.menu.application.service;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import com.sparta.spartadelivery.user.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MenuPermissionPolicy {

    private final StoreRepository storeRepository;

    /**
     * 메뉴 수정 권한 검증
     */
    public void validateMenuModifyPermission(UserPrincipal requester, Menu menu) {
        if (requester == null) throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);

        // 1. MASTER, MANAGER는 모든 메뉴 수정 가능
        if (isMasterOrManager(requester)) return;

        // 2. OWNER는 본인 가게 메뉴만 수정 가능
        if (requester.getRole() == Role.OWNER) {
            // 1. 메뉴가 속한 가게를 조회
            Store store = storeRepository.findByIdAndDeletedAtIsNull(menu.getStoreId())
                    .orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));

            // 2. 그 가게의 주인 ID와 현재 로그인한 사용자의 ID를 비교
            if (isOwnerOfStore(requester, store)) return;
        }

        // 3. 그 외 모든 경우(CUSTOMER 등) 권한 거부
        throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
    }

    /**
     * 메뉴 상세 조회 권한 검증
     */
    public void validateMenuDetailPermission(UserPrincipal requester, Store store, Menu menu) {
        // 1. MASTER, MANAGER는 모든 상태 조회 가능
        if (isMasterOrManager(requester)) return;

        // 2. OWNER 본인 가게 체크
        if (isOwnerOfStore(requester, store)) return;

        // 3. 그 외 (CUSTOMER 또는 다른 가게 OWNER)
        // 숨김 처리되었거나 삭제된 메뉴는 접근 불가
        if (menu.isHidden() || menu.isDeleted()) {
            throw new AppException(MenuErrorCode.MENU_NOT_FOUND);
        }
    }

    /**
     * 메뉴 목록 조회 범위 결정
     */
    public MenuViewScope getMenuListScope(UserPrincipal requester, Store store) {
        // MASTER, MANAGER 이거나 본인 가게 OWNER면 모든 메뉴(숨김 포함) 조회 가능
        if (isMasterOrManager(requester) || isOwnerOfStore(requester, store)) {
            return MenuViewScope.ALL;
        }

        return MenuViewScope.ACTIVE_ONLY;
    }

    // --- Private Helpers (중복 제거용) ---



    private boolean isMasterOrManager(UserPrincipal requester) {
        // request == null check가 유의미한지?
        if (requester == null) return false;
        Role role = requester.getRole();
        return role == Role.MASTER || role == Role.MANAGER;
    }

    // 가게의 주인인지 확인
    private boolean isOwnerOfStore(UserPrincipal requester, Store store) {
        if (requester == null || store == null || requester.getRole() != Role.OWNER) {
            return false;
        }
        // Objects.equals를 사용하여 N+1 예방하는 ID 비교
        return Objects.equals(store.getOwner().getId(), requester.getId());
    }

}