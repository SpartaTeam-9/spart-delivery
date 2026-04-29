package com.sparta.spartadelivery.menu.application.service;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.domain.repository.MenuRepository;
import com.sparta.spartadelivery.menu.domain.vo.MoneyVO;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.menu.presentation.dto.request.MenuCreateRequest;
import com.sparta.spartadelivery.menu.presentation.dto.response.MenuDetailResponse;
import com.sparta.spartadelivery.menu.presentation.dto.response.MenuListResponse;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import com.sparta.spartadelivery.user.domain.entity.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    /**
     * 메뉴 등록
     */
    @Transactional
    public MenuDetailResponse createMenu(UUID storeId, MenuCreateRequest request, UserPrincipal requester) {
        // 권한 검증, 데이터 검증, 오류 없이 return 잘 되나 검증

        Store store = getStore(storeId);

        // 1. CUSTOMER 이하 차단
        validateNotCustomer(requester);

        // 2. OWNER라면 자기 가게인지 검증
        validateOwnerStore(store, requester);

        // 3. 데이터 검증
        validateMenuData(request);

        // 4. 메뉴 생성
        Menu menu = createMenuEntity(storeId, request);

        // 5. 저장
        Menu savedMenu = menuRepository.save(menu);
        return MenuDetailResponse.from(savedMenu);
    }

    /**
     * 메뉴 목록 조회
     */
    @Transactional
    public List<MenuListResponse> getMenusByRole(UUID storeId, UserPrincipal requester) {

        Role role = (requester == null) ? Role.CUSTOMER : requester.getRole();

        List<Menu> menus = switch (role) {
            case MASTER, MANAGER -> menuRepository.findAllByStoreId(storeId);
            case OWNER -> menuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId);
            default -> menuRepository.findAllByStoreIdAndDeletedAtIsNullAndIsHiddenFalse(storeId);
        };

        return menus.stream()
                .map(MenuListResponse::from)
                .toList();
    }

    /**
     * 메뉴 상세 조회
     */
    @Transactional
    public MenuDetailResponse getMenuByRole(UUID menuId, UserPrincipal requester) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new AppException(MenuErrorCode.MENU_NOT_FOUND));

        Role role = (requester == null) ? Role.CUSTOMER : requester.getRole();

        // 권한별 노출 정책 검증
        validateMenuVisibility(menu, role);

        return MenuDetailResponse.from(menu);
    }



    // --- Private Helper Methods ---

    /**
     * Store 조회
     */
    private Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .filter(s -> !s.isDeleted()) // 폐업한 가게 체크 추가
                .orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));
    }

    /**
     * 권한 - CUSTOMER 이하 차단
     */
    private void validateNotCustomer(UserPrincipal requester) {
        if (requester == null || requester.getRole() == Role.CUSTOMER) {
            throw new AppException(MenuErrorCode.MENU_ACCESS_DENIED);
        }
    }

    /**
     * 권한 - OWNER라면 자기 가게인지 검증
     */
    private void validateOwnerStore(Store store, UserPrincipal requester) {

        if (requester.getRole() != Role.OWNER) return; // OWNER만 검증

        if (!store.getOwner().getId().equals(requester.getId())) {
            throw new AppException(MenuErrorCode.MENU_CREATE_ACCESS_DENIED);
        }
    }

    /**
     * 데이터 검증
     */
    private void validateMenuData(MenuCreateRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new AppException(MenuErrorCode.MENU_INVALID_REQUEST);
        }

        if (request.price() == null || request.price() < 0) {
            throw new AppException(MenuErrorCode.MENU_INVALID_REQUEST);
        }
    }

    /**
     * active 메뉴 권한
     */
    private void validateMenuVisibility(Menu menu, Role role) {
        if (role == Role.MASTER || role == Role.MANAGER) return;

        // 삭제된 메뉴는 MASTER/MANAGER 외에 조회 불가
        if (menu.isDeleted()) {
            throw new AppException(MenuErrorCode.MENU_NOT_FOUND);
        }

        // CUSTOMER는 숨겨진 메뉴 조회 불가
        if (role == Role.CUSTOMER && menu.isHidden()) {
            throw new AppException(MenuErrorCode.MENU_NOT_FOUND);
        }
    }

    /**
     * MENU 생성
     */
    private Menu createMenuEntity(UUID storeId, MenuCreateRequest request) {
        // 가격 유효성 검증은 MoneyVO 생성자에게 위임
        MoneyVO priceVO = new MoneyVO(request.price());

        return new Menu(
                storeId,
                request.menuCategoryId(),
                request.name().strip(),
                priceVO.getPrice(),
                request.description(),
                request.menuPictureUrl(),
                request.isHidden(),
                request.aiDescription(),
                request.aiPrompt()
        );
    }

}
