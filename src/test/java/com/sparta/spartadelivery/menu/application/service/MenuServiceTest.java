package com.sparta.spartadelivery.menu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.domain.repository.MenuRepository;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import com.sparta.spartadelivery.menu.presentation.dto.request.MenuCreateRequest;
import com.sparta.spartadelivery.menu.presentation.dto.response.MenuDetailResponse;
import com.sparta.spartadelivery.store.domain.entity.Store;
import com.sparta.spartadelivery.store.domain.repository.StoreRepository;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import com.sparta.spartadelivery.user.domain.entity.Role;
import java.util.Optional;
import java.util.UUID;

import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MenuService menuService;

    // -----------------------------
    // 메뉴 등록 실패 - 폐업한 가게
    // -----------------------------
    @Test
    @DisplayName("메뉴 등록 실패 - 삭제된(폐업) 가게인 경우")
    void createMenu_Fail_DeletedStore() {
        UUID storeId = UUID.randomUUID();

        UserPrincipal requester = principal(Role.OWNER);
        MenuCreateRequest request = createRequest();

        Store store = mock(Store.class);
        when(store.isDeleted()).thenReturn(true);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        assertThatThrownBy(() -> menuService.createMenu(storeId, request, requester))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(StoreErrorCode.STORE_NOT_FOUND);

        verify(menuRepository, never()).save(any());
    }

    // -----------------------------
    // 메뉴 상세 조회 실패 - 고객이 삭제된 메뉴 조회
    // -----------------------------
    @Test
    @DisplayName("메뉴 상세 조회 실패 - 고객이 삭제된 메뉴 조회 시")
    void getMenuByRole_Fail_DeletedMenu() {
        UUID menuId = UUID.randomUUID();
        UserPrincipal requester = principal(Role.CUSTOMER);

        Menu menu = mock(Menu.class);
        when(menu.isDeleted()).thenReturn(true);
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> menuService.getMenuByRole(menuId, requester))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(MenuErrorCode.MENU_NOT_FOUND);
    }

    // -----------------------------
    // 메뉴 등록 성공 - OWNER 본인 가게
    // -----------------------------
    @Test
    @DisplayName("메뉴 등록 성공 - OWNER가 본인 가게에 등록")
    void createMenu_Success() {
        UUID storeId = UUID.randomUUID();
        Long ownerId = 1L;

        UserPrincipal requester = principal(ownerId, Role.OWNER);
        MenuCreateRequest request = createRequest();

        UserEntity owner = mock(UserEntity.class);
        when(owner.getId()).thenReturn(ownerId);

        Store store = mock(Store.class);
        when(store.isDeleted()).thenReturn(false);
        when(store.getOwner()).thenReturn(owner);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        when(menuRepository.save(any(Menu.class)))
                .thenAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            ReflectionTestUtils.setField(menu, "id", UUID.randomUUID());
            return menu;
        });

        MenuDetailResponse response = menuService.createMenu(storeId, request, requester);

        assertThat(response).isNotNull();
        verify(menuRepository).save(any(Menu.class));
    }

    // -----------------------------
    // Helper Methods
    // -----------------------------
    private MenuCreateRequest createRequest() {
        return new MenuCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "후라이드 치킨",
                20000,
                "바삭함",
                null,
                false,
                null,
                null
        );
    }

    private UserPrincipal principal(Long id, Role role) {
        return UserPrincipal.builder()
                .id(id)
                .accountName("tester")
                .password("pw")
                .nickname("nick")
                .email("test@test.com")
                .role(role)
                .build();
    }

    private UserPrincipal principal(Role role) {
        return principal(1L, role);
    }
}