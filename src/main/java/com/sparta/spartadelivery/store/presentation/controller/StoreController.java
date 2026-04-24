package com.sparta.spartadelivery.store.presentation.controller;

import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import com.sparta.spartadelivery.store.application.service.StoreService;
import com.sparta.spartadelivery.store.presentation.dto.request.StoreCreateRequest;
import com.sparta.spartadelivery.store.presentation.dto.response.StoreDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 관리 API")
public class StoreController {

    private final StoreService storeService;

    @Operation(
            summary = "가게 등록 API",
            description = """
                    새로운 가게를 등록합니다.

                    **요청 가능 권한**

                    - OWNER

                    **처리 정책**

                    - 로그인한 OWNER 사용자를 가게 소유자로 저장합니다.
                    - 삭제되지 않은 가게 카테고리와 지역만 참조할 수 있습니다.
                    """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StoreDetailResponse>> createStore(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody StoreCreateRequest request
    ) {
        StoreDetailResponse response = storeService.createStore(request, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "CREATED", response));
    }
}
