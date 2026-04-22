package com.sparta.spartadelivery.review.service;

import com.sparta.spartadelivery.review.domain.entity.Review;
import com.sparta.spartadelivery.review.domain.repository.ReviewRepository;
import com.sparta.spartadelivery.review.presentation.dto.ReviewDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewDetailDto viewDetail(UUID reviewId) {
        Review review = reviewRepository.getReferenceById(reviewId);
        return ReviewDetailDto.from(review);
    }


}
