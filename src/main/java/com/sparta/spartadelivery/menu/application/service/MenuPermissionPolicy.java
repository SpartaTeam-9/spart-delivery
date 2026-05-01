package com.sparta.spartadelivery.menu.application.service;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuPermissionPolicy {

    private final StoreRepository storeRepository;

    public MenuPermissionPolicy(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * 삭제 전용 추가 검증 (이미 삭제된 경우 등)
     */
    public void validateDeleteCondition(UserPrincipal user, Menu menu) {
        validateCommon(user, menu);

        if (menu.isDeleted()) {
            throw new AppException(MenuErrorCode.MENU_ALREADY_DELETED);
        }
    }

    /**
     * 숨김 전용 추가 검증 (이미 숨겨진 경우 등)
     */
    public void validateHideCondition(UserPrincipal user, Menu menu) {
        validateCommon(user, menu);

        if (menu.isHidden()) {
            throw new AppException(MenuErrorCode.MENU_ALREADY_HIDDEN);
        }
    }

    /**
     * 수정 전용 추가 검증
     */
    public void validateUpdateCondition(UserPrincipal user, Menu menu) {
        validateCommon(user, menu);

        if (menu.isDeleted()) {
            throw new AppException(MenuErrorCode.MENU_UPDATE_ACCESS_DENIED);
        }
    }

    /**
     * 메뉴 관리(등록, 수정, 삭제) 공통 권한 및 상태 검증
     */
    public void validateManagePermission(UserPrincipal requester, Store store) {
        // 1. 공통: 폐업한 가게는 어떤 관리 작업도 불가
        if (store.isDeleted()) {
            throw new AppException(StoreErrorCode.STORE_NOT_FOUND);
        }

        // 2. 공통: 로그인 및 기본 권한 체크 (CUSTOMER 차단)
        if (requester == null || requester.getRole() == Role.CUSTOMER) {
            throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
        }

        // 3. 역할별 상세 체크
        Role role = requester.getRole();

        // MASTER는 무조건 통과
        if (role == Role.MASTER) return;

        // MANAGER는 메뉴 관리 권한이 없음 (복구만 가능하거나 정책에 따라 다름)
        if (role == Role.MANAGER) {
            throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
        }

        // OWNER는 본인 가게만 가능
        if (role == Role.OWNER) {
            if (!store.getOwner().getId().equals(requester.getId())) {
                throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
            }
        }
    }


    // 공통 권한 체크
    private void validateCommon(UserPrincipal user, Menu menu) {
        if (user == null) {
            throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
        }

        Role role = user.getRole();

        // MASTER는 모두 허용
        if (role == Role.MASTER) return;

        // MANAGER는 수정/숨김 가능
        if (role == Role.MANAGER) return;

        // OWNER는 본인 가게만 가능
        if (role == Role.OWNER) {

            Store store = getStore(menu.getStoreId());

            if (!store.getOwner().getId().equals(user.getId())) {
                throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
            }
            return;
        }

        // CUSTOMER 외
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

    /**
     * Store 조회
     */
    private Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private boolean isMasterOrManager(UserPrincipal requester) {
        if (requester == null) return false;
        Role role = requester.getRole();
        return role == Role.MASTER || role == Role.MANAGER;
    }

    private boolean isOwnerOfStore(UserPrincipal requester, Store store) {
        if (requester == null || store == null || requester.getRole() != Role.OWNER) return false;
        // N+1 문제 추후 리팩토링
        return store.getOwner().getId().equals(requester.getId());
    }
}