package com.sparta.spartadelivery.menu.application.service;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class MenuPermissionPolicy {

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
        if (requester == null) return false;
        Role role = requester.getRole();
        return role == Role.MASTER || role == Role.MANAGER;
    }

    private boolean isOwnerOfStore(UserPrincipal requester, Store store) {
        if (requester == null || requester.getRole() != Role.OWNER) return false;
        // store.getOwner().getId()와 requester.getId() 비교
        return store.getOwner().getId().equals(requester.getId());
    }
}