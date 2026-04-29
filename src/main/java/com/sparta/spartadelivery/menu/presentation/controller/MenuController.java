package com.sparta.spartadelivery.menu.presentation.controller;

import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import com.sparta.spartadelivery.menu.application.service.MenuService;
import com.sparta.spartadelivery.menu.presentation.dto.request.MenuCreateRequest;
import com.sparta.spartadelivery.menu.presentation.dto.response.MenuDetailResponse;
import com.sparta.spartadelivery.menu.presentation.dto.response.MenuListResponse;
import com.sparta.spartadelivery.store.presentation.dto.request.StoreCreateRequest;
import com.sparta.spartadelivery.store.presentation.dto.response.StoreDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Menu", description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(
            summary = "메뉴 등록 API",
            description = """
                    새로운 메뉴를 등록합니다.

                    **요청 가능 권한**

                    - OWNER

                    **처리 정책**

                    - 로그인한 OWNER 사용자의 가게에 메뉴를 저장합니다.
                    """
    )
    @PostMapping("/stores/{storeId}/menus")
    public ResponseEntity<ApiResponse<MenuDetailResponse>> createMenu(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody MenuCreateRequest request
    ) {
        MenuDetailResponse response = menuService.createMenu(storeId, request, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "CREATED", response));
    }

    @Operation(
            summary = "메뉴 목록 조회 API",
            description = """
                    특정 매장의 메뉴 목록을 조회합니다.

                    **권한별 조회 정책**

                    - CUSTOMER: 숨겨지지 않은 메뉴만 조회 가능
                    - OWNER/MANAGER: 숨겨진 메뉴를 포함하여 조회 가능
                    - MASTER: 숨겨진, 삭제된 메뉴를 포함하여 조회 가능
                    """
    )
    @GetMapping("/stores/{storeId}/menus")
    public List<MenuListResponse> getMenus(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return menuService.getMenusByRole(storeId, userPrincipal);
    }

    @Operation(
            summary = "메뉴 상세 조회 API",
            description = """
                    메뉴의 상세 정보를 조회합니다.

                    **권한별 조회 정책**

                    - CUSTOMER: 숨겨지지 않은 메뉴만 조회 가능
                    - OWNER/MANAGER: 숨겨진 메뉴를 포함하여 조회 가능
                    - MASTER: 숨겨진, 삭제된 메뉴를 포함하여 조회 가능
                    """
    )
    @GetMapping("/menus/{menuId}")
    public MenuDetailResponse getMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return menuService.getMenuByRole(menuId, userPrincipal);
    }

}
