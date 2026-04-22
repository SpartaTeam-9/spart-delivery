package com.sparta.spartadelivery.review.presentation.controller;

import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import com.sparta.spartadelivery.review.presentation.dto.ReviewDetailDto;
import com.sparta.spartadelivery.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailDto>> getViewDetail(@PathVariable UUID reviewId) {
        ReviewDetailDto response = reviewService.viewDetail(reviewId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), response));
    }
}
