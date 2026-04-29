package com.sparta.spartadelivery.payment.presentation.controller;

import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import com.sparta.spartadelivery.payment.application.PaymentService;
import com.sparta.spartadelivery.payment.presentation.dto.response.PaymentDetailResponse;
import com.sparta.spartadelivery.payment.presentation.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class GetPaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "모든 결제 정보 조회 API"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPayments(
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {
        List<PaymentResponse> result = paymentService.getAllPayments(userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", result));
    }


    @Operation(
            summary = "단건 결제 정보 조회 API"
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<?>> getPaymentDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID paymentId
            ) {
        PaymentDetailResponse result = paymentService.getDetailPayment(userPrincipal.getId(), paymentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", result));
    }

}
